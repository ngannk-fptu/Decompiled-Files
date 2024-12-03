/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.lang;

import com.mchange.v1.util.StringTokenizerUtils;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;

public final class VersionUtils {
    private static final MLogger logger;
    private static final int[] DFLT_VERSION_ARRAY;
    private static final int[] JDK_VERSION_ARRAY;
    private static final int JDK_VERSION;
    private static final Integer NUM_BITS;

    public static Integer jvmNumberOfBits() {
        return NUM_BITS;
    }

    public static boolean isJavaVersion1_0() {
        return JDK_VERSION == 0;
    }

    public static boolean isJavaVersion1_1() {
        return JDK_VERSION == 1;
    }

    public static boolean isJavaVersion1_2() {
        return JDK_VERSION == 2;
    }

    public static boolean isJavaVersion1_3() {
        return JDK_VERSION == 3;
    }

    public static boolean isJavaVersion1_4() {
        return JDK_VERSION == 4;
    }

    public static boolean isJavaVersion1_5() {
        return JDK_VERSION == 5;
    }

    public static boolean isJavaVersion1_6() {
        return JDK_VERSION == 6;
    }

    public static boolean isJavaVersion1_7() {
        return JDK_VERSION == 7;
    }

    public static boolean isJavaVersion1_8() {
        return JDK_VERSION == 8;
    }

    public static boolean isJavaVersion1_9() {
        return JDK_VERSION == 9;
    }

    public static boolean isJava5() {
        return JDK_VERSION == 5;
    }

    public static boolean isJava6() {
        return JDK_VERSION == 6;
    }

    public static boolean isJava7() {
        return JDK_VERSION == 7;
    }

    public static boolean isJava8() {
        return JDK_VERSION == 8;
    }

    public static boolean isJava9() {
        return JDK_VERSION == 9;
    }

    public static boolean isJava10() {
        return JDK_VERSION == 10;
    }

    public static boolean isJava11() {
        return JDK_VERSION == 11;
    }

    public static boolean isJava12() {
        return JDK_VERSION == 12;
    }

    public static boolean isJava13() {
        return JDK_VERSION == 13;
    }

    public static boolean isAtLeastJavaVersion1_0() {
        return JDK_VERSION >= 0;
    }

    public static boolean isAtLeastJavaVersion1_1() {
        return JDK_VERSION >= 1;
    }

    public static boolean isAtLeastJavaVersion1_2() {
        return JDK_VERSION >= 2;
    }

    public static boolean isAtLeastJavaVersion1_3() {
        return JDK_VERSION >= 3;
    }

    public static boolean isAtLeastJavaVersion1_4() {
        return JDK_VERSION >= 4;
    }

    public static boolean isAtLeastJavaVersion1_5() {
        return JDK_VERSION >= 5;
    }

    public static boolean isAtLeastJavaVersion1_6() {
        return JDK_VERSION >= 6;
    }

    public static boolean isAtLeastJavaVersion1_7() {
        return JDK_VERSION >= 7;
    }

    public static boolean isAtLeastJavaVersion1_8() {
        return JDK_VERSION >= 8;
    }

    public static boolean isAtLeastJavaVersion1_9() {
        return JDK_VERSION >= 9;
    }

    public static boolean isAtLeastJava5() {
        return JDK_VERSION >= 5;
    }

    public static boolean isAtLeastJava6() {
        return JDK_VERSION >= 6;
    }

    public static boolean isAtLeastJava7() {
        return JDK_VERSION >= 7;
    }

    public static boolean isAtLeastJava8() {
        return JDK_VERSION >= 8;
    }

    public static boolean isAtLeastJava9() {
        return JDK_VERSION >= 9;
    }

    public static boolean isAtLeastJava10() {
        return JDK_VERSION >= 10;
    }

    public static boolean isAtLeastJava11() {
        return JDK_VERSION >= 11;
    }

    public static boolean isAtLeastJava12() {
        return JDK_VERSION >= 12;
    }

    public static boolean isAtLeastJava13() {
        return JDK_VERSION >= 13;
    }

    public static boolean isJavaVersion10() {
        return JDK_VERSION == 0;
    }

    public static boolean isJavaVersion11() {
        return JDK_VERSION == 1;
    }

    public static boolean isJavaVersion12() {
        return JDK_VERSION == 2;
    }

    public static boolean isJavaVersion13() {
        return JDK_VERSION == 3;
    }

    public static boolean isJavaVersion14() {
        return JDK_VERSION == 4;
    }

    public static boolean isJavaVersion15() {
        return JDK_VERSION == 5;
    }

