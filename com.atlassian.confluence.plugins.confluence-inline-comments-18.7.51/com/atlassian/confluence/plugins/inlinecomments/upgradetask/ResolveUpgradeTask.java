/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.ContentProperties
 *  com.atlassian.confluence.content.ContentQuery
 *  com.atlassian.confluence.content.CustomContentManager
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.sal.api.message.Message
 *  com.atlassian.sal.api.upgrade.PluginUpgradeTask
 */
package com.atlassian.confluence.plugins.inlinecomments.upgradetask;

import com.atlassian.confluence.content.ContentProperties;
import com.atlassian.confluence.content.ContentQuery;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.plugins.inlinecomments.utils.ResolveCommentConverter;
import com.atlassian.sal.api.message.Message;
import com.atlassian.sal.api.upgrade.PluginUpgradeTask;
import java.util.Collection;
import java.util.Iterator;

public class ResolveUpgradeTask
implements PluginUpgradeTask {
    public static final String RESOLVED_PROP = "resolved";
    public static final String RESOLVED_TIME_PROP = "resolved-time";
    public static final String RESOLVED_USER = "resolved-user";
    private final CustomContentManager customContentManager;

    public ResolveUpgradeTask(CustomContentManager customContentManager) {
        this.customContentManager = customContentManager;
    }

    public int getBuildNumber() {
        return 1;
    }

    public String getShortDescription() {
        return "Update resolve properties";
    }

    public Collection<Message> doUpgrade() throws Exception {
        Iterator comments = this.customContentManager.findByQuery(new ContentQuery("inlinecomment.findAllResolvedComment", new Object[0]), 0, Integer.MAX_VALUE);
        while (comments.hasNext()) {
            ContentProperties properties;
            String resolved;
            Comment comment = (Comment)comments.next();
            if (!comment.isInlineComment() || (resolved = (properties = comment.getProperties()).getStringProperty(RESOLVED_PROP)) == null) continue;
            boolean isDangling = Boolean.parseBoolean(properties.getStringProperty("resolved-by-dangling"));
            properties.setStringProperty("status", ResolveCommentConverter.getStatus(Boolean.parseBoolean(resolved), isDangling));
            String resolvedUser = properties.getStringProperty(RESOLVED_USER);
            if (resolvedUser != null) {
                properties.setStringProperty("status-lastmodifier", properties.getStringProperty(RESOLVED_USER));
            }
            properties.setLongProperty("status-lastmoddate", properties.getLongProperty(RESOLVED_TIME_PROP, 0L));
            properties.removeProperty(RESOLVED_PROP);
            properties.removeProperty(RESOLVED_USER);
            properties.removeProperty(RESOLVED_TIME_PROP);
            properties.removeProperty("resolved-by-dangling");
        }
        return null;
    }

    public String getPluginKey() {
        return "com.atlassian.confluence.plugins.confluence-inline-comments";
    }
}

