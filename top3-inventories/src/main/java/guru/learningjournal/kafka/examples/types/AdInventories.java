
package guru.learningjournal.kafka.examples.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "InventoryID",
    "NewsType"
})
public class AdInventories {

    @JsonProperty("InventoryID")
    private String inventoryID;
    @JsonProperty("NewsType")
    private String newsType;

    @JsonProperty("InventoryID")
    public String getInventoryID() {
        return inventoryID;
    }

    @JsonProperty("InventoryID")
    public void setInventoryID(String inventoryID) {
        this.inventoryID = inventoryID;
    }

    public AdInventories withInventoryID(String inventoryID) {
        this.inventoryID = inventoryID;
        return this;
    }

    @JsonProperty("NewsType")
    public String getNewsType() {
        return newsType;
    }

    @JsonProperty("NewsType")
    public void setNewsType(String newsType) {
        this.newsType = newsType;
    }

    public AdInventories withNewsType(String newsType) {
        this.newsType = newsType;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("inventoryID", inventoryID).append("newsType", newsType).toString();
    }

}
