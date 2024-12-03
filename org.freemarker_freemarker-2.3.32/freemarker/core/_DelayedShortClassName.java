/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core._DelayedConversionToString;
import freemarker.template.utility.ClassUtil;

public class _DelayedShortClassName
extends _DelayedConversionToString {
    public _DelayedShortClassName(Class pClass) {
        super(pClass);
    }

    @Override
    protected String doConversion(Object obj) {
        return ClassUtil.getShortClassName((Class)obj, true);
    }
}

