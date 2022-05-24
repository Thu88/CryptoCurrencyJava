public class Coin {
    private String name;

    public Coin(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Coin{" +
                "name='" + name + '\'' +
                '}';
    }
}