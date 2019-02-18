
package guru.learningjournal.kafka.examples.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "UserName",
    "LoginID",
    "LastLogin"
})
public class UserDetails {

    @JsonProperty("UserName")
    private String userName;
    @JsonProperty("LoginID")
    private String loginID;
    @JsonProperty("LastLogin")
    private Long lastLogin;

    @JsonProperty("UserName")
    public String getUserName() {
        return userName;
    }

    @JsonProperty("UserName")
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserDetails withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    @JsonProperty("LoginID")
    public String getLoginID() {
        return loginID;
    }

    @JsonProperty("LoginID")
    public void setLoginID(String loginID) {
        this.loginID = loginID;
    }

    public UserDetails withLoginID(String loginID) {
        this.loginID = loginID;
        return this;
    }

    @JsonProperty("LastLogin")
    public Long getLastLogin() {
        return lastLogin;
    }

    @JsonProperty("LastLogin")
    public void setLastLogin(Long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public UserDetails withLastLogin(Long lastLogin) {
        this.lastLogin = lastLogin;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("userName", userName).append("loginID", loginID).append("lastLogin", lastLogin).toString();
    }

}
