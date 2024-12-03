/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BuiltIn;
import freemarker.core.Environment;
import freemarker.core.EvalUtil;
import freemarker.core.Expression;
import freemarker.core.InvalidReferenceException;
import freemarker.core.NonDateException;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import java.util.Date;

abstract class BuiltInForDate
extends BuiltIn {
    BuiltInForDate() {
    }

    @Override
    TemplateModel _eval(Environment env) throws TemplateException {
        TemplateModel model = this.target.eval(env);
        if (model instanceof TemplateDateModel) {
            TemplateDateModel tdm = (TemplateDateModel)model;
            return this.calculateResult(EvalUtil.modelToDate(tdm, this.target), tdm.getDateType(), env);
        }
        throw BuiltInForDate.newNonDateException(env, model, this.target);
    }

    protected abstract TemplateModel calculateResult(Date var1, int var2, Environment var3) throws TemplateException;

    static TemplateException newNonDateException(Environment env, TemplateModel model, Expression target) throws InvalidReferenceException {
        TemplateException e = model == null ? InvalidReferenceException.getInstance(target, env) : new NonDateException(target, model, "date", env);
        return e;
    }
}

