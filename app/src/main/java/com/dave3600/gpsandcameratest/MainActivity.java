package com.dave3600.gpsandcameratest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //GPS ------------------------------------------------------------------------------------------

    //This method finds the longitude and latitude of the devices current position and sets a TextView to display these values.
    //If other information about the location is needed you can use the Geocoder class to find things like country and addresses from the longitude and latitude.
    //IMPORTANT remember to add the necessary permissions to the AndroidManifest file (See the app -> manifest -> AndroidManifest.xml)
    public void setGpsText(View view){
        double latitude = 0; //Initializing latitude variable
        double longitude = 0; //Initializing longitude variable

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); //Gets the location manager from the system

        Location gps_loc = null;
        Location network_loc = null;

        //Checks for the necessary permissions for getting GPS Location form the system
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); //Gets location from the GPS
        }

        //Checks for the necessary permissions for getting Network Location form the system
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); //Gets location from the network (can be used as a backup)
        }

        if (gps_loc != null) { //Gets location from the GPS if the gps_loc is not null
            latitude = gps_loc.getLatitude();
            longitude = gps_loc.getLongitude();
        } else if (network_loc != null) { //Gets location from the network if network_loc is not null, only if the GPS was not found
            latitude = network_loc.getLatitude();
            longitude = network_loc.getLongitude();
        } //If nether network or gps can provide the location the default values of 0 and 0 is used instead, should be handled in the real program

        TextView textView = findViewById(R.id.text_view); //Finds the textView on the main app screen
        textView.setText("Lat: " + latitude + " Long: " + longitude); //Sets the text to show the latitude and longitude of the current location of the device
    }

    //Camera ---------------------------------------------------------------------------------------
    int CAMERA_PIC_REQUEST = 1337; //Setting the request code for the camera intent, this is used to identify the result when it is returned after taking the picture in onActivityResult.

    //This method opens the camera app when clicking the "Take image" button
    public void openCamera(View view){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //Makes an intent of the image capture type
        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST); //Starts the camera app and waits for the result
    }

    @Override
    //This method receives the image from the camera app and setts the ImageView to that image.
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CAMERA_PIC_REQUEST) { //If the requestCode matches that of the startActivityForResult of the cameraIntent we know it is the camera app that is returning it's data.
            Bitmap picture; //Initializing the bitmap
            try { //May produce null pointers if the picture is not taken
                picture = (Bitmap) data.getExtras().get("data"); //Gets the picture from the camera app and saves it as a Bitmap
            }catch (NullPointerException e){
                Toast.makeText(this, "Picture not taken", Toast.LENGTH_LONG).show(); //Prints a message to the user, explaining that no picture was taken
                return; //Return if there is no picture
            }

            ImageView imageView = findViewById(R.id.image_view); //Finds the ImageView
            imageView.setImageBitmap(picture); //Sets the ImageView to the picture taken from the camera app
        }

        super.onActivityResult(requestCode, resultCode, data); //Calls the super's onActivityResult (Required by Android)
    }
}