    public static boolean isAtLeastJavaVersion10() {
        return JDK_VERSION >= 0;
    }

    public static boolean isAtLeastJavaVersion11() {
        return JDK_VERSION >= 1;
    }

    public static boolean isAtLeastJavaVersion12() {
        return JDK_VERSION >= 2;
    }

    public static boolean isAtLeastJavaVersion13() {
        return JDK_VERSION >= 3;
    }

    public static boolean isAtLeastJavaVersion14() {
        return JDK_VERSION >= 4;
    }

    public static boolean isAtLeastJavaVersion15() {
        return JDK_VERSION >= 5;
    }

    public static boolean isAtLeastJavaVersion16() {
        return JDK_VERSION >= 6;
    }

    public static boolean isAtLeastJavaVersion17() {
        return JDK_VERSION >= 7;
    }

    public static int[] extractVersionNumberArray(String string) throws NumberFormatException {
        return VersionUtils.extractVersionNumberArray(string, string.split("\\D+"));
    }

    public static int[] extractVersionNumberArray(String string, String string2) throws NumberFormatException {
        String[] stringArray = StringTokenizerUtils.tokenizeToArray(string, string2, false);
        return VersionUtils.extractVersionNumberArray(string, stringArray);
    }

    private static int[] extractVersionNumberArray(String string, String[] stringArray) throws NumberFormatException {
        int n = stringArray.length;
        int[] nArray = new int[n];
        for (int i = 0; i < n; ++i) {
            try {
                nArray[i] = Integer.parseInt(stringArray[i]);
                continue;
            }
            catch (NumberFormatException numberFormatException) {
                if (i == 0 || i == 1 && nArray[0] < 5) {
                    throw numberFormatException;
                }
                if (logger.isLoggable(MLevel.INFO)) {
                    logger.log(MLevel.INFO, "JVM version string (" + string + ") contains non-integral component (" + stringArray[i] + "). Using precending components only to resolve JVM version.");
                }
                int[] nArray2 = new int[i];
                System.arraycopy(nArray, 0, nArray2, 0, i);
                nArray = nArray2;
                break;
            }
        }
        return nArray;
    }

    static {
        Integer n;
        int n2;
        int[] nArray;
        logger = MLog.getLogger(VersionUtils.class);
        DFLT_VERSION_ARRAY = new int[]{1, 1};
        String string = System.getProperty("java.version");
        if (string == null) {
            if (logger.isLoggable(MLevel.WARNING)) {
                logger.warning("Could not find java.version System property. Defaulting to JDK 1.1");
            }
            nArray = DFLT_VERSION_ARRAY;
        } else {
            try {
                nArray = VersionUtils.extractVersionNumberArray(string);
            }
            catch (NumberFormatException numberFormatException) {
                if (logger.isLoggable(MLevel.WARNING)) {
                    logger.warning("java.version \"" + string + "\" could not be parsed. Defaulting to JDK 1.1.");
                }
                nArray = DFLT_VERSION_ARRAY;
            }
        }
        if (nArray.length == 0) {
            if (logger.isLoggable(MLevel.WARNING)) {
                logger.warning("java.version \"" + string + "\" is prefixed by no integral elements. Defaulting to JDK 1.1.");
            }
            nArray = DFLT_VERSION_ARRAY;
        }
        if (nArray[0] > 1) {
            n2 = nArray[0];
        } else if (nArray[0] == 1) {
            if (nArray.length > 1) {
                n2 = nArray[1];
            } else {
                if (logger.isLoggable(MLevel.WARNING)) {
                    logger.warning("java.version \"" + string + "\" looks like a 1.x style bargain, but the second element cannot be parsed. Defaulting to JDK 1.1.");
                }
                n2 = 1;
            }
        } else {
            if (logger.isLoggable(MLevel.WARNING)) {
                logger.warning("Illegal java.version \"" + string + "\". Defaulting to JDK 1.1.");
            }
            n2 = 1;
        }
        JDK_VERSION_ARRAY = nArray;
        JDK_VERSION = n2;
        try {
            String string2 = System.getProperty("sun.arch.data.model");
            n = string2 == null ? null : new Integer(string2);
        }
        catch (Exception exception) {
            n = null;
        }
        if (n == null || n == 32 || n == 64) {
            NUM_BITS = n;
        } else {
            if (logger.isLoggable(MLevel.WARNING)) {
                logger.warning("Determined a surprising jvmNumerOfBits: " + n + ". Setting jvmNumberOfBits to unknown (null).");
            }
            NUM_BITS = null;
        }
    }
}

