/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.web.service;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.spaces.Space;
import java.util.Map;

public interface WebContextFactory {
    public WebInterfaceContext createWebInterfaceContext(Long var1);

    public WebInterfaceContext createWebInterfaceContext(Long var1, Map<String, Object> var2);

    public WebInterfaceContext createWebInterfaceContext(ContentEntityObject var1);

    public WebInterfaceContext createWebInterfaceContext(ContentEntityObject var1, Map<String, Object> var2);

    public WebInterfaceContext createWebInterfaceContextForSpace(Space var1);

    public WebInterfaceContext createWebInterfaceContextForSpace(String var1);

    public Map<String, Object> createTemplateContext(WebInterfaceContext var1, Map<String, Object> var2);

    public Map<String, Object> createWebPanelTemplateContext(WebInterfaceContext var1, Map<String, Object> var2);

    public Map<String, Object> createWebItemTemplateContext(WebInterfaceContext var1, Map<String, Object> var2);
}

