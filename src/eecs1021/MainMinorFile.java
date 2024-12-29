package eecs1021;
import org.firmata4j.I2CDevice;
import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.ssd1306.SSD1306;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Main class for initializing and running the water pumping system.
 * It configures the hardware connections for the soil moisture sensor, water pump, LED, and OLED display.
 * Periodically checks soil moisture and controls watering and LED indicators.
 */
public class MainMinorFile {
    // Constants for hardware configuration
    static final int A1 = 15; // Analog pin A1 for the soil moisture sensor
    static final byte I2C0 = 0x3C;
    static final int D2 = 2; // Digital pin D2 for the water pump control (MOSFET)
    static final int D4 = 4; // Digital pin D4 for the LED indicator

    public static void main(String[] args) throws InterruptedException, IOException {

        // Initialize the Arduino device using the Firmata protocol
        var arduinoDevice = new FirmataDevice("COM3");
        arduinoDevice.start(); // Start the communication with the device
        arduinoDevice.ensureInitializationIsDone(); // Wait until initialization is complete

        // Configure pins for the moisture sensor, water pump, and LED
        var moistureSensor = arduinoDevice.getPin(A1); // Soil moisture sensor pin
        var waterPumpHardware = arduinoDevice.getPin(D2);  // Water pump control pin
        var LED = arduinoDevice.getPin(D4); // LED indicator pin

        // Initialize the OLED display connected via I2C
        I2CDevice i2cObject = arduinoDevice.getI2CDevice((byte) 0x3C); // Use 0x3C for the Grove OLED
        SSD1306 theOledObject = new SSD1306(i2cObject, SSD1306.Size.SSD1306_128_64); // 128x64 OLED SSD1515

        // Initialize the OLED display
        theOledObject.init();

        // Set the water pump and LED pins to OUTPUT mode
        waterPumpHardware.setMode(Pin.Mode.OUTPUT);
        LED.setMode(Pin.Mode.OUTPUT);

        // Create a new task to handle soil moisture monitoring and water control
        TimerTask task = new WaterPumpingSystem(theOledObject,moistureSensor,waterPumpHardware,LED);

        // Schedule the task to run every second (1000 ms)
        Timer timer = new Timer();
        timer.schedule(task,0,1000);
    }
}
