import java.util.List;

public interface ITradeDatabaseObject {

    Trade create(Trade trade);

    Boolean createAll(List<Trade> tradeList);

    Trade read (Trade trade);

    List<Trade> readByByld();

    boolean update(Trade trade);

    boolean delete(Trade trade);

}
