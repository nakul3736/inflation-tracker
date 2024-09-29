import java.time.LocalDate;
import java.util.Objects;

public class Product {
    private LocalDate date;
    private String name;
    private ProductSize size;
    private float cost;

    //store value of size in base unit
    private float sizeInBaseUnit;

    /**
     * Parameterized Constructor for creating Product object
     * @param theDate
     * @param theName
     * @param theSize
     * @param theCost
     */
    public Product(LocalDate theDate, String theName, String theSize, float theCost) {

        //handling corner cases for invalid input
        if(theName.isBlank() || theName.isEmpty() || theCost<0 || theSize.isEmpty() || theSize.isBlank()) {
            throw new IllegalArgumentException("Invalid product input");
        }

        date = theDate;
        name = theName.toLowerCase();           //converting every letter to lower case
        size = new ProductSize(theSize);        //size will be stored as ProductSize object
        cost = theCost;
        sizeInBaseUnit = size.toBaseUnit();
    }

    /**
     * Function overloading of constructor to create product by passing only name and size
     * @param theName
     * @param theSize
     */
    public Product(String theName, String theSize) {

        if(theName.isBlank() || theName.isEmpty() || theSize.isEmpty() || theSize.isBlank()) {
            throw new IllegalArgumentException("Invalid product input");
        }

        name = theName;                 //converting every letter to lower case
        size = new ProductSize(theSize);
        sizeInBaseUnit = size.toBaseUnit();
    }

    //getter for Date
    public LocalDate getDate() {
        return date;
    }

    // getter for Name
    public String getName() {
        return name;
    }

    // getter for size
    public ProductSize getSize() {
        return size;
    }

    // getter for cost
    public float getCost() {
        return cost;
    }

    // function which will concatenate value and unit to return size as String
    public String getOriginalSize() {
        return size.getValue()+" "+size.getUnit();
    }

    //getter for size in base unit
    public float getSizeInBaseUnit() {
        return sizeInBaseUnit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, sizeInBaseUnit, cost, date);
    }

    //override equals method of product to identify unique product in Set
    //reference from Head first java book and chatGPT
    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;

        //if passed obj is null or not the object of same class
        if(obj == null || getClass() != obj.getClass()) return false;

        Product product = (Product) obj;

        return  Float.compare(cost, product.cost) == 0 &&
                Objects.equals(name, product.name) &&
                Objects.equals(sizeInBaseUnit, product.sizeInBaseUnit) &&
                Objects.equals(date, product.date);

    }

    public boolean isProductIntroducedInBetweenGivenDates(LocalDate startDate, LocalDate endDate) {
        return (this.date.isAfter(startDate) || this.date.isEqual(startDate))
                && (this.date.isBefore(endDate) || this.date.isEqual(endDate));
    }

    public boolean isProductIntroducedBeforeOrEqualGiveDate(LocalDate date) {
        return this.date.isBefore(date) || this.date.isEqual(date);
    }
}
