/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core._ErrorDescriptionBuilder;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.ClassUtil;

public class _TemplateModelException
extends TemplateModelException {
    public _TemplateModelException(String description) {
        super(description);
    }

    public _TemplateModelException(Throwable cause, String description) {
        this(cause, null, description);
    }

    public _TemplateModelException(Environment env, String description) {
        this((Throwable)null, env, description);
    }

    public _TemplateModelException(Throwable cause, Environment env) {
        this(cause, env, (String)null);
    }

    public _TemplateModelException(Throwable cause) {
        this(cause, null, (String)null);
    }

    public _TemplateModelException(Throwable cause, Environment env, String description) {
        super(cause, env, description, true);
    }

    public _TemplateModelException(_ErrorDescriptionBuilder description) {
        this(null, description);
    }

    public _TemplateModelException(Environment env, _ErrorDescriptionBuilder description) {
        this(null, env, description);
    }

    public _TemplateModelException(Throwable cause, Environment env, _ErrorDescriptionBuilder description) {
        super(cause, env, description, true);
    }

    public _TemplateModelException(Object ... descriptionParts) {
        this((Environment)null, descriptionParts);
    }

    public _TemplateModelException(Environment env, Object ... descriptionParts) {
        this((Throwable)null, env, descriptionParts);
    }

    public _TemplateModelException(Throwable cause, Object ... descriptionParts) {
        this(cause, null, descriptionParts);
    }

    public _TemplateModelException(Throwable cause, Environment env, Object ... descriptionParts) {
        super(cause, env, new _ErrorDescriptionBuilder(descriptionParts), true);
    }

    public _TemplateModelException(Expression blamed, Object ... descriptionParts) {
        this(blamed, null, descriptionParts);
    }

    public _TemplateModelException(Expression blamed, Environment env, Object ... descriptionParts) {
        this(blamed, null, env, descriptionParts);
    }

    public _TemplateModelException(Expression blamed, Throwable cause, Environment env, Object ... descriptionParts) {
        super(cause, env, new _ErrorDescriptionBuilder(descriptionParts).blame(blamed), true);
    }

    public _TemplateModelException(Expression blamed, String description) {
        this(blamed, null, description);
    }

    public _TemplateModelException(Expression blamed, Environment env, String description) {
        this(blamed, null, env, description);
    }

    public _TemplateModelException(Expression blamed, Throwable cause, Environment env, String description) {
        super(cause, env, new _ErrorDescriptionBuilder(description).blame(blamed), true);
    }

    static Object[] modelHasStoredNullDescription(Class expected, TemplateModel model) {
        Object[] objectArray;
        Object[] objectArray2 = new Object[5];
        objectArray2[0] = "The FreeMarker value exists, but has nothing inside it; the TemplateModel object (class: ";
        objectArray2[1] = model.getClass().getName();
        objectArray2[2] = ") has returned a null";
        if (expected != null) {
            Object[] objectArray3 = new Object[2];
            objectArray3[0] = " instead of a ";
            objectArray = objectArray3;
            objectArray3[1] = ClassUtil.getShortClassName(expected);
        } else {
            objectArray = "";
        }
        objectArray2[3] = objectArray;
        objectArray2[4] = ". This is possibly a bug in the non-FreeMarker code that builds the data-model.";
        return objectArray2;
    }
}

