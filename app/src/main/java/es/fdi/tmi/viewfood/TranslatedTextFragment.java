package es.fdi.tmi.viewfood;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Fragment used within a TabbedLayout with the purpose of showing some text.
 * */
public class TranslatedTextFragment extends Fragment
{
    private TextView _translatedText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_translated_text, container, false);

        _translatedText = v.findViewById(R.id.TranslatedText);

        return v;
    }

    /**
     * Sets some text to the fragment's text view.
     * */
    public void setText(String text)
    {
        _translatedText.setText(text);
    }

    /**
     * Restores the fragment to its default state.
     * Overrides the current fragment's text with a default one.
     * */
    public void clearData()
    {
        _translatedText.setText(R.string.translation_text_placeholder);
    }
}