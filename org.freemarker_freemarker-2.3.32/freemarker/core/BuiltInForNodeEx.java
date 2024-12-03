/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BuiltIn;
import freemarker.core.Environment;
import freemarker.core.NonExtendedNodeException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNodeModelEx;

public abstract class BuiltInForNodeEx
extends BuiltIn {
    @Override
    TemplateModel _eval(Environment env) throws TemplateException {
        TemplateModel model = this.target.eval(env);
        if (model instanceof TemplateNodeModelEx) {
            return this.calculateResult((TemplateNodeModelEx)model, env);
        }
        throw new NonExtendedNodeException(this.target, model, env);
    }

    abstract TemplateModel calculateResult(TemplateNodeModelEx var1, Environment var2) throws TemplateModelException;
}

