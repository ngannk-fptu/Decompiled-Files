/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateModelIterator;

public interface TemplateCollectionModel
extends TemplateModel {
    public TemplateModelIterator iterator() throws TemplateModelException;
}

