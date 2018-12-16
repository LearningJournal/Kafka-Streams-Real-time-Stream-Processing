
package guru.learningjournal.kafka.examples;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "symbol",
    "series",
    "open",
    "high",
    "low",
    "close",
    "last",
    "previousClose",
    "totalTradedQty",
    "totalTradedVal",
    "tradeDate",
    "totalTrades",
    "isinCode"
})
public class StockData {

    @JsonProperty("symbol")
    private String symbol;
    @JsonProperty("series")
    private String series;
    @JsonProperty("open")
    private String open;
    @JsonProperty("high")
    private Double high;
    @JsonProperty("low")
    private Double low;
    @JsonProperty("close")
    private Double close;
    @JsonProperty("last")
    private Double last;
    @JsonProperty("previousClose")
    private Double previousClose;
    @JsonProperty("totalTradedQty")
    private Double totalTradedQty;
    @JsonProperty("totalTradedVal")
    private Double totalTradedVal;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MMM-yyyy", timezone = "IST")
    @JsonProperty("tradeDate")
    private Date tradeDate;
    @JsonProperty("totalTrades")
    private Double totalTrades;
    @JsonProperty("isinCode")
    private String isinCode;

    @JsonProperty("symbol")
    public String getSymbol() {
        return symbol;
    }

    @JsonProperty("symbol")
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @JsonProperty("series")
    public String getSeries() {
        return series;
    }

    @JsonProperty("series")
    public void setSeries(String series) {
        this.series = series;
    }

    @JsonProperty("open")
    public String getOpen() {
        return open;
    }

    @JsonProperty("open")
    public void setOpen(String open) {
        this.open = open;
    }

    @JsonProperty("high")
    public Double getHigh() {
        return high;
    }

    @JsonProperty("high")
    public void setHigh(Double high) {
        this.high = high;
    }

    @JsonProperty("low")
    public Double getLow() {
        return low;
    }

    @JsonProperty("low")
    public void setLow(Double low) {
        this.low = low;
    }

    @JsonProperty("close")
    public Double getClose() {
        return close;
    }

    @JsonProperty("close")
    public void setClose(Double close) {
        this.close = close;
    }

    @JsonProperty("last")
    public Double getLast() {
        return last;
    }

    @JsonProperty("last")
    public void setLast(Double last) {
        this.last = last;
    }

    @JsonProperty("previousClose")
    public Double getPreviousClose() {
        return previousClose;
    }

    @JsonProperty("previousClose")
    public void setPreviousClose(Double previousClose) {
        this.previousClose = previousClose;
    }

    @JsonProperty("totalTradedQty")
    public Double getTotalTradedQty() {
        return totalTradedQty;
    }

    @JsonProperty("totalTradedQty")
    public void setTotalTradedQty(Double totalTradedQty) {
        this.totalTradedQty = totalTradedQty;
    }

    @JsonProperty("totalTradedVal")
    public Double getTotalTradedVal() {
        return totalTradedVal;
    }

    @JsonProperty("totalTradedVal")
    public void setTotalTradedVal(Double totalTradedVal) {
        this.totalTradedVal = totalTradedVal;
    }

    @JsonProperty("tradeDate")
    public Date getTradeDate() {
        return tradeDate;
    }

    @JsonProperty("tradeDate")
    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    @JsonProperty("totalTrades")
    public Double getTotalTrades() {
        return totalTrades;
    }

    @JsonProperty("totalTrades")
    public void setTotalTrades(Double totalTrades) {
        this.totalTrades = totalTrades;
    }

    @JsonProperty("isinCode")
    public String getIsinCode() {
        return isinCode;
    }

    @JsonProperty("isinCode")
    public void setIsinCode(String isinCode) {
        this.isinCode = isinCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("symbol", symbol).append("series", series).append("open", open).append("high", high).append("low", low).append("close", close).append("last", last).append("previousClose", previousClose).append("totalTradedQty", totalTradedQty).append("totalTradedVal", totalTradedVal).append("tradeDate", tradeDate).append("totalTrades", totalTrades).append("isinCode", isinCode).toString();
    }

}
