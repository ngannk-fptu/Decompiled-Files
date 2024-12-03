/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.plugins.createcontent.api.contextproviders;

import com.atlassian.annotations.ExperimentalApi;

@ExperimentalApi
public enum BlueprintContextKeys {
    SPACE_KEY("spaceKey"),
    BLUEPRINT_MODULE_KEY("blueprintKey"),
    BLUEPRINT_ID("blueprintId"),
    TEMPLATE_LABEL("templateLabel"),
    ANALYTICS_KEY("analyticsKey"),
    CREATE_RESULT("createResult"),
    CREATE_FROM_TEMPLATE_LABEL("createFromTemplateLabel"),
    CONTENT_PAGE_TITLE("ContentPageTitle"),
    NO_PAGE_TITLE_PREFIX("noPageTitlePrefix");

    private final String key;

    private BlueprintContextKeys(String key) {
        this.key = key;
    }

    public String key() {
        return this.key;
    }
}

