/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core._Java8;
import freemarker.log.Logger;
import freemarker.template.Version;
import freemarker.template.utility.SecurityUtilities;

public final class _JavaVersions {
    private static final boolean IS_AT_LEAST_8;
    public static final _Java8 JAVA_8;

    private _JavaVersions() {
    }

    static {
        _Java8 java8;
        boolean result = false;
        String vStr = SecurityUtilities.getSystemProperty("java.version", null);
        if (vStr != null) {
            try {
                Version v = new Version(vStr);
                result = v.getMajor() == 1 && v.getMinor() >= 8 || v.getMajor() > 1;
            }
            catch (Exception exception) {}
        } else {
            try {
                Class.forName("java.time.Instant");
                result = true;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        IS_AT_LEAST_8 = result;
        if (IS_AT_LEAST_8) {
            try {
                java8 = (_Java8)Class.forName("freemarker.core._Java8Impl").getField("INSTANCE").get(null);
            }
            catch (Exception e) {
                try {
                    Logger.getLogger("freemarker.runtime").error("Failed to access Java 8 functionality", e);
                }
                catch (Exception exception) {
                    // empty catch block
                }
                java8 = null;
            }
        } else {
            java8 = null;
        }
        JAVA_8 = java8;
    }
}

