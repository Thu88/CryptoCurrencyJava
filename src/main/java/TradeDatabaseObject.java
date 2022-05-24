import java.sql.*;
import java.util.Date;
import java.util.List;

public class TradeDatabaseObject implements ITradeDatabaseObject {


    @Override
    public Trade read(Trade trade) {
        Connection conn;
        PreparedStatement preparedStatement;
        String sql;
        ResultSet resultSet;
        Trade returnTrade;

        sql = "SELECT * FROM " + trade.getSymbol() + " WHERE trade_id = " + trade.getTradeID();

        conn = DatabaseConnectionManager.getConn();

        returnTrade = new Trade();

        try {
            preparedStatement = conn.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while(resultSet.next() == true)
            {
                trade.setTradeID(resultSet.getLong("trade_id"));
                trade.setSymbol(resultSet.getString("symbol"));
                trade.setPrice(resultSet.getDouble("price"));
                trade.setQuantity(resultSet.getDouble("qty"));
                trade.setTradeTime(resultSet.getTimestamp("trade_time"));
                trade.setBuyerMaker(resultSet.getBoolean("is_buyer_maker"));
                trade.setBuyerOrderID(resultSet.getLong("buyer_order_id"));
                trade.setSellerOrderID(resultSet.getLong("seller_order_id"));
                trade.setEventTime(resultSet.getTimestamp("event_time"));
            }
        }
        catch (SQLException sqlE)
        {
            System.out.println("SQL error: ");
            sqlE.printStackTrace();
        }

        return trade;
    }

    public double readLastID(String coin)
    {
        Connection conn;
        PreparedStatement preparedStatement;
        String sql;
        ResultSet resultSet;
        double last_tradeID = -1;

        sql = "SELECT MAX(trade_id) FROM " + coin;

        conn = DatabaseConnectionManager.getConn();


        try {
            preparedStatement = conn.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            resultSet.next();

            last_tradeID = resultSet.getInt(1);


        }
        catch (SQLException sqlE)
        {
            System.out.println("SQL error: " +sqlE.getMessage());
            sqlE.printStackTrace();
        }

        //System.out.println("readLastID done "+last_tradeID);
        return last_tradeID;
    }

    @Override
    public Trade create(Trade trade) {
        Connection conn;
        PreparedStatement preparedStatement;
        String sql;

        conn = DatabaseConnectionManager.getConn();
        sql = "INSERT INTO " + trade.getSymbol() + " (trade_id, symbol, price, qty, trade_time, is_buyer_maker, buyer_order_id, seller_order_id, event_time)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            preparedStatement = conn.prepareStatement(sql);

            preparedStatement.setLong(1, trade.getTradeID());
            preparedStatement.setString(2, trade.getSymbol());
            preparedStatement.setDouble(3, trade.getPrice());
            preparedStatement.setDouble(4, trade.getQuantity());
            preparedStatement.setTimestamp(5, trade.getTradeTime());
            preparedStatement.setBoolean(6, trade.isBuyerMaker);
            preparedStatement.setLong(7, trade.getBuyerOrderID());
            preparedStatement.setLong(8, trade.getSellerOrderID());
            preparedStatement.setTimestamp(9, trade.getEventTime());

            preparedStatement.executeUpdate();

        }
        catch (SQLException sqlE)
        {
            System.out.println(sqlE);
            sqlE.printStackTrace();
        }

        return trade;
    }

    @Override
    public Boolean createAll(List<Trade> tradeList) {
        String sql;

        sql = "";


        try (Connection conn = DatabaseConnectionManager.getConn();)
        {
            Statement statement = conn.createStatement();
            conn.setAutoCommit(false);
            int i = 0;

            //System.out.println("createALL starting loop");

            for (Trade trade : tradeList)
            {
                //System.out.println("createALL loop number " + i);

                i++;
                sql = String.format("INSERT INTO %s (trade_id, symbol, price, qty, trade_time, is_buyer_maker, buyer_order_id, seller_order_id, event_time)" +
                                    "VALUES  (%d, \'%s\', %f, %f, \'%s\', %b, %d, %d, \'%s\')",
                        trade.getSymbol(), trade.getTradeID(), trade.getSymbol(), trade.getPrice(), trade.getQuantity(), trade.getTradeTime(), trade.isBuyerMaker(), trade.getBuyerOrderID(), trade.getSellerOrderID(), trade.getEventTime());

                statement.addBatch(sql);

                if (i % 1000 == 0 || i == tradeList.size()) {

                    statement.executeBatch();
                    conn.setAutoCommit(true);
                    conn.close();
                    System.out.println(trade.getSymbol() + " has been saved to database and connection closed = " + conn.isClosed());
                    return true;
                }

            }
        }
        catch (SQLException sqlE)
        {
            System.out.println("SQL ERROR :");
            sqlE.printStackTrace();
            return false;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        //System.out.println("Createall done.");
        return true;
    }

    @Override
    public List<Trade> readByByld() {
        return null;
    }

    @Override
    public boolean update(Trade trade) {
        return false;
    }

    @Override
    public boolean delete(Trade trade) {
        return false;
    }
}

