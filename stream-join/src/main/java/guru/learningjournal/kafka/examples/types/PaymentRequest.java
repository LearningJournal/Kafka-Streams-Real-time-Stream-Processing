
package guru.learningjournal.kafka.examples.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "TransactionID",
    "CreatedTime",
    "SourceAccountID",
    "TargetAccountID",
    "Amount",
    "OTP"
})
public class PaymentRequest {

    @JsonProperty("TransactionID")
    private String transactionID;
    @JsonProperty("CreatedTime")
    private Long createdTime;
    @JsonProperty("SourceAccountID")
    private String sourceAccountID;
    @JsonProperty("TargetAccountID")
    private String targetAccountID;
    @JsonProperty("Amount")
    private Double amount;
    @JsonProperty("OTP")
    private String oTP;

    @JsonProperty("TransactionID")
    public String getTransactionID() {
        return transactionID;
    }

    @JsonProperty("TransactionID")
    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public PaymentRequest withTransactionID(String transactionID) {
        this.transactionID = transactionID;
        return this;
    }

    @JsonProperty("CreatedTime")
    public Long getCreatedTime() {
        return createdTime;
    }

    @JsonProperty("CreatedTime")
    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public PaymentRequest withCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    @JsonProperty("SourceAccountID")
    public String getSourceAccountID() {
        return sourceAccountID;
    }

    @JsonProperty("SourceAccountID")
    public void setSourceAccountID(String sourceAccountID) {
        this.sourceAccountID = sourceAccountID;
    }

    public PaymentRequest withSourceAccountID(String sourceAccountID) {
        this.sourceAccountID = sourceAccountID;
        return this;
    }

    @JsonProperty("TargetAccountID")
    public String getTargetAccountID() {
        return targetAccountID;
    }

    @JsonProperty("TargetAccountID")
    public void setTargetAccountID(String targetAccountID) {
        this.targetAccountID = targetAccountID;
    }

    public PaymentRequest withTargetAccountID(String targetAccountID) {
        this.targetAccountID = targetAccountID;
        return this;
    }

    @JsonProperty("Amount")
    public Double getAmount() {
        return amount;
    }

    @JsonProperty("Amount")
    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public PaymentRequest withAmount(Double amount) {
        this.amount = amount;
        return this;
    }

    @JsonProperty("OTP")
    public String getOTP() {
        return oTP;
    }

    @JsonProperty("OTP")
    public void setOTP(String oTP) {
        this.oTP = oTP;
    }

    public PaymentRequest withOTP(String oTP) {
        this.oTP = oTP;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("transactionID", transactionID).append("createdTime", createdTime).append("sourceAccountID", sourceAccountID).append("targetAccountID", targetAccountID).append("amount", amount).append("oTP", oTP).toString();
    }

}
