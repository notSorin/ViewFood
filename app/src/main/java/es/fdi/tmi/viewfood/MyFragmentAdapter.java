package es.fdi.tmi.viewfood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * A FragmentPagerAdapter used in combination with a ViewPager and TabbedLayout to hold
 * information on multiple tabs in the same activity.
 * */
public class MyFragmentAdapter extends FragmentPagerAdapter
{
    private final List<Fragment> _fragmentsList;
    private final List<String> _fragmentsTitles;

    public MyFragmentAdapter(FragmentManager fm)
    {
        super(fm);

        _fragmentsList = new ArrayList<>();
        _fragmentsTitles = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        return _fragmentsList.get(position);
    }

    @Override
    public int getCount()
    {
        return _fragmentsList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        return _fragmentsTitles.get(position);
    }

    public void addFragment(Fragment fragment, String title)
    {
        _fragmentsList.add(fragment);
        _fragmentsTitles.add(title);
    }
}