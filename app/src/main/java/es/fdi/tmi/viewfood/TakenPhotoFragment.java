package es.fdi.tmi.viewfood;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.github.chrisbanes.photoview.PhotoView;

public class TakenPhotoFragment extends Fragment
{
    private View _view;
    private PhotoView _takenImage;
    private TextView _textPlaceholder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        _view = inflater.inflate(R.layout.fragment_taken_photo, container, false);
        _textPlaceholder = _view.findViewById(R.id.UserPhotoPlaceholder);
        _takenImage = _view.findViewById(R.id.TakenPhoto);

        return _view;
    }

    public void setPhoto(Uri photoURI)
    {
        _textPlaceholder.setVisibility(View.GONE);
        _takenImage.setVisibility(View.VISIBLE);
        _takenImage.setImageURI(photoURI);
    }
}