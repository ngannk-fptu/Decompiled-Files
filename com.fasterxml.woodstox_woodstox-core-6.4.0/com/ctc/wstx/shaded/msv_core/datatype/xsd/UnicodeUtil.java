/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

public class UnicodeUtil {
    public static int countLength(String str) {
        int len = str.length();
        int count = 0;
        for (int i = 0; i < len; ++i) {
            char ch = str.charAt(i);
            if ('\ud800' <= ch && ch < '\udc00') continue;
            ++count;
        }
        return count;
    }
}

