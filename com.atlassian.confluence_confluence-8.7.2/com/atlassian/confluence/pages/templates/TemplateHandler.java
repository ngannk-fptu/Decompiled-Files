/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.templates;

import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.pages.templates.PageTemplate;
import com.atlassian.confluence.pages.templates.variables.Variable;
import java.io.Reader;
import java.util.List;

public interface TemplateHandler {
    public List<Variable> getTemplateVariables(PageTemplate var1) throws XhtmlException;

    public String insertVariables(Reader var1, List<? extends Variable> var2);

    public String generateEditorFormat(PageTemplate var1, List<? extends Variable> var2, String var3) throws XhtmlException;
}

