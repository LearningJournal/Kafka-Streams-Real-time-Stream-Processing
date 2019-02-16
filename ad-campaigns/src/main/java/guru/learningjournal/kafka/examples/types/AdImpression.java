
package guru.learningjournal.kafka.examples.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ImpressionID",
    "Campaigner"
})
public class AdImpression {

    @JsonProperty("ImpressionID")
    private String impressionID;
    @JsonProperty("Campaigner")
    private String campaigner;

    @JsonProperty("ImpressionID")
    public String getImpressionID() {
        return impressionID;
    }

    @JsonProperty("ImpressionID")
    public void setImpressionID(String impressionID) {
        this.impressionID = impressionID;
    }

    public AdImpression withImpressionID(String impressionID) {
        this.impressionID = impressionID;
        return this;
    }

    @JsonProperty("Campaigner")
    public String getCampaigner() {
        return campaigner;
    }

    @JsonProperty("Campaigner")
    public void setCampaigner(String campaigner) {
        this.campaigner = campaigner;
    }

    public AdImpression withCampaigner(String campaigner) {
        this.campaigner = campaigner;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("impressionID", impressionID).append("campaigner", campaigner).toString();
    }

}
