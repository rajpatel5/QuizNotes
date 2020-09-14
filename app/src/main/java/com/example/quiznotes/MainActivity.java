package com.example.quiznotes;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final int CAMERA_REQUEST_CODE = 101;
    public static final int CAMERA_INTENT_REQUEST_CODE = 201;
    public static final int GALLERY_INTENT_REQUEST_CODE = 102;
    FloatingActionButton main, camera, gallery;
    Boolean menuOpen = false;
    OvershootInterpolator interpolator = new OvershootInterpolator();
    ImageView image;

    private static String TAG = "MainActivity";
    static {
        if (OpenCVLoader.initDebug()){
            Log.d(TAG, "OpenCV is configured");
        }
        else{
            Log.d(TAG, "OpenCV is not configured");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        image = findViewById(R.id.image_taken);
        showMenu();
    }

    private void showMenu(){
        main = findViewById(R.id.fab_main);
        camera = findViewById(R.id.fab_camera);
        gallery = findViewById(R.id.fab_gallery);

        camera.setAlpha(0f);
        gallery.setAlpha(0f);

        camera.setTranslationY(100f);
        gallery.setTranslationY(100f);

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuOpen){
                    closeMenu();
                }
                else {
                    openMenu();
                }
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askCameraPermission();
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery, GALLERY_INTENT_REQUEST_CODE);
            }
        });
    }

    private void openMenu(){
        menuOpen = !menuOpen;

        main.setImageResource(R.drawable.ic_cancel_white_36dp);
        camera.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        gallery.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
    }

    private void closeMenu(){
        menuOpen = !menuOpen;

        main.setImageResource(R.drawable.ic_add_circle_white_36dp);
        camera.animate().translationY(100f).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        gallery.animate().translationY(100f).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
    }

    private void askCameraPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (CAMERA_REQUEST_CODE == requestCode){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                openCamera();
            } else {
                Toast.makeText(this, "Camera Permission is Required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, CAMERA_INTENT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_INTENT_REQUEST_CODE){
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            image.setImageBitmap(imageBitmap);
        }

        if (requestCode == GALLERY_INTENT_REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK){
                Uri contentUri = data.getData();
                image.setImageURI(contentUri);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
