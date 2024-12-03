/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public interface TemplateScalarModel
extends TemplateModel {
    public static final TemplateModel EMPTY_STRING = new SimpleScalar("");

    public String getAsString() throws TemplateModelException;
}

