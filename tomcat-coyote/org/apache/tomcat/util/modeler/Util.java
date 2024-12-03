/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.modeler;

public class Util {
    private Util() {
    }

    public static boolean objectNameValueNeedsQuote(String input) {
        for (int i = 0; i < input.length(); ++i) {
            char ch = input.charAt(i);
            if (ch != ',' && ch != '=' && ch != ':' && ch != '*' && ch != '?') continue;
            return true;
        }
        return false;
    }
}

