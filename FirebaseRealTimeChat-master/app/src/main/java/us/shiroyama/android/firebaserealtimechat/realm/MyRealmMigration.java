package us.shiroyama.android.firebaserealtimechat.realm;

import io.realm.DynamicRealm;
import io.realm.RealmMigration;

/**
 * @author Fumihiko Shiroyama (fu.shiroyama@gmail.com)
 */
public class MyRealmMigration implements RealmMigration {
    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        // NOP
    }
}
