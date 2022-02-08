public class Coin {
    private String charCode;
    private String name;
    private double valueCoin;
    private int nominal;


    public Coin(String charCode, String name, double valueCoin, int nominal) {
        if (charCode != null && !charCode.isEmpty()
                && name != null && !name.isEmpty()
                && valueCoin > 0
                && nominal > 0) {
            this.charCode = charCode;
            this.name = name;
            this.valueCoin = valueCoin;
            this.nominal = nominal;
        }
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
