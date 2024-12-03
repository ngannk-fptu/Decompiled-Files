/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.InvalidReferenceException;
import freemarker.core.TemplateMarkupOutputModel;
import freemarker.core.UnexpectedTypeException;
import freemarker.core._ErrorDescriptionBuilder;
import freemarker.template.TemplateModel;

public class NonMarkupOutputException
extends UnexpectedTypeException {
    private static final Class[] EXPECTED_TYPES = new Class[]{TemplateMarkupOutputModel.class};

    public NonMarkupOutputException(Environment env) {
        super(env, "Expecting markup output value here");
    }

    public NonMarkupOutputException(String description, Environment env) {
        super(env, description);
    }

    NonMarkupOutputException(Environment env, _ErrorDescriptionBuilder description) {
        super(env, description);
    }

    NonMarkupOutputException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
        super(blamed, model, "markup output", EXPECTED_TYPES, env);
    }

    NonMarkupOutputException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
        super(blamed, model, "markup output", EXPECTED_TYPES, tip, env);
    }

    NonMarkupOutputException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
        super(blamed, model, "markup output", EXPECTED_TYPES, (Object[])tips, env);
    }
}

