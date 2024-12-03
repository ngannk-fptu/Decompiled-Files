/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.BlogPost
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition
 *  com.atlassian.confluence.util.UrlUtils
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.tinymceplugin.conditions;

import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.util.UrlUtils;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.ServletActionContext;

public class EditOrCreateBlogCondition
extends BaseConfluenceCondition {
    public boolean shouldDisplay(WebInterfaceContext context) {
        HttpServletRequest req = ServletActionContext.getRequest();
        String uri = req.getServletPath();
        if (UrlUtils.isBlogPostCreationUrl((String)uri) || UrlUtils.isBlogPostEditUrl((String)uri)) {
            return true;
        }
        return UrlUtils.isEditorLoaderUrl((String)uri) && context.getPage() instanceof BlogPost;
    }
}

