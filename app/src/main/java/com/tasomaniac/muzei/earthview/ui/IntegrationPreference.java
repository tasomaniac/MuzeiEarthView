package com.tasomaniac.muzei.earthview.ui;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.preference.CheckBoxPreference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;

import com.tasomaniac.muzei.earthview.App;
import com.tasomaniac.muzei.earthview.R;
import com.tasomaniac.muzei.earthview.util.AppInstallEnabler;

import javax.inject.Inject;

public class IntegrationPreference extends CheckBoxPreference {

    @Inject
    PackageManager packageManager;
    @Inject
    ContentResolver contentResolver;

    AppInstallEnabler appInstallEnabler;

    Intent originalIntent;
    Intent alternativeIntent;

    public IntegrationPreference(Context context) {
        super(context);
        initialize(context, null);
    }

    public IntegrationPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public IntegrationPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IntegrationPreference(
            Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        App.get(context).component().inject(this);

        setWidgetLayoutResource(R.layout.preference_widget_error);
        setDefaultValue(false);

        final TypedArray sa = context.obtainStyledAttributes(attrs,
                R.styleable.IntegrationPreference);

        extractAlternativeIntent(sa);

        sa.recycle();

        setDisableDependentsState(true);

        appInstallEnabler = new AppInstallEnabler(context, this);
    }

    @Override
    protected void onAttachedToActivity() {
        super.onAttachedToActivity();
        originalIntent = getIntent();

        if (originalIntent != null && !hasIntent(alternativeIntent)) {
            ComponentName component = originalIntent.getComponent();
            if (component != null) {
                alternativeIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + component.getPackageName()));
            }
        }
        checkState();
    }

    public void resume() {
        appInstallEnabler.resume();
    }

    public void pause() {
        appInstallEnabler.pause();
    }

    public void checkState() {
        if (hasIntent(originalIntent)) {
            adjustPreference(originalIntent, false);
        } else {
            adjustPreference(alternativeIntent, true);
        }
    }

    private void extractAlternativeIntent(TypedArray sa) {
        //Parse the alternative Intent
        alternativeIntent = new Intent();

        alternativeIntent.setAction(
                sa.getString(R.styleable.IntegrationPreference_alternativeIntentAction));

        String data = sa.getString(R.styleable.IntegrationPreference_alternativeIntentData);
        String mimeType = sa.getString(R.styleable.IntegrationPreference_alternativeIntentMimeType);
        alternativeIntent.setDataAndType(data != null ? Uri.parse(data) : null, mimeType);

        String packageName =
                sa.getString(R.styleable.IntegrationPreference_alternativeIntentTargetPackage);
        String className =
                sa.getString(R.styleable.IntegrationPreference_alternativeIntentTargetClass);
        if (packageName != null && className != null) {
            alternativeIntent.setComponent(new ComponentName(packageName, className));
        }
    }

    @Override
    public void setSummaryOn(CharSequence summary) {
        if (summary != null) {
            SpannableString summarySpan = getErrorString(summary);
            super.setSummaryOn(summarySpan);
        } else {
            super.setSummaryOn(null);
        }
    }

    private boolean hasIntent(@Nullable Intent intent) {
        return intent != null
                && packageManager.resolveActivity(intent, 0) != null;
    }

    @Override
    protected void onClick() {
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);

        if (!checked) {
            setTitle(getTitle().toString());
        } else {
            SpannableString titleSpan = getErrorString(getTitle());
            setTitle(titleSpan);
        }
    }

    @NonNull
    private SpannableString getErrorString(CharSequence originalString) {
        int errorColor = getContext().getResources().getColor(R.color.error_color);
        SpannableString errorSpan = new SpannableString(originalString);
        errorSpan.setSpan(new ForegroundColorSpan(errorColor), 0, errorSpan.length(), 0);
        return errorSpan;
    }

    private void adjustPreference(Intent intentOff, boolean checked) {
        setIntent(intentOff);
        setChecked(checked);

        if (!hasIntent(intentOff)) {
            setIntent(null);
        }
    }
}
