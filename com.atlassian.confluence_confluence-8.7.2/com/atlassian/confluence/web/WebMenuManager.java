/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.web;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.web.WebMenu;
import java.util.Collection;

public interface WebMenuManager {
    public WebMenu getMenu(String var1, String var2, WebInterfaceContext var3);

    default public WebMenu getMenu(Collection<String> menuKeys, WebInterfaceContext context) {
        return this.getMenu("", menuKeys, context);
    }

    public WebMenu getMenu(String var1, Collection<String> var2, WebInterfaceContext var3);
}

