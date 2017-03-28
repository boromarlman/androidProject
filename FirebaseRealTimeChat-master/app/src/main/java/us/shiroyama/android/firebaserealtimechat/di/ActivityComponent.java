package us.shiroyama.android.firebaserealtimechat.di;

import dagger.Subcomponent;
import us.shiroyama.android.firebaserealtimechat.activity.ChatActivity;
import us.shiroyama.android.firebaserealtimechat.activity.LoginActivity;
import us.shiroyama.android.firebaserealtimechat.di.scope.ActivityScope;

/**
 * @author Fumihiko Shiroyama (fu.shiroyama@gmail.com)
 */
@ActivityScope
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {
    void inject(LoginActivity activity);

    void inject(ChatActivity activity);
}
