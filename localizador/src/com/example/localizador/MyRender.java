package com.example.localizador;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.graphics.PointF;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView.Renderer;

public class MyRender implements Renderer {
	
	
	SensorManager sensorManager = null;
	
//	float[] verteices = new float[] { 	0.0f, 0.0f, -0.9f,
//										-0.3f, 0.4f, -0.9f,
//										0.3f, 0.4f, -0.9f };
	
	float[] verteices = new float[] { 	1.0f   , -1.0f ,   0.0f ,
										1.0f   ,  1.0f ,   0.0f ,
										-10.0f , 1.0f  ,   0.0f ,
										-10.0f , -1.0f  ,   0.0f };
	
	int[] colors = new int[] {  25535, 0, 0, 65535, 
								60535, 0, 0, 65535,
								60535, 0, 0, 65535 };


	
	FloatBuffer vBuffer = makeFloatBuffer(verteices);
	IntBuffer cBuffer = makeIntBuffer(colors);

	private List<PointF> pontos = null;
	
	public MyRender(){
		//Giroscopio g = new Giroscopio();
	}
	

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(0.0f, 0.0f, -1.0f);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vBuffer);
		gl.glColorPointer(4, GL10.GL_FIXED, 0, cBuffer);
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 4);
		
		gl.glFinish();
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		float ratio = (float) width / height;
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 9);
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glDisable(GL10.GL_DITHER);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
		gl.glClearColor(0, 0, 0, 0);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
	}

	public static FloatBuffer makeFloatBuffer(float[] arr) {
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}

	
	public static IntBuffer makeIntBuffer(int[] arr) {
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
		bb.order(ByteOrder.nativeOrder());
		IntBuffer ib = bb.asIntBuffer();
		ib.put(arr);
		ib.position(0);
		return ib;
	}

	public void setPoints(List<PointF> pontos){
		this.pontos  = pontos;
	}
}


//public class MyRender implements Renderer {
//    private int _rectangleProgram;
//    private int _rectangleAPositionLocation;
//    private FloatBuffer _rectangleVFB;
//    
//    
//    private float[] mMVPMatrix = new float[16];
//	private int mMVPMatrixHandle;
//	private int mProjMatrixHandle;
//	private int mMMatrixHandle;
//	private int mVMatrixHandle;
//	private float[] mProjectionMatrix = new float[16];
//	private float[] mViewMatrix = new float[16];
//	private float[] mModelMatrix = new float[16];
//
//    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
//        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
//        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 0, 0, 0, -5, 0, -5, 0);
//        initShapes();
//        int _rectangleVertexShader = loadShader(GLES20.GL_VERTEX_SHADER, _rectangleVertexShaderCode);
//        int _rectangleFragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, _rectangleFragmentShaderCode);
//        _rectangleProgram = GLES20.glCreateProgram();
//        GLES20.glAttachShader(_rectangleProgram, _rectangleVertexShader);
//        GLES20.glAttachShader(_rectangleProgram, _rectangleFragmentShader);
//        GLES20.glLinkProgram(_rectangleProgram);
//        _rectangleAPositionLocation = GLES20.glGetAttribLocation(_rectangleProgram, "aPosition");
//    }
//
//    public void onSurfaceChanged(GL10 gl, int width, int height) {
//		gl.glViewport(0, 0, width, height);
//		final float ratio = (float) width / height;
//		
////		gl.glMatrixMode(GL10.GL_PROJECTION);
////		gl.glLoadIdentity();
////		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 9);
//
//		final float left = -ratio;
//		final float right = ratio;
//		final float bottom = -1.0f;
//		final float top = 1.0f;
//		final float near = 1.0f;
//		final float far = 10.0f;
//		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
//	
//    }
//
//    public void onDrawFrame(GL10 gl) {
//        gl.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
//
//        mMVPMatrixHandle = GLES20.glGetUniformLocation(_rectangleProgram, "u_MVPMatrix");
//        
//        mProjMatrixHandle = GLES20.glGetUniformLocation(_rectangleProgram, "u_PMatrix");
//        mVMatrixHandle = GLES20.glGetUniformLocation(_rectangleProgram, "u_VMatrix");
//        mMMatrixHandle = GLES20.glGetUniformLocation(_rectangleProgram, "u_MMatrix");
//        
//        GLES20.glUseProgram(_rectangleProgram);
//        GLES20.glVertexAttribPointer(_rectangleAPositionLocation, 3, GLES20.GL_FLOAT, false, 12, _rectangleVFB);
//        GLES20.glEnableVertexAttribArray(_rectangleAPositionLocation);
//        
//
//        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
//     
//        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
//        // (which now contains model * view * projection).
//        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
//     
//        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
//        
//        GLES20.glUniformMatrix4fv(mProjMatrixHandle, 1, false, mProjectionMatrix, 0);
//        GLES20.glUniformMatrix4fv(mVMatrixHandle, 1, false, mViewMatrix, 0);
//        GLES20.glUniformMatrix4fv(mMMatrixHandle, 1, false, mModelMatrix, 0);
//        
//        
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);     
//    }
//
//    private void initShapes()  {
//        float rectangleVFA[] = {
//                    0,      0,      0,
//                    0,      5.5f,   0,
//                    70.75f,  50.5f,   100.0f,
//                    70.75f,  50.5f,   0,
//                    70.75f,  0,      100.0f,
//                    0,      0,      0,
//                };
//        ByteBuffer rectangleVBB = ByteBuffer.allocateDirect(rectangleVFA.length * 4);
//        rectangleVBB.order(ByteOrder.nativeOrder());
//        _rectangleVFB = rectangleVBB.asFloatBuffer();
//        _rectangleVFB.put(rectangleVFA);
//        _rectangleVFB.position(0);
//    }
//
//    private final String _rectangleVertexShaderCode = 
//    	       "uniform mat4 u_MVPMatrix;      \n"
//    		 + "uniform mat4 u_MMatrix;        \n"
//    		 + "uniform mat4 u_PMatrix;        \n"
//    		 + "uniform mat4 u_VMatrix;        \n"
//    	     + "attribute vec4 a_Position;     \n"
//    	     + "void main()                    \n"
//    	     + "{                              \n"
//    	     + "   gl_Position = u_MVPMatrix   \n"
//    	     + "               * a_Position;   \n"
//    	    // + "   gl_PointSize = 5.0;         \n"
//    	     + "}                              \n";
//
//    private final String _rectangleFragmentShaderCode = 
//            "void main() {                          \n"
//        +   " gl_FragColor = vec4(1.0, 0.5, 0.5, 1.0);         \n"
//        +   "}                                      \n";
//
//    private int loadShader(int type, String source)  {
//        int shader = GLES20.glCreateShader(type);
//        GLES20.glShaderSource(shader, source);
//        GLES20.glCompileShader(shader);
//        return shader;
//    }
//}
