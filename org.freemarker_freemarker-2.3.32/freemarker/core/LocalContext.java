/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.util.Collection;

public interface LocalContext {
    public TemplateModel getLocalVariable(String var1) throws TemplateModelException;

    public Collection getLocalVariableNames() throws TemplateModelException;
}

