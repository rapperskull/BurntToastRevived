package me.rapperskull.burnttoastrevived;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import java.io.File;

import de.robv.android.xposed.XposedBridge;

public class MainActivity extends AppCompatActivity {
    final boolean isN=(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N);
    private Context context;
    static String mPrefsName="settings";
    private Toast toast;
    final int baseSize=32;
    final int increment=16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this.getBaseContext();
        if(isN){
            context = !context.isDeviceProtectedStorage() ? context.createDeviceProtectedStorageContext() : context;
        }

        final SharedPreferences sharedPref = getPreferencesAndKeepItReadable(context, mPrefsName);
        int storedValue = sharedPref.getInt("icon_size",96);
        int marginValue = sharedPref.getInt("margin_size", 1);

        SeekBar iconSeekBar = (SeekBar) findViewById(R.id.seekBar);
        if(storedValue>0){
            iconSeekBar.setProgress(sizeToSlide(storedValue));
        }

        iconSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                savePreferences(slideToSize(progress),sharedPref);
            }
        });

        SeekBar marginSeekBar = (SeekBar) findViewById(R.id.seekBar2);
        if(marginValue>=0){
            marginSeekBar.setProgress(marginValue);
        }

        marginSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                saveMargin(progress,sharedPref);
            }
        });

    }

    private void savePreferences(int newValue, SharedPreferences sharedPref){
        if(newValue>0){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("icon_size", newValue);
            editor.commit();
            generateToast("Set size to " + Integer.toString(newValue),true);
        } else {
            XposedBridge.log("Burnt Toast Revived: Tried to set an icon value of " + Integer.toString(newValue));
        }
    }

    private void saveMargin(int newValue, SharedPreferences sharedPref){
        if(newValue>=0){
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("margin_size", newValue);
            editor.commit();
            generateToast("Set margin to " + Integer.toString(newValue),true);
        } else {
            XposedBridge.log("Burnt Toast Revived: Tried to set a margin value of " + Integer.toString(newValue));
        }
    }

    private void generateToast(String message, boolean center){
        if (toast != null) toast.cancel();
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        if(center) {
            toast.setGravity(Gravity.CENTER, 0, 0);
        }
        toast.show();
    }

    private void generateToast(String message){
        generateToast(message,false);
    }

    public SharedPreferences getPreferencesAndKeepItReadable(Context ctx, String prefName) {
        SharedPreferences prefs;
        if(isN){
            prefs = ctx.getSharedPreferences(prefName, 0);
        } else {
            prefs = ctx.getSharedPreferences(prefName, MODE_WORLD_READABLE);
        }
        createFile(ctx, prefName);
        fixPermissions(ctx, prefName);
        return prefs;
    }

    private void createFile(Context ctx, String prefName) {
        try {
            File appBaseDir;
            if(isN){
                appBaseDir = new File(ctx.getDataDir().getAbsolutePath());
            } else {
                appBaseDir = new File(ctx.getFilesDir() + "/..");
            }
            appBaseDir.setExecutable(true, false);
            appBaseDir.setReadable(true, false);

            File sharedPrefsFolder = new File(appBaseDir.getAbsolutePath() + "/shared_prefs");
            if (!sharedPrefsFolder.exists()) {
                sharedPrefsFolder.mkdir();
                sharedPrefsFolder.setExecutable(true, false);
                sharedPrefsFolder.setReadable(true, false);
            }

            File f = new File(sharedPrefsFolder.getAbsolutePath() + "/" + prefName + ".xml");
            if (!f.exists()) {
                f.createNewFile();
                f.setReadable(true, false);
            }
        } catch (Exception e) {

        }
    }

    private void fixPermissions(Context ctx, String prefName) {
        File appBaseDir;
        if (isN) {
            appBaseDir = new File(ctx.getDataDir().getAbsolutePath());
        } else {
            appBaseDir = new File(ctx.getFilesDir() + "/..");
        }
        appBaseDir.setExecutable(true, false);
        appBaseDir.setReadable(true, false);

        File sharedPrefsFolder = new File(appBaseDir.getAbsolutePath() + "/shared_prefs");
        if (sharedPrefsFolder.exists()) {
            sharedPrefsFolder.setExecutable(true, false);
            sharedPrefsFolder.setReadable(true, false);

            File f = new File(sharedPrefsFolder.getAbsolutePath() + "/" + prefName + ".xml");
            if (f.exists()) {
                f.setExecutable(true, false);
                f.setReadable(true, false);
            }
        }
    }

    private int slideToSize(int slide){
        return baseSize+increment*slide;
    }
    private int sizeToSlide(int size){
        return Math.round((size-baseSize)/increment);
    }

}
