import org.glassfish.tyrus.client.ClientManager;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.net.ssl.HttpsURLConnection;
import javax.websocket.Session;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.*;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class OrderBook {

    static List<RequestTrade> requestTrades = new ArrayList<>();
    static List<OrderList> orderLists = new ArrayList<>();
    static Session mainSession;

    public static void followOrderBook(String symbol)
    {
        /* If the order book is already followed, cancel method */
        for (OrderList o : orderLists)
        {
            if (o.getCoinName().equals(symbol))
            {
                return;
            }
        }

        OrderList orderList;

        mainSession = Client.mainSession;
        orderList = new OrderList(symbol);

        orderLists.add(orderList);

        ClientManager clientManager = ClientManager.createClient();
        try
        {
            Map<String, Object> subscribe = new HashMap<>();

            List<String> params = new ArrayList<>();

            params.add(orderList.getCoinName().toLowerCase() + "@depth");

            subscribe.put("method", "SUBSCRIBE");
            subscribe.put("params", params);
            subscribe.put("id", 10);

            JsonObject jsonObjectSubscribe = Json.createObjectBuilder(subscribe).build();

            TimeUnit.SECONDS.sleep(1);
            System.out.println("Subscribing to " + orderList.getCoinName() + " order book.");
            mainSession.getBasicRemote().sendText(jsonObjectSubscribe.toString());
            TimeUnit.SECONDS.sleep(1);
        }

        catch (IOException | InterruptedException ioe)
        {
            ioe.printStackTrace();
            System.exit(1);

        }
    }

    public static void unfollowOrderBook(OrderList orderList)
    {
        ClientManager clientManager = ClientManager.createClient();
        try
        {
            Map<String, Object> unsubscribe = new HashMap<>();

            List<String> params = new ArrayList<>();

            params.add(orderList.getCoinName().toLowerCase() + "@depth");

            unsubscribe.put("method", "UNSUBSCRIBE");
            unsubscribe.put("params", params);
            unsubscribe.put("id", 10);

            JsonObject jsonObjectUnSubscribe = Json.createObjectBuilder(unsubscribe).build();

            mainSession.getBasicRemote().sendText(jsonObjectUnSubscribe.toString());
            TimeUnit.SECONDS.sleep(1);

            orderLists.remove(orderList);




        }

        catch (IOException | InterruptedException ioe)
        {
            ioe.printStackTrace();
            System.exit(1);

        }
    }

    public static void incomingOrder(JsonObject jsonObject)
    {
        String symbol;
        OrderList orderList;

        orderList = null;
        symbol = jsonObject.getString("s").toLowerCase();

        for (OrderList o : orderLists)
        {
            if (o.getCoinName().equals(symbol))
            {
                orderList = o;
                break;
            }
        }

        if (orderList == null)
        {
            orderList = new OrderList(symbol);
        }

        orderList.modifyAllOrdersFromStream(jsonObject);
    }

    public static OrderList getOrderList(String symbol)
    {
        URL url;
        HttpsURLConnection connection;
        String urlName;
        BufferedReader input;
        StringBuffer buffer = null;
        String inputLine;
        RequestTrade requestTrade = null;


        urlName = String.format("https://api.binance.com/api/v3/depth?symbol=%s&limit=1000", symbol.toUpperCase());

        try
        {

            requestTrade = new RequestTrade(new Timestamp(System.currentTimeMillis()), symbol);

            if (requestTrades.size() > 0)
            {
                while (requestTrade.getTime().getTime() < requestTrades.get(requestTrades.size()-1).getTime().getTime() + 60 * 1000 * 2)
                {
                    TimeUnit.MILLISECONDS.sleep(500);
                }
            }

            requestTrades.add(requestTrade);

            url = new URL(urlName);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            System.out.println("Order list response code: " + connection.getResponseCode());

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                buffer = new StringBuffer();

                while ((inputLine = input.readLine()) != null) {
                    buffer.append(inputLine);
                }

            }
            else
            {
                System.out.println(connection.getResponseCode());
                System.exit(1);
            }
        }
        catch (MalformedURLException mal)
        {
            System.out.println("Wrong url");
            mal.printStackTrace();
            System.exit(1);
        }
        catch (IOException io)
        {
            System.out.println("IO exception");
            io.printStackTrace();
            System.exit(1);
        }
        catch (InterruptedException intE)
        {
            System.out.println("Interrupted Eception");
            intE.printStackTrace();
            System.exit(1);
        }
        JsonReader jsonReader = Json.createReader(new StringReader(buffer.toString()));
        JsonObject jsonObject = jsonReader.readObject();

        requestTrades.remove(requestTrade);

        return JSONUtil.decodeOrders(jsonObject);
    }
}
