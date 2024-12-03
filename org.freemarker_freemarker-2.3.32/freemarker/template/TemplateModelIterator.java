/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public interface TemplateModelIterator {
    public TemplateModel next() throws TemplateModelException;

    public boolean hasNext() throws TemplateModelException;
}

