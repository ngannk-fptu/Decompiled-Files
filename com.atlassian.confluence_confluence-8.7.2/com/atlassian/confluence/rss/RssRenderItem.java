/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.rss;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.rss.FeedProperties;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.user.User;
import java.util.Map;

public class RssRenderItem<T> {
    private T entity;
    private boolean showContent;
    private User modifier;
    private User user;
    private DateFormatter dateFormatter;

    public RssRenderItem(T entity, FeedProperties feedProperties, User modifier, User user, DateFormatter dateFormatter) {
        this.modifier = modifier;
        this.entity = entity;
        this.showContent = feedProperties.isShowContent();
        this.user = user;
        this.dateFormatter = dateFormatter;
    }

    public T getEntity() {
        return this.entity;
    }

    public boolean isShowContent() {
        return this.showContent;
    }

    public User getUser() {
        return this.user;
    }

    public User getModifier() {
        return this.modifier;
    }

    public DateFormatter getDateFormatter() {
        return this.dateFormatter;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public void setShowContent(boolean showContent) {
        this.showContent = showContent;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDateFormatter(DateFormatter dateFormatter) {
        this.dateFormatter = dateFormatter;
    }

    public Map<String, Object> getDefaultVelocityContext() {
        Map<String, Object> context = MacroUtils.defaultVelocityContext();
        ConfluenceActionSupport cas = new ConfluenceActionSupport();
        ContainerManager.getInstance().getContainerContext().autowireComponent((Object)cas);
        context.put("action", cas);
        context.put("user", this.getUser());
        context.put("modifier", this.getModifier());
        context.put("dateFormatter", this.getDateFormatter());
        return context;
    }
}

