package es.fdi.tmi.viewfood;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

public class TranslatedPhotoFragment extends Fragment
{
    private TextView _textPlaceholder;
    private PhotoView _translatedPhoto;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_translated_photo, container, false);

        _textPlaceholder = v.findViewById(R.id.TranslatedPhotoPlaceholder);
        _translatedPhoto = v.findViewById(R.id.TranslatedPhoto);

        return v;
    }

    public void setPhoto(String url)
    {
        _textPlaceholder.setVisibility(View.GONE);
        _translatedPhoto.setVisibility(View.VISIBLE);
        Picasso.get().load(url).into(_translatedPhoto);
    }
}