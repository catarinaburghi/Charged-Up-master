package frc.robot.subsystems;

// Used for the solenoids
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Timer;
// Allows for the use of an ultrasonic sensor
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
// Allows the use of state machines
import frc.robot.liblite.StateMachine;
import frc.robot.liblite.StateMetadata;

public class Drawer extends SubsystemBase {

    // Tells us if an instance of this already exists
    private static Drawer mInstance = null;

    // Initializes Doublesolenoid
    private DoubleSolenoid extender;

    // Initializes the ultrasonic and creates it
    private static Ultrasonic m_rangeFinder = new Ultrasonic(Constants.DrawerConstants.usPingID,
            Constants.DrawerConstants.usEchoID);

    // Stores the distance that the ultrasoic is detecting
    private double distanceMillimeters = 0.0;

    // Changes based on the output from the ultrasonic
    private Boolean objectIn = false;

    // Stores the current state of the drawer
    public boolean drawerExtended;

    // Creates the timer for the ultrasonic
    private Timer timer = new Timer();

    // Makes it a singleton
    public static Drawer getInstance() {
        if (mInstance == null) {
            mInstance = new Drawer();
        }
        return mInstance;
    }

    // Creates the states for the Drawer
    public enum DrawerStates {

        // State where the drawer is retracted
        DRAWERIN,

        // State where the drawer is extended
        DRAWEROUT;

    }

    // Creates the variable that stores the state machine
    private StateMachine<DrawerStates> stateMachine;

    // Constructor
    private Drawer() {

        // Creates state machine
        stateMachine = new StateMachine<>("Drawer Statemachine");

        // Assigns the states to their methods
        stateMachine.setDefaultState(DrawerStates.DRAWERIN, this::handleDrawerIn);
        stateMachine.addState(DrawerStates.DRAWEROUT, this::handleDrawerOut);

        // Initialize Double Solenoid
        extender = new DoubleSolenoid(Constants.PneumaticConstants.pneumaticHub, PneumaticsModuleType.REVPH,
                Constants.DrawerConstants.drawerForwardID,
                Constants.DrawerConstants.drawerReverseID);

        // Sets the ultrasonic to digital mode
        m_rangeFinder.setAutomaticMode(true);
    }

    // Method that states what the retracted state does
    public void handleDrawerIn(StateMetadata<DrawerStates> metaData) {

        if (metaData.isFirstRun()) {

            // Sets the solenoid to retracted position when in this state
            extender.set(Value.kReverse);

            // Sets the variable that stores if the drawer is extended or not to false
            drawerExtended = false;
        }

    }

    // Method that states what the extended state does
    public void handleDrawerOut(StateMetadata<DrawerStates> metaData) {

        if (metaData.isFirstRun()) {

            // Sets the solenoid to extended position when in this state
            extender.set(Value.kForward);

            // Sets the variable that stores if the drawer is extended or not to true
            drawerExtended = true;
        }

        // Stores the distance (in MM) being detected by the ultrasonic inside the
        // "distanceMillimeters" variable
        distanceMillimeters = m_rangeFinder.getRangeMM();

        // Checks if the distance being detected by the ultrasonic is smaller than the
        // distance to the wall
        // or if it is greater than 5000 (happens when an object is too close)
        // It also has a timer that checks for how long an object has been in,
        // after an object has been in for a certain period of time retracts drawer,
        // f the object leaves the timer restarts

    }

    // Sets the drawer to extended state
    public void extendDrawer() {

        stateMachine.setState(DrawerStates.DRAWEROUT);

    }

    // Sets the drawer to retracted state
    public void retractDrawer() {

        stateMachine.setState(DrawerStates.DRAWERIN);
    }

    // Makes the state machine run periodically
    @Override
    public void periodic() {
        stateMachine.update();

        // if ((distanceMillimeters < Constants.DrawerConstants.distanceWall
        //         || (distanceMillimeters >= Constants.DrawerConstants.cubeNonsenseValue))) {

        //     timer.start();

        // }

        // if (timer.get() >= Constants.DrawerConstants.drawerTimer) {
        //     if (distanceMillimeters < Constants.DrawerConstants.distanceWall
        //             || distanceMillimeters >= Constants.DrawerConstants.cubeNonsenseValue) {
        //         timer.stop();
        //         timer.reset();
        //         retractDrawer();

        //     } else {

        //         timer.reset();
        //     }
        // }

        //System.out.println("Distance millimeters: " + distanceMillimeters);

    }

}