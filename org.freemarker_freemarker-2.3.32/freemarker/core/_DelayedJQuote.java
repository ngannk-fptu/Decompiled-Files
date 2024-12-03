/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core._DelayedConversionToString;
import freemarker.core._ErrorDescriptionBuilder;
import freemarker.template.utility.StringUtil;

public class _DelayedJQuote
extends _DelayedConversionToString {
    public _DelayedJQuote(Object object) {
        super(object);
    }

    @Override
    protected String doConversion(Object obj) {
        return StringUtil.jQuote(_ErrorDescriptionBuilder.toString(obj));
    }
}

