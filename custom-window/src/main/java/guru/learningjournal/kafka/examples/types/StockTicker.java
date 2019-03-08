
package guru.learningjournal.kafka.examples.types;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "tickerSymbol",
    "tickerTime",
    "lastTradedPrice"
})
public class StockTicker {

    @JsonProperty("tickerSymbol")
    private String tickerSymbol;
    @JsonProperty("tickerTime")
    private Long tickerTime;
    @JsonProperty("lastTradedPrice")
    private Double lastTradedPrice;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("tickerSymbol")
    public String getTickerSymbol() {
        return tickerSymbol;
    }

    @JsonProperty("tickerSymbol")
    public void setTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
    }

    public StockTicker withTickerSymbol(String tickerSymbol) {
        this.tickerSymbol = tickerSymbol;
        return this;
    }

    @JsonProperty("tickerTime")
    public Long getTickerTime() {
        return tickerTime;
    }

    @JsonProperty("tickerTime")
    public void setTickerTime(Long tickerTime) {
        this.tickerTime = tickerTime;
    }

    public StockTicker withTickerTime(Long tickerTime) {
        this.tickerTime = tickerTime;
        return this;
    }

    @JsonProperty("lastTradedPrice")
    public Double getLastTradedPrice() {
        return lastTradedPrice;
    }

    @JsonProperty("lastTradedPrice")
    public void setLastTradedPrice(Double lastTradedPrice) {
        this.lastTradedPrice = lastTradedPrice;
    }

    public StockTicker withLastTradedPrice(Double lastTradedPrice) {
        this.lastTradedPrice = lastTradedPrice;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public StockTicker withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("tickerSymbol", tickerSymbol).append("tickerTime", tickerTime).append("lastTradedPrice", lastTradedPrice).append("additionalProperties", additionalProperties).toString();
    }

}
