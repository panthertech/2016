
package org.usfirst.frc.team292.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
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
    final double armUpPosition = 300;
    final double armBallPosition = 520;
    final double armDownPosition = 590;
    final double armP = 30.0;
    final double armI = 0.002;
    final double armD = 0.004;
    final double liftArmP = 30.0;
    final double liftArmI = 0.002;
    final double liftArmD = 0.004;
    final boolean manualArmControl = false;
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
	CANTalon liftArm;
	CANTalon winch;
    Encoder leftEncoder;
    Encoder rightEncoder;
	AnalogInput ballSensor;
	Camera cam;
	Thread background;
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        chooser = new SendableChooser();
        chooser.addDefault("Default Auto", defaultAuto);
        chooser.addObject("My Auto", customAuto);
        SmartDashboard.putData("Auto choices", chooser);

		myRobot = new RobotDrive(0, 1, 2, 3);
		myRobot.setExpiration(0.1);
		leftStick = new Joystick(0);
		rightStick = new Joystick(1);
		operatorStick = new Joystick(2);
		pickup = new CANTalon(3);
		winch = new CANTalon(6);
		
		arm = new CANTalon(2);
		arm.changeControlMode(TalonControlMode.Position);
		arm.setFeedbackDevice(FeedbackDevice.AnalogPot);
		arm.setForwardSoftLimit(1000);
		arm.setReverseSoftLimit(20);
		arm.enableForwardSoftLimit(true);
		arm.enableReverseSoftLimit(true);
		arm.setPID(armP, armI, armD);
		
		liftArm = new CANTalon(4);
		liftArm.changeControlMode(TalonControlMode.Position);
		liftArm.setFeedbackDevice(FeedbackDevice.AnalogPot);
		liftArm.setPID(liftArmP, liftArmI, liftArmD);
		SmartDashboard.putNumber("Lift Arm Setpoint", 500);
		SmartDashboard.putNumber("Lift Arm P", liftArmP);
		SmartDashboard.putNumber("Lift Arm I", liftArmI);
		SmartDashboard.putNumber("Lift Arm D", liftArmD);
		
		ballSensor = new AnalogInput(0);

		leftEncoder = new Encoder(6, 7);
		rightEncoder = new Encoder(9, 8);
		
		cam = new Camera();
		
		background = new Thread(new Background());
		background.start();
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
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	//pick up boulder
    	myRobot.tankDrive(-leftStick.getY(), -rightStick.getY());
    	
    	if(operatorStick.getRawButton(2)) {
        	pickup.set(pickupSpeed);
    	} else if (operatorStick.getTrigger()) {
        	pickup.set(shootSpeed);
    	} else {
        	pickup.set(0);
    	}

		if(operatorStick.getRawButton(3)) {
			arm.set(armUpPosition);
			arm.clearIAccum();
		}
		if(operatorStick.getRawButton(4)) {
			arm.set(armBallPosition);
			arm.clearIAccum();
		}
		if(operatorStick.getRawButton(5)) {
			arm.set(armDownPosition);
			arm.clearIAccum();
		}
		
		liftArm.setPID(SmartDashboard.getNumber("Lift Arm P"), SmartDashboard.getNumber("Lift Arm I"), SmartDashboard.getNumber("Lift Arm D"));
		liftArm.set(SmartDashboard.getNumber("Lift Arm Setpoint"));
		
		winch.set(operatorStick.getY());
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    	
    }
    
    /**
     * This function is called periodically during disabled mode
     */
    public void disabledPeriodic() {
    	
    }
    
    private class Background implements Runnable{
		public void run() {
			while(true) {
		    	SmartDashboard.putNumber("Arm Position", arm.get());
		    	if(ballSensor.getVoltage() < 0.5) {
		    		SmartDashboard.putBoolean("Ball Detected", true);
		    	} else {
		    		SmartDashboard.putBoolean("Ball Detected", false);
		    	}
		    	SmartDashboard.putNumber("Arm Setpoint ", arm.getSetpoint());
		    	SmartDashboard.putBoolean("Arm Forward Limit", arm.isFwdLimitSwitchClosed());
		    	SmartDashboard.putBoolean("Arm Reverse Limit", arm.isRevLimitSwitchClosed());
		    	SmartDashboard.putNumber("Arm Current", arm.getOutputCurrent());
		    	SmartDashboard.putNumber("Arm Voltage", arm.getOutputVoltage());
		    	SmartDashboard.putNumber("Left Encoder", leftEncoder.get());
		    	SmartDashboard.putNumber("Right Encoder", rightEncoder.get());
		    	SmartDashboard.putNumber("Lift Arm Position", liftArm.get());
		    	SmartDashboard.putNumber("Lift Arm Current", liftArm.getOutputCurrent());
		    	SmartDashboard.putNumber("Lift Arm Voltage", liftArm.getOutputVoltage());
		    	cam.periodic();
			}
		}
    }
}
// yellow right front
//orange right rear
//green left rear
//purple left front
//blue whatever one larry made
//white another one larry made