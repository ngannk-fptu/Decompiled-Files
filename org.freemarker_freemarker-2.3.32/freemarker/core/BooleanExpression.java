/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

abstract class BooleanExpression
extends Expression {
    BooleanExpression() {
    }

    @Override
    TemplateModel _eval(Environment env) throws TemplateException {
        return this.evalToBoolean(env) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
    }
}

