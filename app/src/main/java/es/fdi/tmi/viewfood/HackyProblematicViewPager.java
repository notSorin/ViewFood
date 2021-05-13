package es.fdi.tmi.viewfood;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * This class is used to override the onInterceptTouchEvent method for a ViewPager,
 * which proved to be problematic in some occasions.
 * */
public class HackyProblematicViewPager extends ViewPager
{
    public HackyProblematicViewPager(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        try
        {
            return super.onInterceptTouchEvent(ev);
        }
        catch(Exception ignored)
        {
            //Treat the event as not processed if an exception is thrown.
            return false;
        }
    }
}