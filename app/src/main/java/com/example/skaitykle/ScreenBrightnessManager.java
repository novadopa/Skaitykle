package com.example.skaitykle;

import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public abstract class ScreenBrightnessManager extends AppCompatActivity /*implements SensorEventListener*/ {
    private static final int BRIGHTNESS_THRESHOLD = 15;
    private static final int HYSTERESIS = 6;

    private ContentObserver brightnessObserver;
    private boolean isInDarkMode = false;
    private long lastSwitchTime = 0;
    private static final long SWITCH_COOLDOWN_MS = 1000;

    @Override
    protected void onResume() {
        super.onResume();

        int currentMode = AppCompatDelegate.getDefaultNightMode();
        if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
            isInDarkMode = true;
        } else if (currentMode == AppCompatDelegate.MODE_NIGHT_NO) {
            isInDarkMode = false;
        } else {
            int nightModeFlags = getResources().getConfiguration().uiMode
                    & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
            isInDarkMode = (nightModeFlags
                    == android.content.res.Configuration.UI_MODE_NIGHT_YES);
        }

        brightnessObserver = new ContentObserver(new Handler(Looper.getMainLooper())) {
            @Override
            public void onChange(boolean selfChange) {
                checkBrightness();
            }
        };

        Uri brightnessUri = Settings.System.getUriFor(
                Settings.System.SCREEN_BRIGHTNESS);
        getContentResolver().registerContentObserver(
                brightnessUri, false, brightnessObserver);

        checkBrightness();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (brightnessObserver != null) {
            getContentResolver().unregisterContentObserver(brightnessObserver);
            brightnessObserver = null;
        }
    }

    private void checkBrightness() {
        int brightness;
        try {
            brightness = Settings.System.getInt(
                    getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            return;
        }

        boolean shouldBeDark;
        if (isInDarkMode) {
            shouldBeDark = brightness < (BRIGHTNESS_THRESHOLD + HYSTERESIS);
        } else {
            shouldBeDark = brightness < (BRIGHTNESS_THRESHOLD - HYSTERESIS);
        }

        if (shouldBeDark != isInDarkMode) {
            long now = System.currentTimeMillis();
            if (now - lastSwitchTime < SWITCH_COOLDOWN_MS) return;
            lastSwitchTime = now;

            isInDarkMode = shouldBeDark;
            int mode = isInDarkMode
                    ? AppCompatDelegate.MODE_NIGHT_YES
                    : AppCompatDelegate.MODE_NIGHT_NO;
            AppCompatDelegate.setDefaultNightMode(mode);
            recreate();
        }
    }
    /*SensorManager sensorManager;
    Sensor lightSensor;

    float LUX_THRESHOLD = 50f;
    float HYSTERESIS = 10f;

    boolean isInDarkMode = false;
    private long lastSwitchTime = 0;
    private static final long SWITCH_COOLDOWN_MS = 2000;

    @Override
    protected void onResume(){
        super.onResume();

        int currentMode = AppCompatDelegate.getDefaultNightMode();
        if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) {
            isInDarkMode = true;
        } else if (currentMode == AppCompatDelegate.MODE_NIGHT_NO) {
            isInDarkMode = false;
        } else {
            int nightModeFlags = getResources().getConfiguration().uiMode
                    & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
            isInDarkMode = (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES);
        }

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if(sensorManager != null){
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            if(lightSensor != null){
                sensorManager.registerListener(this, lightSensor,
                        SensorManager.SENSOR_DELAY_UI);
            }
        }
    }


    @Override
    protected void onPause(){
        super.onPause();
        if(sensorManager != null){
            sensorManager.unregisterListener(this);
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event){
        if(event.sensor.getType() != Sensor.TYPE_LIGHT) return;

        float lux = event.values[0];

        boolean shouldBeDark;
        if(isInDarkMode){
            shouldBeDark = lux < (LUX_THRESHOLD + HYSTERESIS);
        }else{
            shouldBeDark = lux < (LUX_THRESHOLD - HYSTERESIS);
        }

        if(shouldBeDark != isInDarkMode){
            long now = System.currentTimeMillis();
            if(now - lastSwitchTime < SWITCH_COOLDOWN_MS) return;
            lastSwitchTime = now;

            isInDarkMode = shouldBeDark;

            int mode = isInDarkMode ? AppCompatDelegate.MODE_NIGHT_YES
                    : AppCompatDelegate.MODE_NIGHT_NO;

            AppCompatDelegate.setDefaultNightMode(mode);
            recreate();
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }*/
}
