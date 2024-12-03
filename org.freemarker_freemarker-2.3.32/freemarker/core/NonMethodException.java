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
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModel;

public class NonMethodException
extends UnexpectedTypeException {
    private static final Class[] EXPECTED_TYPES = new Class[]{TemplateMethodModel.class};
    private static final Class[] EXPECTED_TYPES_WITH_FUNCTION = new Class[]{TemplateMethodModel.class, Macro.class};

    public NonMethodException(Environment env) {
        super(env, "Expecting method value here");
    }

    public NonMethodException(String description, Environment env) {
        super(env, description);
    }

    NonMethodException(Environment env, _ErrorDescriptionBuilder description) {
        super(env, description);
    }

    NonMethodException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
        super(blamed, model, "method", EXPECTED_TYPES, env);
    }

    NonMethodException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
        super(blamed, model, "method", EXPECTED_TYPES, tip, env);
    }

    NonMethodException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
        this(blamed, model, false, false, tips, env);
    }

    NonMethodException(Expression blamed, TemplateModel model, boolean allowFTLFunction, boolean allowLambdaExp, String[] tips, Environment env) throws InvalidReferenceException {
        super(blamed, model, "method" + (allowFTLFunction ? " or function" : "") + (allowLambdaExp ? " or lambda expression" : ""), allowFTLFunction ? EXPECTED_TYPES_WITH_FUNCTION : EXPECTED_TYPES, (Object[])tips, env);
    }
}

