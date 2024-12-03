/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.misc.ConcurrentConversionUtil
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.util.concurrent.Timeout
 *  io.atlassian.util.concurrent.Timeout
 */
package com.atlassian.confluence.rss;

import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.compatibility.BodyTypeAwareRenderer;
import com.atlassian.confluence.rss.RssRenderItem;
import com.atlassian.confluence.rss.RssRenderSupport;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.ConfluenceRenderUtils;
import com.atlassian.confluence.util.misc.ConcurrentConversionUtil;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.util.concurrent.Timeout;
import java.util.Map;

public abstract class AbstractRenderSupport<T>
implements RssRenderSupport<T> {
    protected SettingsManager settingsManager;
    protected UserAccessor userAccessor;
    protected WebResourceManager webResourceManager;
    protected BodyTypeAwareRenderer viewBodyTypeAwareRenderer;

    protected Map<String, Object> contextMap(RssRenderItem<? extends T> item, io.atlassian.util.concurrent.Timeout timeout) {
        Map<String, Object> contextMap = item.getDefaultVelocityContext();
        contextMap.put("baseurl", this.settingsManager.getGlobalSettings().getBaseUrl());
        contextMap.put("stylesheet", ConfluenceRenderUtils.renderDefaultStylesheet());
        contextMap.put("userAccessor", this.userAccessor);
        contextMap.put("viewBodyTypeAwareRenderer", this.viewBodyTypeAwareRenderer);
        contextMap.put("webResourceManager", this.webResourceManager);
        contextMap.put("conversionContext", new DefaultConversionContext(this.renderContext(item.getEntity(), timeout)));
        return contextMap;
    }

    @Deprecated
    protected Map<String, Object> getContextMap(RssRenderItem<? extends T> item, Timeout timeout) {
        return this.contextMap(item, ConcurrentConversionUtil.toIoTimeout((Timeout)timeout));
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public void setWebResourceManager(WebResourceManager webResourceManager) {
        this.webResourceManager = webResourceManager;
    }

    public void setViewBodyTypeAwareRenderer(BodyTypeAwareRenderer viewBodyTypeAwareRenderer) {
        this.viewBodyTypeAwareRenderer = viewBodyTypeAwareRenderer;
    }

    @Deprecated
    protected RenderContext getRenderContext(T entity, Timeout timeout) {
        return this.renderContext(entity, ConcurrentConversionUtil.toIoTimeout((Timeout)timeout));
    }

    protected abstract RenderContext renderContext(T var1, io.atlassian.util.concurrent.Timeout var2);
}

