/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.databind.EnumNamingStrategy;

public class EnumNamingStrategies {
    private EnumNamingStrategies() {
    }

    public static class CamelCaseStrategy
    implements EnumNamingStrategy {
        public static final CamelCaseStrategy INSTANCE = new CamelCaseStrategy();

        @Override
        public String convertEnumToExternalName(String enumName) {
            if (enumName == null) {
                return null;
            }
            String UNDERSCORE = "_";
            StringBuilder out = null;
            int iterationCnt = 0;
            int lastSeparatorIdx = -1;
            do {
                if ((lastSeparatorIdx = CamelCaseStrategy.indexIn(enumName, lastSeparatorIdx + 1)) == -1) continue;
                if (iterationCnt == 0) {
                    out = new StringBuilder(enumName.length() + 4 * "_".length());
                    out.append(CamelCaseStrategy.toLowerCase(enumName.substring(iterationCnt, lastSeparatorIdx)));
                } else {
                    out.append(CamelCaseStrategy.normalizeWord(enumName.substring(iterationCnt, lastSeparatorIdx)));
                }
                iterationCnt = lastSeparatorIdx + "_".length();
            } while (lastSeparatorIdx != -1);
            if (iterationCnt == 0) {
                return CamelCaseStrategy.toLowerCase(enumName);
            }
            out.append(CamelCaseStrategy.normalizeWord(enumName.substring(iterationCnt)));
            return out.toString();
        }

        private static int indexIn(CharSequence sequence, int start) {
            int length = sequence.length();
            for (int i = start; i < length; ++i) {
                if ('_' != sequence.charAt(i)) continue;
                return i;
            }
            return -1;
        }

        private static String normalizeWord(String word) {
            int length = word.length();
            if (length == 0) {
                return word;
            }
            return new StringBuilder(length).append(CamelCaseStrategy.charToUpperCaseIfLower(word.charAt(0))).append(CamelCaseStrategy.toLowerCase(word.substring(1))).toString();
        }

        private static String toLowerCase(String string) {
            int length = string.length();
            StringBuilder builder = new StringBuilder(length);
            for (int i = 0; i < length; ++i) {
                builder.append(CamelCaseStrategy.charToLowerCaseIfUpper(string.charAt(i)));
            }
            return builder.toString();
        }

        private static char charToUpperCaseIfLower(char c) {
            return Character.isLowerCase(c) ? Character.toUpperCase(c) : c;
        }

        private static char charToLowerCaseIfUpper(char c) {
            return Character.isUpperCase(c) ? Character.toLowerCase(c) : c;
        }
    }
}

