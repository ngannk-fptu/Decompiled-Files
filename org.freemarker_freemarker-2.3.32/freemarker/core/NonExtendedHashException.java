/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.InvalidReferenceException;
import freemarker.core.UnexpectedTypeException;
import freemarker.core._ErrorDescriptionBuilder;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModel;

public class NonExtendedHashException
extends UnexpectedTypeException {
    private static final Class[] EXPECTED_TYPES = new Class[]{TemplateHashModelEx.class};

    public NonExtendedHashException(Environment env) {
        super(env, "Expecting extended hash value here");
    }

    public NonExtendedHashException(String description, Environment env) {
        super(env, description);
    }

    NonExtendedHashException(Environment env, _ErrorDescriptionBuilder description) {
        super(env, description);
    }

    NonExtendedHashException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
        super(blamed, model, "extended hash", EXPECTED_TYPES, env);
    }

    NonExtendedHashException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
        super(blamed, model, "extended hash", EXPECTED_TYPES, tip, env);
    }

    NonExtendedHashException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
        super(blamed, model, "extended hash", EXPECTED_TYPES, (Object[])tips, env);
    }
}

