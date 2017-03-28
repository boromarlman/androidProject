package us.shiroyama.android.firebaserealtimechat.di.scope;

import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@link android.app.Activity}スコープを表現する{@link Scope}
 *
 * @author Fumihiko Shiroyama (fu.shiroyama@gmail.com)
 */
@Scope
@Retention(RUNTIME)
public @interface ActivityScope {
}
