public class Currency {
    private final String charCode;
    private final String name;
    private final double value;
    private final int nominal;


    public Currency(String charCode, String name, double value, int nominal) {
        this.charCode = charCode;
        this.name = name;
        this.value = value;
        this.nominal = nominal;
    }

    public String getCharCode() {
        return charCode;
    }

    public String getName() {
        return name;
    }

    public double getValue() {
        return value;
    }

    public int getNominal() {
        return nominal;
    }
}
