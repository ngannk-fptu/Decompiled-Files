/*
 * Decompiled with CFR 0.152.
 */
package groovy.json.internal;

public class ArrayUtils {
    public static char[] copyRange(char[] source, int startIndex, int endIndex) {
        int len = endIndex - startIndex;
        char[] copy = new char[len];
        System.arraycopy(source, startIndex, copy, 0, Math.min(source.length - startIndex, len));
        return copy;
    }
}

