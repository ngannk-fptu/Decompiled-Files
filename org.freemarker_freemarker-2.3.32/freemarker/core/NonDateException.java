/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.InvalidReferenceException;
import freemarker.core.UnexpectedTypeException;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateModel;

public class NonDateException
extends UnexpectedTypeException {
    private static final Class[] EXPECTED_TYPES = new Class[]{TemplateDateModel.class};

    public NonDateException(Environment env) {
        super(env, "Expecting date/time value here");
    }

    public NonDateException(String description, Environment env) {
        super(env, description);
    }

    NonDateException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
        super(blamed, model, "date/time", EXPECTED_TYPES, env);
    }

    NonDateException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
        super(blamed, model, "date/time", EXPECTED_TYPES, tip, env);
    }

    NonDateException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
        super(blamed, model, "date/time", EXPECTED_TYPES, (Object[])tips, env);
    }
}

