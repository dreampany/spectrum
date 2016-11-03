package com.thebluealliance.spectrum;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.DialogPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceDialogFragmentCompat;
import android.view.View;

import com.thebluealliance.spectrum.views.ColorPickerView;

public class CircleColorPickerPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {

    private ColorPickerView mColorPickerView;

    public static CircleColorPickerPreferenceDialogFragmentCompat newInstance(Preference preference) {
        CircleColorPickerPreferenceDialogFragmentCompat fragment = new CircleColorPickerPreferenceDialogFragmentCompat();
        Bundle bundle = new Bundle(1);
        bundle.putString("key", preference.getKey());
        fragment.setArguments(bundle);
        return fragment;
    }

    protected void onBindDialogView(View view) {
        mColorPickerView = (ColorPickerView) view.findViewById(R.id.color_picker);

        DialogPreference preference = getPreference();
        int color = preference instanceof CircleColorPickerPreferenceCompat ?
                ((CircleColorPickerPreferenceCompat) preference).getColor() : 0;

        mColorPickerView.setOldCenterColor(color);
        mColorPickerView.setColor(color);
    }

    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            int color = mColorPickerView.getColor();
            DialogPreference preference = getPreference();
            String key = preference.getKey();

            SharedPreferences preferences = preference.getPreferenceManager().getSharedPreferences();
            preferences.edit().putInt(key, color).apply();
        }
    }
}
