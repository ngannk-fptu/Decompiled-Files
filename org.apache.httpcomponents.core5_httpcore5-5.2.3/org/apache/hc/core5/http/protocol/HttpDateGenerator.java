/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.protocol;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.TimeZone;
import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;

@Contract(threading=ThreadingBehavior.SAFE)
public class HttpDateGenerator {
    private static final int GRANULARITY_MILLIS = 1000;
    public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
    @Deprecated
    public static final TimeZone GMT = TimeZone.getTimeZone("GMT");
    public static final ZoneId GMT_ID = ZoneId.of("GMT");
    public static final HttpDateGenerator INSTANCE = new HttpDateGenerator("EEE, dd MMM yyyy HH:mm:ss zzz", GMT_ID);
    private final DateTimeFormatter dateTimeFormatter;
    private long dateAsMillis;
    private String dateAsText;
    private ZoneId zoneId;

    HttpDateGenerator() {
        this.dateTimeFormatter = new DateTimeFormatterBuilder().parseLenient().parseCaseInsensitive().appendPattern(PATTERN_RFC1123).toFormatter();
        this.zoneId = GMT_ID;
    }

    private HttpDateGenerator(String pattern, ZoneId zoneId) {
        this.dateTimeFormatter = new DateTimeFormatterBuilder().parseLenient().parseCaseInsensitive().appendPattern(pattern).toFormatter();
        this.zoneId = zoneId;
    }

    public synchronized String getCurrentDate() {
        long now = System.currentTimeMillis();
        if (now - this.dateAsMillis > 1000L) {
            this.dateAsText = this.dateTimeFormatter.format(Instant.now().atZone(this.zoneId));
            this.dateAsMillis = now;
        }
        return this.dateAsText;
    }
}

