/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public interface TemplateHashModel
extends TemplateModel {
    public TemplateModel get(String var1) throws TemplateModelException;

    public boolean isEmpty() throws TemplateModelException;
}

