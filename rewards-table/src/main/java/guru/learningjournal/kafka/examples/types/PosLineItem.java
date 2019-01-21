
package guru.learningjournal.kafka.examples.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ItemCode",
    "ItemDescription",
    "ItemPrice",
    "ItemQty",
    "TotalValue"
})
public class PosLineItem {

    @JsonProperty("ItemCode")
    private String itemCode;
    @JsonProperty("ItemDescription")
    private String itemDescription;
    @JsonProperty("ItemPrice")
    private Double itemPrice;
    @JsonProperty("ItemQty")
    private Integer itemQty;
    @JsonProperty("TotalValue")
    private Double totalValue;

    @JsonProperty("ItemCode")
    public String getItemCode() {
        return itemCode;
    }

    @JsonProperty("ItemCode")
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public PosLineItem withItemCode(String itemCode) {
        this.itemCode = itemCode;
        return this;
    }

    @JsonProperty("ItemDescription")
    public String getItemDescription() {
        return itemDescription;
    }

    @JsonProperty("ItemDescription")
    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public PosLineItem withItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
        return this;
    }

    @JsonProperty("ItemPrice")
    public Double getItemPrice() {
        return itemPrice;
    }

    @JsonProperty("ItemPrice")
    public void setItemPrice(Double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public PosLineItem withItemPrice(Double itemPrice) {
        this.itemPrice = itemPrice;
        return this;
    }

    @JsonProperty("ItemQty")
    public Integer getItemQty() {
        return itemQty;
    }

    @JsonProperty("ItemQty")
    public void setItemQty(Integer itemQty) {
        this.itemQty = itemQty;
    }

    public PosLineItem withItemQty(Integer itemQty) {
        this.itemQty = itemQty;
        return this;
    }

    @JsonProperty("TotalValue")
    public Double getTotalValue() {
        return totalValue;
    }

    @JsonProperty("TotalValue")
    public void setTotalValue(Double totalValue) {
        this.totalValue = totalValue;
    }

    public PosLineItem withTotalValue(Double totalValue) {
        this.totalValue = totalValue;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("itemCode", itemCode).append("itemDescription", itemDescription).append("itemPrice", itemPrice).append("itemQty", itemQty).append("totalValue", totalValue).toString();
    }

}
