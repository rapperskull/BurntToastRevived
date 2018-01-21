package me.rapperskull.burnttoastrevived;

import android.os.Build;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.content.res.XResources;
import android.view.Gravity;
import android.widget.GridLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.File;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;

public class Main implements IXposedHookZygoteInit {
    private XSharedPreferences pref;
    private int size;
    private Context context;
    private int LateralMargin=0;
    private int VerticalMargin=0;
    private int CentralMargin=0;
    final boolean isN=(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N);
    private static final File prefsFile = new File("/data/user_de/0/me.rapperskull.burnttoastrevived/shared_prefs/settings.xml");

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        XC_LayoutInflated hook = new XC_LayoutInflated() {
            @Override
            public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable {

                commonScripts();

                if(liparam.view instanceof LinearLayout){
                    XposedBridge.log("Burnt Toast Revived: Found LinearLayout");

                    if (isN){
                        LateralMargin = 20;
                        VerticalMargin = 15;
                        CentralMargin = 10;
                    } else {
                        LateralMargin = 0;
                        VerticalMargin = 0;
                        CentralMargin = 10;
                    }

                    LinearLayout layout = (LinearLayout) liparam.view;
                    context = layout.getContext();
                    TextView view = (TextView) liparam.view.findViewById(android.R.id.message);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.CENTER;
                    params.rightMargin = LateralMargin;

                    view.setLayoutParams(params);

                    PackageManager pm = context.getPackageManager();

                    ImageView imageView = new ImageView(context);
                    imageView.setMaxHeight(view.getHeight() + size);
                    imageView.setMaxWidth(view.getHeight() + size);
                    imageView.setAdjustViewBounds(true);
                    imageView.setImageDrawable(context.getApplicationInfo().loadIcon(pm));

                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    params1.gravity = Gravity.CENTER;
                    params1.setMargins(LateralMargin,VerticalMargin,CentralMargin,VerticalMargin);
                    imageView.setLayoutParams(params1);

                    layout.setOrientation(LinearLayout.HORIZONTAL);
                    layout.addView(imageView, 0);

                } else if(liparam.view instanceof RelativeLayout){
                    XposedBridge.log("Burnt Toast Revived: Found RelativeLayout");

                    LateralMargin = 0;
                    VerticalMargin = 0;
                    CentralMargin = 10;

                    RelativeLayout layout = (RelativeLayout) liparam.view;
                    context = layout.getContext();
                    TextView view = (TextView) liparam.view.findViewById(android.R.id.message);

                    layout.setBackground(view.getBackground());

                    LinearLayout inner = new LinearLayout(context);
                    RelativeLayout.LayoutParams innerParams = new RelativeLayout.LayoutParams(
                            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    innerParams.addRule(RelativeLayout.CENTER_IN_PARENT , RelativeLayout.TRUE);
                    innerParams.setMargins(0,0,0,0);
                    inner.setLayoutParams(innerParams);

                    PackageManager pm = context.getPackageManager();

                    ImageView imageView = new ImageView(context);
                    imageView.setMaxHeight(view.getHeight() + size);
                    imageView.setMaxWidth(view.getHeight() + size);
                    imageView.setAdjustViewBounds(true);
                    imageView.setImageDrawable(context.getApplicationInfo().loadIcon(pm));

                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    params1.gravity = Gravity.CENTER;
                    params1.setMargins(LateralMargin,VerticalMargin,CentralMargin,VerticalMargin);
                    imageView.setLayoutParams(params1);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    params.gravity = Gravity.CENTER;
                    params.rightMargin = LateralMargin;
                    params.leftMargin = 0;

                    view.setLayoutParams(params);
                    view.setBackground(null);

                    layout.removeAllViews();
                    inner.setOrientation(LinearLayout.HORIZONTAL);
                    inner.addView(imageView, 0);
                    inner.addView(view, 1);
                    layout.addView(inner, 0);

                } else {
                    XposedBridge.log("Burnt Toast Revived: Unknown Layout");
                }

            }
        };

        XResources.hookSystemWideLayout("android", "layout", "transient_notification", hook);
        try {
            XResources.hookSystemWideLayout("android", "layout", "tw_transient_notification", hook);
        } catch (Resources.NotFoundException e) {


        } catch (Throwable t) {

        }
    }

    private void commonScripts(){
        if (isN) {
            pref = new XSharedPreferences(prefsFile);
        } else {
            pref = new XSharedPreferences(Main.class.getPackage().getName(), "settings");
        }
        pref.getFile().setReadable(true, false);
        pref.reload();
        size = pref.getInt("icon_size", 128);
        XposedBridge.log("Burnt Toast Revived: Got size of " + Integer.toString(size));
    }

}
