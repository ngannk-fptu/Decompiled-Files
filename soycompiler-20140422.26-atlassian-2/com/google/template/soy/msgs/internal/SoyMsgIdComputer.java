/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Charsets
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nullable
 */
package com.google.template.soy.msgs.internal;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.template.soy.msgs.internal.IcuSyntaxUtils;
import com.google.template.soy.msgs.restricted.SoyMsgPart;
import com.google.template.soy.msgs.restricted.SoyMsgPlaceholderPart;
import com.google.template.soy.msgs.restricted.SoyMsgRawTextPart;
import javax.annotation.Nullable;

public class SoyMsgIdComputer {
    private SoyMsgIdComputer() {
    }

    public static long computeMsgId(ImmutableList<SoyMsgPart> msgParts, @Nullable String meaning, @Nullable String contentType) {
        return SoyMsgIdComputer.computeMsgIdHelper(msgParts, false, meaning, contentType);
    }

    public static long computeMsgIdUsingBracedPhs(ImmutableList<SoyMsgPart> msgParts, @Nullable String meaning, @Nullable String contentType) {
        return SoyMsgIdComputer.computeMsgIdHelper(msgParts, true, meaning, contentType);
    }

    private static long computeMsgIdHelper(ImmutableList<SoyMsgPart> msgParts, boolean doUseBracedPhs, @Nullable String meaning, @Nullable String contentType) {
        String msgContentStrForMsgIdComputation = SoyMsgIdComputer.buildMsgContentStrForMsgIdComputation(msgParts, doUseBracedPhs);
        long fp = SoyMsgIdComputer.fingerprint(msgContentStrForMsgIdComputation);
        if (meaning != null) {
            fp = (fp << 1) + (long)(fp < 0L ? 1 : 0) + SoyMsgIdComputer.fingerprint(meaning);
        }
        if (contentType != null && !contentType.equals("text/html")) {
            fp = (fp << 1) + (long)(fp < 0L ? 1 : 0) + SoyMsgIdComputer.fingerprint(contentType);
        }
        return fp & Long.MAX_VALUE;
    }

    @VisibleForTesting
    static String buildMsgContentStrForMsgIdComputation(ImmutableList<SoyMsgPart> msgParts, boolean doUseBracedPhs) {
        msgParts = IcuSyntaxUtils.convertMsgPartsToEmbeddedIcuSyntax(msgParts, false);
        StringBuilder msgStrSb = new StringBuilder();
        for (SoyMsgPart msgPart : msgParts) {
            if (msgPart instanceof SoyMsgRawTextPart) {
                msgStrSb.append(((SoyMsgRawTextPart)msgPart).getRawText());
                continue;
            }
            if (msgPart instanceof SoyMsgPlaceholderPart) {
                if (doUseBracedPhs) {
                    msgStrSb.append('{');
                }
                msgStrSb.append(((SoyMsgPlaceholderPart)msgPart).getPlaceholderName());
                if (!doUseBracedPhs) continue;
                msgStrSb.append('}');
                continue;
            }
            throw new AssertionError();
        }
        return msgStrSb.toString();
    }

    @VisibleForTesting
    static long fingerprint(String str) {
        byte[] strBytes = str.getBytes(Charsets.UTF_8);
        int hi = SoyMsgIdComputer.hash32(strBytes, 0, strBytes.length, 0);
        int lo = SoyMsgIdComputer.hash32(strBytes, 0, strBytes.length, 102072);
        if (hi == 0 && (lo == 0 || lo == 1)) {
            hi ^= 0x130F9BEF;
            lo ^= 0x94A0A928;
        }
        return (long)hi << 32 | (long)lo & 0xFFFFFFFFL;
    }

    private static int hash32(byte[] str, int start, int limit, int c) {
        int a = -1640531527;
        int b = -1640531527;
        int i = start;
        while (i + 12 <= limit) {
            a += (str[i + 0] & 0xFF) << 0 | (str[i + 1] & 0xFF) << 8 | (str[i + 2] & 0xFF) << 16 | (str[i + 3] & 0xFF) << 24;
            a -= (b += (str[i + 4] & 0xFF) << 0 | (str[i + 5] & 0xFF) << 8 | (str[i + 6] & 0xFF) << 16 | (str[i + 7] & 0xFF) << 24);
            a -= (c += (str[i + 8] & 0xFF) << 0 | (str[i + 9] & 0xFF) << 8 | (str[i + 10] & 0xFF) << 16 | (str[i + 11] & 0xFF) << 24);
            b -= c;
            b -= (a ^= c >>> 13);
            c -= a;
            c -= (b ^= a << 8);
            a -= b;
            a -= (c ^= b >>> 13);
            b -= c;
            b -= (a ^= c >>> 12);
            c -= a;
            c -= (b ^= a << 16);
            a -= b;
            a -= (c ^= b >>> 5);
            b -= c;
            b -= (a ^= c >>> 3);
            c -= a;
            c -= (b ^= a << 10);
            c ^= b >>> 15;
            i += 12;
        }
        c += limit - start;
        switch (limit - i) {
            case 11: {
                c += (str[i + 10] & 0xFF) << 24;
            }
            case 10: {
                c += (str[i + 9] & 0xFF) << 16;
            }
            case 9: {
                c += (str[i + 8] & 0xFF) << 8;
            }
            case 8: {
                b += (str[i + 7] & 0xFF) << 24;
            }
            case 7: {
                b += (str[i + 6] & 0xFF) << 16;
            }
            case 6: {
                b += (str[i + 5] & 0xFF) << 8;
            }
            case 5: {
                b += str[i + 4] & 0xFF;
            }
            case 4: {
                a += (str[i + 3] & 0xFF) << 24;
            }
            case 3: {
                a += (str[i + 2] & 0xFF) << 16;
            }
            case 2: {
                a += (str[i + 1] & 0xFF) << 8;
            }
            case 1: {
                a += str[i + 0] & 0xFF;
            }
        }
        a -= b;
        a -= c;
        b -= c;
        b -= (a ^= c >>> 13);
        c -= a;
        c -= (b ^= a << 8);
        a -= b;
        a -= (c ^= b >>> 13);
        b -= c;
        b -= (a ^= c >>> 12);
        c -= a;
        c -= (b ^= a << 16);
        a -= b;
        a -= (c ^= b >>> 5);
        b -= c;
        b -= (a ^= c >>> 3);
        c -= a;
        c -= (b ^= a << 10);
        return c ^= b >>> 15;
    }
}

