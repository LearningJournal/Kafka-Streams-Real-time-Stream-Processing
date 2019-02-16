
package guru.learningjournal.kafka.examples.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "TransactionID",
    "CreatedTime",
    "OTP"
})
public class PaymentConfirmation {

    @JsonProperty("TransactionID")
    private String transactionID;
    @JsonProperty("CreatedTime")
    private Long createdTime;
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

    public PaymentConfirmation withTransactionID(String transactionID) {
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

    public PaymentConfirmation withCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
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

    public PaymentConfirmation withOTP(String oTP) {
        this.oTP = oTP;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("transactionID", transactionID).append("createdTime", createdTime).append("oTP", oTP).toString();
    }

}
