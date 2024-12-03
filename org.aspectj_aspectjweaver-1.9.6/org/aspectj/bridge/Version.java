/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.bridge;

import java.io.IOException;
import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class Version {
    public static final String DEVELOPMENT = "DEVELOPMENT";
    public static final long NOTIME = 0L;
    public static final String UNREPLACED_TEXT = "${project.version}";
    public static final String UNREPLACED_TIME_TEXT = "${version.time_text}";
    private static String text;
    private static String time_text;
    private static long time;
    public static final String SIMPLE_DATE_FORMAT = "EEEE MMM d, yyyy 'at' HH:mm:ss z";

    public static long getTime() {
        if (time == -1L) {
            long foundTime = 0L;
            try {
                SimpleDateFormat format = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
                ParsePosition pos = new ParsePosition(0);
                Date date = format.parse(time_text, pos);
                if (date != null) {
                    foundTime = date.getTime();
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            time = foundTime;
        }
        return time;
    }

    public static void main(String[] args) {
        if (null != args && 0 < args.length && !text.equals(args[0])) {
            System.err.println("version expected: \"" + args[0] + "\" actual=\"" + text + "\"");
        }
    }

    public static String getTimeText() {
        return time_text;
    }

    public static String getText() {
        return text;
    }

    static {
        time = -1L;
        try {
            URL resource = Version.class.getResource("version.properties");
            Properties p = new Properties();
            p.load(resource.openStream());
            text = p.getProperty("version.text", "");
            if (text.equals(UNREPLACED_TEXT)) {
                text = DEVELOPMENT;
            }
            if ((time_text = p.getProperty("version.time_text", "")).equals(UNREPLACED_TIME_TEXT)) {
                time_text = "";
            }
        }
        catch (IOException e) {
            text = DEVELOPMENT;
            time_text = "";
        }
    }
}

