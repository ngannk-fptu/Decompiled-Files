/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;

public interface TemplateHashModelEx
extends TemplateHashModel {
    public int size() throws TemplateModelException;

    public TemplateCollectionModel keys() throws TemplateModelException;

    public TemplateCollectionModel values() throws TemplateModelException;
}

