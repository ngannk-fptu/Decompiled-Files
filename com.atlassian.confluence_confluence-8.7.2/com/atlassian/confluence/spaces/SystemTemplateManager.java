/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.spaces;

import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.templates.variables.Variable;
import java.util.List;

public interface SystemTemplateManager {
    public static final String NEW_GLOBAL_SPACE_TEMPLATE_NAME = "Default Space Content";
    public static final String NEW_PERSONAL_SPACE_TEMPLATE_NAME = "Default Personal Space Content";
    public static final String NEW_GLOBAL_SPACE_TEMPLATE_KEY = "com.atlassian.confluence.plugins.confluence-default-space-content-plugin:spacecontent-global";
    public static final String NEW_PERSONAL_SPACE_TEMPLATE_KEY = "com.atlassian.confluence.plugins.confluence-default-space-content-plugin:spacecontent-personal";

    public String getTemplate(String var1, List<Variable> var2);

    public String getTemplate(String var1, List<Variable> var2, Page var3);

    public void saveTemplate(String var1, String var2, String var3);
}

