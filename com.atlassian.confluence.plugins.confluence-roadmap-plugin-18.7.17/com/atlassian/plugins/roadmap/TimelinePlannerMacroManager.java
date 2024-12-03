/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink
 *  com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.DefaultSaveContext
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.CommentManager
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.xhtml.api.Link
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.atlassian.confluence.xhtml.api.MacroDefinitionHandler
 *  com.atlassian.confluence.xhtml.api.MacroDefinitionUpdater
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.user.User
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugins.roadmap;

import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.DefaultSaveContext;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.atlassian.confluence.xhtml.api.MacroDefinitionHandler;
import com.atlassian.confluence.xhtml.api.MacroDefinitionUpdater;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.plugins.roadmap.BarParam;
import com.atlassian.plugins.roadmap.PageLinkParser;
import com.atlassian.plugins.roadmap.RoadmapMacroCacheSupplier;
import com.atlassian.plugins.roadmap.TimelinePlannerJsonBuilder;
import com.atlassian.plugins.roadmap.models.Bar;
import com.atlassian.plugins.roadmap.models.Lane;
import com.atlassian.plugins.roadmap.models.RoadmapPageLink;
import com.atlassian.plugins.roadmap.models.TimelinePlanner;
import com.atlassian.renderer.RenderContext;
import com.atlassian.user.User;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class TimelinePlannerMacroManager {
    private static final String ROADMAP_MACRO_NAME = "roadmap";
    private static final String PAGELINK_CACHE_PREFIX = "pagelink-";
    private PageManager pageManager;
    private CommentManager commentManager;
    private XhtmlContent xhtmlContent;
    private PermissionManager permissionManager;
    private final PageLinkParser pageLinkParser;
    private final ContentEntityManager contentEntityManager;
    private final RoadmapMacroCacheSupplier cacheSupplier;

    public TimelinePlannerMacroManager(XhtmlContent xhtmlContent, PageManager pageManager, CommentManager commentManager, PageLinkParser pageLinkParser, PermissionManager permissionManager, ContentEntityManager contentEntityManager, RoadmapMacroCacheSupplier cacheSupplier) {
        this.pageManager = pageManager;
        this.commentManager = commentManager;
        this.xhtmlContent = xhtmlContent;
        this.permissionManager = permissionManager;
        this.pageLinkParser = pageLinkParser;
        this.contentEntityManager = contentEntityManager;
        this.cacheSupplier = cacheSupplier;
    }

    public void updatePagelinkToRoadmapBar(BarParam barParam, long linkPageId) {
        RoadmapPageLink pageLink = new RoadmapPageLink(this.pageManager.getAbstractPage(linkPageId));
        this.updatePagelinkToRoadmapBar(barParam, pageLink);
    }

    public void updatePagelinkToRoadmapBar(final BarParam barParam, final RoadmapPageLink linkPage) {
        this.checkUpdatePagePermission(barParam.contentId);
        if (linkPage.getId() != null || linkPage.getId() == null && !barParam.updateRoadmap.booleanValue()) {
            this.putBarPageLink(barParam.barId, linkPage);
        } else {
            this.removeBarPageLink(barParam.barId);
        }
        if (barParam.updateRoadmap.booleanValue()) {
            ContentEntityObject content = this.getLatestVersionContent(barParam.contentId);
            final PageContext pageContext = content.toPageContext();
            String updateContent = null;
            try {
                updateContent = this.xhtmlContent.updateMacroDefinitions(content.getBodyAsString(), (ConversionContext)new DefaultConversionContext((RenderContext)pageContext), new MacroDefinitionUpdater(){

                    public MacroDefinition update(MacroDefinition macroDefinition) {
                        Map params = macroDefinition.getParameters();
                        if (macroDefinition.getName().equals(TimelinePlannerMacroManager.ROADMAP_MACRO_NAME) && ((String)params.get("hash")).equals(barParam.roadmapHash)) {
                            TimelinePlanner roadmap = TimelinePlannerJsonBuilder.fromJson(macroDefinition.getParameter("source"));
                            Bar bar = TimelinePlannerMacroManager.this.getBarInRoadmap(roadmap, barParam.barId);
                            bar.setPageLink(linkPage);
                            macroDefinition.setTypedParameter("source", (Object)TimelinePlannerJsonBuilder.toJson(roadmap));
                            String maplinks = StringUtils.defaultString((String)((String)macroDefinition.getTypedParameter("maplinks", String.class)));
                            List<Link> pagelinks = TimelinePlannerMacroManager.this.extractLinksFromMacroParam(macroDefinition.getTypedParameter("pagelinks", Object.class));
                            String wikiLink = StringEscapeUtils.escapeHtml4((String)linkPage.getWikiLink());
                            ResourceIdentifier resourceIdentifier = TimelinePlannerMacroManager.this.pageLinkParser.parse(StringUtils.substringBetween((String)wikiLink, (String)"[", (String)"]"), pageContext.getSpaceKey());
                            if (!StringUtils.contains((CharSequence)maplinks, (CharSequence)barParam.barId) && resourceIdentifier != null) {
                                maplinks = StringUtils.isEmpty((CharSequence)maplinks) ? barParam.barId : maplinks + "~~~~~" + barParam.barId;
                                pagelinks.add((Link)new DefaultLink(resourceIdentifier, null));
                            } else {
                                ArrayList maplinksList = Lists.newArrayList((Object[])maplinks.split("~~~~~"));
                                int linkIndex = maplinksList.indexOf(barParam.barId);
                                if (resourceIdentifier != null) {
                                    Link newLink = pagelinks.get(linkIndex).updateDestination(resourceIdentifier);
                                    pagelinks.set(linkIndex, newLink);
                                } else if (linkIndex >= 0) {
                                    maplinksList.remove(linkIndex);
                                    maplinks = StringUtils.join((Iterable)maplinksList, (String)"~~~~~");
                                    pagelinks.remove(linkIndex);
                                }
                            }
                            macroDefinition.setTypedParameter("maplinks", (Object)maplinks);
                            macroDefinition.setTypedParameter("pagelinks", pagelinks);
                            return macroDefinition;
                        }
                        return macroDefinition;
                    }
                });
            }
            catch (XhtmlException e) {
                throw new ServiceException("Can not update content: ", (Throwable)e);
            }
            if (content instanceof AbstractPage) {
                content.setBodyAsString(updateContent);
                this.pageManager.saveContentEntity(content, DefaultSaveContext.MINOR_EDIT);
            } else if (content instanceof Comment) {
                this.commentManager.updateCommentContent((Comment)content, updateContent);
            }
        }
    }

    public MacroDefinition findRoadmapMacroDefinition(long pageId, int version, final String roadmapHash) throws XhtmlException {
        ContentEntityObject pageContent = this.getLatestVersionContent(pageId);
        final AtomicReference ref = new AtomicReference();
        this.xhtmlContent.handleMacroDefinitions(pageContent.getBodyAsString(), (ConversionContext)new DefaultConversionContext(new RenderContext()), new MacroDefinitionHandler(){

            public void handle(MacroDefinition macroDefinition) {
                Map params = macroDefinition.getParameters();
                if (macroDefinition.getName().equals(TimelinePlannerMacroManager.ROADMAP_MACRO_NAME) && ((String)params.get("hash")).equals(roadmapHash)) {
                    ref.set(macroDefinition);
                }
            }
        });
        return (MacroDefinition)ref.get();
    }

    private Bar getBarInRoadmap(TimelinePlanner timelinePlanner, String barUUID) {
        for (Lane lane : timelinePlanner.getLanes()) {
            for (Bar bar : lane.getBars()) {
                if (!StringUtils.equals((CharSequence)barUUID, (CharSequence)bar.getId())) continue;
                return bar;
            }
        }
        return null;
    }

    private ContentEntityObject getLatestVersionContent(long contentId) {
        ContentEntityObject content = this.contentEntityManager.getById(contentId);
        if (content == null) {
            throw new NotFoundException("No content found with id: " + contentId);
        }
        if ((content = (ContentEntityObject)content.getLatestVersion()) instanceof Comment) {
            return this.commentManager.getComment(content.getId());
        }
        return content;
    }

    public void put(String barId, LinkStatus linkStatus) {
        this.cacheSupplier.getLinkStatusCache().put((Object)barId, (Object)linkStatus);
    }

    public LinkStatus checkStatus(String barId) {
        return Optional.ofNullable((LinkStatus)((Object)this.cacheSupplier.getLinkStatusCache().get((Object)barId))).orElse(LinkStatus.UNKNOWN);
    }

    public void removeStatus(String barId) {
        this.cacheSupplier.getLinkStatusCache().remove((Object)barId);
    }

    public void putBarPageLink(String barId, RoadmapPageLink barPageLink) {
        this.cacheSupplier.getPageLinkCache().put((Object)barId, (Object)barPageLink);
    }

    public RoadmapPageLink getBarPageLink(String barId) {
        return Optional.ofNullable((RoadmapPageLink)this.cacheSupplier.getPageLinkCache().get((Object)barId)).orElse(new RoadmapPageLink());
    }

    public void removeBarPageLink(String barId) {
        this.cacheSupplier.getPageLinkCache().remove((Object)barId);
    }

    private void checkUpdatePagePermission(Long pageId) {
        if (pageId != null) {
            ContentEntityObject content = this.contentEntityManager.getById(pageId.longValue());
            ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
            if (!this.permissionManager.hasPermission((User)currentUser, Permission.EDIT, (Object)content)) {
                throw new PermissionException();
            }
        }
    }

    private List<Link> extractLinksFromMacroParam(Object pagelinksObj) {
        if (pagelinksObj instanceof Link) {
            return Lists.newArrayList((Object[])new Link[]{(Link)pagelinksObj});
        }
        if (pagelinksObj instanceof List) {
            return (List)pagelinksObj;
        }
        return Lists.newArrayList();
    }

    public static enum LinkStatus {
        PENDING,
        REDEEM,
        UNKNOWN;

    }
}

