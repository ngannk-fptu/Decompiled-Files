/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNodeModel;

public interface TemplateNodeModelEx
extends TemplateNodeModel {
    public TemplateNodeModelEx getPreviousSibling() throws TemplateModelException;

    public TemplateNodeModelEx getNextSibling() throws TemplateModelException;
}

