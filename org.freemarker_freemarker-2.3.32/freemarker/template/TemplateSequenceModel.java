/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public interface TemplateSequenceModel
extends TemplateModel {
    public TemplateModel get(int var1) throws TemplateModelException;

    public int size() throws TemplateModelException;
}

