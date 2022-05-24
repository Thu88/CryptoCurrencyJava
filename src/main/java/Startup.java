import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Startup {

    public static List<Coin> createCoinTables()
    {
        PreparedStatement preparedStatement;
        String sql;
        List<Coin> coinList;
        ResultSet resultSet;
        int size;

        coinList = downLoadCoinList();
        System.out.println("Coin list downloaded");
        /*try (Connection conn = DatabaseConnectionManager.getConn();)
        {
            List<String> tables = new ArrayList<>();
            sql = "SHOW TABLES";
            preparedStatement = conn.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            Statement statement;

            while(resultSet.next())
            {
                tables.add(resultSet.getString(1));
            }

            conn.setAutoCommit(false);
            statement = conn.createStatement();


            for (Coin coin: coinList)
            {
               if (tables.contains(coin.getName()))
               {
                   continue;
               }

               sql = "CREATE TABLE cryptocurrencytrader." + coin.getName() + " " +
                        "(" +
                        "trade_id BIGINT UNSIGNED primary key, " +
                        "symbol VARCHAR(10), " +
                        "price double, " +
                        "qty double, " +
                        "trade_time TIMESTAMP NULL, " +
                        "is_buyer_maker TINYINT(1), " +
                        "buyer_order_id BIGINT UNSIGNED," +
                        "seller_order_id BIGINT UNSIGNED," +
                        "event_time TIMESTAMP NULL" +
                        ")";

                statement.addBatch(sql);
            }
            statement.executeBatch();
            conn.setAutoCommit(true);
            System.out.println("Tables created");
            conn.close();
        }
        catch (SQLException sqlE)
        {
            System.out.println("SQL error: ");
            sqlE.printStackTrace();
            System.exit(1);
        }*/

        return coinList;
    }
    public static List<Coin> downLoadCoinList()
    {
        URL url;
        HttpsURLConnection connection;
        List<Coin> coinList = null;
        String urlName;
        BufferedReader input;
        StringBuffer buffer;
        String inputLine;

        urlName = String.format("https://api.binance.com/api/v3/exchangeInfo");

        try
        {
            url = new URL(urlName);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            System.out.println("Coin list response code: " + connection.getResponseCode());

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                buffer = new StringBuffer();

                while ((inputLine = input.readLine()) != null) {
                    buffer.append(inputLine);
                }

                coinList = decodeJSONsymbols(buffer.toString());
            }
            else
            {
                System.out.println(connection.getResponseCode());
                System.exit(1);
            }

        }
        catch (MalformedURLException urlE)
        {
            System.out.println("URL error:");
            urlE.printStackTrace();
        }
        catch (IOException ioE)
        {
            System.out.println("IO error:");
            ioE.printStackTrace();
        }

        return coinList;
    }

    public static List<Coin> decodeJSONsymbols(String buffer)
    {
        List<Coin> coinList;
        Map<String, Object> jsonMap;
        List<Map<String, Object>> jsonList;

        jsonMap = JSONUtil.decodeMap(buffer.toString());
        jsonList = JSONUtil.decodeListOfMaps(jsonMap.get("symbols").toString());

        coinList = new ArrayList<>();

        for (Map<String, Object> map : jsonList)
        {
            if (map.get("quoteAsset").equals("BTC"))
            {
                coinList.add(new Coin(map.get("symbol").toString().toLowerCase()));
            }
        }

        return coinList;
    }

}
