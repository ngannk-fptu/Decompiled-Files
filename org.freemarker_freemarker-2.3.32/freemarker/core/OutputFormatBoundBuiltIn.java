/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.OutputFormat;
import freemarker.core.SpecialBuiltIn;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.utility.NullArgumentException;

abstract class OutputFormatBoundBuiltIn
extends SpecialBuiltIn {
    protected OutputFormat outputFormat;
    protected int autoEscapingPolicy;

    OutputFormatBoundBuiltIn() {
    }

    void bindToOutputFormat(OutputFormat outputFormat, int autoEscapingPolicy) {
        NullArgumentException.check(outputFormat);
        this.outputFormat = outputFormat;
        this.autoEscapingPolicy = autoEscapingPolicy;
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

