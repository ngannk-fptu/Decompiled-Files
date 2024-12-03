/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BuiltInBannedWhenAutoEscaping;
import freemarker.core.Environment;
import freemarker.core.EvalUtil;
import freemarker.core.NonStringException;
import freemarker.core.TemplateMarkupOutputModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

abstract class BuiltInForLegacyEscaping
extends BuiltInBannedWhenAutoEscaping {
    BuiltInForLegacyEscaping() {
    }

    @Override
    TemplateModel _eval(Environment env) throws TemplateException {
        TemplateModel tm = this.target.eval(env);
        Object moOrStr = EvalUtil.coerceModelToStringOrMarkup(tm, this.target, null, env);
        if (moOrStr instanceof String) {
            return this.calculateResult((String)moOrStr, env);
        }
        TemplateMarkupOutputModel mo = (TemplateMarkupOutputModel)moOrStr;
        if (mo.getOutputFormat().isLegacyBuiltInBypassed(this.key)) {
            return mo;
        }
        throw new NonStringException(this.target, tm, env);
    }

    abstract TemplateModel calculateResult(String var1, Environment var2) throws TemplateException;
}

