/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core._ErrorDescriptionBuilder;
import freemarker.template.TemplateException;

public class _MiscTemplateException
extends TemplateException {
    public _MiscTemplateException(String description) {
        super(description, (Environment)null);
    }

    public _MiscTemplateException(Environment env, String description) {
        super(description, env);
    }

    public _MiscTemplateException(Throwable cause, String description) {
        this(cause, null, description);
    }

    public _MiscTemplateException(Throwable cause, Environment env) {
        this(cause, env, (String)null);
    }

    public _MiscTemplateException(Throwable cause) {
        this(cause, null, (String)null);
    }

    public _MiscTemplateException(Throwable cause, Environment env, String description) {
        super(description, cause, env);
    }

    public _MiscTemplateException(_ErrorDescriptionBuilder description) {
        this(null, description);
    }

    public _MiscTemplateException(Environment env, _ErrorDescriptionBuilder description) {
        this(null, env, description);
    }

    public _MiscTemplateException(Throwable cause, Environment env, _ErrorDescriptionBuilder description) {
        super(cause, env, null, description);
    }

    public _MiscTemplateException(Object ... descriptionParts) {
        this((Environment)null, descriptionParts);
    }

    public _MiscTemplateException(Environment env, Object ... descriptionParts) {
        this((Throwable)null, env, descriptionParts);
    }

    public _MiscTemplateException(Throwable cause, Object ... descriptionParts) {
        this(cause, null, descriptionParts);
    }

    public _MiscTemplateException(Throwable cause, Environment env, Object ... descriptionParts) {
        super(cause, env, null, new _ErrorDescriptionBuilder(descriptionParts));
    }

    public _MiscTemplateException(Expression blamed, Object ... descriptionParts) {
        this(blamed, null, descriptionParts);
    }

    public _MiscTemplateException(Expression blamed, Environment env, Object ... descriptionParts) {
        this(blamed, null, env, descriptionParts);
    }

    public _MiscTemplateException(Expression blamed, Throwable cause, Environment env, Object ... descriptionParts) {
        super(cause, env, blamed, new _ErrorDescriptionBuilder(descriptionParts).blame(blamed));
    }

    public _MiscTemplateException(Expression blamed, String description) {
        this(blamed, null, description);
    }

    public _MiscTemplateException(Expression blamed, Environment env, String description) {
        this(blamed, null, env, description);
    }

    public _MiscTemplateException(Expression blamed, Throwable cause, Environment env, String description) {
        super(cause, env, blamed, new _ErrorDescriptionBuilder(description).blame(blamed));
    }
}

