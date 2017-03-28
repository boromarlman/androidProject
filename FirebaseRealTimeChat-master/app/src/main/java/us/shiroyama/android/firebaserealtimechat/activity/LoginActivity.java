package us.shiroyama.android.firebaserealtimechat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import us.shiroyama.android.firebaserealtimechat.R;
import us.shiroyama.android.firebaserealtimechat.helper.AnalyticsHelper;
import us.shiroyama.android.firebaserealtimechat.helper.LoginHelper;
import us.shiroyama.android.firebaserealtimechat.helper.RemoteConfigHelper;

/**
 * Login Activity
 *
 * @author Fumihiko Shiroyama (fu.shiroyama@gmail.com)
 */
public class LoginActivity extends BaseActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.twitter_login_button)
    TwitterLoginButton twitterLoginButton;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Inject
    AnalyticsHelper analyticsHelper;

    @Inject
    RemoteConfigHelper remoteConfigHelper;

    @Inject
    LoginHelper loginHelper;

    @OnClick(R.id.twitter_login_button)
    void onClickTwitterLoginButton(TwitterLoginButton button) {
        progressBar.setVisibility(View.VISIBLE);
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        getComponent().inject(this);
        analyticsHelper.logOpenScreen(TAG);
        loginHelper.onCreate(twitterLoginButton, progressBar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        remoteConfigHelper.fetch();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        loginHelper.onActivityResult(requestCode, resultCode, data);
    }

}
