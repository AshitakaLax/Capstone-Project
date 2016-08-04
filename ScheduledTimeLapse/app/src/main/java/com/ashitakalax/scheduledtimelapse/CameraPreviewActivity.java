package com.ashitakalax.scheduledtimelapse;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.ashitakalax.scheduledtimelapse.controller.CameraController;
import com.ashitakalax.scheduledtimelapse.views.CameraPreview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Using hardware.camera to stay compatible with api 18(I want to be able to go back to the oldest hardware possible.
 */
public class CameraPreviewActivity extends AppCompatActivity implements Camera.PictureCallback{

    static final String TAG = "CAMERA_PREVIEW_ACTIVITY";

    private Camera mCamera;
    private CameraPreview mPreview;

    //todo move all the camera components to another file. since we will need to do this from a alarm clock service.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_preview);

        // Add a listener to the Capture button
        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        mCamera.takePicture(null, null, CameraController.handlePictureCallback);
                    }
                }
        );

        //check whether we can get a camera(or if already occupied).
//        if(!checkCameraHardware(this))
//        {
//            // we don't have a camera to use.
//            return;
//        }
//        // Create an instance of Camera
//        mCamera = getCameraInstance();
        mCamera = CameraController.getCameraInstance(this);

        if(mCamera != null) {
            // Create our Preview view and set it as the content of our activity.
            mPreview = new CameraPreview(this, mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);
        }
    }

//
//
//    /** A safe way to get an instance of the Camera object. */
//    public static Camera getCameraInstance(){
//        Camera c = null;
//        try {
//            c = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK); // attempt to get a Camera instance
//        }
//        catch (Exception e){
//            // Camera is not available (in use or does not exist)
//            Log.e("CAMERA_OPEN", "Failed to open Camera" + e.getMessage());
//        }
//        return c; // returns null if camera is unavailable
//    }
//
//    /** Check if this device has a camera */
//    private boolean checkCameraHardware(Context context) {
//        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
//            // this device has a camera
//            return true;
//        } else {
//            // no camera on this device
//            return false;
//        }
//    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes , 0, bytes.length);

        if (bitmap == null){
            Log.d(TAG, "Error creating media file, check storage permissions: ");
            return;
        }
        //todo change to project name
        File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "timeLapsed");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }
}
