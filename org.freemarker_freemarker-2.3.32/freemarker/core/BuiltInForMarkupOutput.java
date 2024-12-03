/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BuiltIn;
import freemarker.core.Environment;
import freemarker.core.NonMarkupOutputException;
import freemarker.core.TemplateMarkupOutputModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

abstract class BuiltInForMarkupOutput
extends BuiltIn {
    BuiltInForMarkupOutput() {
    }

    @Override
    TemplateModel _eval(Environment env) throws TemplateException {
        TemplateModel model = this.target.eval(env);
        if (!(model instanceof TemplateMarkupOutputModel)) {
            throw new NonMarkupOutputException(this.target, model, env);
        }
        return this.calculateResult((TemplateMarkupOutputModel)model);
    }

    protected abstract TemplateModel calculateResult(TemplateMarkupOutputModel var1) throws TemplateModelException;
}

