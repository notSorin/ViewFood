package es.fdi.tmi.viewfood;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
{
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView _takenImage;
    private String _currentPhotoPath;
    private Uri _photoURI;
    private Button _translateButton, _photoButton;
    private TextView _translatedText;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _takenImage = this.findViewById(R.id.TakenImage);
        _photoButton = this.findViewById(R.id.RetryButton);
        _translateButton = this.findViewById(R.id.TranslateButton);
        _translatedText = findViewById(R.id.TranslatedText);

        _photoButton.setOnClickListener(v -> dispatchTakePictureIntent());
        _translateButton.setOnClickListener(v -> {
            //TODO Indicate progress of the image being processed.
            //UploadTask ut = new UploadTask();

            //ut.execute(_currentPhotoPath);
            _takenImage.setVisibility(View.GONE);
            _translatedText.setVisibility(View.VISIBLE);
            _translateButton.setVisibility(View.GONE);
        });

        //dispatchTakePictureIntent();
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Ensure that there's a camera activity to handle the intent.
        if(takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            //Create the File where the photo should go.
            File photoFile = null;

            try
            {
                photoFile = createImageFile();
            }
            catch(IOException ex)
            {
                // Error occurred while creating the File
            }

            if(photoFile != null)
            {
                _currentPhotoPath = photoFile.getAbsolutePath();
                _photoURI = FileProvider.getUriForFile(this, "es.fdi.tmi.viewfood", photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, _photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            _takenImage.setVisibility(View.VISIBLE);
            _takenImage.setRotation(getCameraPhotoOrientation(_currentPhotoPath));
            _takenImage.setImageURI(_photoURI);
            _takenImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            _translateButton.setVisibility(View.VISIBLE);
            _translatedText.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private File createImageFile() throws IOException
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    public static int getCameraPhotoOrientation(String imagePath)
    {
        int rotate, orientation = 0;

        try
        {
            ExifInterface exif = new ExifInterface(imagePath);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
        }
        catch(Exception e1)
        {
            e1.printStackTrace();
        }

        switch(orientation)
        {
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 90;
                break;
            default:
                rotate = 0;
        }

        return rotate;
    }
}