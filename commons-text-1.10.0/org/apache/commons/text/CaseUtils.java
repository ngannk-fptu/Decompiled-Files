/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.text;

import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class CaseUtils {
    public static String toCamelCase(String str, boolean capitalizeFirstLetter, char ... delimiters) {
        if (StringUtils.isEmpty((CharSequence)str)) {
            return str;
        }
        str = str.toLowerCase();
        int strLen = str.length();
        int[] newCodePoints = new int[strLen];
        int outOffset = 0;
        Set<Integer> delimiterSet = CaseUtils.toDelimiterSet(delimiters);
        boolean capitalizeNext = capitalizeFirstLetter;
        int index = 0;
        while (index < strLen) {
            int codePoint = str.codePointAt(index);
            if (delimiterSet.contains(codePoint)) {
                capitalizeNext = outOffset != 0;
                index += Character.charCount(codePoint);
                continue;
            }
            if (capitalizeNext || outOffset == 0 && capitalizeFirstLetter) {
                int titleCaseCodePoint = Character.toTitleCase(codePoint);
                newCodePoints[outOffset++] = titleCaseCodePoint;
                index += Character.charCount(titleCaseCodePoint);
                capitalizeNext = false;
                continue;
            }
            newCodePoints[outOffset++] = codePoint;
            index += Character.charCount(codePoint);
        }
        return new String(newCodePoints, 0, outOffset);
    }

    private static Set<Integer> toDelimiterSet(char[] delimiters) {
        HashSet<Integer> delimiterHashSet = new HashSet<Integer>();
        delimiterHashSet.add(Character.codePointAt(new char[]{' '}, 0));
        if (ArrayUtils.isEmpty((char[])delimiters)) {
            return delimiterHashSet;
        }
        for (int index = 0; index < delimiters.length; ++index) {
            delimiterHashSet.add(Character.codePointAt(delimiters, index));
        }
        return delimiterHashSet;
    }
}

