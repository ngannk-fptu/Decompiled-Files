/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core._DelayedConversionToString;

public class _DelayedJoinWithComma
extends _DelayedConversionToString {
    public _DelayedJoinWithComma(String[] items) {
        super(items);
    }

    @Override
    protected String doConversion(Object obj) {
        String[] items = (String[])obj;
        int totalLength = 0;
        for (int i = 0; i < items.length; ++i) {
            if (i != 0) {
                totalLength += 2;
            }
            totalLength += items[i].length();
        }
        StringBuilder sb = new StringBuilder(totalLength);
        for (int i = 0; i < items.length; ++i) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append(items[i]);
        }
        return sb.toString();
    }
}

