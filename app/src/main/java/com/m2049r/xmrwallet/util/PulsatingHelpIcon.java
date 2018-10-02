package com.m2049r.xmrwallet.util;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.m2049r.xmrwallet.R;

import junit.framework.Assert;

public class PulsatingHelpIcon {

    private static final float SCALE_VALUE = 1.2f;
    private static final long ANIMATION_DURATION = 500;

    final private Context mContext;
    private MenuItem mHelpMenu;
    private int mMenuItemId;
    private Menu mMenu;

    public PulsatingHelpIcon(Context context) {
        mContext = context;
    }

    public void init(Menu menu, int menuItemId) {
        mMenu = menu;
        mMenuItemId = menuItemId;
        mHelpMenu = menu.findItem(menuItemId);

        Assert.assertNotNull(mHelpMenu);
        startAnimation();
    }

    public void startAnimation() {
        if (mHelpMenu == null) {
            return;
        }

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView iv = (ImageView) inflater.inflate(R.layout.iv_helpicon, null);

        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(iv,
                PropertyValuesHolder.ofFloat("scaleX", SCALE_VALUE),
                PropertyValuesHolder.ofFloat("scaleY", SCALE_VALUE));

        scaleDown.setDuration(ANIMATION_DURATION);
        scaleDown.setInterpolator(new FastOutSlowInInterpolator());
        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);

        scaleDown.start();

        iv.setOnClickListener(v -> {
            mMenu.performIdentifierAction(mMenuItemId, 0);
        });
        mHelpMenu.setActionView(iv);
    }

    public void stopAnimation() {
        if (mHelpMenu == null) {
            return;
        }

        View actionView = mHelpMenu.getActionView();
        if (actionView != null) {
            actionView.clearAnimation();
            mHelpMenu.setActionView(null);
        }
    }

}
