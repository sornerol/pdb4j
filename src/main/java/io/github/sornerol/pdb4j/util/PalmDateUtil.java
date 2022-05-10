package io.github.sornerol.pdb4j.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.TimeZone;

import static io.github.sornerol.pdb4j.util.PdbDatabaseConstants.PALM_EPOCH_YEAR;
import static io.github.sornerol.pdb4j.util.PdbDatabaseConstants.UNIX_EPOCH_YEAR;

/**
 * Helper methods for working with Palm OS timestamps
 */
@Slf4j
public class PalmDateUtil {
    /**
     * Translate a Palm OS timestamp to a Java Calendar.
     *
     * Some Palm OS applications use the Unix epoch base (01-01-1970) instead of the Palm OS epoch base (01-01-1904).
     * This method looks at the most significant bit of the timestamp to determine which epoch base date is used. If the
     * most significant bit is not set, we can assume that the timestamp is relative to the Unix epoch, since otherwise
     * the date would be well before the PDB format was created.
     *
     * @param pdbTime A Palm OS timestamp
     * @return {@link Calendar} set to the corresponding time from the Palm OS timestamp
     */
    public static Calendar calendarFromPdbTime(int pdbTime) {
        /*
          Some Palm applications use the Unix epoch base instead of the Palm epoch base.
          If the highest bit is not set in the timestamp, we can assume this is the case, since otherwise
          the date would be a date well before the PDB format was created.
        */
        int epochYear = ((pdbTime & 0x80000000) == 0) ? UNIX_EPOCH_YEAR : PALM_EPOCH_YEAR;
        log.debug("Epoch year detected as " + epochYear + ".");
        Calendar calendar = getBase(epochYear);
        calendar.setTimeInMillis(calendar.getTimeInMillis() + (Integer.toUnsignedLong(pdbTime) * 1000));
        return calendar;
    }

    /**
     * Get a Palm OS timestamp from a Java Calendar.
     *
     * @param calendar The {@link Calendar} to return a Palm OS timestamp from
     * @param useUnixEpoch If true, create a timestamp relative to the Unix epoch date instead of the Palm OS epoch.
     * @return Palm OS timestamp
     */
    public static int pdbTimestampFromCalendar(Calendar calendar, boolean useUnixEpoch) {
        int epochYear = useUnixEpoch ? UNIX_EPOCH_YEAR : PALM_EPOCH_YEAR;
        Calendar offset = getBase(epochYear);
        int calendarSeconds = Math.toIntExact(calendar.getTimeInMillis() / 1000);
        int offsetSeconds = Math.toIntExact(offset.getTimeInMillis() / 1000);
        return calendarSeconds - offsetSeconds;
    }

    private static Calendar getBase(int epochYear) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.clear();
        calendar.set(epochYear, Calendar.JANUARY, 1, 0, 0, 0);
        return calendar;
    }
}
