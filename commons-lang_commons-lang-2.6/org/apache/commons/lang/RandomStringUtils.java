/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang;

import java.util.Random;

public class RandomStringUtils {
    private static final Random RANDOM = new Random();

    public static String random(int count) {
        return RandomStringUtils.random(count, false, false);
    }

    public static String randomAscii(int count) {
        return RandomStringUtils.random(count, 32, 127, false, false);
    }

    public static String randomAlphabetic(int count) {
        return RandomStringUtils.random(count, true, false);
    }

    public static String randomAlphanumeric(int count) {
        return RandomStringUtils.random(count, true, true);
    }

    public static String randomNumeric(int count) {
        return RandomStringUtils.random(count, false, true);
    }

    public static String random(int count, boolean letters, boolean numbers) {
        return RandomStringUtils.random(count, 0, 0, letters, numbers);
    }

    public static String random(int count, int start, int end, boolean letters, boolean numbers) {
        return RandomStringUtils.random(count, start, end, letters, numbers, null, RANDOM);
    }

    public static String random(int count, int start, int end, boolean letters, boolean numbers, char[] chars) {
        return RandomStringUtils.random(count, start, end, letters, numbers, chars, RANDOM);
    }

    public static String random(int count, int start, int end, boolean letters, boolean numbers, char[] chars, Random random) {
        if (count == 0) {
            return "";
        }
        if (count < 0) {
            throw new IllegalArgumentException("Requested random string length " + count + " is less than 0.");
        }
        if (start == 0 && end == 0) {
            end = 123;
            start = 32;
            if (!letters && !numbers) {
                start = 0;
                end = Integer.MAX_VALUE;
            }
        }
        char[] buffer = new char[count];
        int gap = end - start;
        while (count-- != 0) {
            char ch = chars == null ? (char)(random.nextInt(gap) + start) : chars[random.nextInt(gap) + start];
            if (letters && Character.isLetter(ch) || numbers && Character.isDigit(ch) || !letters && !numbers) {
                if (ch >= '\udc00' && ch <= '\udfff') {
                    if (count == 0) {
                        ++count;
                        continue;
                    }
                    buffer[count] = ch;
                    buffer[--count] = (char)(55296 + random.nextInt(128));
                    continue;
                }
                if (ch >= '\ud800' && ch <= '\udb7f') {
                    if (count == 0) {
                        ++count;
                        continue;
                    }
                    buffer[count] = (char)(56320 + random.nextInt(128));
                    buffer[--count] = ch;
                    continue;
                }
                if (ch >= '\udb80' && ch <= '\udbff') {
                    ++count;
                    continue;
                }
                buffer[count] = ch;
                continue;
            }
            ++count;
        }
        return new String(buffer);
    }

    public static String random(int count, String chars) {
        if (chars == null) {
            return RandomStringUtils.random(count, 0, 0, false, false, null, RANDOM);
        }
        return RandomStringUtils.random(count, chars.toCharArray());
    }

    public static String random(int count, char[] chars) {
        if (chars == null) {
            return RandomStringUtils.random(count, 0, 0, false, false, null, RANDOM);
        }
        return RandomStringUtils.random(count, 0, chars.length, false, false, chars, RANDOM);
    }
}

