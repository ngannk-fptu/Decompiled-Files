/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.InvalidReferenceException;
import freemarker.core.Macro;
import freemarker.core.UnexpectedTypeException;
import freemarker.core._ErrorDescriptionBuilder;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateTransformModel;

class NonUserDefinedDirectiveLikeException
extends UnexpectedTypeException {
    private static final Class[] EXPECTED_TYPES = new Class[]{TemplateDirectiveModel.class, TemplateTransformModel.class, Macro.class};

    public NonUserDefinedDirectiveLikeException(Environment env) {
        super(env, "Expecting user-defined directive, transform or macro value here");
    }

    public NonUserDefinedDirectiveLikeException(String description, Environment env) {
        super(env, description);
    }

    NonUserDefinedDirectiveLikeException(Environment env, _ErrorDescriptionBuilder description) {
        super(env, description);
    }

    NonUserDefinedDirectiveLikeException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
        super(blamed, model, "user-defined directive, transform or macro", EXPECTED_TYPES, env);
    }

    NonUserDefinedDirectiveLikeException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
        super(blamed, model, "user-defined directive, transform or macro", EXPECTED_TYPES, tip, env);
    }

    NonUserDefinedDirectiveLikeException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
        super(blamed, model, "user-defined directive, transform or macro", EXPECTED_TYPES, (Object[])tips, env);
    }
}

