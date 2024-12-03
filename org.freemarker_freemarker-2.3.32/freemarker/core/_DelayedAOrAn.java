/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core._DelayedConversionToString;
import freemarker.core._MessageUtil;

public class _DelayedAOrAn
extends _DelayedConversionToString {
    public _DelayedAOrAn(Object object) {
        super(object);
    }

    @Override
    protected String doConversion(Object obj) {
        String s = obj.toString();
        return _MessageUtil.getAOrAn(s) + " " + s;
    }
}

