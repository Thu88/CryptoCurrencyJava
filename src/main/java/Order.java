import java.sql.Timestamp;

public class Order implements Comparable {
    private String type;
    double price;
    double quantity;
    Timestamp eventTime;
    long firstUpdateIDinEvent;
    long finalUpdateIDinEvent;

    public Order(String type, double price, double quantity) {
        this.type = type;
        this.price = price;
        this.quantity = quantity;
    }
    public Order(String type, double price, double quantity, Timestamp eventTime, long firstUpdateIDinEvent, long finalUpdateIDinEvent) {
        this.type = type;
        this.price = price;
        this.quantity = quantity;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Timestamp getEventTime() {
        return eventTime;
    }

    public void setEventTime(Timestamp eventTime) {
        this.eventTime = eventTime;
    }

    public long getFirstUpdateIDinEvent() {
        return firstUpdateIDinEvent;
    }

    public void setFirstUpdateIDinEvent(long firstUpdateIDinEvent) {
        this.firstUpdateIDinEvent = firstUpdateIDinEvent;
    }

    public long getFinalUpdateIDinEvent() {
        return finalUpdateIDinEvent;
    }

    public void setFinalUpdateIDinEvent(long finalUpdateIDinEvent) {
        this.finalUpdateIDinEvent = finalUpdateIDinEvent;
    }

    public double total()
    {
        return price * quantity;
    }

    @Override
    public String toString() {
        return "Order{" +
                "type='" + type + '\'' +
                ", price=" + price +
                ", total=" + total() +
                '}';
    }


    @Override
    public int compareTo(Object o) {
        {
            Order order = (Order) o;

            if (order.getPrice() < price)
                return -1;
            else if (order.getPrice() == price)
            {
                return 0;
            }
            else
                return 1;
        }

    }
}
