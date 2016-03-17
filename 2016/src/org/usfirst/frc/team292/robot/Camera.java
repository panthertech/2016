package org.usfirst.frc.team292.robot;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.*;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.vision.USBCamera;

public class Camera extends java.lang.Thread {
    CameraServer server;
    USBCamera frontCam, rearCam;
    boolean frontCameraSelected = true;
    boolean frontCameraStarted = false;
    boolean rearCameraStarted = false;
    
	public Camera(String frontCameraName, String rearCameraName) {
		server = CameraServer.getInstance();
		server.setQuality(50);
		
		frontCam = new USBCamera(frontCameraName);
		if(frontCameraName.equals(rearCameraName)) {
			rearCam = frontCam;
		} else {
			rearCam = new USBCamera(rearCameraName);
		}
	}
	
 	public void run() {
 	    Image image = NIVision.imaqCreateImage(ImageType.IMAGE_RGB, 0);
		while(true)
		{
			if(frontCameraSelected) {
				if(rearCameraStarted) {
					rearCam.stopCapture();
					rearCameraStarted = false;
				}
				if(!frontCameraStarted) {
					frontCam.startCapture();
					frontCameraStarted = true;
				}
				frontCam.getImage(image);
				NIVision.imaqFlip(image, image, NIVision.FlipAxis.CENTER_AXIS);
			} else {
				if(frontCameraStarted) {
					frontCam.stopCapture();
					frontCameraStarted = false;
				}
				if(!rearCameraStarted) {
					rearCam.startCapture();
					rearCameraStarted = true;
				}
				rearCam.getImage(image);
			}
	        
			server.setImage(image);
		}
	}
 	
 	public void viewFrontCamera() {
 		frontCameraSelected = true;
 	}
 	
 	public void viewRearCamera() {
 		frontCameraSelected = false;
 	}
}
