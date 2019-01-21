
package guru.learningjournal.kafka.examples.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "InvoiceNumber",
    "CustomerCardNo",
    "TotalAmount",
    "EarnedLoyaltyPoints",
    "TotalLoyaltyPoints"
})
public class Notification {

    @JsonProperty("InvoiceNumber")
    private String invoiceNumber;
    @JsonProperty("CustomerCardNo")
    private String customerCardNo;
    @JsonProperty("TotalAmount")
    private Double totalAmount;
    @JsonProperty("EarnedLoyaltyPoints")
    private Double earnedLoyaltyPoints;
    @JsonProperty("TotalLoyaltyPoints")
    private Double totalLoyaltyPoints;

    @JsonProperty("InvoiceNumber")
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    @JsonProperty("InvoiceNumber")
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Notification withInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
        return this;
    }

    @JsonProperty("CustomerCardNo")
    public String getCustomerCardNo() {
        return customerCardNo;
    }

    @JsonProperty("CustomerCardNo")
    public void setCustomerCardNo(String customerCardNo) {
        this.customerCardNo = customerCardNo;
    }

    public Notification withCustomerCardNo(String customerCardNo) {
        this.customerCardNo = customerCardNo;
        return this;
    }

    @JsonProperty("TotalAmount")
    public Double getTotalAmount() {
        return totalAmount;
    }

    @JsonProperty("TotalAmount")
    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Notification withTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
        return this;
    }

    @JsonProperty("EarnedLoyaltyPoints")
    public Double getEarnedLoyaltyPoints() {
        return earnedLoyaltyPoints;
    }

    @JsonProperty("EarnedLoyaltyPoints")
    public void setEarnedLoyaltyPoints(Double earnedLoyaltyPoints) {
        this.earnedLoyaltyPoints = earnedLoyaltyPoints;
    }

    public Notification withEarnedLoyaltyPoints(Double earnedLoyaltyPoints) {
        this.earnedLoyaltyPoints = earnedLoyaltyPoints;
        return this;
    }

    @JsonProperty("TotalLoyaltyPoints")
    public Double getTotalLoyaltyPoints() {
        return totalLoyaltyPoints;
    }

    @JsonProperty("TotalLoyaltyPoints")
    public void setTotalLoyaltyPoints(Double totalLoyaltyPoints) {
        this.totalLoyaltyPoints = totalLoyaltyPoints;
    }

    public Notification withTotalLoyaltyPoints(Double totalLoyaltyPoints) {
        this.totalLoyaltyPoints = totalLoyaltyPoints;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("invoiceNumber", invoiceNumber).append("customerCardNo", customerCardNo).append("totalAmount", totalAmount).append("earnedLoyaltyPoints", earnedLoyaltyPoints).append("totalLoyaltyPoints", totalLoyaltyPoints).toString();
    }

}
