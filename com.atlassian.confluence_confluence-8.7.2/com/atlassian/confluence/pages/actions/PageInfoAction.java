/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.links.Link
 *  com.atlassian.renderer.links.LinkResolver
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.Multimap
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.pages.actions;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.core.VersionHistorySummary;
import com.atlassian.confluence.event.Evented;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.content.blogpost.BlogPostInfoViewEvent;
import com.atlassian.confluence.event.events.content.page.PageInfoViewEvent;
import com.atlassian.confluence.languages.LocaleInfo;
import com.atlassian.confluence.links.OutgoingLink;
import com.atlassian.confluence.links.linktypes.AbstractContentEntityLink;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.actions.ViewPageAction;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.PageChangesBean;
import com.atlassian.confluence.util.breadcrumbs.spaceia.ContentDetailAction;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.links.Link;
import com.atlassian.renderer.links.LinkResolver;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PageInfoAction
extends ViewPageAction
implements Evented<ConfluenceEvent>,
ContentDetailAction {
    private static final Logger log = LoggerFactory.getLogger(PageInfoAction.class);
    private Multimap<Space, SpaceContentEntityObject> outgoingLinks;
    private Multimap<Space, SpaceContentEntityObject> incomingLinksMap;
    private Collection<OutgoingLink> externalLinks;
    private LinkResolver linkResolver;
    private static final int MAX_REVISIONS = 5;
    private List<VersionHistorySummary> latestChanges;
    private static final int INITIAL_VISIBLE_CHILD_COUNT = 10;
    private List<VersionHistorySummary> versionHistorySummaryList;
    private static final int URL_DISPLAY_LENGTH = 60;

    @Override
    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        this.versionHistorySummaryList = this.pageManager.getVersionHistorySummaries(this.getPage());
        if (this.pageIsLatestVersionAndDoesNotHaveSpace()) {
            this.addActionError("error.corrupt.page", "" + this.getPage().getId(), this.getPage().getBodyContent().getBody());
            return "error";
        }
        return "success";
    }

    @Override
    public ConfluenceEvent getEventToPublish(String result) {
        LocaleInfo localeInfo = this.getLocaleManager().getLocaleInfo(AuthenticatedUserThreadLocal.get());
        if (this.getPage() instanceof Page) {
            return new PageInfoViewEvent((Object)this, (Page)this.getPage(), localeInfo);
        }
        if (this.getPage() instanceof BlogPost) {
            return new BlogPostInfoViewEvent((Object)this, (BlogPost)this.getPage(), localeInfo);
        }
        return null;
    }

    public void setLinkResolver(LinkResolver linkResolver) {
        this.linkResolver = linkResolver;
    }

    @Deprecated
    public Multimap<Space, SpaceContentEntityObject> getIncomingLinksMap() {
        if (this.incomingLinksMap == null) {
            List<OutgoingLink> incomingLinks = this.getIncomingLinks();
            this.incomingLinksMap = ArrayListMultimap.create();
            for (OutgoingLink outgoingLink : incomingLinks) {
                ContentEntityObject sourceContent = outgoingLink.getSourceContent();
                if (!(sourceContent instanceof SpaceContentEntityObject)) continue;
                SpaceContentEntityObject spaceContentEntityObject = (SpaceContentEntityObject)sourceContent;
                this.incomingLinksMap.put((Object)spaceContentEntityObject.getSpace(), (Object)spaceContentEntityObject);
            }
        }
        return this.incomingLinksMap;
    }

    public Map<Space, Collection<SpaceContentEntityObject>> getIncomingLinksBySpace() {
        return this.getIncomingLinksMap().asMap();
    }

    @Deprecated
    public Multimap<Space, SpaceContentEntityObject> getOutgoingLinks() {
        if (this.outgoingLinks == null) {
            HashSet<OutgoingLink> noDuplicatesSet = new HashSet<OutgoingLink>();
            noDuplicatesSet.addAll(this.getPage().getOutgoingLinks());
            this.outgoingLinks = ArrayListMultimap.create();
            this.externalLinks = new HashSet<OutgoingLink>();
            for (OutgoingLink outgoingLink : noDuplicatesSet) {
                ContentEntityObject destinationContent = this.getDestinationContentEntity(outgoingLink);
                if (destinationContent instanceof AbstractPage) {
                    AbstractPage page = (AbstractPage)destinationContent;
                    this.outgoingLinks.put((Object)page.getSpace(), (Object)page);
                    continue;
                }
                if (!outgoingLink.isUrlLink()) continue;
                this.externalLinks.add(outgoingLink);
            }
        }
        return this.outgoingLinks;
    }

    public Map<Space, Collection<SpaceContentEntityObject>> getOutgoingLinksBySpace() {
        return this.getOutgoingLinks().asMap();
    }

    public Collection<OutgoingLink> getExternalLinks() {
        if (this.externalLinks == null) {
            this.getOutgoingLinksBySpace();
        }
        return this.externalLinks;
    }

    public String renderUrlLink(OutgoingLink link) {
        if (link.isUrlLink()) {
            return this.url2HtmlLink(link.getUrlLink());
        }
        return null;
    }

    private String url2HtmlLink(String url) {
        String shortenedUrl = GeneralUtil.displayShortUrl(url, 60);
        return "<a href=\"" + HtmlUtil.htmlEncode(url) + "\" title=\"" + HtmlUtil.htmlEncode(url) + "\" rel=\"nofollow\">" + HtmlUtil.htmlEncode(shortenedUrl) + "</a>";
    }

    public ContentEntityObject getDestinationContentEntity(OutgoingLink link) {
        Link sLinky = this.linkResolver.createLink((RenderContext)this.getPage().toPageContext(), link.getDestinationSpaceKey() + ":" + link.getDestinationPageTitle());
        if (sLinky instanceof AbstractContentEntityLink) {
            return ((AbstractContentEntityLink)sLinky).getDestinationContent();
        }
        return null;
    }

    @Override
    public boolean isLatestVersionRequired() {
        return true;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return true;
    }

    public PageChangesBean getChangesSinceLastEdit() {
        try {
            if (this.getAuthenticatedUser() == null) {
                return null;
            }
            LinkedHashSet<ConfluenceUser> previousAuthorsSet = new LinkedHashSet<ConfluenceUser>();
            int edits = 0;
            for (VersionHistorySummary versionHistorySummary : this.versionHistorySummaryList) {
                if (versionHistorySummary.getLastModifier() != null && versionHistorySummary.getLastModifier().equals(this.getAuthenticatedUser())) {
                    return edits == 0 ? null : new PageChangesBean(edits, new ArrayList<ConfluenceUser>(previousAuthorsSet));
                }
                ++edits;
                previousAuthorsSet.add(versionHistorySummary.getLastModifier());
            }
            return null;
        }
        catch (Exception e) {
            log.error("Error getting changes since last edit: " + e, (Throwable)e);
            return null;
        }
    }

    public PageChangesBean getChangesSinceLastLogin() {
        try {
            if (this.getAuthenticatedUser() == null) {
                return null;
            }
            Date previousLoginDate = this.getPreviousLoginDate();
            log.debug("Previous login: {}", (Object)previousLoginDate);
            LinkedHashSet<ConfluenceUser> previousAuthorsSet = new LinkedHashSet<ConfluenceUser>();
            int edits = 0;
            for (VersionHistorySummary versionHistorySummary : this.versionHistorySummaryList) {
                Date modificationDate = versionHistorySummary.getLastModificationDate();
                if (previousLoginDate != null && (modificationDate.before(previousLoginDate) || modificationDate.equals(previousLoginDate))) {
                    return edits == 0 ? null : new PageChangesBean(edits, new ArrayList<ConfluenceUser>(previousAuthorsSet));
                }
                ++edits;
                previousAuthorsSet.add(versionHistorySummary.getLastModifier());
            }
            return null;
        }
        catch (Exception e) {
            log.error("Error getting changes since last edit: " + e, (Throwable)e);
            return null;
        }
    }

    public List<VersionHistorySummary> getLatestChanges() {
        if (this.latestChanges == null) {
            this.latestChanges = this.versionHistorySummaryList.size() > 5 ? this.versionHistorySummaryList.subList(0, 5) : this.versionHistorySummaryList;
        }
        return this.latestChanges;
    }

    public int getInitialVisibleChildCount() {
        return 10;
    }
}

