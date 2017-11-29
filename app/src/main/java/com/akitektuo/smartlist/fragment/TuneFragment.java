package com.akitektuo.smartlist.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.akitektuo.smartlist.R;
import com.akitektuo.smartlist.util.Preference;

import static com.akitektuo.smartlist.util.Constant.CURRENCY_AED;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_AUD;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_CAD;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_CHF;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_CNY;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_EUR;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_GBP;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_JPY;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_KRW;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_RON;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_RUB;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_SEK;
import static com.akitektuo.smartlist.util.Constant.CURRENCY_USD;
import static com.akitektuo.smartlist.util.Constant.KEY_AUTO_FILL;
import static com.akitektuo.smartlist.util.Constant.KEY_AUTO_FILL_WANTED;
import static com.akitektuo.smartlist.util.Constant.KEY_CURRENCY;
import static com.akitektuo.smartlist.util.Constant.KEY_RECOMMENDATIONS;

/**
 * Created by AoD Akitektuo on 30-Aug-17 at 21:13.
 */

public class TuneFragment extends Fragment implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private Switch switchRecommendations;
    private Switch switchFill;
    private int layoutId;
    private Preference preference;

    public TuneFragment() {
        layoutId = R.layout.fragment_tune;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("layout_id", layoutId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        preference = new Preference(getContext());

        switchRecommendations = getActivity().findViewById(R.id.switch_light_recommendations);
        switchFill = getActivity().findViewById(R.id.switch_light_fill);

        switchRecommendations.setChecked(preference.getPreferenceBoolean(KEY_RECOMMENDATIONS));
        switchFill.setChecked(preference.getPreferenceBoolean(KEY_AUTO_FILL));

        switchRecommendations.setOnCheckedChangeListener(this);
        switchFill.setOnCheckedChangeListener(this);

        getActivity().findViewById(R.id.layout_light_tune_recommendations).setOnClickListener(this);
        getActivity().findViewById(R.id.layout_light_tune_fill).setOnClickListener(this);
        getActivity().findViewById(R.id.layout_light_currency).setOnClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            layoutId = savedInstanceState.getInt("layout_id");
        }
        return inflater.inflate(layoutId, container, false);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()) {
            case R.id.switch_light_recommendations:
                preference.setPreference(KEY_RECOMMENDATIONS, isChecked);
                if (isChecked && preference.getPreferenceBoolean(KEY_AUTO_FILL_WANTED)) {
                    switchFill.setChecked(true);
                    preference.setPreference(KEY_AUTO_FILL, true);
                } else if (!isChecked) {
                    switchFill.setChecked(false);
                    preference.setPreference(KEY_AUTO_FILL, false);
                }
                break;
            case R.id.switch_light_fill:
                preference.setPreference(KEY_AUTO_FILL, isChecked);
                if (isChecked && !preference.getPreferenceBoolean(KEY_RECOMMENDATIONS)) {
                    switchRecommendations.setChecked(true);
                    preference.setPreference(KEY_AUTO_FILL, true);
                    preference.setPreference(KEY_AUTO_FILL_WANTED, true);
                } else if (isChecked) {
                    preference.setPreference(KEY_AUTO_FILL_WANTED, true);
                } else if (preference.getPreferenceBoolean(KEY_RECOMMENDATIONS)) {
                    preference.setPreference(KEY_AUTO_FILL_WANTED, false);
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_light_currency:
                changeCurrency();
                break;
            case R.id.layout_light_tune_recommendations:
                switchRecommendations.setChecked(!switchRecommendations.isChecked());
                break;
            case R.id.layout_light_tune_fill:
                switchFill.setChecked(!switchFill.isChecked());
                break;
        }
    }

    private void changeCurrency() {
        AlertDialog.Builder builderCurrency = new AlertDialog.Builder(getContext());
        builderCurrency.setTitle("Select currency");
        builderCurrency.setItems(R.array.currency, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String currency = "";
                switch (i) {
                    case 0:
                        currency = CURRENCY_RON;
                        break;
                    case 1:
                        currency = CURRENCY_USD;
                        break;
                    case 2:
                        currency = CURRENCY_EUR;
                        break;
                    case 3:
                        currency = CURRENCY_JPY;
                        break;
                    case 4:
                        currency = CURRENCY_GBP;
                        break;
                    case 5:
                        currency = CURRENCY_AUD;
                        break;
                    case 6:
                        currency = CURRENCY_CAD;
                        break;
                    case 7:
                        currency = CURRENCY_CHF;
                        break;
                    case 8:
                        currency = CURRENCY_CNY;
                        break;
                    case 9:
                        currency = CURRENCY_RUB;
                        break;
                    case 10:
                        currency = CURRENCY_KRW;
                        break;
                    case 11:
                        currency = CURRENCY_SEK;
                        break;
                    case 12:
                        currency = CURRENCY_AED;
                        break;
                }
                preference.setPreference(KEY_CURRENCY, currency);
            }
        });
        builderCurrency.setNeutralButton("Cancel", null);
        AlertDialog alertDialogCurrency = builderCurrency.create();
        alertDialogCurrency.show();
    }
}
