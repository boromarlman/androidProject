package us.shiroyama.android.firebaserealtimechat.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * User Entity
 *
 * @author Fumihiko Shiroyama (fu.shiroyama@gmail.com)
 */
public class User {
    public static final String PATH = "users";

    private String name;

    @Nullable
    private String thumbnail;

    public User() {
    }

    public User(@NonNull String name, @Nullable String thumbnail) {
        this.name = name;
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public String getThumbnail() {
        return thumbnail;
    }
}
