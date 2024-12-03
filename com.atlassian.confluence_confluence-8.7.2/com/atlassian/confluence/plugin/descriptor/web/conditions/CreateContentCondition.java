/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.user.User
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.user.User;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateContentCondition
extends BaseConfluenceCondition {
    private static final Logger log = LoggerFactory.getLogger(CreateContentCondition.class);
    private PermissionManager permissionManager;
    private Class contentTypeClass = null;

    @Override
    public void init(Map<String, String> params) throws PluginParseException {
        String contentType = params.get("content");
        if ("Page".equalsIgnoreCase(contentType)) {
            this.contentTypeClass = Page.class;
        } else if ("News".equalsIgnoreCase(contentType)) {
            log.info("Parameter 'news' is deprecated use 'blogpost' instead.");
            this.contentTypeClass = BlogPost.class;
        } else if ("Blogpost".equalsIgnoreCase(contentType)) {
            this.contentTypeClass = BlogPost.class;
        } else if ("Comment".equalsIgnoreCase(contentType)) {
            this.contentTypeClass = Comment.class;
        } else if ("Attachment".equalsIgnoreCase(contentType)) {
            this.contentTypeClass = Attachment.class;
        } else if ("Space".equalsIgnoreCase(contentType)) {
            this.contentTypeClass = Space.class;
        } else {
            throw new PluginParseException("Could not determine type of class to check permissions against: contentType = " + contentType);
        }
        super.init(params);
    }

    @Override
    public boolean shouldDisplay(WebInterfaceContext context) {
        ConfluenceUser user = context.getCurrentUser();
        if (Space.class.equals((Object)this.contentTypeClass)) {
            return this.permissionManager.hasCreatePermission((User)user, PermissionManager.TARGET_APPLICATION, this.contentTypeClass);
        }
        if (Attachment.class.equals((Object)this.contentTypeClass) || Comment.class.equals((Object)this.contentTypeClass)) {
            return this.permissionManager.hasCreatePermission((User)user, (Object)context.getPage(), this.contentTypeClass);
        }
        if (SpaceContentEntityObject.class.isAssignableFrom(this.contentTypeClass)) {
            return this.permissionManager.hasCreatePermission((User)user, (Object)context.getSpace(), this.contentTypeClass);
        }
        return false;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }
}

