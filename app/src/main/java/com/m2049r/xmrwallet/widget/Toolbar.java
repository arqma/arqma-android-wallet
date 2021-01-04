/*
 * Copyright (c) 2017 m2049r
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// based on https://code.tutsplus.com/tutorials/creating-compound-views-on-android--cms-22889

package com.m2049r.xmrwallet.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.m2049r.xmrwallet.R;

import androidx.core.content.ContextCompat;
import timber.log.Timber;

public class Toolbar extends androidx.appcompat.widget.Toolbar {
    public interface OnButtonListener {
        void onButton(int type);
    }

    OnButtonListener onButtonListener;

    public void setOnButtonListener(OnButtonListener listener) {
        onButtonListener = listener;
    }

    ImageView toolbarImage;
    TextView toolbarTitle;
    TextView toolbarSubtitle;
    Button bToolbar;

    public Toolbar(Context context) {
        super(context);
        initializeViews(context);
    }

    public Toolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeViews(context);
    }

    public Toolbar(Context context,
                   AttributeSet attrs,
                   int defStyle) {
        super(context, attrs, defStyle);
        initializeViews(context);
    }

    /**
     * Inflates the views in the layout.
     *
     * @param context the current context for the view.
     */
    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_toolbar, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setContentInsetsAbsolute (0,0);
        toolbarImage = (ImageView) findViewById(R.id.toolbarImage);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            // the vector image does not work well for androis < Nougat
            toolbarImage.getLayoutParams().width = (int) getResources().getDimension(R.dimen.logo_width);
            toolbarImage.setImageResource(R.drawable.ic_text_128x64);
        }

        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        toolbarSubtitle = (TextView) findViewById(R.id.toolbarSubtitle);
        bToolbar = (Button) findViewById(R.id.bToolbar);
        bToolbar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (onButtonListener != null) {
                    onButtonListener.onButton(buttonType);
                }
            }
        });
    }

    public void setTitle(String title, String subtitle) {
        setTitle(title);
        setSubtitle(subtitle);
    }

    public void setTitle(String title) {
        toolbarTitle.setText(title);
        if (title != null) {
            toolbarImage.setVisibility(View.GONE);
            toolbarTitle.setVisibility(View.VISIBLE);
        } else {
            toolbarImage.setVisibility(View.VISIBLE);
            toolbarTitle.setVisibility(View.GONE);
        }
    }

    public final static int BUTTON_NONE = 0;
    public final static int BUTTON_BACK = 1;
    public final static int BUTTON_CLOSE = 2;
    public final static int BUTTON_CREDITS = 3;
    public final static int BUTTON_CANCEL = 4;

    int buttonType = BUTTON_NONE;

    public void setButton(int type) {
        switch (type) {
            case BUTTON_BACK:
                Timber.d("BUTTON_BACK");
                bToolbar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_arrow_back_white_24dp, 0, 0, 0);
                bToolbar.setText(null);
                bToolbar.setVisibility(View.VISIBLE);
                break;
            case BUTTON_CLOSE:
                Timber.d("BUTTON_CLOSE");
                bToolbar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_white_24dp, 0, 0, 0);
                bToolbar.setText(R.string.label_close);
                bToolbar.setVisibility(View.VISIBLE);
                break;
            case BUTTON_CREDITS:
                Timber.d("BUTTON_CREDITS");
                bToolbar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart_white_24dp, 0, 0, 0);
                bToolbar.setText(R.string.label_credits);
                bToolbar.setVisibility(View.VISIBLE);
                break;
            case BUTTON_CANCEL:
                Timber.d("BUTTON_CANCEL");
                bToolbar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_close_white_24dp, 0, 0, 0);
                bToolbar.setText(R.string.label_cancel);
                bToolbar.setVisibility(View.VISIBLE);
                break;
            case BUTTON_NONE:
            default:
                Timber.d("BUTTON_NONE");
                bToolbar.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                bToolbar.setText(null);
                bToolbar.setVisibility(View.GONE);
        }
        buttonType = type;
    }

    public void setSubtitle(String subtitle) {
        toolbarSubtitle.setText(subtitle);
        if (subtitle != null) {
            toolbarSubtitle.setVisibility(View.VISIBLE);
        } else {
            toolbarSubtitle.setVisibility(View.GONE);
        }
    }

    public void setNetworkSubTitleTextColor(String subtitle) {
        setSubtitle(subtitle);
        if (subtitle.equals(getResources().getString(R.string.connect_mainnet))) {
            toolbarSubtitle.setTextColor(ContextCompat.getColor(getContext(), R.color.colorBrandLight));
        } else if (subtitle.equals(getResources().getString(R.string.connect_stagenet))) {
            toolbarSubtitle.setTextColor(ContextCompat.getColor(getContext(), R.color.colorSecLight));
        }
    }
}
