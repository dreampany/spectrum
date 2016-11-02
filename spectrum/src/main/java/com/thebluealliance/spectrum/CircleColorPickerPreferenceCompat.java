package com.thebluealliance.spectrum;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.app.DialogFragment;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;

import com.thebluealliance.spectrum.internal.ColorCircleDrawable;
import com.thebluealliance.spectrum.internal.SpectrumPreferenceDialogFragmentCompat;

public class CircleColorPickerPreferenceCompat extends DialogPreference {

    private static final String DIALOG_FRAGMENT_TAG = "android.support.v7.preference.PreferenceFragment.DIALOG";

    private static final
    @ColorInt
    int DEFAULT_VALUE = Color.BLACK;
    private static final int ALPHA_DISABLED = 97; //38% alpha

    private
    @ColorInt
    int mCurrentValue;
    private View mColorView;

    private SharedPreferences.OnSharedPreferenceChangeListener mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if (getKey().equals(key)) {
                mCurrentValue = prefs.getInt(key, mCurrentValue);
                updateColorView();
            }
        }
    };

    public CircleColorPickerPreferenceCompat(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDialogLayoutResource(R.layout.dialog_circle_color_picker);
        setWidgetLayoutResource(R.layout.color_preference_widget);
    }

    @Override
    public void onAttached() {
        super.onAttached();
        getSharedPreferences().registerOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    protected void onPrepareForRemoval() {
        super.onPrepareForRemoval();
        getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        mColorView = holder.findViewById(R.id.color_preference_widget);
        updateColorView();
    }

    private void updateColorView() {
        if (mColorView == null) {
            return;
        }
        ColorCircleDrawable drawable = new ColorCircleDrawable(mCurrentValue);
        if (!isEnabled()) {
            // Show just a gray circle outline
            drawable.setColor(Color.WHITE);
            drawable.setAlpha(0);
            drawable.setOutlineWidth(getContext().getResources().getDimensionPixelSize(R.dimen.color_preference_disabled_outline_size));
            drawable.setOutlineColor(Color.BLACK);
            drawable.setOutlineAlpha(ALPHA_DISABLED);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mColorView.setBackground(drawable);
        } else {
            // noinspection deprecation
            mColorView.setBackgroundDrawable(drawable);
        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            mCurrentValue = this.getPersistedInt(DEFAULT_VALUE);
        } else {
            // Set default state from the XML attribute
            mCurrentValue = (Integer) defaultValue;
            persistInt(mCurrentValue);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, DEFAULT_VALUE);
    }

    @ColorInt
    public int getColor() {
        return mCurrentValue;
    }

    public static boolean onDisplayPreferenceDialog(Preference preference, PreferenceFragmentCompat target) {
        boolean handled = false;
        if (target.getTargetFragment() instanceof PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback) {
            handled = ((PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback) target.getTargetFragment())
                    .onPreferenceDisplayDialog(target, preference);
        }
        if (!handled && target.getActivity() instanceof PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback) {
            handled = ((PreferenceFragmentCompat.OnPreferenceDisplayDialogCallback) target.getActivity())
                    .onPreferenceDisplayDialog(target, preference);
        }
        // check if dialog is already showing
        if (!handled && target.getFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG) != null) {
            handled = true;
        }

        if (!handled && preference instanceof CircleColorPickerPreferenceCompat) {
            DialogFragment f = SpectrumPreferenceDialogFragmentCompat.newInstance(preference.getKey());
            f.setTargetFragment(target, 0);
            f.show(target.getFragmentManager(), DIALOG_FRAGMENT_TAG);
            handled = true;
        }
        return handled;
    }
}
