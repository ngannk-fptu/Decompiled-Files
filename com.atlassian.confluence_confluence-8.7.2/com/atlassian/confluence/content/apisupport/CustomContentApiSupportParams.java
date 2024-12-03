/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.apisupport;

import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.content.apisupport.ApiSupportProvider;
import com.atlassian.confluence.content.apisupport.ContentCreator;
import com.atlassian.confluence.core.ContentEntityManager;

public interface CustomContentApiSupportParams {
    public ApiSupportProvider getProvider();

    public CustomContentManager getCustomContentManager();

    public ContentCreator getContentCreator();

    public ContentEntityManager getContentEntityManager();
}

