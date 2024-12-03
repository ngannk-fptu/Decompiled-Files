/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BuiltIn;
import freemarker.core.Environment;
import freemarker.core.NonNodeException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNodeModel;

abstract class BuiltInForNode
extends BuiltIn {
    BuiltInForNode() {
    }

    @Override
    TemplateModel _eval(Environment env) throws TemplateException {
        TemplateModel model = this.target.eval(env);
        if (model instanceof TemplateNodeModel) {
            return this.calculateResult((TemplateNodeModel)model, env);
        }
        throw new NonNodeException(this.target, model, env);
    }

    abstract TemplateModel calculateResult(TemplateNodeModel var1, Environment var2) throws TemplateModelException;
}

