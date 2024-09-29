import java.io.BufferedReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class CostOfLiving {
    //number of fields in product input
    static final int TOTAL_FIELDS_IN_PRODUCT_LINE = 4;

    //number of fields in cart input
    static final int TOTAL_FIELDS_IN_CART_LINE = 2;

    //to keep count of number of products introduced
    int productCount = 0;

    //Map to store the cart Object with cartId as key and cart as value
    Map<Integer , Cart> cartMap;

    //Map to store the productList groupBy productName and with in that groupBy product baseUnit size
    //i.e. {Milk -> { 1000 ml - [Product1, Product 2] , 500 ml - [Product 3, Product 4] }}
    Map<String, Map<Float, Set<Product>>> productList;

    //constructor to initialize cartMap and productList
    public CostOfLiving() {
        cartMap = new HashMap<>();
        productList = new HashMap<>();
    }

    /**
     * Function to load product using BufferReader
     * @param productStream
     * @return total number of products introduced till now
     */
    public int loadProductHistory( BufferedReader productStream )  {

        if(productStream == null) return -1;

        String line;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");   // to covert date in LocalDate format
        Set<Product> productsToBeAdded = new HashSet<>();

        try {
            //looping through every line present in bufferReader
            while((line = productStream.readLine()) != null) {

                if(line.isBlank()) continue;

                String[] productDetails = line.split("\t");     //split the input string with tab

                //handling the case for less or more fields than required
                if(productDetails.length != TOTAL_FIELDS_IN_PRODUCT_LINE) {
                    return -1;
                }

                //converting date into proper format
                LocalDate date = LocalDate.parse(productDetails[0], dateFormatter);
                String name = productDetails[1];
                String size = productDetails[2];
                float cost = Float.parseFloat(productDetails[3]);

                //create object of product class
                Product product =  new Product(date, name, size, cost);

                // Get the product map for the current product name
                Map<Float, Set<Product>> sizemap = productList.get(product.getName());

                //checking if map already contains the same product
                if(sizemap != null) {
                    Set<Product> products = sizemap.get(product.getSizeInBaseUnit());

                    if(products != null && products.contains(product)) {
                        continue;
                    }
                }

                //if yes continue no need to increment productCount
                if(!productsToBeAdded.add(product)) {
                    continue;
                }

                //maintaining number of product count for CostOfLiving object
                productCount++;
            }

            productsToBeAdded.forEach(product ->  {
                productList.computeIfAbsent(product.getName().toLowerCase(), k -> new HashMap<>()).computeIfAbsent(product.getSizeInBaseUnit(), k -> new HashSet<>()).add(product);
            });

            return productCount;
        } catch (Exception e) {
            // throwing -1 in case of any error or invalid input
            return -1;
        }
    }

    /**
     * Function to create and load cart with products and return created cartId
     * @param cartStream
     * @return cartId of newly created cart object
     */
    public int loadShoppingCart( BufferedReader cartStream ) {

        //handle in case of NULL
        if(cartStream == null) return -1;

        String line;
        Set<Product> cartItems = new HashSet<>();

        try{
            //looping over each line in cart stream
            while((line = cartStream.readLine()) != null) {

                if(line.isBlank()) continue;

                String[] productDetails = line.split("\t");

                //handling the case for less or more fields than required
                if(productDetails.length != TOTAL_FIELDS_IN_CART_LINE) {
                    return -1;
                }

                String name = productDetails[0];
                String size = productDetails[1];

                //creating product Object
                Product product = new Product(name, size);

                if(cartItems.contains(product)) return -1;

                cartItems.add(product);
            }

            //generating random Integer value for cartId
            int cartId = Math.abs(new Random().nextInt());
            Cart cart = new Cart(cartId, cartItems);

            //put created cart along with its product in cartMap
            cartMap.put(cartId, cart);

            return cartId;

        } catch(Exception e) {
            //return -1 as output in case of error or invalid input
            return -1;
        }
    }

    /**
     * Function will return cost of given cartId on the first date of the given month and year
     * @param cartNumber
     * @param year
     * @param month
     * @return the most efficient cost of cart on 1st date of given month and year
     */
    public float shoppingCartCost( int cartNumber, int year, int month ) {
        try {
            // get the cart from cartId
            Cart cart = (Cart) cartMap.get(cartNumber);

            //handling boundary cases
            if(cart == null || year< 0) return -1f;

            float totalCartCost = 0;
            LocalDate lastDate = LocalDate.of(year, month, 1);

            //looping over every product in cart
            for(Product item: cart.getCartItems()) {

                //get different size for current looping product
                Map<Float, Set<Product>> filteredProductWithDifferentSize = productList.get(item.getName());

                //It will store most efficient one
                float costOfItem = Float.MAX_VALUE;

                if(filteredProductWithDifferentSize == null) return -1f;

                //looping over different sizes to see which is most cost-efficient
                for(Float size: filteredProductWithDifferentSize.keySet()) {

                    //get the closest product available near given date
                    Optional<Product> closestProductForGivenDate = filteredProductWithDifferentSize.get(size).stream()
                            .filter(product -> product.getDate().isBefore(lastDate) || product.getDate().isEqual(lastDate))
                            .reduce((product1, product2) -> product1.getDate().isAfter(product2.getDate()) ? product1 : product2 );

                    //if it exists calculate how much it cost and how much quantity
                    if(closestProductForGivenDate.isPresent() && closestProductForGivenDate.get().getCost() > 0) {
                        Product product = closestProductForGivenDate.get();

                        //covert required size to base unit divide by available size in base unit
                        int requiredQuantity = (int) Math.ceil(item.getSizeInBaseUnit()/product.getSizeInBaseUnit());

                        //taking minimum from all available sizes
                        costOfItem = (float) Math.min(costOfItem, requiredQuantity*product.getCost());
                    }
                }

                if(costOfItem == Float.MAX_VALUE) return -1f;

                //If required size is available then pick that one and discard most efficient one
                totalCartCost += costOfItem;

            }

            //handling case if any of the product not available
            return totalCartCost == Float.MAX_VALUE ? -1f : totalCartCost ;
        } catch(Exception e) {
            return -1f;
        }

    }

    /**
     *
     * @param startYear
     * @param startMonth
     * @param endYear
     * @param endMonth
     * @return Map with key as productName and latest size separated by space and value as inflation or shrinkflation value
     */
    public Map<String, Float> inflation( int startYear, int startMonth, int endYear, int endMonth ) {
        try{
            Map<String, Float> productExhibitingInflationOrShrinkflation = new HashMap<>();

            LocalDate startDate = LocalDate.of(startYear, startMonth, 1);
            LocalDate endDate = LocalDate.of(endYear, endMonth, 1);

            if(startDate.isAfter(endDate)) return null;

            //List to store products available between given time period to use in shrinkflation calculation if required
            List<Product> productsBetweenGivenTime = new ArrayList<>();
            PriceVariationCalculator inflationCalculator;
            PriceVariationCalculator shrinkflationCalculator;

            for(String productName: productList.keySet()) {
                Map<Float, Set<Product>> productWithSpecificSize = productList.get(productName);

                //looping over different sizes of same product
                for(Float size: productWithSpecificSize.keySet()) {

                    //querying product between start time and end time , including start and last date
                    List<Product> productsBeforeStartDate = productWithSpecificSize.get(size).stream().filter(product -> product.isProductIntroducedInBetweenGivenDates(startDate, endDate)).toList();
                    productsBetweenGivenTime.addAll(productsBeforeStartDate);
                }

            }

            inflationCalculator = new Inflation();
            shrinkflationCalculator = new Shrinkflation(productsBetweenGivenTime);

            for(String productName: productList.keySet()) {
                //getting map of individual product
                Map<Float, Set<Product>> filteredProductWithDifferentSize = productList.get(productName);

                productExhibitingInflationOrShrinkflation.putAll(inflationCalculator.calculatePriceIncrease(filteredProductWithDifferentSize, startDate, endDate));
                productExhibitingInflationOrShrinkflation.putAll(shrinkflationCalculator.calculatePriceIncrease(filteredProductWithDifferentSize, startDate, endDate));
            }
            return productExhibitingInflationOrShrinkflation;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     *
     * @param year
     * @param month
     * @param tolerance
     * @return List of string which will be product name separated by larger size + " " + smaller size which has gone price inversion
     */
    public List<String> priceInversion( int year, int month, int tolerance ) {
        try{
            if(year < 0 || tolerance >= 100 || tolerance < 0) return null;

            //convert year and month into LocalDate
            LocalDate startDate = LocalDate.of(year, month, 1);
            List<String> productsUnderGoingPriceInversion = new ArrayList<>();

            //looping through all products in product history
            for(String productName: productList.keySet()) {

                //getting different size of products
                Map<Float, Set<Product>> productWithSpecificSize = productList.get(productName);
                List<Product> productBeforeStartDateWithSpecificSize = new ArrayList<>();

                //looping through all products and pushing filtered product in separate List for later use
                for(Float size: productWithSpecificSize.keySet()) {

                    //filtering products with non-zero cost and between given time period
                    List<Product> productsBeforeStartDate = productWithSpecificSize.get(size).stream().
                                    filter(product -> product.getCost() !=0 &&
                                    product.isProductIntroducedBeforeOrEqualGiveDate(startDate)).toList();

                    //appending into the List
                    productBeforeStartDateWithSpecificSize.addAll(productsBeforeStartDate);
                }

                //looping through all filtered products
                for(Product product1: productBeforeStartDateWithSpecificSize) {

                    //nested loop to compare one product with all other
                    for(Product product2: productBeforeStartDateWithSpecificSize) {

                        //make sure we don't compare with same product
                        if(!product1.equals(product2)) {

                            //if product1 is larger then product2
                            if(product1.getSizeInBaseUnit() > product2.getSizeInBaseUnit()) {

                                //calculating per unit cost for both the products
                                double perUnitCostOfProduct1 = product1.getCost()/product1.getSizeInBaseUnit();
                                double perUnitCostOfProduct2 = product2.getCost()/product2.getSizeInBaseUnit();

                                //if larger product has per unit cost larger than smaller onw
                                if( perUnitCostOfProduct1 > perUnitCostOfProduct2 ) {
                                    double percentageDifference = (perUnitCostOfProduct1-perUnitCostOfProduct2)/perUnitCostOfProduct1*100;

                                    //if percentage difference is greater than tolerance add it to result
                                    if(percentageDifference > tolerance) {
                                        productsUnderGoingPriceInversion.add(productName + "\t" + product1.getOriginalSize() + "\t" + product2.getOriginalSize());
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return productsUnderGoingPriceInversion;
        } catch(Exception e) {
            return null;
        }
    }
}
