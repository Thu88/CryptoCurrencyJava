import com.google.gson.Gson;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONUtil {

    public static List<Map<String, Object>> decodeListOfMaps(String jsonData)
    {
        List<Map<String, Object>> convertedJSONdata;
        Gson gson;

        gson = new Gson();
        convertedJSONdata = new ArrayList<>();

        convertedJSONdata = gson.fromJson(jsonData, convertedJSONdata.getClass());

        return convertedJSONdata;
    }

    public static Map<String, Object> decodeMap(String jsonData)
    {
        Map<String, Object> convertedJSONdata;
        Gson gson;

        gson = new Gson();

        convertedJSONdata = gson.fromJson(jsonData, Map.class);

        return  convertedJSONdata;
    }

    public static OrderList decodeOrders(JsonObject jsonObject)
    {
        List<Order> buyOrderList;
        List<Order> sellOrderList;
        long lastUpdateId;

        buyOrderList = new ArrayList<>();

        lastUpdateId = 0;

        if (jsonObject.containsKey("lastUpdateId")) {

            lastUpdateId = jsonObject.getJsonNumber("lastUpdateId").longValue();
            for (JsonArray a: jsonObject.getJsonArray("bids").getValuesAs(JsonArray.class)) {
                Order order = new Order("BID", Double.parseDouble(a.getString(0)), Double.parseDouble(a.getString(1)));
                buyOrderList.add(order);
            }

            sellOrderList = new ArrayList<>();
            for (JsonArray a: jsonObject.getJsonArray("asks").getValuesAs(JsonArray.class))
            {
                Order order = new Order("ASK", Double.parseDouble(a.getString(0)), Double.parseDouble(a.getString(1)));
                sellOrderList.add(order);
            }
            return new OrderList(lastUpdateId, buyOrderList, sellOrderList);

        }
        else
        {
            for (JsonArray a : jsonObject.getJsonArray("b").getValuesAs(JsonArray.class)) {
                Order order = new Order("BID", Double.parseDouble(a.getString(0)), Double.parseDouble(a.getString(1)),
                        new Timestamp(jsonObject.getJsonNumber("E").longValue()), jsonObject.getJsonNumber("U").longValue(), jsonObject.getJsonNumber("u").longValue());
                buyOrderList.add(order);
            }

            sellOrderList = new ArrayList<>();
            for (JsonArray a : jsonObject.getJsonArray("a").getValuesAs(JsonArray.class)) {
                Order order = new Order("ASK", Double.parseDouble(a.getString(0)), Double.parseDouble(a.getString(1)));
                sellOrderList.add(order);
            }

            return new OrderList(lastUpdateId, buyOrderList, sellOrderList);
        }

    }

}
