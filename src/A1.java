import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

public class A1 {

    public static void main(String[] args) {
       try{

           String productHistory = "2024/09/13\tmilk\t500 ml\t5\n" +
                   "2023/11/30\tmiLk\t250 ml\t3\n" +
                   "2023/10/11\tcookies\t1000 g\t2\n" +
                   "2023/11/11\tcookies\t1 kg\t0\n" +               //product discontinued
                   "2023/11/15\tcookies\t500 g\t7\n" +              // smaller size introduced in the same month
                   "2023/09/15\tcookies\t250 g\t2\n" +
                   "2024/09/15\tmilk\t0.5 l\t7\n" +
                   "2024/09/20\tmilk\t0.250 l\t5\n" +
                   "2021/05/02\trice\t1000 g\t3\n" +
                   "2023/01/07\tMilK\t750 ml\t2\n" +
                   "2023/12/13\tMILK\t0.750 l\t3\n" +           //inflation for 750ml
                   "2023/10/01\toreo\t500 g\t3\n" +
                   "2023/12/10\toreo\t500 g\t5\n";

           //BufferReader for loadingProductHistory and cart
           BufferedReader reader = new BufferedReader(new StringReader(productHistory));
           BufferedReader shoppingCart1 = new BufferedReader(new StringReader("milk\t2000 ml\n" + "cookies\t250 g\n"));
           BufferedReader shoppingCart2 = new BufferedReader(new StringReader("oreo\t1 kg\n"));

           CostOfLiving demo = new CostOfLiving();
           int loadProducts = demo.loadProductHistory(reader);

           System.out.println("Number of products in product history " + loadProducts);     // printing number of products in product history right now

           int cart1Id = demo.loadShoppingCart(shoppingCart1);        //getting cart ID as output
           int cart2Id = demo.loadShoppingCart(shoppingCart2);

            //printing loaded carts with their cost

           Cart cart1 = (Cart) demo.cartMap.get(cart1Id);
           System.out.println("\nCart ID: " + cart1Id);

           cart1.getCartItems().forEach(item -> System.out.println(item.getName() + " " + item.getOriginalSize()));
           float cart1Cost = demo.shoppingCartCost(cart1Id, 2023, 12);
           System.out.println("Cost of cart with id " + cart1Id +" on 01/12/2023 is $" + cart1Cost + "\n\n");

           Cart cart2 = (Cart) demo.cartMap.get(cart1Id);
           System.out.println("\nCart ID: " + cart1Id);

           cart2.getCartItems().forEach(item -> System.out.println(item.getName() + " " + item.getOriginalSize()));
           float cart2Cost = demo.shoppingCartCost(cart1Id, 2025, 1);
           System.out.println("Cost of cart with id " + cart2Id + " on 01/01/2023 is $" + cart2Cost + "\n\n");


           //getting products underGoing inflation or shrinkflation
           Map<String, Float> productsUnderGoingInflationOrShrinkflation = demo.inflation(2021, 1, 2024, 12);

           System.out.println("Products under going inflation or shrinkflation are\n");
           for(String productNameAndSize: productsUnderGoingInflationOrShrinkflation.keySet()) {
               System.out.println(productNameAndSize + " : " + productsUnderGoingInflationOrShrinkflation.get(productNameAndSize));
           }

           //getting list of products going through price inversion
           List<String> productsUnderGoingPriceInversion = demo.priceInversion(2024, 11, 1);
           System.out.println("\n\nProducts under going price inversion are\n");
           productsUnderGoingPriceInversion.forEach(System.out::println);

       }catch (Exception error) {
           return;
       }
    }
}