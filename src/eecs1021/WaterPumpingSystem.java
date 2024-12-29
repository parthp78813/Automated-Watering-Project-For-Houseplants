package eecs1021;
import org.firmata4j.Pin;
import org.firmata4j.ssd1306.SSD1306;
import java.io.IOException;
import java.util.TimerTask;

/**
 * Represents a water pumping system that monitors soil moisture and automatically waters plants as needed.
 * This class extends TimerTask to allow periodic execution.
 */
public class WaterPumpingSystem extends TimerTask {
    // Instance variables
    private final SSD1306 theOledObject; // The OLED display object for displaying soil moisture readings and messages.
    private final Pin WaterPumpPin;      // The pin controlling the water pump hardware.
    private final Pin SoilSensorPin;     // The pin connected to the soil moisture sensor.
    private final Pin LEDPin;            // The pin controlling an LED indicator.


    /**
     * Constructor to initialize the water pumping system.
     *
     * @param theDisplayObject The OLED display object.
     * @param SoilSensor       The pin connected to the soil moisture sensor.
     * @param waterPumpHardware The pin controlling the water pump.
     * @param LED              The pin controlling the LED indicator.
     */
    public WaterPumpingSystem(SSD1306 theDisplayObject, Pin SoilSensor, Pin waterPumpHardware, Pin LED ){
        theOledObject = theDisplayObject;
        WaterPumpPin = waterPumpHardware;
        SoilSensorPin = SoilSensor;
        LEDPin = LED;
    }

    /**
     * The task's main logic, executed periodically.
     * Reads the soil moisture value, updates the OLED display with status messages,
     * and controls the water pump and LED based on soil moisture conditions.
     */
    @Override
    public void run() {

        // Clear the OLED display canvas for new content
        theOledObject.getCanvas().clear();

        // Set the text size on the OLED display
        theOledObject.getCanvas().setTextsize(4); // clear contents first.

        // Read the current soil moisture value from the sensor
        long SoilMoistureValue = SoilSensorPin.getValue();

        // Display the soil moisture value on the OLED
        theOledObject.getCanvas().drawString(0,0, "Soil Moisture Value = " + String.valueOf(SoilMoistureValue));

        try {
            // Check the soil moisture conditions and act accordingly
            if (SoilMoistureValue > 750) {
                // Dry soil condition - water the plant
                theOledObject.getCanvas().drawString(0, 20, "\nPlant has Dry soil. \nWater it!");
                WaterPumpPin.setValue(1); // Turn on the water pump
                LEDPin.setValue(1); // Turn on the LED indicator

            } else if (SoilMoistureValue > 730 && SoilMoistureValue < 800) {
                // Soil is still too dry - continue watering
                theOledObject.getCanvas().drawString(0, 20, "\nStill too dry, \nPlease water!");
                WaterPumpPin.setValue(1); // Turn on the water pump
                LEDPin.setValue(1); // Turn on the LED indicator

            } else {
                // Soil is wet enough - stop watering
                theOledObject.getCanvas().drawString(0, 20, "\nWet Soil, \nStop Watering!");
                WaterPumpPin.setValue(0); // Turn off the water pump
                LEDPin.setValue(0); // Turn OFF the LED indicator
            }
            // Update the OLED display with the latest messages
            theOledObject.display();
        } catch (IOException e) {

            // Handle any exceptions that occur while updating the OLED display
            e.printStackTrace();
        }
    }
}



