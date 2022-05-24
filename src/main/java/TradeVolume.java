import java.sql.Timestamp;

public class TradeVolume {
    private double buyVolume;
    private double sellVolume;
    private double realVolume;
    private Timestamp startTime;
    private Timestamp endTime;

    public TradeVolume(double buyVolume, double sellVolume, double realVolume, Timestamp time) {
        this.buyVolume = buyVolume;
        this.sellVolume = sellVolume;
        this.realVolume = realVolume;
        this.startTime = new Timestamp(time.getTime());
        this.endTime = new Timestamp(startTime.getTime() + 60 * 1000);

    }

    public TradeVolume (TradeVolume t)
    {
        buyVolume = t.getBuyVolume();
        sellVolume = t.getSellVolume();
        realVolume = t.getRealVolume();
        this.startTime = new Timestamp(t.getStartTime().getTime());
        this.endTime = new Timestamp(startTime.getTime() + 60 * 1000);
    }

    public boolean isTradeInCurrentPeriod(Trade trade)
    {
        if (trade.getTradeTime().getTime() >= startTime.getTime() && trade.getTradeTime().getTime() < endTime.getTime())
        {
            return true;
        }

        return false;
    }

    public boolean isTradeInNextPeriod(Trade trade)
    {
        if (trade.getTradeTime().getTime() > startTime.getTime() + 60 * 1000 && trade.getTradeTime().getTime() < endTime.getTime() + 60 * 1000)
        {
            return true;
        }

        return false;
    }

    public void addTrade(Trade trade)
    {
        double vol = trade.getQuantity() * trade.getPrice();
        if (!trade.isBuyerMaker())
            buyVolume += vol;
        else
            sellVolume += vol;

        realVolume = buyVolume-sellVolume;

    }

    public double getBuyVolume() {
        return buyVolume;
    }

    public void setBuyVolume(double buyVolume) {
        this.buyVolume = buyVolume;
    }

    public double getSellVolume() {
        return sellVolume;
    }

    public void setSellVolume(double sellVolume) {
        this.sellVolume = sellVolume;
    }

    public double getRealVolume() {
        return realVolume;
    }

    public void setRealVolume(double realVolume) {
        this.realVolume = realVolume;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public double getTotalVol() { return buyVolume + sellVolume; }

    @Override
    public String toString() {
        return "TradeVolume{" +
                "buyVolume=" + buyVolume +
                ", sellVolume=" + sellVolume +
                ", realVolume=" + realVolume +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
