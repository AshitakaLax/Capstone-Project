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
public class CameraController{

    static final String TAG = "CAMERA_CONTROLLER";
    public static final String PIC_SIZE_GET_KEY = "picture-size-values";
    public static final String PIC_SIZE_SET_KEY = "picture-size";
    public static final String ISO_GET_KEY = "iso-values";
    public static final String ISO_SET_KEY = "iso";
    public static final String FLASH_GET_KEY = "flash-mode-values";
    public static final String FLASH_SET_KEY = "flash-mode";
    public static final String FOCUS_GET_KEY = "focus-mode-values";
    public static final String FOCUS_SET_KEY = "focus-mode";
    public static final String FORMAT_GET_KEY = "picture-format-values";
    public static final String FORMAT_SET_KEY = "picture-format";

    public static Camera.PictureCallback handlePictureCallback = new Camera.PictureCallback() {
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
    };

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(Context context){
        Camera c = null;
        //check whether we can get a camera(or if already occupied).
        if(!checkCameraHardware(context))
        {
            // we don't have a camera to use.
            return null;
        }
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
    private static boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public static List<String> getCameraOptions(Context context, String option)
    {
        Camera camera = getCameraInstance(context);
        if(camera == null) {
            return new ArrayList<>();
        }
        Camera.Parameters parameters = camera.getParameters();
        String values = parameters.get(option);
        camera.release();
        return Arrays.asList(values.split("\\s*,\\s*"));
    }

    public static String getCameraOption(Context context, String option)
    {
        Camera camera = getCameraInstance(context);
        Camera.Parameters parameters = camera.getParameters();
        String value = parameters.get(option);
        camera.release();
        return value;
    }


    public static List<String> getCameraTypeOptions(Context context, String parameterType)
    {
        Camera camera = getCameraInstance(context);
        Camera.Parameters parameters = camera.getParameters();
        String values = parameters.get(parameterType);
        camera.release();
        return Arrays.asList(values.split("\\s*,\\s*"));
    }

    public static boolean setCameraOption(Context context, String parameterType, String value)
    {
        try {
            Camera camera = getCameraInstance(context);
            Camera.Parameters parameters = camera.getParameters();
            parameters.set(parameterType, value);
            camera.setParameters(parameters);
            camera.release();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
