package us.shiroyama.android.firebaserealtimechat;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;
import us.shiroyama.android.firebaserealtimechat.di.ApplicationComponent;
import us.shiroyama.android.firebaserealtimechat.di.ApplicationModule;
import us.shiroyama.android.firebaserealtimechat.di.DaggerApplicationComponent;

/**
 * Custom Application
 *
 * @author Fumihiko Shiroyama (fu.shiroyama@gmail.com)
 */
public class MyApplication extends Application {
    public ApplicationComponent applicationComponent;

    public ApplicationComponent getComponent() {
        return applicationComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        TwitterAuthConfig authConfig = new TwitterAuthConfig(BuildConfig.TWITTER_KEY, BuildConfig.TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig), new Crashlytics());

        /**
         * avoid crashing FirebaseDatabase's initialization when used in conjunction with Firebase Crash Reporting
         * http://stackoverflow.com/questions/37346363/java-lang-illegalstateexception-firebaseapp-with-name-default
         */
        if (!FirebaseApp.getApps(this).isEmpty()) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(BuildConfig.PERSISTENCE_ENABLED);
        }

        AndroidThreeTen.init(this);
    }

}
