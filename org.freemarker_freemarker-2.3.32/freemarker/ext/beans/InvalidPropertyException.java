/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.template.TemplateModelException;

public class InvalidPropertyException
extends TemplateModelException {
    public InvalidPropertyException(String description) {
        super(description);
    }
}

