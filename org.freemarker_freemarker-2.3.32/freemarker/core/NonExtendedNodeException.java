/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.InvalidReferenceException;
import freemarker.core.UnexpectedTypeException;
import freemarker.core._ErrorDescriptionBuilder;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateNodeModelEx;

public class NonExtendedNodeException
extends UnexpectedTypeException {
    private static final Class<?>[] EXPECTED_TYPES = new Class[]{TemplateNodeModelEx.class};

    public NonExtendedNodeException(Environment env) {
        super(env, "Expecting extended node value here");
    }

    public NonExtendedNodeException(String description, Environment env) {
        super(env, description);
    }

    NonExtendedNodeException(Environment env, _ErrorDescriptionBuilder description) {
        super(env, description);
    }

    NonExtendedNodeException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
        super(blamed, model, "extended node", EXPECTED_TYPES, env);
    }

    NonExtendedNodeException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
        super(blamed, model, "extended node", (Class[])EXPECTED_TYPES, tip, env);
    }

    NonExtendedNodeException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
        super(blamed, model, "extended node", (Class[])EXPECTED_TYPES, (Object[])tips, env);
    }
}

