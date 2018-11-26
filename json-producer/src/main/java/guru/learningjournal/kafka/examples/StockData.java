/*
 * Copyright (c) 2018. Prashant Kumar Pandey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package guru.learningjournal.kafka.examples;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Stock data class represents NSE data element
 *
 * @author prashant
 * @author www.learningjournal.guru
 */
@JsonPropertyOrder({"symbol", "series", "open", "high", "low", "close", "last", "previousClose",
        "totalTradedQty", "totalTradedVal", "tradeDate", "totalTrades", "isinCode"})
public class StockData {
    private String symbol;
    private String series;
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Double last;
    private Double previousClose;
    private Double totalTradedQty;
    private Double totalTradedVal;
    private Date tradeDate;
    private String totalTrades;
    private String isinCode;
    private static final Logger logger = LogManager.getLogger();

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public void setClose(Double close) {
        this.close = close;
    }

    public void setLast(Double last) {
        this.last = last;
    }

    public void setPreviousClose(Double previousClose) {
        this.previousClose = previousClose;
    }

    public void setTotalTradedQty(Double totalTradedQty) {
        this.totalTradedQty = totalTradedQty;
    }

    public void setTotalTradedVal(Double totalTradedVal) {
        this.totalTradedVal = totalTradedVal;
    }

    /**
     * converts a String data into a Date
     * @param tradeDate
     */
    public void setTradeDate(String tradeDate) {
        Date dt = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            dt = dateFormat.parse(tradeDate);
            this.tradeDate = dt;
        } catch (ParseException e) {
            logger.error("Invalid date format - " + tradeDate + "\n Defaulting to current date.");
            this.tradeDate = dt;
        }
    }

    public void setTotalTrades(String totalTrades) {
        this.totalTrades = totalTrades;
    }

    public void setIsinCode(String isinCode) {
        this.isinCode = isinCode;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getSeries() {
        return series;
    }

    public Double getOpen() {
        return open;
    }

    public Double getHigh() {
        return high;
    }

    public Double getLow() {
        return low;
    }

    public Double getClose() {
        return close;
    }

    public Double getLast() {
        return last;
    }

    public Double getPreviousClose() {
        return previousClose;
    }

    public Double getTotalTradedQty() {
        return totalTradedQty;
    }

    public Double getTotalTradedVal() {
        return totalTradedVal;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public String getTotalTrades() {
        return totalTrades;
    }

    public String getIsinCode() {
        return isinCode;
    }

    /**
     * Static function to read data from given dataFile
     * @param dataFile data file name in resource folder
     * @return List of StockData Instance
     * @throws IOException
     */
    static List<StockData> getStocks(String dataFile) throws IOException, NullPointerException {

        URL fileURI = ClassLoader.class.getResource(dataFile);
        File file = new File(fileURI.getFile());
        MappingIterator<StockData> stockDataIterator = new CsvMapper().readerWithTypedSchemaFor(StockData.class).readValues(file);
        return stockDataIterator.readAll();
    }
}

