/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core._DelayedConversionToString;
import freemarker.template.TemplateException;

public class _DelayedGetMessageWithoutStackTop
extends _DelayedConversionToString {
    public _DelayedGetMessageWithoutStackTop(TemplateException exception) {
        super(exception);
    }

    @Override
    protected String doConversion(Object obj) {
        return ((TemplateException)obj).getMessageWithoutStackTop();
    }
}

