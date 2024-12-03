/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.servlet.AbstractFileServerServlet
 *  com.atlassian.plugin.servlet.DownloadStrategy
 *  com.atlassian.spring.container.LazyComponentReference
 *  com.google.common.base.Supplier
 */
package com.atlassian.confluence.servlet;

import com.atlassian.confluence.setup.SetupContext;
import com.atlassian.plugin.servlet.AbstractFileServerServlet;
import com.atlassian.plugin.servlet.DownloadStrategy;
import com.atlassian.spring.container.LazyComponentReference;
import com.google.common.base.Supplier;
import java.util.Collections;
import java.util.List;

public class FileServerServlet
extends AbstractFileServerServlet {
    public static final String ATTACHMENTS_URL_PREFIX = "attachments";
    public static final String TOKEN_AUTH_ATTACHMENTS_URL_PREFIX = "token-auth/attachments";
    public static final String RESOURCE_URL_PREFIX = "resources";
    public static final String THUMBNAILS_URL_PREFIX = "thumbnails";
    private Supplier<List<DownloadStrategy>> downloadStrategies = new LazyComponentReference("downloadStrategies");

    protected List<DownloadStrategy> getDownloadStrategies() {
        if (!SetupContext.isAvailable()) {
            return (List)this.downloadStrategies.get();
        }
        return Collections.singletonList((DownloadStrategy)SetupContext.get().getBean("setupPluginDownload"));
    }
}

