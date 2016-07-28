package com.ashitakalax.scheduledtimelapse.controller;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lballing on 7/26/2016.
 * This is a helper class that allows me to take pictures outside the camera view activity and in the alarm service.
 */
public class CameraController implements Camera.PictureCallback{

    static final String TAG = "CAMERA_CONTROLLER";
    private Camera mCamera;
    private Camera.PictureCallback tempCallback;
    public CameraController()
    {
        //todo setup a way to have a camera preview to reference this.
    }


    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            Log.e("CAMERA_OPEN", "Failed to open Camera" + e.getMessage());
        }
        return c; // returns null if camera is unavailable
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public List<String> getIsoOptions()
    {
        return getCameraTypeOptions("iso-values");
    }

    private List<String> getCameraTypeOptions(String parameterType)
    {
        Camera camera = getCameraInstance();
        Camera.Parameters parameters = camera.getParameters();
        String values = parameters.get(parameterType);
        camera.release();
        return Arrays.asList(values.split("\\s*,\\s*"));
    }

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
