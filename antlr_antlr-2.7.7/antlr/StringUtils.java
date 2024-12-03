/*
 * Decompiled with CFR 0.152.
 */
package antlr;

public class StringUtils {
    public static String stripBack(String string, char c) {
        while (string.length() > 0 && string.charAt(string.length() - 1) == c) {
            string = string.substring(0, string.length() - 1);
        }
        return string;
    }

    public static String stripBack(String string, String string2) {
        boolean bl;
        do {
            bl = false;
            for (int i = 0; i < string2.length(); ++i) {
                char c = string2.charAt(i);
                while (string.length() > 0 && string.charAt(string.length() - 1) == c) {
                    bl = true;
                    string = string.substring(0, string.length() - 1);
                }
            }
        } while (bl);
        return string;
    }

    public static String stripFront(String string, char c) {
        while (string.length() > 0 && string.charAt(0) == c) {
            string = string.substring(1);
        }
        return string;
    }

    public static String stripFront(String string, String string2) {
        boolean bl;
        do {
            bl = false;
            for (int i = 0; i < string2.length(); ++i) {
                char c = string2.charAt(i);
                while (string.length() > 0 && string.charAt(0) == c) {
                    bl = true;
                    string = string.substring(1);
                }
            }
        } while (bl);
        return string;
    }

    public static String stripFrontBack(String string, String string2, String string3) {
        int n = string.indexOf(string2);
        int n2 = string.lastIndexOf(string3);
        if (n == -1 || n2 == -1) {
            return string;
        }
        return string.substring(n + 1, n2);
    }
}

