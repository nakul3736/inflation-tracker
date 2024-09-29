import java.time.LocalDate;
import java.util.*;

public class Inflation implements PriceVariationCalculator{
    @Override
    public Map<String, Float> calculatePriceIncrease(Map<Float, Set<Product>> productWithDifferentSize, LocalDate startDate, LocalDate endDate) {

        Map<String, Float> productExhibitingInflation = new HashMap<>();

        //looping through each size product available in
        for(Float size: productWithDifferentSize.keySet()) {
            //get products between given time period
            List<Product> productBetweenGivenTimePeriod = productWithDifferentSize.get(size).stream()
                    .filter(product -> product.isProductIntroducedInBetweenGivenDates(startDate, endDate)).toList();

            //querying closest start date
            Optional<Product> productClosestToStartDate = productBetweenGivenTimePeriod.stream()
                    .reduce((product1, product2) -> product1.getDate().isBefore(product2.getDate()) ? product1 : product2 );

            //querying closest end date
            Optional<Product> productClosestToEndDate = productBetweenGivenTimePeriod.stream()
                    .reduce((product1, product2) -> product1.getDate().isAfter(product2.getDate()) ? product1 : product2 );

            //if both present then
            if(productClosestToStartDate.isPresent())  {

                if(productClosestToStartDate.get().getCost() == 0 || productClosestToEndDate.get().getCost() == 0 ) continue;

                //calculation for inflation
                Product notNullStartProduct = productClosestToStartDate.get();
                Product notNullEndProduct = productClosestToEndDate.get();

                //for inflation end product cost > start product cost
                if(notNullStartProduct.getCost() < notNullEndProduct.getCost()) {
                    float inflation = calculateInflation(notNullStartProduct, notNullEndProduct);

                    // should not equal to zero
                    if(inflation != 0) {
                        productExhibitingInflation.put(notNullStartProduct.getName() + " " + notNullEndProduct.getOriginalSize(), inflation);
                    }
                }

            }
        }

        return productExhibitingInflation;
    }

    //calculate inflation between two products with same size and different cost
    public float calculateInflation(Product startProduct, Product endProduct) {
        return (endProduct.getCost() - startProduct.getCost())/startProduct.getCost();
    }
}
