package us.shiroyama.android.firebaserealtimechat.util;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * Date & Time Utility
 *
 * @author Fumihiko Shiroyama (fu.shiroyama@gmail.com)
 */
public class DateTimeUtils {
    public static String formatTimestamp(long epochMilli) {
        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneId.systemDefault());
        return zonedDateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}
