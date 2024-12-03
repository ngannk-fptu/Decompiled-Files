/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 */
package org.apache.commons.text;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class WordUtils {
    public static String abbreviate(String str, int lower, int upper, String appendToEnd) {
        Validate.isTrue((upper >= -1 ? 1 : 0) != 0, (String)"upper value cannot be less than -1", (Object[])new Object[0]);
        Validate.isTrue((upper >= lower || upper == -1 ? 1 : 0) != 0, (String)"upper value is less than lower value", (Object[])new Object[0]);
        if (StringUtils.isEmpty((CharSequence)str)) {
            return str;
        }
        if (lower > str.length()) {
            lower = str.length();
        }
        if (upper == -1 || upper > str.length()) {
            upper = str.length();
        }
        StringBuilder result = new StringBuilder();
        int index = StringUtils.indexOf((CharSequence)str, (CharSequence)" ", (int)lower);
        if (index == -1) {
            result.append(str, 0, upper);
            if (upper != str.length()) {
                result.append(StringUtils.defaultString((String)appendToEnd));
            }
        } else {
            result.append(str, 0, Math.min(index, upper));
            result.append(StringUtils.defaultString((String)appendToEnd));
        }
        return result.toString();
    }

    public static String capitalize(String str) {
        return WordUtils.capitalize(str, null);
    }

    public static String capitalize(String str, char ... delimiters) {
        if (StringUtils.isEmpty((CharSequence)str)) {
            return str;
        }
        Set<Integer> delimiterSet = WordUtils.generateDelimiterSet(delimiters);
        int strLen = str.length();
        int[] newCodePoints = new int[strLen];
        int outOffset = 0;
        boolean capitalizeNext = true;
        int index = 0;
        while (index < strLen) {
            int codePoint = str.codePointAt(index);
            if (delimiterSet.contains(codePoint)) {
                capitalizeNext = true;
                newCodePoints[outOffset++] = codePoint;
                index += Character.charCount(codePoint);
                continue;
            }
            if (capitalizeNext) {
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

    public static String capitalizeFully(String str) {
        return WordUtils.capitalizeFully(str, null);
    }

    public static String capitalizeFully(String str, char ... delimiters) {
        if (StringUtils.isEmpty((CharSequence)str)) {
            return str;
        }
        str = str.toLowerCase();
        return WordUtils.capitalize(str, delimiters);
    }

    public static boolean containsAllWords(CharSequence word, CharSequence ... words) {
        if (StringUtils.isEmpty((CharSequence)word) || ArrayUtils.isEmpty((Object[])words)) {
            return false;
        }
        for (CharSequence w : words) {
            if (StringUtils.isBlank((CharSequence)w)) {
                return false;
            }
            Pattern p = Pattern.compile(".*\\b" + w + "\\b.*");
            if (p.matcher(word).matches()) continue;
            return false;
        }
        return true;
    }

    private static Set<Integer> generateDelimiterSet(char[] delimiters) {
        HashSet<Integer> delimiterHashSet = new HashSet<Integer>();
        if (delimiters == null || delimiters.length == 0) {
            if (delimiters == null) {
                delimiterHashSet.add(Character.codePointAt(new char[]{' '}, 0));
            }
            return delimiterHashSet;
        }
        for (int index = 0; index < delimiters.length; ++index) {
            delimiterHashSet.add(Character.codePointAt(delimiters, index));
        }
        return delimiterHashSet;
    }

    public static String initials(String str) {
        return WordUtils.initials(str, null);
    }

    public static String initials(String str, char ... delimiters) {
        int codePoint;
        if (StringUtils.isEmpty((CharSequence)str)) {
            return str;
        }
        if (delimiters != null && delimiters.length == 0) {
            return "";
        }
        Set<Integer> delimiterSet = WordUtils.generateDelimiterSet(delimiters);
        int strLen = str.length();
        int[] newCodePoints = new int[strLen / 2 + 1];
        int count = 0;
        boolean lastWasGap = true;
        for (int i = 0; i < strLen; i += Character.charCount(codePoint)) {
            codePoint = str.codePointAt(i);
            if (delimiterSet.contains(codePoint) || delimiters == null && Character.isWhitespace(codePoint)) {
                lastWasGap = true;
                continue;
            }
            if (!lastWasGap) continue;
            newCodePoints[count++] = codePoint;
            lastWasGap = false;
        }
        return new String(newCodePoints, 0, count);
    }

    @Deprecated
    public static boolean isDelimiter(char ch, char[] delimiters) {
        if (delimiters == null) {
            return Character.isWhitespace(ch);
        }
        for (char delimiter : delimiters) {
            if (ch != delimiter) continue;
            return true;
        }
        return false;
    }

    @Deprecated
    public static boolean isDelimiter(int codePoint, char[] delimiters) {
        if (delimiters == null) {
            return Character.isWhitespace(codePoint);
        }
        for (int index = 0; index < delimiters.length; ++index) {
            int delimiterCodePoint = Character.codePointAt(delimiters, index);
            if (delimiterCodePoint != codePoint) continue;
            return true;
        }
        return false;
    }

    public static String swapCase(String str) {
        int newCodePoint;
        if (StringUtils.isEmpty((CharSequence)str)) {
            return str;
        }
        int strLen = str.length();
        int[] newCodePoints = new int[strLen];
        int outOffset = 0;
        boolean whitespace = true;
        for (int index = 0; index < strLen; index += Character.charCount(newCodePoint)) {
            int oldCodepoint = str.codePointAt(index);
            if (Character.isUpperCase(oldCodepoint) || Character.isTitleCase(oldCodepoint)) {
                newCodePoint = Character.toLowerCase(oldCodepoint);
                whitespace = false;
            } else if (Character.isLowerCase(oldCodepoint)) {
                if (whitespace) {
                    newCodePoint = Character.toTitleCase(oldCodepoint);
                    whitespace = false;
                } else {
                    newCodePoint = Character.toUpperCase(oldCodepoint);
                }
            } else {
                whitespace = Character.isWhitespace(oldCodepoint);
                newCodePoint = oldCodepoint;
            }
            newCodePoints[outOffset++] = newCodePoint;
        }
        return new String(newCodePoints, 0, outOffset);
    }

    public static String uncapitalize(String str) {
        return WordUtils.uncapitalize(str, null);
    }

    public static String uncapitalize(String str, char ... delimiters) {
        if (StringUtils.isEmpty((CharSequence)str)) {
            return str;
        }
        Set<Integer> delimiterSet = WordUtils.generateDelimiterSet(delimiters);
        int strLen = str.length();
        int[] newCodePoints = new int[strLen];
        int outOffset = 0;
        boolean uncapitalizeNext = true;
        int index = 0;
        while (index < strLen) {
            int codePoint = str.codePointAt(index);
            if (delimiterSet.contains(codePoint)) {
                uncapitalizeNext = true;
                newCodePoints[outOffset++] = codePoint;
                index += Character.charCount(codePoint);
                continue;
            }
            if (uncapitalizeNext) {
                int titleCaseCodePoint = Character.toLowerCase(codePoint);
                newCodePoints[outOffset++] = titleCaseCodePoint;
                index += Character.charCount(titleCaseCodePoint);
                uncapitalizeNext = false;
                continue;
            }
            newCodePoints[outOffset++] = codePoint;
            index += Character.charCount(codePoint);
        }
        return new String(newCodePoints, 0, outOffset);
    }

    public static String wrap(String str, int wrapLength) {
        return WordUtils.wrap(str, wrapLength, null, false);
    }

    public static String wrap(String str, int wrapLength, String newLineStr, boolean wrapLongWords) {
        return WordUtils.wrap(str, wrapLength, newLineStr, wrapLongWords, " ");
    }

    public static String wrap(String str, int wrapLength, String newLineStr, boolean wrapLongWords, String wrapOn) {
        if (str == null) {
            return null;
        }
        if (newLineStr == null) {
            newLineStr = System.lineSeparator();
        }
        if (wrapLength < 1) {
            wrapLength = 1;
        }
        if (StringUtils.isBlank((CharSequence)wrapOn)) {
            wrapOn = " ";
        }
        Pattern patternToWrapOn = Pattern.compile(wrapOn);
        int inputLineLength = str.length();
        int offset = 0;
        StringBuilder wrappedLine = new StringBuilder(inputLineLength + 32);
        int matcherSize = -1;
        while (offset < inputLineLength) {
            int spaceToWrapAt = -1;
            Matcher matcher = patternToWrapOn.matcher(str.substring(offset, Math.min((int)Math.min(Integer.MAX_VALUE, (long)(offset + wrapLength) + 1L), inputLineLength)));
            if (matcher.find()) {
                if (matcher.start() == 0) {
                    matcherSize = matcher.end();
                    if (matcherSize != 0) {
                        offset += matcher.end();
                        continue;
                    }
                    ++offset;
                }
                spaceToWrapAt = matcher.start() + offset;
            }
            if (inputLineLength - offset <= wrapLength) break;
            while (matcher.find()) {
                spaceToWrapAt = matcher.start() + offset;
            }
            if (spaceToWrapAt >= offset) {
                wrappedLine.append(str, offset, spaceToWrapAt);
                wrappedLine.append(newLineStr);
                offset = spaceToWrapAt + 1;
                continue;
            }
            if (wrapLongWords) {
                if (matcherSize == 0) {
                    --offset;
                }
                wrappedLine.append(str, offset, wrapLength + offset);
                wrappedLine.append(newLineStr);
                offset += wrapLength;
                matcherSize = -1;
                continue;
            }
            matcher = patternToWrapOn.matcher(str.substring(offset + wrapLength));
            if (matcher.find()) {
                matcherSize = matcher.end() - matcher.start();
                spaceToWrapAt = matcher.start() + offset + wrapLength;
            }
            if (spaceToWrapAt >= 0) {
                if (matcherSize == 0 && offset != 0) {
                    --offset;
                }
                wrappedLine.append(str, offset, spaceToWrapAt);
                wrappedLine.append(newLineStr);
                offset = spaceToWrapAt + 1;
                continue;
            }
            if (matcherSize == 0 && offset != 0) {
                --offset;
            }
            wrappedLine.append(str, offset, str.length());
            offset = inputLineLength;
            matcherSize = -1;
        }
        if (matcherSize == 0 && offset < inputLineLength) {
            --offset;
        }
        wrappedLine.append(str, offset, str.length());
        return wrappedLine.toString();
    }
}

