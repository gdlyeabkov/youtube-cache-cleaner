package softtrack.apps.youtubecachecleaner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public final String youTubeAppName = "com.google.android.youtube";
    public boolean isYouTubeDetected = false;
    public ApplicationInfo youTubeApp = null;
    public TextView cacheCleanerStatusLabel = null;
    public String failedMsg = "Не удается почистить кеш";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    public void initialize() {
        cacheCleanerStatusLabel = findViewById(R.id.cache_cleaner_status_label);
        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            String appName = packageInfo.packageName;
            boolean isYouTubeExists = appName.contains(youTubeAppName);
            if (isYouTubeExists) {
                Log.d("debug", "You Tube is detected :");
                isYouTubeDetected = true;
                youTubeApp = packageInfo;
            }
        }
        if (isYouTubeDetected) {
            boolean isCacheCleaned = false;
            try {
                Context context = createPackageContext(youTubeAppName, CONTEXT_IGNORE_SECURITY);
                isCacheCleaned = deleteCache(context);
            } catch (Exception e) {
                Log.d("debug", "ошибка очистки кеша");
                isCacheCleaned = false;
            }
            if (isCacheCleaned) {
                Log.d("debug", "Кеш очищен");
            } else {
                Log.d("debug", failedMsg);
                cacheCleanerStatusLabel.setText(failedMsg);
            }
        }
    }

    public static boolean deleteCache(Context context) {
        boolean isCacheCleaned = false;
        try {
            File dir = context.getCacheDir();
            isCacheCleaned = deleteDir(dir);
        } catch (Exception e) {
            Log.d("debug", "ошибка с файлами");
            isCacheCleaned = false;
        }
        return isCacheCleaned;
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

}