/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core._DelayedConversionToString;
import freemarker.template.TemplateModel;
import freemarker.template.utility.ClassUtil;

public class _DelayedFTLTypeDescription
extends _DelayedConversionToString {
    public _DelayedFTLTypeDescription(TemplateModel tm) {
        super(tm);
    }

    @Override
    protected String doConversion(Object obj) {
        return ClassUtil.getFTLTypeDescription((TemplateModel)obj);
    }
}

