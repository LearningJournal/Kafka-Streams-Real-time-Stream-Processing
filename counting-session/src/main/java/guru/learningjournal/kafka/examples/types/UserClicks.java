
package guru.learningjournal.kafka.examples.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "UserID",
    "CreatedTime",
    "CurrentLink",
    "NextLink"
})
public class UserClicks {

    @JsonProperty("UserID")
    private String userID;
    @JsonProperty("CreatedTime")
    private Long createdTime;
    @JsonProperty("CurrentLink")
    private String currentLink;
    @JsonProperty("NextLink")
    private String nextLink;

    @JsonProperty("UserID")
    public String getUserID() {
        return userID;
    }

    @JsonProperty("UserID")
    public void setUserID(String userID) {
        this.userID = userID;
    }

    public UserClicks withUserID(String userID) {
        this.userID = userID;
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

    public UserClicks withCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
        return this;
    }

    @JsonProperty("CurrentLink")
    public String getCurrentLink() {
        return currentLink;
    }

    @JsonProperty("CurrentLink")
    public void setCurrentLink(String currentLink) {
        this.currentLink = currentLink;
    }

    public UserClicks withCurrentLink(String currentLink) {
        this.currentLink = currentLink;
        return this;
    }

    @JsonProperty("NextLink")
    public String getNextLink() {
        return nextLink;
    }

    @JsonProperty("NextLink")
    public void setNextLink(String nextLink) {
        this.nextLink = nextLink;
    }

    public UserClicks withNextLink(String nextLink) {
        this.nextLink = nextLink;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("userID", userID).append("createdTime", createdTime).append("currentLink", currentLink).append("nextLink", nextLink).toString();
    }

}
