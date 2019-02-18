
package guru.learningjournal.kafka.examples.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "LoginID",
    "CreatedTime"
})
public class UserLogin {

    @JsonProperty("LoginID")
    private String loginID;
    @JsonProperty("CreatedTime")
    private Long createdTime;

    @JsonProperty("LoginID")
    public String getLoginID() {
        return loginID;
    }

    @JsonProperty("LoginID")
    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    public UserLogin withLoginID(String loginID) {
        this.loginID = loginID;
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

    public UserLogin withCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("loginID", loginID).append("createdTime", createdTime).toString();
    }

}
