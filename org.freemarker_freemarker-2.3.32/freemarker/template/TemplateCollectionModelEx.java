/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateModelException;

public interface TemplateCollectionModelEx
extends TemplateCollectionModel {
    public int size() throws TemplateModelException;

    public boolean isEmpty() throws TemplateModelException;
}

