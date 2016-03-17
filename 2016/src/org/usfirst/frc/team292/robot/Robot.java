
package org.usfirst.frc.team292.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.kauailabs.navx.frc.AHRS;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
    final String doNothingAuto = "Default";	//do nothing
    final String driveAndStopAuto= "Drive and Stop";
    final String lowStopAuto = "Low Bar";						//low bar
    final String bruteForceAuto = "RAMMING SPEED";
    double autoStateTimer =0;
    final double armUpPosition = 300;
    final double armBallPosition = 520;
    final double armDownPosition = 555;
    final double armP = 30.0;
    final double armI = 0.002;
    final double armD = 0.004;
    final boolean manualArmControl = false;
    final double pickupSpeed = -0.40;
    final double shootSpeed = 1.0;
    String autoSelected;
    SendableChooser chooser;
    Lights lights;
    
    RobotDrive myRobot;  // class that handles basic drive operations
    Joystick leftStick;  // set to ID 1 in DriverStation
    Joystick rightStick; // set to ID 2 in DriverStation
    Joystick operatorStick; //set to ID 3 in DriverStation
    CANTalon pickup;
	CANTalon arm;
	CANTalon liftArm;
	boolean liftArmAutoUp = false;
	CANTalon winch;
	CANTalon armExtender;
    Encoder leftEncoder;
    Encoder rightEncoder;
	AnalogInput ballSensor;
	Camera cam;
	Thread background;
	AHRS navx;
	
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
        chooser = new SendableChooser();
        chooser.addDefault("Low Bar Auto", lowStopAuto);
        chooser.addObject("Do nothing Auto", doNothingAuto);
        chooser.addObject("Drive and Stop", driveAndStopAuto);
        chooser.addObject("Ramming Speed", bruteForceAuto);
        SmartDashboard.putData("Auto choices", chooser);

		myRobot = new RobotDrive(0, 1, 2, 3);
		myRobot.setExpiration(0.1);
		leftStick = new Joystick(0);
		rightStick = new Joystick(1);
		operatorStick = new Joystick(2);
		pickup = new CANTalon(3);
		winch = new CANTalon(6);
		armExtender = new CANTalon(5);
		
		arm = new CANTalon(2);
		arm.changeControlMode(TalonControlMode.Position);
		arm.setFeedbackDevice(FeedbackDevice.AnalogPot);
		arm.setForwardSoftLimit(1000);
		arm.setReverseSoftLimit(20);
		arm.enableForwardSoftLimit(true);
		arm.enableReverseSoftLimit(true);
		arm.setPID(armP, armI, armD);
		
		liftArm = new CANTalon(4);
		liftArm.setFeedbackDevice(FeedbackDevice.AnalogPot);
		liftArm.setForwardSoftLimit(945);
		liftArm.setReverseSoftLimit(500);
		liftArm.enableForwardSoftLimit(true);
		liftArm.enableReverseSoftLimit(true);
		
		ballSensor = new AnalogInput(0);

		leftEncoder = new Encoder(6, 7);
		rightEncoder = new Encoder(9, 8);
		
		cam = new Camera("cam1", "cam0");
		cam.start();
		
		navx = new AHRS(SerialPort.Port.kMXP);
		navx.zeroYaw();
		
		background = new Thread(new Background());
		background.start();
		
		lights = new Lights(0,1);
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
    int autoState =0;
    public void autonomousInit() {
    	autoSelected = (String) chooser.getSelected();
//		autoSelected = SmartDashboard.getString("Auto Selector", doNothingAuto);
		System.out.println("Auto selected: " + autoSelected);
		leftEncoder.reset();
		rightEncoder.reset();
		autoState=0;
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
    	switch(autoSelected) {
    		
    	case driveAndStopAuto:
    			if (rightEncoder.get() < 360) {
    				arm.set(armBallPosition);
    				System.out.println("1");
    				myRobot.tankDrive(0.7, 0.7);
    			} else {
    				myRobot.tankDrive(0, 0);
    			arm.set(armUpPosition);
    			}
    	
    	case lowStopAuto:	
    		switch(autoState) {									// low bar auto
    		case 0:
    			arm.set(armBallPosition);
    			if (arm.get() > (armBallPosition-20)) {
    				autoState++;
    			}
    			break;
    		
    		case 1:

    			if (rightEncoder.get() < 2250) {
    				
    				myRobot.tankDrive(0.65, 0.65);
    			} else {
    				myRobot.tankDrive(0, 0);
    				autoState++;
    					arm.set(armUpPosition);
    				rightEncoder.reset();
    				leftEncoder.reset();
    			}
    		break;

    		case 2:
    			if (rightEncoder.get() < 2200) {
    				myRobot.tankDrive(0.60,0.41);
    			}
    			else {
    				myRobot.tankDrive(0, 0);
    				autoState++;
    				rightEncoder.reset();
    				leftEncoder.reset();
    				autoStateTimer = Timer.getFPGATimestamp();
    			}
			break;

    		case 3:
    			if (Timer.getFPGATimestamp() - autoStateTimer >= 1) {
    				autoState++;
    			} else {
    				//pickup.set(shootSpeed);
    				pickup.set(0);
    			}
    		break;
    		
    		case 4:
    			pickup.set(0);
    			break;
    		default:
    			myRobot.tankDrive(0, 0);
    		break;
    			
    		}
		break;

    	case bruteForceAuto:								//RAMS EVERYTHING
    		if (rightEncoder.get() < 2500){
				 arm.set(armUpPosition);
				myRobot.tankDrive(0.8, 0.8);
			} else {
				myRobot.tankDrive(0, 0);
				 arm.set(armUpPosition);
			}
    		break;

    	case doNothingAuto:									// do nothing auto
    	default:
    		myRobot.tankDrive(0, 0);
            break;
    	}
    }
    
    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	//pick up boulder
		if (rightStick.getRawButton(7)) {
			myRobot.tankDrive(0, 0);
		} else {
			myRobot.tankDrive(-leftStick.getY(), -rightStick.getY());
		}
    	
    	if(operatorStick.getRawButton(2)) {
        	pickup.set(pickupSpeed);
    	} else if (operatorStick.getTrigger()) {
        	pickup.set(shootSpeed);
    	} else {
        	pickup.set(0);
    	}

		if(operatorStick.getRawButton(5)) {
			arm.set(armUpPosition);
			arm.clearIAccum();
		}
		if(operatorStick.getRawButton(3)) {
			arm.set(armBallPosition);
			arm.clearIAccum();
		}
		if(operatorStick.getRawButton(4)) {
			arm.set(armDownPosition);
			arm.clearIAccum();
		}

		if(operatorStick.getRawButton(7)) {
			if (operatorStick.getY() <0){
				winch.set(operatorStick.getY());
			} else {
				winch.set(operatorStick.getY() *0.2);
			}
		}else {
			winch.set(0);	
		}
		
		if(operatorStick.getRawButton(8)) {
			arm.set(armBallPosition);
			liftArmAutoUp = true;
		}
		
		//if(operatorStick.getRawButton(12)) {
		//	double i = operatorStick.getY();
		//	if (i<0){
		//		liftArm.set(i);
			//} else {
				//liftArm.set(i*0.3);
			//}
			//liftArmAutoUp = false;
		//} else {
		//	liftArm.set(0);
		//}
		
		if(liftArmAutoUp) {
			//if (liftArm.getPosition() > 600) {
			//	liftArm.set(-1);
		//	} else {
			//	liftArm.set(0);
		//	}
		}

		if(operatorStick.getRawButton(11)) {
		//	armExtender.set(-operatorStick.getY());
		} else {
			armExtender.set(0);
		}
		
		if (rightStick.getRawButton(7)){
			if (rightStick.getTrigger()) {
				if (rightStick.getY() <0){
					winch.set(rightStick.getY());
				} else {
					winch.set(rightStick.getY() *0.2);
				}
			}else {
				winch.set(0);	
			}
			
		//	if (leftStick.getTrigger()) {
		//		armExtender.set(-leftStick.getY());
		//	} else {
		//		armExtender.set(0);
		//	}
		}
		
		lights.periodic();
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
		    	SmartDashboard.putNumber("Left Encoder", leftEncoder.get());
		    	SmartDashboard.putNumber("Right Encoder", rightEncoder.get());
		    	SmartDashboard.putNumber("Lift Arm Position", liftArm.getPosition());
		    	SmartDashboard.putNumber("Operator POV", operatorStick.getPOV());
		    	SmartDashboard.putNumber("Angle", navx.getYaw());
				
				if (leftStick.getRawButton(2)) {
					cam.viewFrontCamera();
				} 
				if (rightStick.getRawButton(2)) {
					cam.viewRearCamera();
				}
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