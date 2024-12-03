/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BuiltIn;
import freemarker.core.Environment;
import freemarker.core.InvalidReferenceException;
import freemarker.core.NonExtendedHashException;
import freemarker.core._ErrorDescriptionBuilder;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

abstract class BuiltInForHashEx
extends BuiltIn {
    BuiltInForHashEx() {
    }

    @Override
    TemplateModel _eval(Environment env) throws TemplateException {
        TemplateModel model = this.target.eval(env);
        if (model instanceof TemplateHashModelEx) {
            return this.calculateResult((TemplateHashModelEx)model, env);
        }
        throw new NonExtendedHashException(this.target, model, env);
    }

    abstract TemplateModel calculateResult(TemplateHashModelEx var1, Environment var2) throws TemplateModelException, InvalidReferenceException;

    protected InvalidReferenceException newNullPropertyException(String propertyName, TemplateModel tm, Environment env) {
        if (env.getFastInvalidReferenceExceptions()) {
            return InvalidReferenceException.FAST_INSTANCE;
        }
        return new InvalidReferenceException(new _ErrorDescriptionBuilder("The exteneded hash (of class ", tm.getClass().getName(), ") has returned null for its \"", propertyName, "\" property. This is maybe a bug. The extended hash was returned by this expression:").blame(this.target), env, this);
    }
}

