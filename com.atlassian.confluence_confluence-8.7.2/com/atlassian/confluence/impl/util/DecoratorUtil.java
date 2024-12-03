/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.opensymphony.xwork2.ActionSupport
 *  javax.servlet.http.HttpServletRequest
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.util;

import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.themes.ThemeHelper;
import com.atlassian.confluence.web.context.StaticHttpContext;
import com.google.common.base.Preconditions;
import com.opensymphony.xwork2.ActionSupport;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecoratorUtil {
    private static final String SIDEBAR_CONTEXT_KEY = "space-ia.sidebar.context.key";
    private static final String CONTENTNAV_CONTEXT_KEY = "content.navigation.context.key";
    private static final Logger log = LoggerFactory.getLogger(DecoratorUtil.class);

    public static void setSidebarContext(Space space, AbstractPage page, String pageId, String context, String contextKey, String collectorKey) {
        DecoratorUtil.setContext(SIDEBAR_CONTEXT_KEY, new SideBarContext(space, page, pageId, context, contextKey, collectorKey));
    }

    public static void setContentNavContext(AbstractPage page, String mode, String context, ThemeHelper helper) {
        DecoratorUtil.setContext(CONTENTNAV_CONTEXT_KEY, new ContentNavigationContext(page, mode, context, helper));
    }

    private static void setContext(String key, Object context) {
        HttpServletRequest request = new StaticHttpContext().getRequest();
        try {
            if (request != null) {
                request.setAttribute(key, context);
            }
        }
        catch (RuntimeException e) {
            log.error("Error setting context {}", (Object)key, (Object)e);
        }
    }

    public static boolean hasSidebarContext() {
        return DecoratorUtil.hasContext(SIDEBAR_CONTEXT_KEY);
    }

    public static boolean hasContentNavContext() {
        return DecoratorUtil.hasContext(CONTENTNAV_CONTEXT_KEY);
    }

    private static boolean hasContext(String key) {
        HttpServletRequest request = new StaticHttpContext().getRequest();
        return request != null && request.getAttribute(key) != null;
    }

    public static SideBarContext getSidebarContext() {
        return DecoratorUtil.getContext(SIDEBAR_CONTEXT_KEY, SideBarContext.defaultContext);
    }

    public static ContentNavigationContext getContentNavContext() {
        return DecoratorUtil.getContext(CONTENTNAV_CONTEXT_KEY, ContentNavigationContext.defaultContext);
    }

    private static <T> T getContext(String key, T defaultValue) {
        HttpServletRequest request = new StaticHttpContext().getRequest();
        return (T)(request != null && request.getAttribute(key) != null ? request.getAttribute(key) : defaultValue);
    }

    public static class SideBarContext {
        public static final SideBarContext defaultContext = new SideBarContext();
        private final Space space;
        private final AbstractPage page;
        private final String pageId;
        private final String context;
        private final String contextKey;
        private final String collectorKey;
        private ActionSupport action;

        SideBarContext() {
            this.space = null;
            this.page = null;
            this.pageId = null;
            this.context = null;
            this.contextKey = null;
            this.collectorKey = null;
        }

        public SideBarContext(Space space, AbstractPage page, String pageId, String context, String contextKey, String collectorKey) {
            this.space = space;
            this.page = page;
            this.pageId = pageId;
            this.context = (String)Preconditions.checkNotNull((Object)context);
            this.contextKey = (String)Preconditions.checkNotNull((Object)contextKey);
            this.collectorKey = collectorKey;
        }

        public AbstractPage getPage() {
            return this.page;
        }

        public String getPageId() {
            return this.pageId;
        }

        public String getContext() {
            return this.context;
        }

        public String getContextKey() {
            return this.contextKey;
        }

        public String getCollectorKey() {
            return this.collectorKey;
        }

        public void setAction(ActionSupport action) {
            this.action = action;
        }

        public ActionSupport getAction() {
            return this.action;
        }

        public Map<String, Object> toMap(ActionSupport action) {
            HashMap<String, Object> result = new HashMap<String, Object>();
            result.put("space", this.space);
            result.put("page", this.page);
            result.put("contentId", this.pageId);
            result.put("context", this.context);
            result.put("context-key", this.contextKey);
            result.put("collector-key", this.collectorKey);
            result.put("action", action);
            return result;
        }
    }

    public static class ContentNavigationContext {
        public static final ContentNavigationContext defaultContext = new ContentNavigationContext();
        private final AbstractPage page;
        private final String mode;
        private final String context;
        private final ThemeHelper helper;

        ContentNavigationContext() {
            this.page = null;
            this.mode = null;
            this.context = null;
            this.helper = null;
        }

        public ContentNavigationContext(AbstractPage page, String mode, String context, ThemeHelper helper) {
            this.page = page;
            this.mode = (String)Preconditions.checkNotNull((Object)mode);
            this.context = (String)Preconditions.checkNotNull((Object)context);
            this.helper = (ThemeHelper)Preconditions.checkNotNull((Object)helper);
        }

        public AbstractPage getPage() {
            return this.page;
        }

        public String getMode() {
            return this.mode;
        }

        public String getContext() {
            return this.context;
        }

        public ThemeHelper getHelper() {
            return this.helper;
        }
    }
}

