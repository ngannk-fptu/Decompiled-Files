/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.dom;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public interface XPathSupport {
    public TemplateModel executeQuery(Object var1, String var2) throws TemplateModelException;
}

