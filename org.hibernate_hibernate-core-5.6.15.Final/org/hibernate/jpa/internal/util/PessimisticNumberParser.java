/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jpa.internal.util;

public final class PessimisticNumberParser {
    private PessimisticNumberParser() {
    }

    public static Integer toNumberOrNull(String parameterName) {
        if (PessimisticNumberParser.isValidNumber(parameterName)) {
            try {
                return Integer.valueOf(parameterName);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return null;
    }

    private static boolean isValidNumber(String parameterName) {
        if (parameterName.length() == 0) {
            return false;
        }
        char firstDigit = parameterName.charAt(0);
        if (Character.isDigit(firstDigit) || '-' == firstDigit || '+' == firstDigit) {
            for (int i = 1; i < parameterName.length(); ++i) {
                if (Character.isDigit(parameterName.charAt(i))) continue;
                return false;
            }
            return true;
        }
        return false;
    }
}

