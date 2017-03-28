package us.shiroyama.android.firebaserealtimechat.di;

import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;

import dagger.Module;
import dagger.Provides;
import us.shiroyama.android.firebaserealtimechat.di.scope.ActivityScope;

/**
 * @author Fumihiko Shiroyama (fu.shiroyama@gmail.com)
 */
@Module
public class ActivityModule {
    private AppCompatActivity activity;

    public ActivityModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @ActivityScope
    @Provides
    public AppCompatActivity provideActivity() {
        return activity;
    }

    @ActivityScope
    @Provides
    public LayoutInflater provideLayoutInflater() {
        return activity.getLayoutInflater();
    }
}
