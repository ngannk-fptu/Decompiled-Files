/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.IteratorBlock;
import freemarker.core.SpecialBuiltIn;
import freemarker.core._DelayedJQuote;
import freemarker.core._MiscTemplateException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

abstract class BuiltInForLoopVariable
extends SpecialBuiltIn {
    private String loopVarName;

    BuiltInForLoopVariable() {
    }

    void bindToLoopVariable(String loopVarName) {
        this.loopVarName = loopVarName;
    }

    @Override
    TemplateModel _eval(Environment env) throws TemplateException {
        IteratorBlock.IterationContext iterCtx = env.findEnclosingIterationContextWithVisibleVariable(this.loopVarName);
        if (iterCtx == null) {
            throw new _MiscTemplateException((Expression)this, env, "There's no iteration in context that uses loop variable ", new _DelayedJQuote(this.loopVarName), ".");
        }
        return this.calculateResult(iterCtx, env);
    }

    abstract TemplateModel calculateResult(IteratorBlock.IterationContext var1, Environment var2) throws TemplateException;
}

