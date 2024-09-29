import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


//abstract class which will calculate inflation or shrinkflation
public interface PriceVariationCalculator {
    public abstract Map<String, Float> calculatePriceIncrease(Map<Float, Set<Product>> productWithDifferentSize, LocalDate startDate, LocalDate endDate);
}
