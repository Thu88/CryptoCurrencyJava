import javax.json.JsonObject;
import java.sql.Timestamp;

public class Trade {
    Timestamp eventTime;
    String symbol;
    long tradeID;
    double price;
    double quantity;
    long buyerOrderID;
    long sellerOrderID;
    Timestamp tradeTime;
    boolean isBuyerMaker;

    public Trade() {
    }

    public Trade(Timestamp eventTime, String symbol, long tradeID, double price, long quantity, long buyerOrderID, long sellerOrderID, Timestamp tradeTime, boolean isBuyerMaker) {
        this.eventTime = eventTime;
        this.symbol = symbol;
        this.tradeID = tradeID;
        this.price = price;
        this.quantity = quantity;
        this.buyerOrderID = buyerOrderID;
        this.sellerOrderID = sellerOrderID;
        this.tradeTime = tradeTime;
        this.isBuyerMaker = isBuyerMaker;
    }

    public Trade(JsonObject jsonObject)
    {
        this.eventTime = (new Timestamp(jsonObject.getJsonNumber("E").longValue()));
        this.symbol = (jsonObject.getString("s")).toLowerCase();
        this.tradeID = (jsonObject.getJsonNumber("t").longValue());
        this.price = (Double.parseDouble(jsonObject.getString("p")));
        this.quantity = (Double.parseDouble(jsonObject.getString("q")));
        this.buyerOrderID = (jsonObject.getJsonNumber("b").longValue());
        this.sellerOrderID = (jsonObject.getJsonNumber("a").longValue());
        this.tradeTime = (new Timestamp(jsonObject.getJsonNumber("T").longValue()));
        this.isBuyerMaker = (jsonObject.getBoolean("m"));
    }

    public Timestamp getEventTime() {
        return eventTime;
    }

    public void setEventTime(Timestamp eventTime) {
        this.eventTime = eventTime;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public long getTradeID() {
        return tradeID;
    }

    public void setTradeID(long tradeID) {
        this.tradeID = tradeID;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public long getBuyerOrderID() {
        return buyerOrderID;
    }

    public void setBuyerOrderID(long buyerOrderID) {
        this.buyerOrderID = buyerOrderID;
    }

    public long getSellerOrderID() {
        return sellerOrderID;
    }

    public void setSellerOrderID(long sellerOrderID) {
        this.sellerOrderID = sellerOrderID;
    }

    public Timestamp getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(Timestamp tradeTime) {
        this.tradeTime = tradeTime;
    }

    public boolean isBuyerMaker() {
        return isBuyerMaker;
    }

    public void setBuyerMaker(boolean buyerMaker) {
        isBuyerMaker = buyerMaker;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "eventTime=" + eventTime +
                ", symbol='" + symbol + '\'' +
                ", tradeID=" + tradeID +
                ", price=" + price +
                ", quantity=" + quantity +
                ", buyerOrderID=" + buyerOrderID +
                ", sellerOrderID=" + sellerOrderID +
                ", tradeTime=" + tradeTime +
                ", isBuyerMaker=" + isBuyerMaker +
                '}';
    }
}
