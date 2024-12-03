/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.web.WebItemView
 *  com.atlassian.confluence.api.service.web.WebViewService
 *  com.atlassian.confluence.content.render.xhtml.links.WebLink
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.rpc.NotPermittedException
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.ia.impl;

import com.atlassian.confluence.api.model.web.WebItemView;
import com.atlassian.confluence.api.service.web.WebViewService;
import com.atlassian.confluence.content.render.xhtml.links.WebLink;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.plugins.ia.SidebarLink;
import com.atlassian.confluence.plugins.ia.SidebarLinkCategory;
import com.atlassian.confluence.plugins.ia.SidebarLinkDelegate;
import com.atlassian.confluence.plugins.ia.SidebarLinkManager;
import com.atlassian.confluence.plugins.ia.impl.AbstractSidebarService;
import com.atlassian.confluence.plugins.ia.rest.SidebarLinkBean;
import com.atlassian.confluence.plugins.ia.service.SidebarLinkService;
import com.atlassian.confluence.rpc.NotPermittedException;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSidebarLinkService
extends AbstractSidebarService
implements SidebarLinkService {
    private static final Logger log = LoggerFactory.getLogger(DefaultSidebarLinkService.class);
    private final SidebarLinkManager sidebarLinkManager;
    private final ContentEntityManager contentEntityManager;
    private final AttachmentManager attachmentManager;
    private final Function<SidebarLink, SidebarLinkBean> linkToBeanTransformer;
    private final WebViewService webViewService;
    private final Map<SidebarLink.Type, SidebarLinkDelegate> sidebarLinkDelegates;
    private final Predicate<? super SidebarLinkBean> HIDDEN_LINKS_FILTER = new HiddenLinksFilter();
    private final Predicate<? super SidebarLink> permittedLinksFilter = new PermittedResourceLinksFilter();

    public DefaultSidebarLinkService(SidebarLinkManager sidebarLinkManager, PermissionManager permissionManager, SpaceManager spaceManager, ContentEntityManager contentEntityManager, AttachmentManager attachmentManager, WebViewService webViewService, Map<SidebarLink.Type, SidebarLinkDelegate> sidebarLinkDelegates) {
        super(permissionManager, spaceManager);
        this.sidebarLinkManager = sidebarLinkManager;
        this.contentEntityManager = contentEntityManager;
        this.attachmentManager = attachmentManager;
        this.webViewService = webViewService;
        this.linkToBeanTransformer = new LinkToBeanTransformer(sidebarLinkDelegates);
        this.sidebarLinkDelegates = sidebarLinkDelegates;
    }

    @Override
    public List<SidebarLinkBean> getLinksForSpace(SidebarLinkCategory category, String spaceKey, boolean includeHiddenLinks) {
        this.checkViewPermissions(spaceKey);
        Stream<SidebarLinkBean> links = this.getLinksForSpace(category, spaceKey).stream();
        if (!includeHiddenLinks) {
            links = links.filter(this.HIDDEN_LINKS_FILTER);
        }
        return links.sorted().collect(Collectors.toList());
    }

    private Collection<SidebarLinkBean> getLinksForSpace(SidebarLinkCategory category, String spaceKey) {
        switch (category) {
            case MAIN: {
                return this.getLinksForSpace(spaceKey, SidebarLinkCategory.MAIN, "system.space.sidebar/main-links");
            }
            case ADVANCED: {
                return this.getLinksForSpace(spaceKey, SidebarLinkCategory.ADVANCED, "system.space.sidebar/advanced-links");
            }
            case QUICK: {
                return this.getPermittedQuickLinksForSpace(spaceKey);
            }
        }
        throw new IllegalArgumentException("Unexpected category: " + category);
    }

    private Collection<SidebarLinkBean> getLinksForSpace(String spaceKey, SidebarLinkCategory category, String webSection) {
        Collection<SidebarLink> links = this.sidebarLinkManager.findBySpace(spaceKey).getLinks(category);
        Iterable webItems = this.webViewService.forSpace(spaceKey).getItemsForSection(webSection, null);
        ArrayList<SidebarLink> linksToDelete = new ArrayList<SidebarLink>();
        boolean needsRefresh = false;
        for (WebItemView item : webItems) {
            boolean matched = false;
            for (SidebarLink link : links) {
                if (!Objects.equals(item.getModuleKey(), link.getWebItemKey())) continue;
                if (!matched) {
                    matched = true;
                    continue;
                }
                linksToDelete.add(this.sidebarLinkManager.findById(link.getID()));
            }
            if (matched) continue;
            needsRefresh = true;
            this.sidebarLinkManager.createLink(spaceKey, category, SidebarLink.Type.WEB_ITEM, item.getModuleKey(), item.getWeight(), null, null, null, -2L);
        }
        for (SidebarLink link : linksToDelete) {
            needsRefresh = true;
            this.sidebarLinkManager.deleteLink(link);
        }
        if (needsRefresh) {
            links = this.sidebarLinkManager.findBySpace(spaceKey).getLinks(category);
        }
        return links.stream().map(new WebItemAwareLinkToBeanTransformer(this.linkToBeanTransformer, webItems)).filter(new UntitledBeanFilter()).collect(Collectors.toList());
    }

    private Collection<SidebarLinkBean> getPermittedQuickLinksForSpace(String spaceKey) {
        Collection<SidebarLink> links = this.sidebarLinkManager.findBySpace(spaceKey).getLinks(SidebarLinkCategory.QUICK);
        return links.stream().filter(this.permittedLinksFilter).map(this.linkToBeanTransformer).collect(Collectors.toList());
    }

    @Override
    public void move(String spaceKey, Integer id, Integer after) {
        this.move(id, after);
    }

    @Override
    public void move(Integer id, Integer after) {
        int to;
        if (id == null) {
            log.warn("Couldn't move a sidebar link with null ID");
            return;
        }
        SidebarLink link = this.sidebarLinkManager.findById(id);
        if (link == null) {
            log.warn("Couldn't move a sidebar link with null ID");
            return;
        }
        String spaceKey = link.getSpaceKey();
        if (spaceKey == null) {
            log.warn("Couldn't move a sidebar link with null spacekey");
            return;
        }
        this.checkEditPermissions(spaceKey);
        SidebarLink afterLink = after == null ? null : this.sidebarLinkManager.findById(after);
        int from = link.getPosition();
        int n = to = afterLink == null ? 0 : afterLink.getPosition() + 1;
        if (to > from) {
            --to;
        }
        if (from != to) {
            this.sidebarLinkManager.moveLink(link, from, to);
        }
    }

    @Override
    public void delete(String spaceKey, Integer id) {
        this.delete(id);
    }

    @Override
    public void delete(Integer id) {
        if (id == null) {
            log.warn("Couldn't delete a sidebar link with null ID");
            return;
        }
        SidebarLink link = this.sidebarLinkManager.findById(id);
        if (link == null) {
            log.warn("Couldn't delete a sidebar link with null ID");
            return;
        }
        String spaceKey = link.getSpaceKey();
        if (spaceKey == null) {
            log.warn("Couldn't delete a sidebar link with null spacekey");
            return;
        }
        this.checkEditPermissions(spaceKey);
        this.sidebarLinkManager.deleteLink(link);
    }

    @Override
    public SidebarLinkBean create(String spaceKey, Long pageId, String customTitle, String url) {
        return this.create(spaceKey, pageId, customTitle, url, null);
    }

    @Override
    public SidebarLinkBean create(String spaceKey, Long pageId, String customTitle, String url, String iconClass) {
        this.checkEditPermissions(spaceKey);
        return this.forceCreate(spaceKey, pageId, customTitle, url, iconClass);
    }

    @Override
    public SidebarLinkBean forceCreate(String spaceKey, Long pageId, String customTitle, String url, String iconClass) {
        ContentEntityObject page;
        String type = null;
        if (pageId != null && (page = this.contentEntityManager.getById(pageId.longValue())) != null) {
            type = page.getType();
        }
        return this.forceCreate(spaceKey, type, pageId, customTitle, url, iconClass);
    }

    @Override
    public SidebarLinkBean create(String spaceKey, String resourceType, Long resourceId, String customTitle, String url) throws NotPermittedException {
        return this.create(spaceKey, resourceType, resourceId, customTitle, url, null);
    }

    @Override
    public SidebarLinkBean create(String spaceKey, String resourceType, Long resourceId, String customTitle, String url, String iconClass) throws NotPermittedException {
        this.checkEditPermissions(spaceKey);
        return this.forceCreate(spaceKey, resourceType, resourceId, customTitle, url, iconClass);
    }

    @Override
    public SidebarLinkBean forceCreate(String spaceKey, String resourceType, Long resourceId, String customTitle, String url, String iconClass) {
        SidebarLink.Type type = SidebarLink.Type.fromResourceType(resourceType);
        SidebarLinkDelegate delegate = this.sidebarLinkDelegates.get((Object)type);
        SidebarLink sidebarLink = delegate.createSidebarLink(spaceKey, resourceId, type, customTitle, url, iconClass);
        if (sidebarLink == null) {
            throw new IllegalStateException("Unable to create link");
        }
        return this.linkToBeanTransformer.apply(sidebarLink);
    }

    @Override
    public void hide(String spaceKey, Integer id) {
        this.hide(id);
    }

    @Override
    public void hide(Integer id) {
        if (id == null) {
            log.warn("Couldn't hide a sidebar link with null ID");
            return;
        }
        SidebarLink link = this.sidebarLinkManager.findById(id);
        if (link == null) {
            log.warn("Couldn't hide a sidebar link with null ID");
            return;
        }
        String spaceKey = link.getSpaceKey();
        if (spaceKey == null) {
            log.warn("Couldn't hide a sidebar link with null spacekey");
            return;
        }
        this.checkEditPermissions(spaceKey);
        if (link.getCategory() != SidebarLinkCategory.QUICK) {
            this.sidebarLinkManager.hideLink(link);
        }
    }

    @Override
    public void show(String spaceKey, Integer id) {
        this.show(id);
    }

    @Override
    public void show(Integer id) {
        if (id == null) {
            log.warn("Couldn't show a sidebar link with null ID");
            return;
        }
        SidebarLink link = this.sidebarLinkManager.findById(id);
        if (link == null) {
            log.warn("Couldn't show a sidebar link with null ID");
            return;
        }
        String spaceKey = link.getSpaceKey();
        if (spaceKey == null) {
            log.warn("Couldn't show a sidebar link with null spacekey");
            return;
        }
        this.checkEditPermissions(spaceKey);
        if (link.getCategory() != SidebarLinkCategory.QUICK) {
            this.sidebarLinkManager.showLink(link);
        }
    }

    @Override
    public boolean hasQuickLink(String spaceKey, Long pageId) {
        this.checkViewPermissions(spaceKey);
        if (pageId == null) {
            return false;
        }
        Collection<SidebarLink> linksByDestPage = this.sidebarLinkManager.findBySpace(spaceKey).getLinksByDestPage(SidebarLinkCategory.QUICK, pageId);
        return !linksByDestPage.isEmpty();
    }

    @Override
    public Collection<SidebarLinkBean> getQuickLinksForDestinationPage(String spaceKey, Long pageId) {
        this.checkEditPermissions(spaceKey);
        if (pageId == null) {
            return Collections.emptyList();
        }
        Collection<SidebarLink> links = this.sidebarLinkManager.findBySpace(spaceKey).getLinksByDestPage(SidebarLinkCategory.QUICK, pageId);
        return links.stream().map(this.linkToBeanTransformer).collect(Collectors.toList());
    }

    private class PermittedResourceLinksFilter
    implements Predicate<SidebarLink> {
        private PermittedResourceLinksFilter() {
        }

        @Override
        public boolean test(SidebarLink link) {
            Space entity;
            long destPageId = link.getDestPageId();
            switch (link.getType()) {
                case EXTERNAL_LINK: {
                    return true;
                }
                case PINNED_SPACE: {
                    entity = DefaultSidebarLinkService.this.spaceManager.getSpace(destPageId);
                    break;
                }
                case PINNED_ATTACHMENT: {
                    entity = DefaultSidebarLinkService.this.attachmentManager.getAttachment(destPageId);
                    break;
                }
                default: {
                    entity = DefaultSidebarLinkService.this.contentEntityManager.getById(destPageId);
                }
            }
            return DefaultSidebarLinkService.this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)entity);
        }
    }

    private static class HiddenLinksFilter
    implements Predicate<SidebarLinkBean> {
        private HiddenLinksFilter() {
        }

        @Override
        public boolean test(SidebarLinkBean sidebarLinkBean) {
            return !sidebarLinkBean.getCanHide() || !sidebarLinkBean.getHidden();
        }
    }

    private static class UntitledBeanFilter
    implements Predicate<SidebarLinkBean> {
        private UntitledBeanFilter() {
        }

        @Override
        public boolean test(SidebarLinkBean sidebarLinkBean) {
            return StringUtils.isNotBlank((CharSequence)sidebarLinkBean.getTitle());
        }
    }

    private static class WebItemAwareLinkToBeanTransformer
    implements Function<SidebarLink, SidebarLinkBean> {
        private Iterable<WebItemView> webItems;
        private Function<SidebarLink, SidebarLinkBean> delegate;

        WebItemAwareLinkToBeanTransformer(Function<SidebarLink, SidebarLinkBean> linkToBeanTransformer, Iterable<WebItemView> webItems) {
            this.delegate = linkToBeanTransformer;
            this.webItems = webItems;
        }

        private WebItemView getWebItem(String key) {
            for (WebItemView webItem : this.webItems) {
                if (!webItem.getModuleKey().equals(key)) continue;
                return webItem;
            }
            return null;
        }

        @Override
        public SidebarLinkBean apply(SidebarLink input) {
            WebItemView item;
            SidebarLinkBean bean = this.delegate.apply(input);
            if (input.getType() == SidebarLink.Type.WEB_ITEM && (item = this.getWebItem(input.getWebItemKey())) != null) {
                bean.setTitle(item.getLabel());
                bean.setStyleClass(item.getStyleClass());
                bean.setUrl(WebLink.isValidURL((String)item.getLinkUrl()) ? item.getLinkUrl() : "#");
                bean.setUrlWithoutContextPath(WebLink.isValidURL((String)item.getUrlWithoutContextPath()) ? item.getUrlWithoutContextPath() : null);
                bean.setTooltip(item.getTooltip() == null ? item.getLabel() : item.getTooltip());
                bean.setCanHide(true);
            }
            return bean;
        }
    }

    private static class LinkToBeanTransformer
    implements Function<SidebarLink, SidebarLinkBean> {
        private final Map<SidebarLink.Type, SidebarLinkDelegate> sidebarLinkDelegates;

        LinkToBeanTransformer(Map<SidebarLink.Type, SidebarLinkDelegate> sidebarLinkDelegates) {
            this.sidebarLinkDelegates = sidebarLinkDelegates;
        }

        @Override
        public SidebarLinkBean apply(SidebarLink input) {
            return this.sidebarLinkDelegates.get((Object)input.getType()).getSidebarLinkBean(input);
        }
    }
}

