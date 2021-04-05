package com.company;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import java.io.File;

public class FileMoveTest extends CamelTestSupport {
    String pathIn;
    String pathOut;
    @Override
    protected RouteBuilder createRouteBuilder() throws Exception{

        this.pathIn = "src/main/resources/testIn";
        this.pathOut = "src/main/resources/testOut";
        return new FileMove(this.pathIn,this.pathOut);
    }
    @Test
    public void testMoveFile() throws  Exception{

        Thread.sleep(2000);

        //Check the existence of the 1st file
        File target1 = new File(this.pathOut + "\\javaFile1_5ab067d0-dbac-4abd-9413-edabf953edf6.json");
        assertTrue("The 1st file doesn't exist",target1.exists());

        //Check the existence of the 2nd file
        File target2 = new File(this.pathOut + "\\javaFile1_5ab067d0-dbac-4abd-9414-edabf953edf6.json");
        assertTrue("The 2nd file doesn't exist",target2.exists());

        //Check the content of the 1st file
        String content1 = context.getTypeConverter().convertTo(String.class,target1);
        assertEquals("{\"UniqueID\":\"5ab067d0-dbac-4abd-9413-edabf953edf6\",\"ProductName\":\"BOOSTER KIT\",\"ProductCode\":\"04443-22491\",\"InStock\":218,\"PriceRetail\":126.85,\"PriceWholesale\":112.5}",content1);

        //Check the content of the 2nd file
        String content2 = context.getTypeConverter().convertTo(String.class,target2);
        assertEquals("{\"UniqueID\":\"5ab067d0-dbac-4abd-9414-edabf953edf6\",\"ProductName\":\"LOBSTER KIT\",\"ProductCode\":\"04443-22492\",\"InStock\":218,\"PriceRetail\":126.85,\"PriceWholesale\":112.5}",content2);

        deleteDirectory(this.pathOut);
    }

}