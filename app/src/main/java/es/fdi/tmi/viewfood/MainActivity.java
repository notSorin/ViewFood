package es.fdi.tmi.viewfood;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
{
    private final CharSequence[] LANGUAGE_CHOICES = {"English", "French", "German", "Italian", "Romanian"};
    private final CharSequence[] LANGUAGE_CODES = {"en", "fr", "de", "it", "ro"};
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView _takenImage, _languageSelector;
    private String _currentPhotoPath;
    private Uri _photoURI;
    private Button _translateButton, _photoButton;
    private TextView _translatedText;
    private SharedPreferences _preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();

        _preferences = getPreferences(Context.MODE_PRIVATE);

        //dispatchTakePictureIntent();
    }

    private void initializeViews()
    {
        initializeTakenImage();
        initializeLanguageSelector();
        initializePhotoButton();
        initializeTranslateButton();
        initializeTranslatedText();
    }

    private void initializeTranslatedText()
    {
        _translatedText = findViewById(R.id.TranslatedText);
    }

    private void initializeTranslateButton()
    {
        _translateButton = findViewById(R.id.TranslateButton);

        _translateButton.setOnClickListener(v ->
        {
            //TODO Indicate progress of the image being processed.
            //UploadTask ut = new UploadTask();
            //int selectedIndex = _preferences.getInt(getString(R.string.language_index), 0);

            //ut.execute(_currentPhotoPath, LANGUAGE_CODES[selectedIndex].toString());
            _takenImage.setVisibility(View.GONE);
            _translatedText.setVisibility(View.VISIBLE);
            _translateButton.setVisibility(View.GONE);
        });
    }

    private void initializePhotoButton()
    {
        _photoButton = findViewById(R.id.RetryButton);

        _photoButton.setOnClickListener(v -> dispatchTakePictureIntent());
    }

    private void initializeLanguageSelector()
    {
        _languageSelector = findViewById(R.id.LanguageSelector);

        _languageSelector.setOnClickListener(v ->
        {
            int selectedIndex = _preferences.getInt(getString(R.string.language_index), 0);

            new MaterialAlertDialogBuilder(MainActivity.this)
                    .setTitle("Translate from Spanish into...")
                    .setSingleChoiceItems(LANGUAGE_CHOICES, selectedIndex, null)
                    .setPositiveButton("OK", (dialog, which) ->
                    {
                        //Grab the selected index on the list and save it to disk.
                        ListView lv = ((AlertDialog)dialog).getListView();
                        int index = lv.getCheckedItemCount() == 1 ? lv.getCheckedItemPosition() : 0;
                        SharedPreferences.Editor editor = _preferences.edit();

                        editor.putInt(getString(R.string.language_index), index);
                        editor.apply();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void initializeTakenImage()
    {
        _takenImage = findViewById(R.id.TakenImage);
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