/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.actions.PaginationSupport
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.spaces.actions;

import bucket.core.actions.PaginationSupport;
import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.event.Evented;
import com.atlassian.confluence.event.events.content.attachment.AttachmentListViewEvent;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.pages.ManualTotalPaginationSupport;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.FileExtensionQuery;
import com.atlassian.confluence.search.v2.query.InSpaceQuery;
import com.atlassian.confluence.search.v2.query.LabelQuery;
import com.atlassian.confluence.search.v2.sort.CreatedSort;
import com.atlassian.confluence.search.v2.sort.FilenameSort;
import com.atlassian.confluence.search.v2.sort.FilesizeSort;
import com.atlassian.confluence.search.v2.sort.ModifiedSort;
import com.atlassian.confluence.security.access.annotations.RequiresAnyConfluenceAccess;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.LabelUtil;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

@RequiresAnyConfluenceAccess
public class ListAttachmentsAction
extends AbstractSpaceAction
implements SpaceAware,
Evented<AttachmentListViewEvent> {
    private SearchManager searchManager;
    private int startIndex;
    private String sortBy = "date";
    private String fileExtension;
    private String labels;
    private static final String PLUGIN_KEY = "space-attachments";
    private static final int COUNT_ON_EACH_PAGE = 20;
    private final PaginationSupport<Searchable> paginationSupport = new ManualTotalPaginationSupport<Searchable>(20);
    private static final String labelSplitRegex = "[ |,]";
    private SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory;

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    @XsrfProtectionExcluded
    public String execute() throws Exception {
        GeneralUtil.setCookie("confluence.browse.space.cookie", PLUGIN_KEY);
        this.initialiseAttachments();
        return "success";
    }

    @Override
    public AttachmentListViewEvent getEventToPublish(String result) {
        return new AttachmentListViewEvent(this, this.getSpace(), this.getFileExtension());
    }

    @Override
    public boolean isSpaceRequired() {
        return true;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return true;
    }

    private void initialiseAttachments() {
        SearchResults results;
        HashSet<SearchQuery> requiredQueries = new HashSet<SearchQuery>();
        requiredQueries.add(new InSpaceQuery(this.getSpaceKey()));
        requiredQueries.add(new ContentTypeQuery(ContentTypeEnum.ATTACHMENT));
        if (StringUtils.isNotBlank((CharSequence)this.getFileExtension())) {
            requiredQueries.add(new FileExtensionQuery(this.getFileExtension()));
        }
        boolean emptyResultSet = false;
        List<Label> labelList = LabelUtil.getLabelsFor(this.labels, this.labelManager);
        if (this.getLabelsCount() > 0 && labelList.isEmpty()) {
            emptyResultSet = true;
        }
        for (Label label : labelList) {
            requiredQueries.add(new LabelQuery(label));
        }
        BooleanQuery.Builder searchQueryBuilder = BooleanQuery.builder();
        searchQueryBuilder.addMust(requiredQueries);
        searchQueryBuilder.addFilter(this.siteSearchPermissionsQueryFactory.create());
        try {
            results = this.searchManager.search(new ContentSearch(searchQueryBuilder.build(), this.getSort(), this.startIndex, 20));
        }
        catch (InvalidSearchException e) {
            throw new RuntimeException("Invalid search: " + e, e);
        }
        this.paginationSupport.setStartIndex(this.startIndex);
        this.paginationSupport.setTotal(results.getUnfilteredResultsCount());
        List searchables = emptyResultSet ? Collections.emptyList() : this.searchManager.convertToEntities(results, SearchManager.EntityVersionPolicy.LATEST_VERSION);
        this.paginationSupport.setItems(searchables);
    }

    public PaginationSupport getPaginationSupport() {
        return this.paginationSupport;
    }

    private SearchSort getSort() {
        if ("date".equals(this.sortBy)) {
            return ModifiedSort.DESCENDING;
        }
        if ("createddate".equals(this.sortBy)) {
            return CreatedSort.DESCENDING;
        }
        if ("size".equals(this.sortBy)) {
            return FilesizeSort.DESCENDING;
        }
        return FilenameSort.ASCENDING;
    }

    public String getSortBy() {
        return this.sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getFileExtension() {
        return this.fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension.startsWith(".") ? fileExtension.substring(1).toLowerCase() : fileExtension.toLowerCase();
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getLabels() {
        return this.labels;
    }

    public int getLabelsCount() {
        if (StringUtils.isBlank((CharSequence)this.labels)) {
            return 0;
        }
        return this.labels.split(labelSplitRegex).length;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public void setSearchManager(SearchManager searchManager) {
        this.searchManager = searchManager;
    }

    public void setSiteSearchPermissionsQueryFactory(SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory) {
        this.siteSearchPermissionsQueryFactory = siteSearchPermissionsQueryFactory;
    }
}

