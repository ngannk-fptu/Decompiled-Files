/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.xwork.FlashScope
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.plugins.roadmap;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.xwork.FlashScope;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.plugins.roadmap.NumberUtil;
import com.google.common.collect.Maps;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;

public class CreatePageAction
extends ConfluenceActionSupport {
    private static final String PAGE_VIEW_WITH_ID = "/pages/viewpage.action?pageId=%s&createDialog=true";
    private ContentEntityManager contentEntityManager;

    public String execute() throws Exception {
        FlashScope.put((String)"createDialogInitParams", (Object)Maps.newHashMap((Map)ServletContextThreadLocal.getRequest().getParameterMap()));
        return super.execute();
    }

    public String getParentPage() {
        String contentId = ServletContextThreadLocal.getRequest().getParameter("roadmapContentId");
        String pageId = null;
        if (StringUtils.isBlank((CharSequence)contentId)) {
            pageId = ServletContextThreadLocal.getRequest().getParameter("parentPageId");
        } else {
            ContentEntityObject content = this.contentEntityManager.getById(NumberUtil.parseLongString(contentId));
            if (content instanceof Comment) {
                pageId = ((Comment)content).getContainer().getIdAsString();
            } else if (content instanceof AbstractPage) {
                pageId = contentId;
            }
        }
        if (StringUtils.isNotBlank((CharSequence)pageId)) {
            return String.format(PAGE_VIEW_WITH_ID, pageId);
        }
        return "/dashboard.action?createDialog=true";
    }

    public void setContentEntityManager(@Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }
}

