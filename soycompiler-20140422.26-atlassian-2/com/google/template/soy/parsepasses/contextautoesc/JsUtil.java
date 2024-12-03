/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.google.template.soy.parsepasses.contextautoesc;

import com.google.common.collect.ImmutableSet;
import java.util.Set;

final class JsUtil {
    private static final Set<String> REGEX_PRECEDER_KEYWORDS = ImmutableSet.of((Object)"break", (Object)"case", (Object)"continue", (Object)"delete", (Object)"do", (Object)"else", (Object[])new String[]{"finally", "instanceof", "return", "throw", "try", "typeof"});

    public static boolean isRegexPreceder(String jsTokens) {
        int wordStart;
        int jsTokensLen = jsTokens.length();
        char lastChar = jsTokens.charAt(jsTokensLen - 1);
        switch (lastChar) {
            case '#': 
            case '%': 
            case '&': 
            case '(': 
            case '*': 
            case ',': 
            case ':': 
            case ';': 
            case '<': 
            case '=': 
            case '>': 
            case '?': 
            case '[': 
            case '^': 
            case '{': 
            case '|': 
            case '}': 
            case '~': {
                return true;
            }
            case '+': 
            case '-': {
                int signStart;
                for (signStart = jsTokensLen - 1; signStart > 0 && jsTokens.charAt(signStart - 1) == lastChar; --signStart) {
                }
                int numAdjacent = jsTokensLen - signStart;
                return (numAdjacent & 1) == 1;
            }
            case '.': {
                if (jsTokensLen == 1) {
                    return true;
                }
                char ch = jsTokens.charAt(jsTokensLen - 2);
                return '0' > ch || ch > '9';
            }
        }
        for (wordStart = jsTokensLen; wordStart > 0 && Character.isJavaIdentifierPart(jsTokens.charAt(wordStart - 1)); --wordStart) {
        }
        return REGEX_PRECEDER_KEYWORDS.contains(jsTokens.substring(wordStart));
    }

    private JsUtil() {
    }
}

