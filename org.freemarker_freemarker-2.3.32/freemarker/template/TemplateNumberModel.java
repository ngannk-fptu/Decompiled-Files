/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public interface TemplateNumberModel
extends TemplateModel {
    public Number getAsNumber() throws TemplateModelException;
}

