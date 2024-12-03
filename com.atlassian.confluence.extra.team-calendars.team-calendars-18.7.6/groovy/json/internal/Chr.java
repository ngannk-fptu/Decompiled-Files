/*
 * Decompiled with CFR 0.152.
 */
package groovy.json.internal;

import groovy.json.internal.CharBuf;
import groovy.json.internal.Exceptions;

public class Chr {
    public static char[] array(char ... array) {
        return array;
    }

    public static char[] chars(String array) {
        return array.toCharArray();
    }

    public static boolean in(char value, char[] array) {
        for (char currentValue : array) {
            if (currentValue != value) continue;
            return true;
        }
        return false;
    }

    public static boolean in(int value, char[] array) {
        for (char currentValue : array) {
            if (currentValue != value) continue;
            return true;
        }
        return false;
    }

    public static boolean in(char value, int offset, char[] array) {
        for (int index = offset; index < array.length; ++index) {
            char currentValue = array[index];
            if (currentValue != value) continue;
            return true;
        }
        return false;
    }

    public static boolean in(char value, int offset, int end, char[] array) {
        for (int index = offset; index < end; ++index) {
            char currentValue = array[index];
            if (currentValue != value) continue;
            return true;
        }
        return false;
    }

    public static char[] grow(char[] array, int size) {
        char[] newArray = new char[array.length + size];
        Chr.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }

    public static char[] grow(char[] array) {
        char[] newArray = new char[array.length * 2];
        Chr.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }

    public static char[] copy(char[] array) {
        char[] newArray = new char[array.length];
        Chr.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }

    public static char[] copy(char[] array, int offset, int length) {
        char[] newArray = new char[length];
        Chr.arraycopy(array, offset, newArray, 0, length);
        return newArray;
    }

    public static char[] add(char[] array, char v) {
        char[] newArray = new char[array.length + 1];
        Chr.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = v;
        return newArray;
    }

    public static char[] add(char[] array, String str) {
        return Chr.add(array, str.toCharArray());
    }

    public static char[] add(char[] array, StringBuilder stringBuilder) {
        return Chr.add(array, Chr.getCharsFromStringBuilder(stringBuilder));
    }

    public static char[] add(char[] array, char[] array2) {
        char[] newArray = new char[array.length + array2.length];
        Chr.arraycopy(array, 0, newArray, 0, array.length);
        Chr.arraycopy(array2, 0, newArray, array.length, array2.length);
        return newArray;
    }

    private static char[] getCharsFromStringBuilder(StringBuilder sbuf) {
        int length = sbuf.length();
        char[] array2 = new char[length];
        sbuf.getChars(0, length, array2, 0);
        return array2;
    }

    public static char[] lpad(char[] in, int size, char pad) {
        int index;
        if (in.length >= size) {
            return in;
        }
        int delta = size - in.length;
        char[] newArray = new char[size];
        for (index = 0; index < delta; ++index) {
            newArray[index] = pad;
        }
        for (int index2 = 0; index2 < in.length; ++index2) {
            newArray[index] = in[index2];
            ++index;
        }
        return newArray;
    }

    public static boolean contains(char[] chars, char c, int start, int length) {
        int to = length + start;
        for (int index = start; index < to; ++index) {
            char ch = chars[index];
            if (ch != c) continue;
            return true;
        }
        return false;
    }

    public static void _idx(char[] buffer, int location, byte[] chars) {
        int index2 = 0;
        int endLocation = location + chars.length;
        int index = location;
        while (index < endLocation) {
            buffer[index] = (char)chars[index2];
            ++index;
            ++index2;
        }
    }

    public static void _idx(char[] array, int startIndex, char[] input) {
        try {
            Chr.arraycopy(input, 0, array, startIndex, input.length);
        }
        catch (Exception ex) {
            Exceptions.handle(String.format("array size %d, startIndex %d, input length %d", array.length, startIndex, input.length), (Throwable)ex);
        }
    }

    private static void arraycopy(char[] src, int srcPos, char[] dest, int destPos, int length) {
        System.arraycopy(src, srcPos, dest, destPos, length);
    }

    public static void _idx(char[] array, int startIndex, char[] input, int inputLength) {
        try {
            Chr.arraycopy(input, 0, array, startIndex, inputLength);
        }
        catch (Exception ex) {
            Exceptions.handle(String.format("array size %d, startIndex %d, input length %d", array.length, startIndex, input.length), (Throwable)ex);
        }
    }

    public static void _idx(char[] buffer, int location, byte[] chars, int start, int end) {
        int index2 = start;
        int endLocation = location + (end - start);
        int index = location;
        while (index < endLocation) {
            buffer[index] = (char)chars[index2];
            ++index;
            ++index2;
        }
    }

    public static char[] add(char[] ... strings) {
        int length = 0;
        for (char[] str : strings) {
            if (str == null) continue;
            length += str.length;
        }
        CharBuf builder = CharBuf.createExact(length);
        for (char[] str : strings) {
            if (str == null) continue;
            builder.add(str);
        }
        return builder.toCharArray();
    }
}

