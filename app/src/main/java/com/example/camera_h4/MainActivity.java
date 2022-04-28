package com.example.camera_h4;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Printer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    ImageView imageView;
    Button takePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Find our ImageView
        this.imageView = (ImageView) this.findViewById(R.id.imageView2);
        //Find our TakePhoto button
        this.takePhoto = (Button) this.findViewById(R.id.button);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setPic();
    }

    //Event for our TakePhoto Button
    public void onClickPhotoButton(View view) {
        dispatchTakePictureIntent();
    }

    //Event for SavePhoto Button
    public void onClickSaveButton(View view) {
        galleryAddPic();
        setPic();
    }

    private void dispatchTakePictureIntent() {
        //Creates new Intent with the MediaStore
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Make sure that our camera activity can handle intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Toast.makeText(this, "Something went wrong when creating file.", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        } else {
            Toast.makeText(this, "Couldn't find PacketManager", Toast.LENGTH_SHORT).show();
        }
    }

    String currentPhotoPath;
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("HH:mm:ss_dd-MM-yyyy").format(new Date());
        String imageFileName = "JPEG_" + timeStamp +"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        //Save a file: path for use with Action_View intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //Invokes the system's media scanner and adds your photo to the
    //Media Providers' database and makes it available to the Android Gallery application
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void setPic() {
        //Get dimensions of the ImageView
        int imageView_Width = imageView.getWidth();
        int imageView_Height = imageView.getHeight();

        //Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int photo_Width = bmOptions.outWidth;
        int photo_Height = bmOptions.outHeight;

        //Determine how much to scale down
        int scaleFactor = Math.max(1, Math.min(photo_Width/imageView_Width, photo_Height/imageView_Height));

        //Decode image file into Bitmap size to the ImageView
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

}