package com.company;
import java.math.BigDecimal;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

@CsvRecord(separator = ";",crlf = "UNIX",generateHeaderColumns = true)
public class MapBindyToJson {
    @DataField(pos = 1, columnName = "UniqueID")
    private String uniqueID;

    @DataField(pos = 2, columnName = "ProductCode")
    private String productCode;

    @DataField(pos = 3, columnName = "ProductName")
    private String productName;

    @DataField(pos = 4,precision = 2, columnName = "PriceWholesale")
    private BigDecimal priceWholesale;

    @DataField(pos = 5,precision = 2, columnName = "PriceRetail")
    private BigDecimal priceRetail;

    @DataField(pos = 6, columnName = "InStock")
    private int inStock;

    public String getUniqueID() {
        return uniqueID;
    }
    public String getProductCode() {
        return productCode;
    }
    public String getProductName() {
        return productName;
    }
    public BigDecimal getPriceWholesale() {
        return priceWholesale;
    }
    public BigDecimal getPriceRetail() {
        return priceRetail;
    }
    public int getInStock() {
        return inStock;
    }
}