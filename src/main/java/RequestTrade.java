import java.sql.Timestamp;

public class RequestTrade {
    Timestamp time;
    String symbol;

    public RequestTrade(Timestamp time, String symbol) {
        this.time = time;
        this.symbol = symbol;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
