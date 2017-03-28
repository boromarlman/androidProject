package us.shiroyama.android.firebaserealtimechat.di;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author Fumihiko Shiroyama (fu.shiroyama@gmail.com)
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    ActivityComponent activityComponent(ActivityModule module);
}
