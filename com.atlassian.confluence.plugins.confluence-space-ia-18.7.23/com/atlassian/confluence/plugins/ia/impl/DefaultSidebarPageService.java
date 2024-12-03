/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentPermissionManager
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.ia.impl;

import com.atlassian.confluence.core.ContentPermissionManager;
import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.ia.model.PageNodeBean;
import com.atlassian.confluence.plugins.ia.model.PagesBean;
import com.atlassian.confluence.plugins.ia.service.SidebarPageService;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.user.User;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultSidebarPageService
implements SidebarPageService {
    private final ContextPathHolder contextPathHolder;
    private final PageManager pageManager;
    private final PermissionManager permissionManager;
    private final ContentPermissionManager contentPermissionManager;
    private final LocaleManager localeManager;
    private final I18NBeanFactory i18NBeanFactory;
    private static final int MAX_INITIAL_PAGES = 5;
    private static final int DEFAULT_INITIAL_PAGES = 3;

    public DefaultSidebarPageService(ContextPathHolder contextPathHolder, PageManager pageManager, PermissionManager permissionManager, ContentPermissionManager contentPermissionManager, I18NBeanFactory i18NBeanFactory, LocaleManager localeManager) {
        this.contextPathHolder = contextPathHolder;
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.contentPermissionManager = contentPermissionManager;
        this.localeManager = localeManager;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    @Override
    public PagesBean getPageContextualNav(long id) {
        Page page = this.pageManager.getPage(id);
        if (!this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)page)) {
            return new PagesBean(null, null, Collections.emptyList(), Collections.emptyList(), this.getLinkToCreateChildPage(page));
        }
        if (page == null || !page.hasChildren()) {
            return new PagesBean(this.createParentPageBean(page), this.createCurrentPageBean(page), Collections.emptyList(), Collections.emptyList(), this.getLinkToCreateChildPage(page));
        }
        List children = this.contentPermissionManager.getPermittedChildren(page, (User)AuthenticatedUserThreadLocal.get());
        if (children.size() > 5) {
            return new PagesBean(this.createParentPageBean(page), this.createCurrentPageBean(page), this.createChildPageBeans(children.subList(0, 3)), this.createChildPageBeans(children.subList(3, children.size())), this.getLinkToCreateChildPage(page));
        }
        return new PagesBean(this.createParentPageBean(page), this.createCurrentPageBean(page), this.createChildPageBeans(children), Collections.emptyList(), this.getLinkToCreateChildPage(page));
    }

    @Override
    public PagesBean getChildren(long id) {
        return this.getPageContextualNav(id);
    }

    private List<PageNodeBean> createChildPageBeans(List<Page> pages) {
        String contextPath = this.contextPathHolder.getContextPath();
        return pages.stream().map(page -> new PageNodeBean(page.getIdAsString(), page.getDisplayTitle(), contextPath + page.getUrlPath(), false)).collect(Collectors.toList());
    }

    private PageNodeBean createCurrentPageBean(Page page) {
        if (page == null) {
            return null;
        }
        return new PageNodeBean(page.getIdAsString(), page.getDisplayTitle(), this.contextPathHolder.getContextPath() + page.getUrlPath(), true);
    }

    private PageNodeBean createParentPageBean(Page page) {
        if (page == null) {
            return null;
        }
        Page parentPage = page.getParent();
        if (parentPage == null) {
            I18NBean i18n = this.i18NBeanFactory.getI18NBean(this.localeManager.getLocale((User)AuthenticatedUserThreadLocal.get()));
            return new PageNodeBean(null, i18n.getText("sidebar.main.wiki"), this.contextPathHolder.getContextPath() + "/collector/pages.action?key=" + GeneralUtil.urlEncode((String)page.getSpaceKey()), false);
        }
        return new PageNodeBean(parentPage.getIdAsString(), parentPage.getDisplayTitle(), this.contextPathHolder.getContextPath() + parentPage.getUrlPath(), false);
    }

    private String getLinkToCreateChildPage(Page page) {
        if (page == null) {
            return null;
        }
        return this.contextPathHolder.getContextPath() + "/pages/createpage.action?spaceKey=" + GeneralUtil.urlEncode((String)page.getSpaceKey()) + "&fromPageId=" + page.getIdAsString();
    }
}

