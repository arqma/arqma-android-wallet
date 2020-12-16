package com.m2049r.xmrwallet.util;
// Credits to mehdico: https://gist.github.com/mehdico/f6f50ba0371c2f2b9b6f428733ad66a7
import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;

public class FilterHelper {

    public enum Mode {
        CLEAR,
        SRC,
        DST,
        SRC_OVER,
        DST_OVER,
        SRC_IN,
        DST_IN,
        SRC_OUT,
        DST_OUT,
        SRC_ATOP,
        DST_ATOP,
        XOR,
        DARKEN,
        LIGHTEN,
        MULTIPLY,
        SCREEN,
        ADD,
        OVERLAY

    }

    private static BlendMode getBlendMode(Mode mode) {
        switch (mode) {
            case CLEAR:
                return BlendMode.CLEAR;
            case SRC:
                return BlendMode.SRC;
            case DST:
                return BlendMode.DST;
            case SRC_OVER:
                return BlendMode.SRC_OVER;
            case DST_OVER:
                return BlendMode.DST_OVER;
            case SRC_IN:
                return BlendMode.SRC_IN;
            case DST_IN:
                return BlendMode.DST_IN;
            case SRC_OUT:
                return BlendMode.SRC_OUT;
            case DST_OUT:
                return BlendMode.DST_OUT;
            case SRC_ATOP:
                return BlendMode.SRC_ATOP;
            case DST_ATOP:
                return BlendMode.DST_ATOP;
            case XOR:
                return BlendMode.XOR;
            case DARKEN:
                return BlendMode.DARKEN;
            case LIGHTEN:
                return BlendMode.LIGHTEN;
            case MULTIPLY:
                return BlendMode.MULTIPLY;
            case SCREEN:
                return BlendMode.SCREEN;
            case ADD:
                return BlendMode.PLUS;
            case OVERLAY:
                return BlendMode.OVERLAY;
        }
        return BlendMode.SCREEN;
    }


    private static PorterDuff.Mode getPorterDuffMode(Mode mode) {
        switch (mode) {
            case CLEAR:
                return PorterDuff.Mode.CLEAR;
            case SRC:
                return PorterDuff.Mode.SRC;
            case DST:
                return PorterDuff.Mode.DST;
            case SRC_OVER:
                return PorterDuff.Mode.SRC_OVER;
            case DST_OVER:
                return PorterDuff.Mode.DST_OVER;
            case SRC_IN:
                return PorterDuff.Mode.SRC_IN;
            case DST_IN:
                return PorterDuff.Mode.DST_IN;
            case SRC_OUT:
                return PorterDuff.Mode.SRC_OUT;
            case DST_OUT:
                return PorterDuff.Mode.DST_OUT;
            case SRC_ATOP:
                return PorterDuff.Mode.SRC_ATOP;
            case DST_ATOP:
                return PorterDuff.Mode.DST_ATOP;
            case XOR:
                return PorterDuff.Mode.XOR;
            case DARKEN:
                return PorterDuff.Mode.DARKEN;
            case LIGHTEN:
                return PorterDuff.Mode.LIGHTEN;
            case MULTIPLY:
                return PorterDuff.Mode.MULTIPLY;
            case SCREEN:
                return PorterDuff.Mode.SCREEN;
            case ADD:
                return PorterDuff.Mode.ADD;
            case OVERLAY:
                return PorterDuff.Mode.OVERLAY;
        }
        return PorterDuff.Mode.SCREEN;
    }


    @SuppressWarnings("deprecation")
    public static void setColorFilter(Drawable background, int color, Mode mode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            background.setColorFilter(new BlendModeColorFilter(color, FilterHelper.getBlendMode(mode)));
        } else {
            background.setColorFilter(color, FilterHelper.getPorterDuffMode(mode));
        }
    }
}