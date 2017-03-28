package us.shiroyama.android.firebaserealtimechat.di;

import android.app.Application;
import android.content.Context;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import us.shiroyama.android.firebaserealtimechat.BuildConfig;
import us.shiroyama.android.firebaserealtimechat.R;
import us.shiroyama.android.firebaserealtimechat.realm.MyRealmMigration;
import us.shiroyama.android.firebaserealtimechat.util.BusHolder;

/**
 * @author Fumihiko Shiroyama (fu.shiroyama@gmail.com)
 */
@Module
public class ApplicationModule {
    private Context applicationContext;

    public ApplicationModule(Application application) {
        applicationContext = application.getApplicationContext();
    }

    @Provides
    public Context provideContext() {
        return applicationContext;
    }

    @Singleton
    @Provides
    public FirebaseAnalytics provideFirebaseAnalytics(Context context) {
        return FirebaseAnalytics.getInstance(context);
    }

    @Singleton
    @Provides
    public FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Singleton
    @Provides
    public FirebaseRemoteConfigSettings provideFirebaseRemoteConfigSettings() {
        return new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
    }

    @Singleton
    @Provides
    public FirebaseRemoteConfig provideFirebaseRemoteConfig(FirebaseRemoteConfigSettings firebaseRemoteConfigSettings) {
        FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseRemoteConfig.setConfigSettings(firebaseRemoteConfigSettings);
        firebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        return firebaseRemoteConfig;
    }

    @Singleton
    @Provides
    public FirebaseDatabase provideFirebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }

    @Singleton
    @Provides
    public DatabaseReference provideDatabaseReference(FirebaseDatabase firebaseDatabase) {
        return firebaseDatabase.getReference();
    }

    @Singleton
    @Provides
    public RealmConfiguration provideRealmConfiguration(Context context) {
        Realm.init(context);
        return new RealmConfiguration.Builder()
                .modules(Realm.getDefaultModule())
                .schemaVersion(BuildConfig.REALM_SCHEMA_VERSION)
                .migration(new MyRealmMigration())
                .build();
    }

    @Singleton
    @Provides
    public Realm provideRealm(RealmConfiguration realmConfiguration) {
        return Realm.getInstance(realmConfiguration);
    }

    @Singleton
    @Provides
    public Bus provideBus() {
        return BusHolder.EVENT_BUS;
    }

    @Singleton
    @Provides
    public FirebaseStorage provideFirebaseStorage() {
        return FirebaseStorage.getInstance();
    }

    @Singleton
    @Provides
    public StorageReference provideStorageReference(FirebaseStorage firebaseStorage) {
        return firebaseStorage.getReferenceFromUrl(BuildConfig.FIREBASE_STORAGE_BUCKET);
    }

}
