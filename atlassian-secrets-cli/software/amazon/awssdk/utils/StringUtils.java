/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils;

import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.Locale;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public final class StringUtils {
    private static final String EMPTY = "";

    private StringUtils() {
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isBlank(CharSequence cs) {
        if (cs == null || cs.length() == 0) {
            return true;
        }
        for (int i = 0; i < cs.length(); ++i) {
            if (Character.isWhitespace(cs.charAt(i))) continue;
            return false;
        }
        return true;
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !StringUtils.isBlank(cs);
    }

    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    public static String trimToNull(String str) {
        String ts = StringUtils.trim(str);
        return StringUtils.isEmpty(ts) ? null : ts;
    }

    public static String trimToEmpty(String str) {
        return str == null ? EMPTY : str.trim();
    }

    public static boolean equals(String cs1, String cs2) {
        if (cs1 == null || cs2 == null) {
            return false;
        }
        if (cs1.length() != cs2.length()) {
            return false;
        }
        return cs1.equals(cs2);
    }

    public static String substring(String str, int start) {
        if (str == null) {
            return null;
        }
        if (start < 0) {
            start = str.length() + start;
        }
        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return EMPTY;
        }
        return str.substring(start);
    }

    public static String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        }
        if (end < 0) {
            end = str.length() + end;
        }
        if (start < 0) {
            start = str.length() + start;
        }
        if (end > str.length()) {
            end = str.length();
        }
        if (start > end) {
            return EMPTY;
        }
        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }
        return str.substring(start, end);
    }

    public static String upperCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase(Locale.ENGLISH);
    }

    public static String lowerCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase(Locale.ENGLISH);
    }

    public static String capitalize(String str) {
        int codepoint;
        int newCodePoint;
        if (str == null || str.length() == 0) {
            return str;
        }
        int firstCodepoint = str.codePointAt(0);
        if (firstCodepoint == (newCodePoint = Character.toTitleCase(firstCodepoint))) {
            return str;
        }
        int[] newCodePoints = new int[str.length()];
        int outOffset = 0;
        newCodePoints[outOffset++] = newCodePoint;
        for (int inOffset = Character.charCount(firstCodepoint); inOffset < str.length(); inOffset += Character.charCount(codepoint)) {
            codepoint = str.codePointAt(inOffset);
            newCodePoints[outOffset++] = codepoint;
        }
        return new String(newCodePoints, 0, outOffset);
    }

    public static String uncapitalize(String str) {
        int codepoint;
        int newCodePoint;
        if (str == null || str.length() == 0) {
            return str;
        }
        int firstCodepoint = str.codePointAt(0);
        if (firstCodepoint == (newCodePoint = Character.toLowerCase(firstCodepoint))) {
            return str;
        }
        int[] newCodePoints = new int[str.length()];
        int outOffset = 0;
        newCodePoints[outOffset++] = newCodePoint;
        for (int inOffset = Character.charCount(firstCodepoint); inOffset < str.length(); inOffset += Character.charCount(codepoint)) {
            codepoint = str.codePointAt(inOffset);
            newCodePoints[outOffset++] = codepoint;
        }
        return new String(newCodePoints, 0, outOffset);
    }

    public static String fromBytes(byte[] bytes, Charset charset) throws UncheckedIOException {
        try {
            return charset.newDecoder().decode(ByteBuffer.wrap(bytes)).toString();
        }
        catch (CharacterCodingException e) {
            throw new UncheckedIOException("Cannot encode string.", e);
        }
    }

    public static boolean startsWithIgnoreCase(String str, String prefix) {
        return str.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    public static String replaceOnce(String text, String searchString, String replacement) {
        return StringUtils.replace(text, searchString, replacement, 1);
    }

    public static String replace(String text, String searchString, String replacement) {
        return StringUtils.replace(text, searchString, replacement, -1);
    }

    private static String replace(String text, String searchString, String replacement, int max) {
        if (StringUtils.isEmpty(text) || StringUtils.isEmpty(searchString) || replacement == null || max == 0) {
            return text;
        }
        int start = 0;
        int end = StringUtils.indexOf(text, searchString, start);
        if (end == -1) {
            return text;
        }
        int replLength = searchString.length();
        int increase = Math.max(replacement.length() - replLength, 0);
        StringBuilder buf = new StringBuilder(text.length() + (increase *= max < 0 ? 16 : Math.min(max, 64)));
        while (end != -1) {
            buf.append(text, start, end).append(replacement);
            start = end + replLength;
            if (--max == 0) break;
            end = StringUtils.indexOf(text, searchString, start);
        }
        buf.append(text, start, text.length());
        return buf.toString();
    }

    public static String replaceEach(String text, String[] searchList, String[] replacementList) {
        int tempIndex;
        if (StringUtils.isEmpty(text)) {
            return text;
        }
        int searchLength = searchList.length;
        int replacementLength = replacementList.length;
        if (searchLength != replacementLength) {
            throw new IllegalArgumentException("Search and Replace array lengths don't match: " + searchLength + " vs " + replacementLength);
        }
        boolean[] noMoreMatchesForReplIndex = new boolean[searchLength];
        int textIndex = -1;
        int replaceIndex = -1;
        for (int i = 0; i < searchLength; ++i) {
            if (noMoreMatchesForReplIndex[i] || StringUtils.isEmpty(searchList[i]) || replacementList[i] == null) continue;
            tempIndex = text.indexOf(searchList[i]);
            if (tempIndex == -1) {
                noMoreMatchesForReplIndex[i] = true;
                continue;
            }
            if (textIndex != -1 && tempIndex >= textIndex) continue;
            textIndex = tempIndex;
            replaceIndex = i;
        }
        if (textIndex == -1) {
            return text;
        }
        int start = 0;
        int increase = 0;
        for (int i = 0; i < searchList.length; ++i) {
            int greater;
            if (searchList[i] == null || replacementList[i] == null || (greater = replacementList[i].length() - searchList[i].length()) <= 0) continue;
            increase += 3 * greater;
        }
        increase = Math.min(increase, text.length() / 5);
        StringBuilder buf = new StringBuilder(text.length() + increase);
        while (textIndex != -1) {
            int i;
            for (i = start; i < textIndex; ++i) {
                buf.append(text.charAt(i));
            }
            buf.append(replacementList[replaceIndex]);
            start = textIndex + searchList[replaceIndex].length();
            textIndex = -1;
            replaceIndex = -1;
            for (i = 0; i < searchLength; ++i) {
                if (noMoreMatchesForReplIndex[i] || searchList[i] == null || searchList[i].isEmpty() || replacementList[i] == null) continue;
                tempIndex = text.indexOf(searchList[i], start);
                if (tempIndex == -1) {
                    noMoreMatchesForReplIndex[i] = true;
                    continue;
                }
                if (textIndex != -1 && tempIndex >= textIndex) continue;
                textIndex = tempIndex;
                replaceIndex = i;
            }
        }
        int textLength = text.length();
        for (int i = start; i < textLength; ++i) {
            buf.append(text.charAt(i));
        }
        return buf.toString();
    }

    private static int indexOf(String seq, String searchSeq, int start) {
        if (seq == null || searchSeq == null) {
            return -1;
        }
        return seq.indexOf(searchSeq, start);
    }

    public static String replacePrefixIgnoreCase(String str, String prefix, String replacement) {
        return str.replaceFirst("(?i)" + prefix, replacement);
    }

    public static Character findFirstOccurrence(String s, char ... charsToMatch) {
        int lowestIndex = Integer.MAX_VALUE;
        for (char toMatch : charsToMatch) {
            int currentIndex = s.indexOf(toMatch);
            if (currentIndex == -1 || currentIndex >= lowestIndex) continue;
            lowestIndex = currentIndex;
        }
        return lowestIndex == Integer.MAX_VALUE ? null : Character.valueOf(s.charAt(lowestIndex));
    }

    public static boolean safeStringToBoolean(String value) {
        if (value.equalsIgnoreCase("true")) {
            return true;
        }
        if (value.equalsIgnoreCase("false")) {
            return false;
        }
        throw new IllegalArgumentException("Value was defined as '" + value + "', but should be 'false' or 'true'");
    }

    public static String repeat(String value, int count) {
        int copied;
        if (count < 0) {
            throw new IllegalArgumentException("count is negative: " + count);
        }
        if (value == null || value.length() == 0 || count == 1) {
            return value;
        }
        if (count == 0) {
            return EMPTY;
        }
        if (value.length() > Integer.MAX_VALUE / count) {
            throw new OutOfMemoryError("Repeating " + value.length() + " bytes String " + count + " times will produce a String exceeding maximum size.");
        }
        int len = value.length();
        int limit = len * count;
        char[] array = new char[limit];
        value.getChars(0, len, array, 0);
        for (copied = len; copied < limit - copied; copied <<= 1) {
            System.arraycopy(array, 0, array, copied, copied);
        }
        System.arraycopy(array, 0, array, copied, limit - copied);
        return new String(array);
    }
}

