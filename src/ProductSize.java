import java.util.HashMap;
import java.util.Map;

public class ProductSize {

    //static map to store unit conversions
    private static final Map<String, Float> unitMap = new HashMap<>();
    private static final int TOTAL_FIELD_IN_PRODUCT_SIZE = 2;

    //static block which will be executed before the main method
    static {
        //weight units
        unitMap.put("g", 1.0f);
        unitMap.put("kg", 1000.0f);

        //liquid units
        unitMap.put("ml", 1.0f);
        unitMap.put("l", 1000.0f);
    }

    private float value;
    private String unit;


    /**
     * parameterized constructor to initialize ProductSize object
    * @param size which will get converted to in value and unit form
    * */
    public ProductSize(String size) {
        String[] part = size.split(" ");

        //handling invalid input i.e. number of spaces between size
        if(part.length != TOTAL_FIELD_IN_PRODUCT_SIZE) {
            throw new IllegalArgumentException("Invalid size format");
        }

        value = Float.parseFloat(part[0]);
        unit = part[1].toLowerCase();       //converting units to lowercase

        //if passed unit is not present in the map then throw error
        if(!unitMap.containsKey(unit)) {
            throw new IllegalArgumentException(("Invalid unit"));
        }else if(value <= 0) {
            throw new IllegalArgumentException("Invalid quantity value");
        }
    }

    //function to convert to base unit
    public float toBaseUnit() {
        return this.value * unitMap.get(this.unit);
    }

    /**
     * Method to introduce a new unit and its conversion factor into the unitMap
     * @param unit The unit as a string (e.g., "oz", "lb")
     * @param conversionFactor The conversion factor to the base unit (e.g., 28.35 for "oz" if "g" is the base unit)
     */
    public static void addNewUnit(String unit, float conversionFactor) {
        if(unit == null || unit.isBlank() || conversionFactor <= 0) {
            throw new IllegalArgumentException("Invalid unit or conversion factor.");
        }

        // Convert the unit to lowercase to ensure case consistency
        unit = unit.toLowerCase();

        // Check if the unit already exists in the map
        if(unitMap.containsKey(unit)) {
            throw new IllegalArgumentException("Unit already exists in the unit map.");
        }

        // Add the new unit and its conversion factor to the map
        unitMap.put(unit, conversionFactor);
    }

    //getter for value
    public float getValue() {
        return value;
    }

    //getter for unit
    public String getUnit() {
        return unit;
    }
}
