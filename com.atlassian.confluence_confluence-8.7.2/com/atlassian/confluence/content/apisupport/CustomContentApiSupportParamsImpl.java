/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.apisupport;

import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.content.apisupport.ApiSupportProvider;
import com.atlassian.confluence.content.apisupport.ContentCreator;
import com.atlassian.confluence.content.apisupport.CustomContentApiSupportParams;
import com.atlassian.confluence.core.ContentEntityManager;

public class CustomContentApiSupportParamsImpl
implements CustomContentApiSupportParams {
    private final ApiSupportProvider provider;
    private final CustomContentManager customContentManager;
    private final ContentCreator contentCreator;
    private final ContentEntityManager contentEntityManager;

    public CustomContentApiSupportParamsImpl(ApiSupportProvider provider, CustomContentManager customContentManager, ContentCreator contentCreator, ContentEntityManager contentEntityManager) {
        this.provider = provider;
        this.customContentManager = customContentManager;
        this.contentCreator = contentCreator;
        this.contentEntityManager = contentEntityManager;
    }

    @Override
    public ApiSupportProvider getProvider() {
        return this.provider;
    }

    @Override
    public CustomContentManager getCustomContentManager() {
        return this.customContentManager;
    }

    @Override
    public ContentCreator getContentCreator() {
        return this.contentCreator;
    }

    @Override
    public ContentEntityManager getContentEntityManager() {
        return this.contentEntityManager;
    }
}

