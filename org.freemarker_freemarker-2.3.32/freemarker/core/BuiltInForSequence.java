/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BuiltIn;
import freemarker.core.Environment;
import freemarker.core.NonSequenceException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;

abstract class BuiltInForSequence
extends BuiltIn {
    BuiltInForSequence() {
    }

    @Override
    TemplateModel _eval(Environment env) throws TemplateException {
        TemplateModel model = this.target.eval(env);
        if (!(model instanceof TemplateSequenceModel)) {
            throw new NonSequenceException(this.target, model, env);
        }
        return this.calculateResult((TemplateSequenceModel)model);
    }

    abstract TemplateModel calculateResult(TemplateSequenceModel var1) throws TemplateModelException;
}

