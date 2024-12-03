/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.EvalUtil;
import freemarker.core.FlowControlException;
import freemarker.core.InvalidReferenceException;
import freemarker.core.NonBooleanException;
import freemarker.core.NonNumericalException;
import freemarker.core.TemplateMarkupOutputModel;
import freemarker.core.TemplateObject;
import freemarker.core._MiscTemplateException;
import freemarker.ext.beans.BeanModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.utility.UndeclaredThrowableException;

@Deprecated
public abstract class Expression
extends TemplateObject {
    TemplateModel constantValue;

    abstract TemplateModel _eval(Environment var1) throws TemplateException;

    abstract boolean isLiteral();

    @Override
    final void setLocation(Template template, int beginColumn, int beginLine, int endColumn, int endLine) {
        super.setLocation(template, beginColumn, beginLine, endColumn, endLine);
        if (this.isLiteral()) {
            try {
                this.constantValue = this._eval(null);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    @Deprecated
    public final TemplateModel getAsTemplateModel(Environment env) throws TemplateException {
        return this.eval(env);
    }

    void enableLazilyGeneratedResult() {
    }

    final TemplateModel eval(Environment env) throws TemplateException {
        try {
            return this.constantValue != null ? this.constantValue : this._eval(env);
        }
        catch (FlowControlException | TemplateException e) {
            throw e;
        }
        catch (Exception e) {
            if (env != null && EvalUtil.shouldWrapUncheckedException(e, env)) {
                throw new _MiscTemplateException(this, (Throwable)e, env, "Expression has thrown an unchecked exception; see the cause exception.");
            }
            if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            throw new UndeclaredThrowableException(e);
        }
    }

    String evalAndCoerceToPlainText(Environment env) throws TemplateException {
        return EvalUtil.coerceModelToPlainText(this.eval(env), this, null, env);
    }

    String evalAndCoerceToPlainText(Environment env, String seqTip) throws TemplateException {
        return EvalUtil.coerceModelToPlainText(this.eval(env), this, seqTip, env);
    }

    Object evalAndCoerceToStringOrMarkup(Environment env) throws TemplateException {
        return EvalUtil.coerceModelToStringOrMarkup(this.eval(env), this, null, env);
    }

    Object evalAndCoerceToStringOrMarkup(Environment env, String seqTip) throws TemplateException {
        return EvalUtil.coerceModelToStringOrMarkup(this.eval(env), this, seqTip, env);
    }

    String evalAndCoerceToStringOrUnsupportedMarkup(Environment env) throws TemplateException {
        return EvalUtil.coerceModelToStringOrUnsupportedMarkup(this.eval(env), this, null, env);
    }

    String evalAndCoerceToStringOrUnsupportedMarkup(Environment env, String seqTip) throws TemplateException {
        return EvalUtil.coerceModelToStringOrUnsupportedMarkup(this.eval(env), this, seqTip, env);
    }

    Number evalToNumber(Environment env) throws TemplateException {
        TemplateModel model = this.eval(env);
        return this.modelToNumber(model, env);
    }

    final Number modelToNumber(TemplateModel model, Environment env) throws TemplateException {
        if (model instanceof TemplateNumberModel) {
            return EvalUtil.modelToNumber((TemplateNumberModel)model, this);
        }
        throw new NonNumericalException(this, model, env);
    }

    boolean evalToBoolean(Environment env) throws TemplateException {
        return this.evalToBoolean(env, null);
    }

    boolean evalToBoolean(Configuration cfg) throws TemplateException {
        return this.evalToBoolean(null, cfg);
    }

    final TemplateModel evalToNonMissing(Environment env) throws TemplateException {
        TemplateModel result = this.eval(env);
        this.assertNonNull(result, env);
        return result;
    }

    private boolean evalToBoolean(Environment env, Configuration cfg) throws TemplateException {
        TemplateModel model = this.eval(env);
        return this.modelToBoolean(model, env, cfg);
    }

    final boolean modelToBoolean(TemplateModel model, Environment env) throws TemplateException {
        return this.modelToBoolean(model, env, null);
    }

    final boolean modelToBoolean(TemplateModel model, Configuration cfg) throws TemplateException {
        return this.modelToBoolean(model, null, cfg);
    }

    private boolean modelToBoolean(TemplateModel model, Environment env, Configuration cfg) throws TemplateException {
        if (model instanceof TemplateBooleanModel) {
            return ((TemplateBooleanModel)model).getAsBoolean();
        }
        if (env != null ? env.isClassicCompatible() : cfg.isClassicCompatible()) {
            return model != null && !Expression.isEmpty(model);
        }
        throw new NonBooleanException(this, model, env);
    }

    final Expression deepCloneWithIdentifierReplaced(String replacedIdentifier, Expression replacement, ReplacemenetState replacementState) {
        Expression clone = this.deepCloneWithIdentifierReplaced_inner(replacedIdentifier, replacement, replacementState);
        if (clone.beginLine == 0) {
            clone.copyLocationFrom(this);
        }
        return clone;
    }

    protected abstract Expression deepCloneWithIdentifierReplaced_inner(String var1, Expression var2, ReplacemenetState var3);

    static boolean isEmpty(TemplateModel model) throws TemplateModelException {
        if (model instanceof BeanModel) {
            return ((BeanModel)model).isEmpty();
        }
        if (model instanceof TemplateSequenceModel) {
            return ((TemplateSequenceModel)model).size() == 0;
        }
        if (model instanceof TemplateScalarModel) {
            String s = ((TemplateScalarModel)model).getAsString();
            return s == null || s.length() == 0;
        }
        if (model == null) {
            return true;
        }
        if (model instanceof TemplateMarkupOutputModel) {
            TemplateMarkupOutputModel mo = (TemplateMarkupOutputModel)model;
            return mo.getOutputFormat().isEmpty(mo);
        }
        if (model instanceof TemplateCollectionModel) {
            return !((TemplateCollectionModel)model).iterator().hasNext();
        }
        if (model instanceof TemplateHashModel) {
            return ((TemplateHashModel)model).isEmpty();
        }
        return !(model instanceof TemplateNumberModel) && !(model instanceof TemplateDateModel) && !(model instanceof TemplateBooleanModel);
    }

    final void assertNonNull(TemplateModel model, Environment env) throws InvalidReferenceException {
        if (model == null) {
            throw InvalidReferenceException.getInstance(this, env);
        }
    }

    static class ReplacemenetState {
        boolean replacementAlreadyInUse;

        ReplacemenetState() {
        }
    }
}

