import org.junit.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ConverterTest {

    @Test
    public void makeConvertationTest() {
        Coin rub = new Coin("RUB", "Ruble", 2.0, 1);
        Coin usd = new Coin("USD", "Dollar", 1.0, 1);
        double actual = Converter.makeConvertation(1, rub, usd);
        assertEquals(2.0, actual);

    }

    @Test//(expected = ArithmeticException.class)
    public void whenDivideByZeroTest() {
        Coin rub = new Coin("", "Ruble", 2.0, 1);
        Coin usd = new Coin("SD", "", 0, 1);
        double actual = Converter.makeConvertation(1, rub, usd);
        double inf = Double.POSITIVE_INFINITY;
        assertEquals(inf - inf, actual);
    }

    @Test
    public void constructorCoinTest() {
        Coin rub = new Coin("", "Ruble", 2.0, 1);
        System.out.println(rub.getName());
    }
}