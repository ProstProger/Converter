public class Coin {
    private final String charCode;
    private final String name;
    private final double valueCoin;
    private final int nominal;


    public Coin(String charCode, String name, double valueCoin, int nominal) {
        this.charCode = charCode;
        this.name = name;
        this.valueCoin = valueCoin;
        this.nominal = nominal;
    }

    public String getCharCode() {
        return charCode;
    }

    public String getName() {
        return name;
    }

    public double getValueCoin() {
        return valueCoin;
    }

    public int getNominal() {
        return nominal;
    }
}
