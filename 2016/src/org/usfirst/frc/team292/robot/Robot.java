
package org.usfirst.frc.team292.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
    final String defaultAuto = "Default";
    final String customAuto = "My Auto";
    final double armUpPosition = 0;
    final double armDownPosition = 1023;
    final double pickupSpeed = -0.40;
    final double shootSpeed = 1.0;
    String autoSelected;
    SendableChooser chooser;
    
    RobotDrive myRobot;  // class that handles basic drive operations
    Joystick leftStick;  // set to ID 1 in DriverStation
    Joystick rightStick; // set to ID 2 in DriverStation
    Joystick operatorStick; //set to ID 3 in DriverStation
    CANTalon pickup;
	CANTalon arm;
	DigitalInput ballSensor;
	Camera cam;
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        chooser = new SendableChooser();
        chooser.addDefault("Default Auto", defaultAuto);
        chooser.addObject("My Auto", customAuto);
        SmartDashboard.putData("Auto choices", chooser);
        SmartDashboard.putNumber("Arm Setpoint", 300);
        SmartDashboard.putNumber("Arm P", 10);
        SmartDashboard.putNumber("Arm I", .002);
        SmartDashboard.putNumber("Arm D", .004);

		myRobot = new RobotDrive(0, 1, 2, 3);
		myRobot.setExpiration(0.1);
		leftStick = new Joystick(0);
		rightStick = new Joystick(1);
		operatorStick = new Joystick(2);
		pickup = new CANTalon(3);
		
		arm = new CANTalon(2);
		arm.changeControlMode(TalonControlMode.Position);
		arm.setFeedbackDevice(FeedbackDevice.AnalogPot);
		arm.setForwardSoftLimit(1000);
		arm.setReverseSoftLimit(20);
		arm.enableForwardSoftLimit(true);
		arm.enableReverseSoftLimit(true);
		arm.configMaxOutputVoltage(8);
		
		ballSensor = new DigitalInput(0);
		cam = new Camera();
    }
    
	/**
	 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
	 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
	 * Dashboard, remove all of the chooser code and uncomment the getString line to get the auto name from the text box
	 * below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the switch structure below with additional strings.
	 * If using the SendableChooser make sure to add them to the chooser code above as well.
	 */
    public void autonomousInit() {
    	autoSelected = (String) chooser.getSelected();
//		autoSelected = SmartDashboard.getString("Auto Selector", defaultAuto);
		System.out.println("Auto selected: " + autoSelected);
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
    	switch(autoSelected) {
    	case customAuto:
        //Put custom auto code here   
            break;
    	case defaultAuto:
    	default:
    	//Put default auto code here
            break;
    	}

    	periodic();
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	//pick up boulder
    	myRobot.tankDrive(-leftStick.getY(), -rightStick.getY());
    	
    	if(operatorStick.getTrigger()) {
        	pickup.set(shootSpeed);
    	} else if (operatorStick.getRawButton(2)) {
        	pickup.set(pickupSpeed);
    	} else {
        	pickup.set(0);
    	}

		arm.setPID(SmartDashboard.getNumber("Arm P", 0), SmartDashboard.getNumber("Arm I", 0), SmartDashboard.getNumber("Arm D", 0));
    	arm.set(SmartDashboard.getNumber("Arm Setpoint", 1000));

    	periodic();
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    	periodic();
    }
    
    /**
     * This function is called periodically during disabled mode
     */
    public void disabledPeriodic() {
    	periodic();
    }
    
    /**
     * Generic periodic function called in all modes
     */
    private void periodic() {
    	SmartDashboard.putNumber("Arm Position", arm.get());
    	SmartDashboard.putNumber("Arm Setpoint Feedback", SmartDashboard.getNumber("Arm Setpoint", 1000));
    	SmartDashboard.putBoolean("Ball Detected", ballSensor.get());
    	SmartDashboard.putBoolean("Arm Forward Limit", arm.isFwdLimitSwitchClosed());
    	SmartDashboard.putBoolean("Arm Reverse Limit", arm.isRevLimitSwitchClosed());
    	SmartDashboard.putNumber("Pickup Current", pickup.getOutputCurrent());
    	cam.periodic();
    }
}
// yellow right front
//orange right rear
//green left rear
//purple left front
//blue whatever one larry made
//white another one larry made