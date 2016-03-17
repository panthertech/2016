package org.usfirst.frc.team292.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;

public class Lights {
	Solenoid Blue, Red ;
	int lightState;
	double lightTime;
	DriverStation ds;
	
	public Lights(int BluePort, int RedPort) {
    	Blue = new Solenoid(BluePort);
    	Red = new Solenoid(RedPort);
    	lightState = 0;
    	lightTime = Timer.getFPGATimestamp();
    	ds = DriverStation.getInstance();
	}
	
	
	private boolean isRedAlliance() {
		return (ds.getAlliance() == DriverStation.Alliance.Red);
	}
	
	private boolean isBlueAlliance() {
		return (ds.getAlliance() == DriverStation.Alliance.Blue);
	}
	
	public void periodic() {
		if(ds.isAutonomous()) {
			mode0();
		}  else {
			mode2();
		}
	}
	
	private void off() {
		Blue.set(false);
		Red.set(false);
	}
	
	private void mode0() {
		switch(lightState) {
		case 0:
			Blue.set(true);
			Red.set(false);
			if(Timer.getFPGATimestamp() > lightTime + 0.5) {
				lightTime = Timer.getFPGATimestamp();
				lightState++;
			}
			break;
		case 1:
			Blue.set(false);
			Red.set(false);
			if(Timer.getFPGATimestamp() > lightTime + 0.5) {
				lightTime = Timer.getFPGATimestamp();
				lightState++;
			}
			break;
		case 2:
			Blue.set(false);
			Red.set(true);
			if(Timer.getFPGATimestamp() > lightTime + 0.5) {
				lightTime = Timer.getFPGATimestamp();
				lightState++;
			}
			break;
		case 3:
			Blue.set(false);
			Red.set(false);
			if(Timer.getFPGATimestamp() > lightTime + 0.5) {
				lightTime = Timer.getFPGATimestamp();
				lightState++;
			}
			break;
		default:
			lightState = 0;
			break;
		}
	}
	
	private void mode1() {
		switch(lightState) {
		case 0:
			Blue.set(isBlueAlliance());
			Red.set(isRedAlliance());
			if(Timer.getFPGATimestamp() > lightTime + 0.75) {
				lightTime = Timer.getFPGATimestamp();
				lightState++;
			}
			break;
		case 1:
			Blue.set(true);
			Red.set(true);
			if(Timer.getFPGATimestamp() > lightTime + 0.25) {
				lightTime = Timer.getFPGATimestamp();
				lightState++;
			}
			break;
		case 2:
			Blue.set(false);
			Red.set(false);
			if(Timer.getFPGATimestamp() > lightTime + 0.25) {
				lightTime = Timer.getFPGATimestamp();
				lightState= 0;
			}
			break;
		default:
			lightState = 0;
			break;
		}
	}
	
	private void mode2() {
		switch(lightState) {
		case 0:
			Blue.set(isBlueAlliance());
			Red.set(isRedAlliance());
			if(Timer.getFPGATimestamp() > lightTime + 0.292) {
				lightTime = Timer.getFPGATimestamp();
				lightState++;
			}
			break;
		case 1:
			Blue.set(false);
			Red.set(false);
			if(Timer.getFPGATimestamp() > lightTime + 0.292) {
				lightTime = Timer.getFPGATimestamp();
				lightState++;
			}
			break;
		default:
			lightState = 0;
			break;
		}
	}
}
