package com.example.localizador;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


class TelaCamera extends SurfaceView implements SurfaceHolder.Callback {

	   private SurfaceHolder 	holdMe;
	   private Camera 			theCamera;
	   private Display 			display;
	   
	   private  boolean isPreviewRunning;

	   public TelaCamera(Context context,Camera camera, Display disp) {
	      super(context);
	      display = disp;
	      theCamera = camera;
	      holdMe = getHolder();
	      holdMe.addCallback(this);
	   }

	   @Override
	   public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		   if (isPreviewRunning)
	        {
	            theCamera.stopPreview();
	        }

	        Parameters parameters = theCamera.getParameters();	        
               
	        if(display.getRotation() == Surface.ROTATION_0)
	        {
	            //parameters.setPreviewSize(height, width);                           
	            theCamera.setDisplayOrientation(90);
	        }
	        if(display.getRotation() == Surface.ROTATION_90)
	        {
	            //parameters.setPreviewSize(width, height); 
	            theCamera.setDisplayOrientation(90);
	        }

	        if(display.getRotation() == Surface.ROTATION_180)
	        {
	        	//parameters.setPreviewSize(height, width); 
	        	theCamera.setDisplayOrientation(180);
	        }

	        if(display.getRotation() == Surface.ROTATION_270)
	        {
	        	//parameters.setPreviewSize(width, height);
	            theCamera.setDisplayOrientation(180);
	        }

	        theCamera.setParameters(parameters);
	        previewCamera(holder); 
	   }

	   @Override
	   public void surfaceCreated(SurfaceHolder holder) {
		   previewCamera(holder);
	   }
	   
	   public void previewCamera(SurfaceHolder holder)
	   {        
	       try 
	       {           
	    	   theCamera.setPreviewDisplay(holder);          
	    	   theCamera.startPreview();
	           isPreviewRunning = true;
	       }
	       catch(Exception e)
	       {
	    	   Log.d("TELA_CAMERA", "Não pode iniciar o preview", e);    
	       }
	   }

	   @Override
	   public void surfaceDestroyed(SurfaceHolder arg0) {
	   }
	   
	   public boolean isRunning(){
		   return isPreviewRunning;
	   }
}