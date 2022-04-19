public class Coin {
    private String charCode;
    private String name;
    private double valueCoin;


    public Coin(String charCode, String name, double valueCoin) {
        if (charCode != null && !charCode.isEmpty()
                && name != null && !name.isEmpty()
                && valueCoin > 0
        ) {
            this.charCode = charCode;
            this.name = name;
            this.valueCoin = valueCoin;
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

}
