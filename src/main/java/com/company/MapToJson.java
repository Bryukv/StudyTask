package com.company;

import java.util.List;
import java.math.BigDecimal;

public class MapToJson {
    private String uniqueID;
    private String productCode;
    private String productName;
    private Double priceWholesale;
    private Double priceRetail;
    private Integer inStock;

    public MapToJson(List<String> rows) {
        this.uniqueID = rows.get(0);
        this.productCode = rows.get(1);
        this.productName = rows.get(2);
        this.priceWholesale = Double.parseDouble(rows.get(3));
        this.priceRetail = Double.parseDouble(rows.get(4));
        this.inStock = Integer.parseInt(rows.get(5));
    }
    public String getUniqueID() {
        return uniqueID;
    }
    public String getProductCode() {
        return productCode;
    }
    public String getProductName() {
        return productName;
    }
    public Double getPriceWholesale() {
        return priceWholesale;
    }
    public Double getPriceRetail() {
        return priceRetail;
    }
    public Integer getInStock() {
        return inStock;
    }

}
