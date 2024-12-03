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
import freemarker.template.TemplateNodeModel;

public class NonNodeException
extends UnexpectedTypeException {
    private static final Class[] EXPECTED_TYPES = new Class[]{TemplateNodeModel.class};

    public NonNodeException(Environment env) {
        super(env, "Expecting node value here");
    }

    public NonNodeException(String description, Environment env) {
        super(env, description);
    }

    NonNodeException(Environment env, _ErrorDescriptionBuilder description) {
        super(env, description);
    }

    NonNodeException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
        super(blamed, model, "node", EXPECTED_TYPES, env);
    }

    NonNodeException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
        super(blamed, model, "node", EXPECTED_TYPES, tip, env);
    }

    NonNodeException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
        super(blamed, model, "node", EXPECTED_TYPES, (Object[])tips, env);
    }
}

