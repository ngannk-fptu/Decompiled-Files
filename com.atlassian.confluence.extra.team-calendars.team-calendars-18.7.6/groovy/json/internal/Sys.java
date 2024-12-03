/*
 * Decompiled with CFR 0.152.
 */
package groovy.json.internal;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Sys {
    private static final boolean is1_7OrLater;
    private static final boolean is1_8OrLater;
    private static final boolean is1_7;
    private static final boolean is1_8;

    Sys() {
    }

    public static boolean is1_7OrLater() {
        return is1_7OrLater;
    }

    public static boolean is1_8OrLater() {
        return is1_8OrLater;
    }

    public static boolean is1_7() {
        return is1_7;
    }

    public static boolean is1_8() {
        return is1_8;
    }

    static {
        BigDecimal v = new BigDecimal("-1");
        String sversion = System.getProperty("java.version");
        if (sversion.indexOf("_") != -1) {
            String[] split = sversion.split("_");
            try {
                String ver = split[0];
                if (ver.startsWith("1.6")) {
                    v = new BigDecimal("1.6");
                }
                if (ver.startsWith("1.7")) {
                    v = new BigDecimal("1.7");
                }
                if (ver.startsWith("1.8")) {
                    v = new BigDecimal("1.8");
                }
                if (ver.startsWith("1.9")) {
                    v = new BigDecimal("1.9");
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
                System.err.println("Unable to determine build number or version");
            }
        } else if ("1.8.0".equals(sversion)) {
            v = new BigDecimal("1.8");
        } else {
            Pattern p = Pattern.compile("^([1-9]\\.[0-9]+)");
            Matcher matcher = p.matcher(sversion);
            if (matcher.find()) {
                v = new BigDecimal(matcher.group(0));
            }
        }
        is1_7OrLater = v.compareTo(new BigDecimal("1.7")) >= 0;
        is1_8OrLater = v.compareTo(new BigDecimal("1.8")) >= 0;
        is1_7 = v.compareTo(new BigDecimal("1.7")) == 0;
        is1_8 = v.compareTo(new BigDecimal("1.8")) == 0;
    }
}

