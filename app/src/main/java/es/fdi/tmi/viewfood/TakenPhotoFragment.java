package es.fdi.tmi.viewfood;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class TakenPhotoFragment extends Fragment
{
    private View _view;
    private ImageView _takenImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        _view = inflater.inflate(R.layout.fragment_taken_photo, container, false);
        _takenImage = _view.findViewById(R.id.TakenImage);

        return _view;
    }

    public void setPhoto(Uri photoURI, int cameraPhotoOrientation)
    {
        _takenImage.setRotation(cameraPhotoOrientation);
        _takenImage.setImageURI(photoURI);
        _takenImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }
}