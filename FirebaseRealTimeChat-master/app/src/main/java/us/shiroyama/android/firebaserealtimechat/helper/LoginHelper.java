package us.shiroyama.android.firebaserealtimechat.helper;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import javax.inject.Inject;

import io.realm.Realm;
import us.shiroyama.android.firebaserealtimechat.activity.ChatActivity;
import us.shiroyama.android.firebaserealtimechat.di.scope.ActivityScope;
import us.shiroyama.android.firebaserealtimechat.model.LoginInfo;
import us.shiroyama.android.firebaserealtimechat.model.User;

/**
 * @author Fumihiko Shiroyama (fu.shiroyama@gmail.com)
 */
@ActivityScope
public class LoginHelper {
    private static final String TAG = LoginHelper.class.getSimpleName();

    private final AppCompatActivity activity;

    private final FirebaseAuth firebaseAuth;

    private final DatabaseReference databaseReference;

    private final Realm realm;

    @Nullable
    private TwitterLoginButton twitterLoginButton;

    @Nullable
    private ProgressBar progressBar;

    @Inject
    public LoginHelper(AppCompatActivity activity, FirebaseAuth firebaseAuth, DatabaseReference databaseReference, Realm realm) {
        this.activity = activity;
        this.firebaseAuth = firebaseAuth;
        this.databaseReference = databaseReference;
        this.realm = realm;
    }

    public void onCreate(@NonNull TwitterLoginButton twitterLoginButton, @NonNull ProgressBar progressBar) {
        this.twitterLoginButton = twitterLoginButton;
        this.progressBar = progressBar;

        if (firebaseAuth.getCurrentUser() != null) {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            String uid = firebaseUser.getUid();
            activity.startActivity(ChatActivity.newIntent(activity, uid));
            activity.finish();
        }

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                twitterLoginButton.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                TwitterSession session = result.data;
                TwitterAuthToken token = session.getAuthToken();
                AuthCredential credential = TwitterAuthProvider.getCredential(token.token, token.secret);

                firebaseAuth.signInWithCredential(credential)
                        .addOnSuccessListener(authResult -> {
                            FirebaseUser firebaseUser = authResult.getUser();
                            String uid = firebaseUser.getUid();

                            UserInfo twitterUser = firebaseUser.getProviderData().get(1);
                            String name = twitterUser.getDisplayName();
                            String thumbnail = twitterUser.getPhotoUrl() != null ? twitterUser.getPhotoUrl().toString() : null;

                            if (TextUtils.isEmpty(name)) {
                                throw new IllegalStateException("name cannot be blank.");
                            }

                            LoginInfo loginInfo = new LoginInfo(uid, name, thumbnail);
                            realm.executeTransaction(innerRealm -> innerRealm.copyToRealmOrUpdate(loginInfo));

                            User user = new User(name, thumbnail);
                            databaseReference
                                    .child(User.PATH)
                                    .child(uid)
                                    .setValue(user)
                                    .addOnSuccessListener(userCreation -> {
                                        Log.d(TAG, "User creation OK");

                                        if (!activity.isFinishing()) {
                                            activity.startActivity(ChatActivity.newIntent(activity, uid));
                                            activity.finish();
                                        }
                                    })
                                    .addOnFailureListener(error -> Log.e(TAG, "User creation NG", error));
                        })
                        .addOnFailureListener(e -> Log.e(TAG, e.getMessage(), e));
            }

            @Override
            public void failure(TwitterException exception) {
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, exception.getMessage(), exception);
                Toast.makeText(activity, exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (twitterLoginButton != null) {
            twitterLoginButton.onActivityResult(requestCode, resultCode, data);
        }
    }
}
