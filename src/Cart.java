import java.util.Set;

public class Cart {
    private int cartId;
    private Set<Product> cartItems;       //List to store products for specific cartId

    /**
     * Constructor to create Cart object
     * @param id
     * @param items
     */
    public Cart(int id, Set<Product> items) {
        cartId = id;
        cartItems = items;
    }

    //getter for cartId
    public int getCartId() {
        return cartId;
    }

    //getter for products present in cart
    public Set<Product> getCartItems() {
        return cartItems;
    }
}
