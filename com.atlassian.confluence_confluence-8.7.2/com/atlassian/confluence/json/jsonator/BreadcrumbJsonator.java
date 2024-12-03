/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.json.jsonator;

import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.json.json.Json;
import com.atlassian.confluence.json.json.JsonObject;
import com.atlassian.confluence.json.jsonator.Jsonator;
import com.atlassian.confluence.util.breadcrumbs.Breadcrumb;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;

public class BreadcrumbJsonator
implements Jsonator<Breadcrumb> {
    private final ContextPathHolder contextPathHolder;
    private final I18NBeanFactory i18NBeanFactory;

    public BreadcrumbJsonator(ContextPathHolder context, I18NBeanFactory i18NBeanFactory) {
        this.contextPathHolder = context;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    @Override
    public Json convert(Breadcrumb breadcrumb) {
        JsonObject json = new JsonObject();
        json.setProperty("title", this.getText(breadcrumb.getTitle()));
        json.setProperty("url", this.contextPathHolder.getContextPath() + breadcrumb.getTarget());
        return json;
    }

    private String getText(String key) {
        return this.i18NBeanFactory.getI18NBean().getText(key);
    }
}

