import java.time.LocalDate;
import java.util.*;

public class Shrinkflation implements PriceVariationCalculator{

    private List<Product> productsBetweenGivenTime = new ArrayList<>();

    public Shrinkflation(List<Product> products) {
        productsBetweenGivenTime = products;
    }
    @Override
    public Map<String, Float> calculatePriceIncrease(Map<Float, Set<Product>> productWithDifferentSize, LocalDate startDate, LocalDate endDate) {

        Map<String, Float> productExhibitingShrinkflation = new HashMap<>();

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

                if(productClosestToStartDate.get().getCost() == 0) continue;

                Product productClosestToStartDateNotNull = productClosestToStartDate.get();
                float perUnitCostOfStartProduct = calculatePerUnitCost(productClosestToStartDateNotNull);

                // in case if it is discontinued
                if(productClosestToEndDate.get().getCost() == 0 ) {
                    // extract product with different size than current one that we are looping
                    List<Product> productsWithDifferentSizeThanCurrent = productsBetweenGivenTime.stream()
                            .filter(product -> product.getName().equals(productClosestToStartDateNotNull.getName()) &&product.getSizeInBaseUnit() != size).toList();

                    //product with less size and introduced in the same month
                    Optional<Product> productWithLessSizeInTheSameMonth = productsWithDifferentSizeThanCurrent.stream()
                            .filter(product -> (
                                    product.getSizeInBaseUnit() < productClosestToEndDate.get().getSizeInBaseUnit() &&     // checking product with smaller size
                                            (product.getDate().isAfter(productClosestToEndDate.get().getDate()) || product.getDate().isEqual(productClosestToEndDate.get().getDate())) &&      //checking product should be after or equal current product
                                            product.getDate().getMonth() == productClosestToEndDate.get().getDate().getMonth() && product.getDate().getYear() == productClosestToEndDate.get().getDate().getYear()   // checking it should be in same month
                            )).reduce((product1, product2) -> {
                                // calculate shrinkflation
                                float shrinkflationForProduct1 = calculateShrinkflation(perUnitCostOfStartProduct, calculatePerUnitCost(product1));
                                float shrinkflationForProduct2 = calculateShrinkflation(perUnitCostOfStartProduct, calculatePerUnitCost(product2));

                                // product with larger shrinkflation will get return
                                return shrinkflationForProduct1 > shrinkflationForProduct2 ? product1 : product2;
                            });

                    //if there is no smaller product in same month then continue to need to calculate inflation as product discontinue in the end
                    if(productWithLessSizeInTheSameMonth.isEmpty()) continue;

                    //calculate per unit cost for both start and end product
                    Product smallerProductInSameMonthNotNull = productWithLessSizeInTheSameMonth.get();

                    float shrinkflation = calculateShrinkflation(perUnitCostOfStartProduct, calculatePerUnitCost(smallerProductInSameMonthNotNull));

                    // if greater than zero then it will consider as shrinkflation
                    if(shrinkflation > 0)
                        productExhibitingShrinkflation.put(smallerProductInSameMonthNotNull.getName() + " " + smallerProductInSameMonthNotNull.getOriginalSize(), shrinkflation);

                }
            }
        }
        return productExhibitingShrinkflation;
    }

    /**
     *
     * @param product product of which we want to calculate per-unit price
     * @return per-unit cost of a product
     */
    private float calculatePerUnitCost(Product product) {
        return product.getCost()/product.getSizeInBaseUnit();
    }

    /**
     *
     * @param perUnitCostOfStartProduct
     * @param perUnitCostOfEndProduct
     * @return calculate shrinkflation using per-unit cost of products
     */
    private float calculateShrinkflation(float perUnitCostOfStartProduct, float perUnitCostOfEndProduct) {
        return (perUnitCostOfEndProduct-perUnitCostOfStartProduct)/ perUnitCostOfStartProduct;
    }
}
