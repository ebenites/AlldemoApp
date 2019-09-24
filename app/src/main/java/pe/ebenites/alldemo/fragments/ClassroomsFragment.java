package pe.ebenites.alldemo.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import pe.ebenites.alldemo.R;
import pe.ebenites.alldemo.adapters.ViewPagerAdapter;

public class ClassroomsFragment extends Fragment {

    private static final String TAG = ClassroomsFragment.class.getSimpleName();

    public static Fragment newInstance(){
        Fragment fragment = new ClassroomsFragment();
        return fragment;
    }

    private TabLayout tabLayout;

    private ViewPager viewPager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(R.string.title_classrooms);
        View view = inflater.inflate(R.layout.fragment_classrooms, container, false);

        // Tab Configuration
        viewPager = view.findViewById(R.id.viewpager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());   // https://stackoverflow.com/a/27950670/2823916
        adapter.addFragment(ClassroomsBoxFragment.newInstance(), getString(R.string.tab_title_box));
        adapter.addFragment(ClassroomsStoreFragment.newInstance(), getString(R.string.tab_title_store));
        viewPager.setAdapter(adapter);

        tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_package);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_cart);

        return view;
    }

}
