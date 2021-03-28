package com.company;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import junit.framework.Assert;
import org.junit.jupiter.api.Test;

class FileTransportTest {
@Test
public void setMap(){
    //test data
    List<String> testData = new ArrayList<>();
    testData.add("5ab067d0-dbac-4abd-9410-edabf953edf6");
    testData.add("04443-22491");
    testData.add("BOOSTER KIT");
    testData.add("112.50");
    testData.add("126.85");
    testData.add("218");
    //expected data
    HashMap<String, Object> expected = new HashMap<String, Object> ();
    expected.put("UniqueID","5ab067d0-dbac-4abd-9410-edabf953edf6");
    expected.put("ProductCode","04443-22491");
    expected.put("ProductName","BOOSTER KIT");
    expected.put("PriceWholesale",112.50);
    expected.put("PriceRetail",126.85);
    expected.put("InStock",218);
    //actual data
    HashMap<String, Object> actual = FileTransport.setMap(testData);
    //expected vs actual
    Assert.assertEquals(expected, actual);
}
}