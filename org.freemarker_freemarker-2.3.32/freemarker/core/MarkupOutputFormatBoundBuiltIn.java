/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.MarkupOutputFormat;
import freemarker.core.SpecialBuiltIn;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.utility.NullArgumentException;

abstract class MarkupOutputFormatBoundBuiltIn
extends SpecialBuiltIn {
    protected MarkupOutputFormat outputFormat;

    MarkupOutputFormatBoundBuiltIn() {
    }

    void bindToMarkupOutputFormat(MarkupOutputFormat outputFormat) {
        NullArgumentException.check(outputFormat);
        this.outputFormat = outputFormat;
    }

    @Override
    TemplateModel _eval(Environment env) throws TemplateException {
        if (this.outputFormat == null) {
            throw new NullPointerException("outputFormat was null");
        }
        return this.calculateResult(env);
    }

    protected abstract TemplateModel calculateResult(Environment var1) throws TemplateException;
}

