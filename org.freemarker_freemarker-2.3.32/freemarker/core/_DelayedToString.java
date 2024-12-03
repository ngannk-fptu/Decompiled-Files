/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core._DelayedConversionToString;

public class _DelayedToString
extends _DelayedConversionToString {
    public _DelayedToString(Object object) {
        super(object);
    }

    public _DelayedToString(int object) {
        super(object);
    }

    @Override
    protected String doConversion(Object obj) {
        return String.valueOf(obj);
    }
}

