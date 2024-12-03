/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  io.atlassian.util.concurrent.Timeout
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.rss;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.rss.AbstractContentEntityRenderSupport;
import com.atlassian.confluence.rss.RssRenderItem;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.user.User;
import io.atlassian.util.concurrent.Timeout;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PageRenderSupport
extends AbstractContentEntityRenderSupport<AbstractPage> {
    private static final Logger log = LoggerFactory.getLogger(PageRenderSupport.class);

    private AbstractPage getAbstractPage(RssRenderItem item) {
        return (AbstractPage)item.getEntity();
    }

    @Override
    public String getTitle(RssRenderItem item) {
        AbstractPage content = this.getAbstractPage(item);
        String contentTitle = content.getDisplayTitle() == null ? content.getSpace().getName() : content.getTitle();
        return contentTitle;
    }

    @Override
    public String getLink(RssRenderItem item) {
        return this.getAbstractPage(item).getUrlPath();
    }

    @Override
    public String renderedContext(RssRenderItem item, Timeout timeout) {
        AbstractPage entity = this.getAbstractPage(item);
        Map<String, Object> contextMap = this.contextMap(item, timeout);
        contextMap.put("showContent", item.isShowContent() ? Boolean.TRUE : Boolean.FALSE);
        contextMap.put("entity", entity);
        contextMap.put("spaceKey", entity.getSpaceKey());
        if ("email.address.public".equals(this.settingsManager.getGlobalSettings().getEmailAddressVisibility())) {
            contextMap.put("creatorMail", this.getEmail(entity.getCreator()));
            contextMap.put("editorMail", this.getEmail(entity.getLastModifier()));
        }
        return VelocityUtils.getRenderedTemplate("templates/rss/page-rss-content.vm", contextMap);
    }

    private String getEmail(User user) {
        return user != null ? GeneralUtil.maskEmail(user.getEmail()) : null;
    }
}

