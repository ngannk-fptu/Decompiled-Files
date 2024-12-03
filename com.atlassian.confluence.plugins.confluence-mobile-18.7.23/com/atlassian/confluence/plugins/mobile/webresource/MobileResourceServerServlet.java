/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.servlet.FileServerServlet
 *  com.atlassian.plugin.servlet.DownloadStrategy
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.mobile.webresource;

import com.atlassian.confluence.servlet.FileServerServlet;
import com.atlassian.plugin.servlet.DownloadStrategy;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;

public class MobileResourceServerServlet
extends FileServerServlet {
    private List<DownloadStrategy> downloadStrategies = new ArrayList<DownloadStrategy>(1);

    MobileResourceServerServlet(@Qualifier(value="mobilePluginResourceDownload") DownloadStrategy mobileDownloadStrategy) {
        this.downloadStrategies.add(mobileDownloadStrategy);
    }

    protected List<DownloadStrategy> getDownloadStrategies() {
        return this.downloadStrategies;
    }
}

