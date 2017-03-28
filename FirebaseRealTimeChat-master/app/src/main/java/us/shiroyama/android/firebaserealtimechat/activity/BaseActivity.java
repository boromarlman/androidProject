package us.shiroyama.android.firebaserealtimechat.activity;

import android.support.v7.app.AppCompatActivity;

import us.shiroyama.android.firebaserealtimechat.MyApplication;
import us.shiroyama.android.firebaserealtimechat.di.ActivityComponent;
import us.shiroyama.android.firebaserealtimechat.di.ActivityModule;

/**
 * 本アプリの全{@link android.app.Activity}の基底クラス
 *
 * @author Fumihiko Shiroyama (fu.shiroyama@gmail.com)
 */
public class BaseActivity extends AppCompatActivity {
    private ActivityComponent activityComponent;

    /**
     * {@link android.app.Activity}の依存性グラフを返す
     *
     * @return
     */
    protected ActivityComponent getComponent() {
        if (activityComponent == null) {
            MyApplication myApplication = (MyApplication) getApplication();
            activityComponent = myApplication.getComponent().activityComponent(new ActivityModule(this));
        }
        return activityComponent;
    }
}
