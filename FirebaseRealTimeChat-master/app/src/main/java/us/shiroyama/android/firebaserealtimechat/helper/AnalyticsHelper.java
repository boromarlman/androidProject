package us.shiroyama.android.firebaserealtimechat.helper;

import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author Fumihiko Shiroyama (fu.shiroyama@gmail.com)
 */
@Singleton
public class AnalyticsHelper {
    private static final String KEY_SCREEN_NAME = "screen_name";

    private static final String KEY_ACTION_NAME = "action_name";

    private static final String EVENT_OPEN_SCREEN = "open_screen";

    private static final String EVENT_ACTION = "action";

    private static final String VALUE_ACTION_SWIPE_REFRESH = "swipe_refresh";

    private static final String VALUE_ACTION_SEND_MESSAGE = "send_message";

    private final FirebaseAnalytics firebaseAnalytics;

    @Inject
    public AnalyticsHelper(FirebaseAnalytics firebaseAnalytics) {
        this.firebaseAnalytics = firebaseAnalytics;
    }

    public void logOpenScreen(String screenName) {
        Bundle params = new Bundle();
        params.putString(KEY_SCREEN_NAME, screenName);
        firebaseAnalytics.logEvent(EVENT_OPEN_SCREEN, params);
    }

    public void logAction(String action) {
        Bundle params = new Bundle();
        params.putString(KEY_ACTION_NAME, action);
        firebaseAnalytics.logEvent(EVENT_ACTION, params);
    }

    public void logSwipeRefresh() {
        logAction(VALUE_ACTION_SWIPE_REFRESH);
    }

    public void logSendMessage() {
        logAction(VALUE_ACTION_SEND_MESSAGE);
    }

}
