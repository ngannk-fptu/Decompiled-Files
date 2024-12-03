/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.web.Icon
 *  com.atlassian.confluence.api.service.search.CQLSearchService
 *  com.atlassian.confluence.search.actions.json.ContentNameMatch
 *  com.atlassian.confluence.search.actions.json.ContentNameSearchResult
 *  com.atlassian.confluence.search.contentnames.Category
 *  com.atlassian.confluence.search.contentnames.ContentNameSearchContext
 *  com.atlassian.confluence.search.contentnames.ContentNameSearchService
 *  com.atlassian.confluence.spaces.SpaceLogo
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.web.context.StaticHttpContext
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.efi.services;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.web.Icon;
import com.atlassian.confluence.api.service.search.CQLSearchService;
import com.atlassian.confluence.efi.rest.beans.RelevantSpaceBean;
import com.atlassian.confluence.efi.services.FindRelevantSpacesService;
import com.atlassian.confluence.search.actions.json.ContentNameMatch;
import com.atlassian.confluence.search.actions.json.ContentNameSearchResult;
import com.atlassian.confluence.search.contentnames.Category;
import com.atlassian.confluence.search.contentnames.ContentNameSearchContext;
import com.atlassian.confluence.search.contentnames.ContentNameSearchService;
import com.atlassian.confluence.spaces.SpaceLogo;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.web.context.StaticHttpContext;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FindRelevantSpacesServiceImpl
implements FindRelevantSpacesService {
    private CQLSearchService cqlSearchService;
    private ContentNameSearchService contentNameSearchService;
    private SpaceManager spaceManager;
    private UserAccessor userAccessor;

    @Autowired
    public FindRelevantSpacesServiceImpl(@ComponentImport CQLSearchService cqlSearchService, @ComponentImport ContentNameSearchService contentNameSearchService, @ComponentImport SpaceManager spaceManager, @ComponentImport UserAccessor userAccessor) {
        this.cqlSearchService = cqlSearchService;
        this.contentNameSearchService = contentNameSearchService;
        this.spaceManager = spaceManager;
        this.userAccessor = userAccessor;
    }

    @Override
    public List<RelevantSpaceBean> getRelevantSpaces(HttpServletRequest httpServletRequest) {
        ArrayList relevantSpaces = Lists.newArrayList();
        this.addSpaceRankFromRecentlyCreatedPages(relevantSpaces, httpServletRequest);
        Collections.sort(relevantSpaces);
        return relevantSpaces;
    }

    @Override
    public List<RelevantSpaceBean> getRelevantSpaces(String query, HttpServletRequest httpServletRequest) {
        int maxHitsPerCategory = 10;
        ArrayList<RelevantSpaceBean> relevantSpaces = new ArrayList<RelevantSpaceBean>();
        ContentNameSearchContext contentNameSearchContext = new ContentNameSearchContext(Arrays.asList(Category.SPACES.getName(), Category.PEOPLE.getName()), null, 10, httpServletRequest, -1);
        ContentNameSearchResult result = this.contentNameSearchService.search(query, contentNameSearchContext);
        Iterable matches = Iterables.concat((Iterable)result.getContentNameMatches());
        String contextPath = this.getContextPath(httpServletRequest);
        for (ContentNameMatch match : matches) {
            if (this.isSearchForMore(match) || this.isUserWithoutPersonalSpace(match)) continue;
            relevantSpaces.add(new RelevantSpaceBean((String)MoreObjects.firstNonNull((Object)match.getSpaceKey(), (Object)("~" + match.getUsername())), (String)MoreObjects.firstNonNull((Object)match.getSpaceName(), (Object)match.getName()), 0, this.getIcon(match, contextPath)));
        }
        return relevantSpaces;
    }

    private Icon getIcon(ContentNameMatch match, String contextPath) {
        return match.getIcon() != null ? this.createIcon(match.getIcon()) : this.createIcon(this.spaceManager.getLogoForSpace(match.getSpaceKey()), contextPath);
    }

    private boolean isUserWithoutPersonalSpace(ContentNameMatch match) {
        if (match.getUsername() == null) {
            return false;
        }
        ConfluenceUser user = this.userAccessor.getUserByName(match.getUsername());
        return user != null && this.spaceManager.getPersonalSpace(user) == null;
    }

    private boolean isSearchForMore(ContentNameMatch match) {
        return "search-for".equals(match.getClassName());
    }

    private Icon createIcon(SpaceLogo logo, String contextPath) {
        return new Icon(contextPath + logo.getDownloadPath(), 48, 48, logo.isDefaultLogo());
    }

    private Icon createIcon(String path) {
        return new Icon(path, 48, 48, false);
    }

    private void addSpaceRankFromRecentlyCreatedPages(List<RelevantSpaceBean> relevantSpaces, HttpServletRequest httpServletRequest) {
        PageResponse response = this.cqlSearchService.searchContent("type in (page,blogpost) order by created desc", (PageRequest)new SimplePageRequest(0, 200), new Expansion[]{new Expansion("space", new Expansions(new Expansion[]{new Expansion("icon")}))});
        this.addRankFromPageHits(response.getResults(), relevantSpaces, httpServletRequest);
    }

    private void addRankFromPageHits(List<Content> contents, List<RelevantSpaceBean> relevantSpaces, HttpServletRequest httpServletRequest) {
        String contextPath = this.getContextPath(httpServletRequest);
        for (Content content : contents) {
            RelevantSpaceBean space = this.getSpaceWithKey(content.getSpace().getKey(), relevantSpaces);
            if (space != null) {
                space.setSpaceRank(space.getSpaceRank() + 1);
                continue;
            }
            Space spaceHit = content.getSpace();
            Icon icon = (Icon)spaceHit.getIconRef().get();
            Icon spaceIcon = new Icon(contextPath + icon.getPath(), icon.getWidth(), icon.getHeight(), icon.getIsDefault());
            boolean initialSpaceRank = true;
            relevantSpaces.add(new RelevantSpaceBean(spaceHit.getKey(), spaceHit.getName(), 1, spaceIcon));
        }
    }

    private String getContextPath(HttpServletRequest httpServletRequest) {
        return httpServletRequest == null ? new StaticHttpContext().getRequest().getContextPath() : httpServletRequest.getContextPath();
    }

    private RelevantSpaceBean getSpaceWithKey(String key, List<RelevantSpaceBean> relevantSpaces) {
        return (RelevantSpaceBean)Iterables.find(relevantSpaces, relevantSpaceBean -> key.equals(relevantSpaceBean.getKey()), null);
    }
}

