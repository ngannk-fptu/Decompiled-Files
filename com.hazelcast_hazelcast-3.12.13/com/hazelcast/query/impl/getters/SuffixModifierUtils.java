/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.getters;

public final class SuffixModifierUtils {
    private static final char MODIFIER_OPENING_TOKEN = '[';
    private static final char MODIFIER_CLOSING_TOKEN = ']';

    private SuffixModifierUtils() {
    }

    public static String removeModifierSuffix(String fullName) {
        int indexOfFirstOpeningToken = fullName.indexOf(91);
        if (indexOfFirstOpeningToken == -1) {
            return fullName;
        }
        int indexOfSecondOpeningToken = fullName.lastIndexOf(91);
        if (indexOfSecondOpeningToken != indexOfFirstOpeningToken) {
            throw new IllegalArgumentException("Attribute name '" + fullName + "' is not valid as it contains more than one " + '[');
        }
        int indexOfFirstClosingToken = fullName.indexOf(93);
        if (indexOfFirstClosingToken != fullName.length() - 1) {
            throw new IllegalArgumentException("Attribute name '" + fullName + "' is not valid as the last character is not " + ']');
        }
        return fullName.substring(0, indexOfFirstOpeningToken);
    }

    public static String getModifierSuffix(String fullName, String baseName) {
        if (fullName.equals(baseName)) {
            return null;
        }
        int indexOfOpeningBracket = fullName.indexOf(91);
        return fullName.substring(indexOfOpeningBracket, fullName.length());
    }
}

