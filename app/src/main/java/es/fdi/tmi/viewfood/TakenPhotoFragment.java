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

/**
 * Fragment used within a TabbedLayout with the purpose of showing a photo taken by the user.
 * */
public class TakenPhotoFragment extends Fragment
{
    private PhotoView _takenImage;
    private TextView _textPlaceholder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_taken_photo, container, false);

        _textPlaceholder = v.findViewById(R.id.UserPhotoPlaceholder);
        _takenImage = v.findViewById(R.id.TakenPhoto);

        return v;
    }

    /**
     * Sets a photo in the fragment's image view.
     * */
    public void setPhoto(Uri photoURI)
    {
        _textPlaceholder.setVisibility(View.GONE);
        _takenImage.setVisibility(View.VISIBLE);
        _takenImage.setImageURI(photoURI);
    }
}