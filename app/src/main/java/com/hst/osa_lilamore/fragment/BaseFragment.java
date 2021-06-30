package com.hst.osa_lilamore.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.activity.MainActivity;
import com.hst.osa_lilamore.interfaces.OnBackPressedListener;

public abstract class BaseFragment extends Fragment implements
        View.OnClickListener, OnBackPressedListener {

    protected Toolbar toolbar;
    protected ActionBar actionBar;
    protected ActionBarDrawerToggle toggle;
    protected DrawerLayout drawer;
    protected boolean mToolBarNavigationListenerIsRegistered = false;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = ((MainActivity) getActivity()).findViewById(R.id.activity_toolbar);
        actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        drawer = ((MainActivity) getActivity()).findViewById(R.id.drawer_layout);
        toggle = ((MainActivity) getActivity()).getToggle();
    }

    /**
     * Simplify fragment replacing in child fragments
     */
    protected void replaceFragment(@NonNull Fragment fragment) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.container, fragment).commit();
    }

    /**
     * Shows Home button as Back button
     * Took from here {@link}https://stackoverflow.com/a/36677279/9381524
     * <p>
     * To keep states of ActionBar and ActionBarDrawerToggle synchronized,
     * when you enable on one, you disable on the other.
     * And as you may notice, the order for this operation is disable first, then enable - VERY VERY IMPORTANT!!!
     *
     * @param show = true to show <showHomeAsUp> or show = false to show <Hamburger> button
     */
    protected void showBackButton(boolean show) {

        if (show) {
            // Remove hamburger
            toggle.setDrawerIndicatorEnabled(false);
            // Show back button
            actionBar.setDisplayHomeAsUpEnabled(true);
            // when DrawerToggle is disabled i.e. setDrawerIndicatorEnabled(false), navigation icon
            // clicks are disabled i.e. the UP button will not work.
            // We need to add a listener, as in below, so DrawerToggle will forward
            // click events to this listener.
            if (!mToolBarNavigationListenerIsRegistered) {
                toggle.setToolbarNavigationClickListener(v -> onBackPressed());
                mToolBarNavigationListenerIsRegistered = true;
            }

        } else {
            // Remove back button
            actionBar.setDisplayHomeAsUpEnabled(false);
            // Show hamburger
            toggle.setDrawerIndicatorEnabled(true);
            // Remove the/any drawer toggle listener
            toggle.setToolbarNavigationClickListener(null);
            mToolBarNavigationListenerIsRegistered = false;
        }
        // So, one may think "Hmm why not simplify to:
        // .....
        // getSupportActionBar().setDisplayHomeAsUpEnabled(enable);
        // mDrawer.setDrawerIndicatorEnabled(!enable);
        // ......
        // To re-iterate, the order in which you enable and disable views IS important #dontSimplify.
    }

    /**
     * Simplify setTitle in child fragments
     */
    protected void setTitle(int resId) {
        getActivity().setTitle(getResources().getString(resId));
    }

    //
    @Override
    public abstract void onClick(View v);

    // Handles BackPress events from MainActivity
    @Override
    public abstract void onBackPressed();
}
