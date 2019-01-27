
package guru.learningjournal.kafka.examples.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "name",
    "department",
    "salary"
})
public class Employee {

    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("department")
    private String department;
    @JsonProperty("salary")
    private Integer salary;

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    public Employee withId(String id) {
        this.id = id;
        return this;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public Employee withName(String name) {
        this.name = name;
        return this;
    }

    @JsonProperty("department")
    public String getDepartment() {
        return department;
    }

    @JsonProperty("department")
    public void setDepartment(String department) {
        this.department = department;
    }

    public Employee withDepartment(String department) {
        this.department = department;
        return this;
    }

    @JsonProperty("salary")
    public Integer getSalary() {
        return salary;
    }

    @JsonProperty("salary")
    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public Employee withSalary(Integer salary) {
        this.salary = salary;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("id", id).append("name", name).append("department", department).append("salary", salary).toString();
    }

}
