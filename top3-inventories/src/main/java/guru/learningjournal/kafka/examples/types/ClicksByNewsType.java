
package guru.learningjournal.kafka.examples.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "NewsType",
    "Clicks"
})
public class ClicksByNewsType {

    @JsonProperty("NewsType")
    private String newsType;
    @JsonProperty("Clicks")
    private Long clicks;

    @JsonProperty("NewsType")
    public String getNewsType() {
        return newsType;
    }

    @JsonProperty("NewsType")
    public void setNewsType(String newsType) {
        this.newsType = newsType;
    }

    public ClicksByNewsType withNewsType(String newsType) {
        this.newsType = newsType;
        return this;
    }

    @JsonProperty("Clicks")
    public Long getClicks() {
        return clicks;
    }

    @JsonProperty("Clicks")
    public void setClicks(Long clicks) {
        this.clicks = clicks;
    }

    public ClicksByNewsType withClicks(Long clicks) {
        this.clicks = clicks;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("newsType", newsType).append("clicks", clicks).toString();
    }

}
