/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.TemplateObject;
import freemarker.core._DelayedConversionToString;

public class _DelayedGetCanonicalForm
extends _DelayedConversionToString {
    public _DelayedGetCanonicalForm(TemplateObject obj) {
        super(obj);
    }

    @Override
    protected String doConversion(Object obj) {
        try {
            return ((TemplateObject)obj).getCanonicalForm();
        }
        catch (Exception e) {
            return "{Error getting canonical form}";
        }
    }
}

