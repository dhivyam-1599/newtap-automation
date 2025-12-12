
package api.utilities;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class DateTimeUtils {
    public static String getReportDateWithinLast90Days() {

        int daysAgo = ThreadLocalRandom.current().nextInt(0, 91);
        LocalDateTime dateTime = LocalDateTime.now().minusDays(daysAgo);
        String formatted = dateTime
                .atZone(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ISO_INSTANT);

        return formatted;
    }


    }

