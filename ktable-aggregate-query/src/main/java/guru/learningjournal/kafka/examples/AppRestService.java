package guru.learningjournal.kafka.examples;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.learningjournal.kafka.examples.types.DepartmentAggregate;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.kafka.streams.state.StreamsMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import spark.Spark;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
class AppRestService {

    private static final Logger logger = LogManager.getLogger();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final KafkaStreams streams;
    private final HostInfo hostInfo;
    private Client client;
    private Boolean isActive = false;
    private final String NO_RESULTS = "No Results Found";
    private final String APPLICATION_NOT_ACTIVE =
        "Application is not active. Try later.";

    AppRestService(KafkaStreams stream, String hostname, int port) {
        this.streams = stream;
        this.hostInfo = new HostInfo(hostname, port);
        client = ClientBuilder.newClient();
    }

    void setActive(Boolean state) {
        isActive = state;
    }

    private String readKeyFromLocal(String searchKey)
        throws JsonProcessingException {
        ReadOnlyKeyValueStore<String, DepartmentAggregate> departmentStore =
            streams.store(
                AppConfigs.aggStateStoreName,
                QueryableStoreTypes.keyValueStore()
            );
        DepartmentAggregate result = departmentStore.get(searchKey);
        return (result == null) ? NO_RESULTS
            : objectMapper.writeValueAsString(result);
    }

    private String readKeyFromRemote(String searchKey, HostInfo hostInfo) {
        String result;
        String targetHost = String.format("http://%s:%d/kv/%s",
            hostInfo.host(),
            hostInfo.port(),
            searchKey);

        result = client.target(targetHost)
            .request(MediaType.APPLICATION_JSON).get(String.class);

        return (result == null) ? NO_RESULTS
            : (result.equals(APPLICATION_NOT_ACTIVE)) ? APPLICATION_NOT_ACTIVE
            : result;
    }

    private List<KeyValue<String, DepartmentAggregate>> readAllFromLocal() {

        List<KeyValue<String, DepartmentAggregate>> localResults =
            new ArrayList<>();
        ReadOnlyKeyValueStore<String, DepartmentAggregate> departmentStore =
            streams.store(
                AppConfigs.aggStateStoreName,
                QueryableStoreTypes.keyValueStore()
            );

        departmentStore.all().forEachRemaining(localResults::add);
        return localResults;
    }

    private List<KeyValue<String, DepartmentAggregate>> readAllFromRemote(
        HostInfo hostInfo) throws IOException {

        List<KeyValue<String, DepartmentAggregate>> remoteResults =
            new ArrayList<>();

        String targetHost = String.format("http://%s:%d/dept/local",
            hostInfo.host(),
            hostInfo.port());

        String result = client.target(targetHost)
            .request(MediaType.APPLICATION_JSON).get(String.class);

        if (!result.equals(NO_RESULTS)) {
            remoteResults = objectMapper.readValue(result,
                remoteResults.getClass());
        }
        return remoteResults;
    }

    void start() {
        logger.info("Starting KTableAggDemo Query Server");
        Spark.port(hostInfo.port());

        Spark.get("/kv/:key", (req, res) -> {
            String results;
            String searchKey = req.params(":key");
            if (!isActive) {
                results = APPLICATION_NOT_ACTIVE;
            } else {
                StreamsMetadata metadata = streams.metadataForKey(
                    AppConfigs.aggStateStoreName,
                    searchKey,
                    AppSerdes.String().serializer()
                );
                if (metadata.hostInfo().equals(hostInfo)) {
                    logger.info("Fetch value from local state store");
                    results = readKeyFromLocal(searchKey);
                } else {
                    //Fetch value from remote state store
                    logger.info("Fetch value from remote state store");
                    results = readKeyFromRemote(searchKey, metadata.hostInfo());
                }
            }
            return results;
        });

        Spark.get("/dept/all", (req, res) -> {
            List<KeyValue<String, DepartmentAggregate>> allResults = new ArrayList<>();
            String results;

            if (!isActive) {
                results = APPLICATION_NOT_ACTIVE;
                logger.info(" Application Inactive");
            } else {
                Collection<StreamsMetadata> allMetadata =
                    streams.allMetadataForStore(AppConfigs.aggStateStoreName);
                for (StreamsMetadata metadata : allMetadata) {
                    if (metadata.hostInfo().equals(hostInfo)) {
                        //Reading all values from local state store
                        allResults.addAll(readAllFromLocal());
                    } else {
                        //Reading all values from remote state store
                        allResults.addAll(readAllFromRemote(metadata.hostInfo()));
                    }
                }
                results = (allResults.size() == 0) ? NO_RESULTS : objectMapper.writeValueAsString(allResults);
            }

            return results;
        });

        Spark.get("/dept/local", (req, res) -> {

            List<KeyValue<String, DepartmentAggregate>> allResults;
            String results;

            if (!isActive) {
                results = APPLICATION_NOT_ACTIVE;
            } else {
                allResults = readAllFromLocal();
                results = (allResults.size() == 0) ? NO_RESULTS
                    : objectMapper.writeValueAsString(allResults);
            }
            return results;
        });
    }

    void stop() {
        client.close();
        Spark.stop();
    }

}
