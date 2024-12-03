/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BuiltIn;
import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

abstract class BuiltInForString
extends BuiltIn {
    BuiltInForString() {
    }

    @Override
    TemplateModel _eval(Environment env) throws TemplateException {
        return this.calculateResult(BuiltInForString.getTargetString(this.target, env), env);
    }

    abstract TemplateModel calculateResult(String var1, Environment var2) throws TemplateException;

    static String getTargetString(Expression target, Environment env) throws TemplateException {
        return target.evalAndCoerceToStringOrUnsupportedMarkup(env);
    }
}

