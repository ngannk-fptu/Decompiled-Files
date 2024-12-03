/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.macro.params;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class MacroParamUtils {
    public static List<String> parseCommaSeparatedStrings(String str) {
        if (StringUtils.isBlank((CharSequence)str)) {
            return Collections.emptyList();
        }
        ArrayList<String> list = new ArrayList<String>();
        boolean newWord = true;
        int firstNonSpaceCharInWordIndex = 0;
        int lastNonSpaceCharInWordIndex = 0;
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            boolean isWhiteSpace = Character.isWhitespace(c);
            if (newWord && isWhiteSpace) continue;
            if (c == ',') {
                MacroParamUtils.addToListIfNotBlank(list, str, firstNonSpaceCharInWordIndex, lastNonSpaceCharInWordIndex);
                newWord = true;
                firstNonSpaceCharInWordIndex = 0;
                lastNonSpaceCharInWordIndex = 0;
                continue;
            }
            if (newWord) {
                newWord = false;
                firstNonSpaceCharInWordIndex = i;
            }
            if (isWhiteSpace) continue;
            lastNonSpaceCharInWordIndex = i;
        }
        MacroParamUtils.addToListIfNotBlank(list, str, firstNonSpaceCharInWordIndex, lastNonSpaceCharInWordIndex);
        return list;
    }

    private static void addToListIfNotBlank(List<String> list, String str, int firstNonSpaceCharInWordIndex, int lastNonSpaceCharInWordIndex) {
        if (firstNonSpaceCharInWordIndex < lastNonSpaceCharInWordIndex) {
            list.add(str.substring(firstNonSpaceCharInWordIndex, lastNonSpaceCharInWordIndex + 1));
        }
    }
}

