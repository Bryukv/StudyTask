
import java.util.List;

public class ConvertToJson {
    String resString;
    public ConvertToJson(List<String> rows) {
        this.resString = CsvToJson(rows);
    }

    public String CsvToJson(List<String> rows) {
        String resString = "";
        String columnValue;

        String[] header = new String[]{
                "UniqueID",
                "ProductCode",
                "ProductName",
                "PriceWholesale",
                "PriceRetail",
                "InStock"
        };

        for (int i = 0; i < rows.size(); i++) {
            columnValue = rows.get(i);
            if (!isNumeric(columnValue)){
                columnValue = "\"" + columnValue + "\"";
            }
            resString = resString + "\"" + header[i] + "\":" + columnValue + "\n";
        }

        resString = "{" + resString + "}";
        return resString;
    }
    public static boolean isNumeric(String str){
        try{
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }
}
