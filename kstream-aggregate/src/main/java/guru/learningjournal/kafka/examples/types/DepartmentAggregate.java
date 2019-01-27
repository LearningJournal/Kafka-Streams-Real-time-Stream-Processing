
package guru.learningjournal.kafka.examples.types;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "total_salary",
    "employee_count",
    "avg_salary"
})
public class DepartmentAggregate {

    @JsonProperty("total_salary")
    private Integer totalSalary;
    @JsonProperty("employee_count")
    private Integer employeeCount;
    @JsonProperty("avg_salary")
    private Double avgSalary;

    @JsonProperty("total_salary")
    public Integer getTotalSalary() {
        return totalSalary;
    }

    @JsonProperty("total_salary")
    public void setTotalSalary(Integer totalSalary) {
        this.totalSalary = totalSalary;
    }

    public DepartmentAggregate withTotalSalary(Integer totalSalary) {
        this.totalSalary = totalSalary;
        return this;
    }

    @JsonProperty("employee_count")
    public Integer getEmployeeCount() {
        return employeeCount;
    }

    @JsonProperty("employee_count")
    public void setEmployeeCount(Integer employeeCount) {
        this.employeeCount = employeeCount;
    }

    public DepartmentAggregate withEmployeeCount(Integer employeeCount) {
        this.employeeCount = employeeCount;
        return this;
    }

    @JsonProperty("avg_salary")
    public Double getAvgSalary() {
        return avgSalary;
    }

    @JsonProperty("avg_salary")
    public void setAvgSalary(Double avgSalary) {
        this.avgSalary = avgSalary;
    }

    public DepartmentAggregate withAvgSalary(Double avgSalary) {
        this.avgSalary = avgSalary;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("totalSalary", totalSalary).append("employeeCount", employeeCount).append("avgSalary", avgSalary).toString();
    }

}
