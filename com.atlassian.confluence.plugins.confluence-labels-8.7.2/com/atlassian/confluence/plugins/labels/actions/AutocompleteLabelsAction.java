/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.service.SpaceService
 *  com.atlassian.confluence.core.Beanable
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.search.actions.json.ContentNameSearchResult
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.plugins.labels.actions;

import com.atlassian.confluence.content.service.SpaceService;
import com.atlassian.confluence.core.Beanable;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.labels.actions.LabelSuggestionService;
import com.atlassian.confluence.search.actions.json.ContentNameSearchResult;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiresAnyConfluenceAccess
public class AutocompleteLabelsAction
extends ConfluenceActionSupport
implements Beanable {
    static final int DEFAULT_MAX = 50;
    private SpaceService spaceService;
    private PageManager pageManager;
    private LabelSuggestionService labelSuggestionService;
    private String query;
    private long contentId;
    private int maxResults;
    private ContentNameSearchResult result;
    private List<String> errors = new ArrayList<String>();
    private String spaceKey;
    private boolean ignoreRelated;
    private boolean isTeamLabel;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        this.validateRequest();
        if (!this.errors.isEmpty()) {
            return "error";
        }
        this.result = this.labelSuggestionService.findSuggestedLabels(this.query.trim().toLowerCase(), this.isTeamLabel, this.maxResults, this.spaceKey, this.ignoreRelated, this.pageManager.getAbstractPage(this.contentId));
        return "success";
    }

    private void validateRequest() {
        Space space;
        if (this.query == null) {
            this.errors.add(this.getText("autocomplete.label.query.empty"));
        }
        if (this.contentId != 0L) {
            AbstractPage page = this.pageManager.getAbstractPage(this.contentId);
            if (page == null || !this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.VIEW, (Object)page)) {
                this.errors.add(this.getText("page.doesnt.exist"));
            } else if (this.spaceKey != null && !Objects.equals(this.spaceKey, page.getSpaceKey())) {
                this.errors.add(this.getText("content.space.mismatch"));
            } else if (this.spaceKey == null) {
                this.spaceKey = page.getSpaceKey();
            }
        }
        if (!(this.spaceKey == null || (space = this.spaceService.getKeySpaceLocator(this.spaceKey).getSpace()) != null && this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.VIEW, (Object)space))) {
            this.errors.add(this.getText("space.permission.or.nonexistent"));
        }
        if (this.maxResults <= 0 || this.maxResults > 50) {
            this.maxResults = 50;
        }
    }

    public Object getBean() {
        if (this.result != null) {
            return this.result;
        }
        return this.errors;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setSpaceService(SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    public void setPageManager(PageManager pageManager) {
        this.pageManager = pageManager;
    }

    public void setLabelSuggestionService(LabelSuggestionService labelSuggestionService) {
        this.labelSuggestionService = labelSuggestionService;
    }

    public int getMaxResults() {
        return this.maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public void setIgnoreRelated(boolean ignoreRelated) {
        this.ignoreRelated = ignoreRelated;
    }

    public void setIsTeamLabel(boolean isTeamLabel) {
        this.isTeamLabel = isTeamLabel;
    }
}

