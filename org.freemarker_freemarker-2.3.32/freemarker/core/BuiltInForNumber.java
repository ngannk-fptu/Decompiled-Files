/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BuiltIn;
import freemarker.core.Environment;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

abstract class BuiltInForNumber
extends BuiltIn {
    BuiltInForNumber() {
    }

    @Override
    TemplateModel _eval(Environment env) throws TemplateException {
        TemplateModel model = this.target.eval(env);
        return this.calculateResult(this.target.modelToNumber(model, env), model);
    }

    abstract TemplateModel calculateResult(Number var1, TemplateModel var2) throws TemplateModelException;
}

