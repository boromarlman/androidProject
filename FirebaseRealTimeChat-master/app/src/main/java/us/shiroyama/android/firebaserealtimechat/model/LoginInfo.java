package us.shiroyama.android.firebaserealtimechat.model;

import android.support.annotation.NonNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Login Info
 *
 * @author Fumihiko Shiroyama (fu.shiroyama@gmail.com)
 */
public class LoginInfo extends RealmObject {
    @Required
    @PrimaryKey
    private String uid;

    private String name;

    private String thumbnail;

    public LoginInfo() {
    }

    public LoginInfo(String uid, String name, String thumbnail) {
        this.uid = uid;
        this.name = name;
        this.thumbnail = thumbnail;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(@NonNull String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(@NonNull String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean senderIsMe(@NonNull String uid) {
        return this.uid.equals(uid);
    }
}
