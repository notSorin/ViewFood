package es.fdi.tmi.viewfood;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.viewpager.widget.ViewPager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity
{
    private final CharSequence[] LANGUAGE_CHOICES = {"English", "French", "German", "Italian", "Romanian"};
    private final CharSequence[] LANGUAGE_CODES = {"en", "fr", "de", "it", "ro"};
    private final int REQUEST_IMAGE_CAPTURE = 1;
    private String _currentPhotoPath;
    private Button _photoButton, _translateButton;
    private Uri _photoURI;
    private SharedPreferences _preferences;
    private TakenPhotoFragment _takenPhotoFragment;
    private TranslatedTextFragment _translatedTextFragment;
    private TranslatedPhotoFragment _translatedPhotoFragment;
    private ProgressBar _progressBar;

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
        initializeLanguageSelector();
        initializeTabbedLayout();
        initializePhotoButton();
        initializeTranslateButton();
        initializeProgressBar();
    }

    private void initializeProgressBar()
    {
        _progressBar = findViewById(R.id.ProgressBar);
    }

    private void initializeTabbedLayout()
    {
        MyFragmentAdapter mfa = new MyFragmentAdapter(getSupportFragmentManager());

        _takenPhotoFragment = new TakenPhotoFragment();
        _translatedTextFragment = new TranslatedTextFragment();
        _translatedPhotoFragment = new TranslatedPhotoFragment();

        mfa.addFragment(_takenPhotoFragment, "Taken Photo");
        mfa.addFragment(_translatedTextFragment, "Translated Text");
        mfa.addFragment(_translatedPhotoFragment, "Translated Photo");

        TabLayout tabLayout = findViewById(R.id.MainTabLayout);
        ViewPager vp = findViewById(R.id.MainPager);

        vp.setOffscreenPageLimit(3);
        vp.setAdapter(mfa);
        tabLayout.setupWithViewPager(vp);
    }

    private void initializeTranslateButton()
    {
        _translateButton = findViewById(R.id.TranslateButton);

        //When pressed, the translate button hides some views, shows the prograss bar, and launches
        //an upload task to the server.
        _translateButton.setOnClickListener(v ->
        {
            _translateButton.setVisibility(View.GONE);
            _photoButton.setVisibility(View.GONE);
            _progressBar.setVisibility(View.VISIBLE);

            UploadTask ut = new UploadTask(this);
            int selectedIndex = _preferences.getInt(getString(R.string.language_index), 0);

            ut.execute(_currentPhotoPath, LANGUAGE_CODES[selectedIndex].toString());
        });
    }

    private void initializePhotoButton()
    {
        _photoButton = findViewById(R.id.PhotoButton);

        _photoButton.setOnClickListener(v -> dispatchTakePictureIntent());
    }

    private void initializeLanguageSelector()
    {
        ImageView languageSelector = findViewById(R.id.LanguageSelector);

        languageSelector.setOnClickListener(v ->
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
            _translateButton.setVisibility(View.VISIBLE);
            processTakenPhoto();
            _takenPhotoFragment.setPhoto(_photoURI);
        }
    }

    //It changes the size of _currentPhotoPath, and it also rotates it according to the exif
    //rotation of the photo.
    private Bitmap processTakenPhoto()
    {
        Bitmap bitmap = BitmapFactory.decodeFile(_currentPhotoPath, null);
        float aspect = (float)bitmap.getWidth() / (float)bitmap.getHeight();
        int width = 1024, height = (int)(width / aspect);
        Matrix matrix = new Matrix();

        //Scale the photo.
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);

        matrix.postRotate(getCameraPhotoRotation(_currentPhotoPath));

        //Rotate the photo.
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        try
        {
            //Override the photo on disk with the new one.
            FileOutputStream fos = new FileOutputStream(_currentPhotoPath);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return bitmap;
    }

    @SuppressLint("SimpleDateFormat")
    private File createImageFile() throws IOException
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    //Returns the rotation (in degrees) of a photo taken using the device's camera.
    public static int getCameraPhotoRotation(String imagePath)
    {
        int rotate, orientation = 0;

        try
        {
            ExifInterface exif = new ExifInterface(imagePath);
            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
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

    //Sets data received from the server to their according fragments.
    public void setResponseFromServer(String translatedText, String translatedPhotoURL)
    {
        _translatedTextFragment.setText(translatedText);
        _translatedPhotoFragment.setPhoto("http://35.246.247.149" + translatedPhotoURL);
        _progressBar.setVisibility(View.GONE);
        _photoButton.setVisibility(View.VISIBLE);
    }

    //Displays an alert using an alert dialog.
    public void displayAlert(String alertText)
    {
        //First, return controls to the user, in case they were taken.
        _progressBar.setVisibility(View.GONE);
        _photoButton.setVisibility(View.VISIBLE);

        //Second, show an alert dialog.
        new MaterialAlertDialogBuilder(this).setMessage(alertText).setPositiveButton("OK", null).show();
    }
}