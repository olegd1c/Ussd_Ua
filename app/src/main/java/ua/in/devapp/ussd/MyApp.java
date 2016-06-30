package ua.in.devapp.ussd;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.Locale;

public class MyApp extends Application{
    private static boolean reloadCon;
    private static Context con;
    private static SharedPreferences sp;

    @Override
    public void onCreate() {
        super.onCreate();
        con = getApplicationContext();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
    }
    public static boolean isReloadCon() {
        return reloadCon;
    }

    public static void setReloadCon(boolean reload) {
        reloadCon = reload;
    }

    public static boolean ChangeLang(Resources resources, MainActivity mainActivity) {
        boolean rezult = false;
        String localeNow = resources.getConfiguration().locale.getLanguage();
        String languagePref = sp.getString("language", "ru");
        if (!localeNow.equals(languagePref)){
            //установить язык
            Locale locale = new Locale(languagePref);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            con.getResources().updateConfiguration(config, con.getResources().getDisplayMetrics());
            rezult = true;
            if (Build.VERSION.SDK_INT >=11) {
                mainActivity.recreate();
            }
            else {
                Toast.makeText(con,
                        R.string.changePref, Toast.LENGTH_SHORT).show();
            }
        }
        return rezult;
    }

    public static boolean ChangeLang(Resources resources, PreferenceActivity mainActivity) {
        boolean rezult = false;
        String localeNow = resources.getConfiguration().locale.getLanguage();
        String languagePref = sp.getString("language", "ru");
        if (!localeNow.equals(languagePref)){
            //установить язык
            Locale locale = new Locale(languagePref);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            con.getResources().updateConfiguration(config, con.getResources().getDisplayMetrics());
            rezult = true;
        }
        if (Build.VERSION.SDK_INT >=11) {
            mainActivity.recreate();
        }
        else {
            Toast.makeText(con,
                    R.string.changePref, Toast.LENGTH_SHORT).show();
        }
       return rezult;
    }
}
