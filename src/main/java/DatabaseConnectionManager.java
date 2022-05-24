import org.apache.commons.dbcp.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnectionManager {
    private static BasicDataSource ds = new BasicDataSource();

    static {
        ds.setUrl("jdbc:mysql://localhost:3306/cryptocurrencytrader?useUnicode=true&useJDBCCompliantTimezoneShift" +
                "=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false");
        ds.setUsername("root");
        ds.setPassword("hierw778YGYGYGY798");
        ds.setMaxActive(50);
        ds.setMaxWait(-1);
        ds.setMaxIdle(10);
        ds.setRemoveAbandoned(true);
        ds.setRemoveAbandonedTimeout(2);
    }

    public static Connection getConn()
    {
        Connection conn = null;
        try
        {
            System.out.println("Number of active connections: " + ds.getNumActive());
            System.out.println("Number of idle connections: " + ds.getNumIdle());
            conn = ds.getConnection();
        }
        catch (SQLException e)
        {
            System.out.println(e);
            e.printStackTrace();
            System.exit(1);
        }
        return conn;
    }

    private DatabaseConnectionManager()
    {

    }
}
