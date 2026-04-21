package dreamdev.moniepoint.utils;

import dreamdev.moniepoint.data.models.Election;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class ElectionStatus {

    private static final ZoneId WAT = ZoneId.of("Africa/Lagos");

    public static LocalDateTime now() {
        return LocalDateTime.now(WAT);
    }

    public static boolean isUpcoming(Election election) {
        return now().isBefore(election.getStartDateTime());
    }

    public static boolean isOngoing(Election election) {
        LocalDateTime now = now();
        return !now.isBefore(election.getStartDateTime()) && !now.isAfter(election.getEndDateTime());
    }

    public static boolean isEnded(Election election) {
        return now().isAfter(election.getEndDateTime());
    }

    public static String getStatus(Election election) {
        if (isUpcoming(election)) return "UPCOMING";
        if (isOngoing(election)) return "ONGOING";
        return "ENDED";
    }
}