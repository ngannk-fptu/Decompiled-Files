/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Splitter
 */
package com.atlassian.crowd.directory.ldap.util;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;

public class GuidHelper {
    public static final String BS = "\\";

    public static String getGUIDAsString(byte[] inArr) {
        StringBuffer guid = new StringBuffer();
        for (int i = 0; i < inArr.length; ++i) {
            StringBuffer dblByte = new StringBuffer(Integer.toHexString(inArr[i] & 0xFF));
            if (dblByte.length() == 1) {
                guid.append("0");
            }
            guid.append(dblByte);
        }
        return guid.toString();
    }

    public static String convertToADGUID(String guid) {
        return GuidHelper.reverse(guid.substring(0, 8)) + "-" + GuidHelper.reverse(guid.substring(8, 12)) + "-" + GuidHelper.reverse(guid.substring(12, 16)) + "-" + guid.substring(16, 20) + "-" + guid.substring(20, 32);
    }

    private static String reverse(String original) {
        StringBuffer result = new StringBuffer();
        for (int i = original.length() - 2; i >= 0; i -= 2) {
            result.append(original.substring(i, i + 2));
        }
        return result.toString();
    }

    public static String encodeGUIDForSearch(String guid) {
        Preconditions.checkArgument((guid.length() == 32 ? 1 : 0) != 0, (Object)"guid should be of length 32 (as encoded by getGUIDAsString)");
        StringBuilder sb = new StringBuilder(48);
        sb.append(BS);
        Iterable split = Splitter.fixedLength((int)2).split((CharSequence)guid);
        return Joiner.on((String)BS).appendTo(sb, split).toString();
    }
}

