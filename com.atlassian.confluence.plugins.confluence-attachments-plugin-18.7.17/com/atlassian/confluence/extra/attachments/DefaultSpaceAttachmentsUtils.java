/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.bonnie.Handle
 *  com.atlassian.confluence.core.persistence.AnyTypeDao
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.v2.ContentSearch
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.SearchSort$Order
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery$Builder
 *  com.atlassian.confluence.search.v2.query.ContentPermissionsQuery$Builder
 *  com.atlassian.confluence.search.v2.query.ContentTypeQuery
 *  com.atlassian.confluence.search.v2.query.FileExtensionQuery
 *  com.atlassian.confluence.search.v2.query.InSpaceQuery
 *  com.atlassian.confluence.search.v2.query.LabelQuery
 *  com.atlassian.confluence.search.v2.sort.CreatedSort
 *  com.atlassian.confluence.search.v2.sort.ModifiedSort
 *  com.atlassian.confluence.search.v2.sort.RelevanceSort
 *  com.atlassian.confluence.search.v2.sort.TitleSort
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.attachments;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.bonnie.Handle;
import com.atlassian.confluence.core.persistence.AnyTypeDao;
import com.atlassian.confluence.extra.attachments.SpaceAttachments;
import com.atlassian.confluence.extra.attachments.SpaceAttachmentsUtils;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentPermissionsQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.FileExtensionQuery;
import com.atlassian.confluence.search.v2.query.InSpaceQuery;
import com.atlassian.confluence.search.v2.query.LabelQuery;
import com.atlassian.confluence.search.v2.sort.CreatedSort;
import com.atlassian.confluence.search.v2.sort.ModifiedSort;
import com.atlassian.confluence.search.v2.sort.RelevanceSort;
import com.atlassian.confluence.search.v2.sort.TitleSort;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DefaultSpaceAttachmentsUtils
implements SpaceAttachmentsUtils {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultSpaceAttachmentsUtils.class);
    private final SearchManager searchManager;
    private final AnyTypeDao anyTypeDao;
    private final UserAccessor userAccessor;

    public DefaultSpaceAttachmentsUtils(@ComponentImport SearchManager searchManager, @ComponentImport AnyTypeDao anyTypeDao, @ComponentImport UserAccessor userAccessor) {
        this.searchManager = searchManager;
        this.anyTypeDao = anyTypeDao;
        this.userAccessor = userAccessor;
    }

    @Override
    public SpaceAttachments getAttachmentList(String spaceKey, int pageNumber, int previousTotalAttachments, int pageSize, String sortBy, String fileExtension, Set<String> labels) throws InvalidSearchException {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        List groupNames = this.userAccessor.getGroupNames((User)user);
        SpaceAttachments spaceAttachments = new SpaceAttachments();
        BooleanQuery.Builder query = BooleanQuery.builder();
        query.addMust((Object)new ContentTypeQuery(ContentTypeEnum.ATTACHMENT));
        query.addMust((Object)new InSpaceQuery(spaceKey));
        if (StringUtils.isNotBlank((CharSequence)fileExtension)) {
            query.addMust((Object)new FileExtensionQuery(fileExtension));
        }
        if (labels != null) {
            labels.stream().map(label -> new LabelQuery(label.trim())).forEach(arg_0 -> ((BooleanQuery.Builder)query).addMust(arg_0));
        }
        RelevanceSort searchSort = new RelevanceSort();
        if ("name".equalsIgnoreCase(sortBy)) {
            searchSort = new TitleSort(SearchSort.Order.ASCENDING);
        } else if ("date".equalsIgnoreCase(sortBy)) {
            searchSort = new ModifiedSort(SearchSort.Order.DESCENDING);
        } else if ("createddate".equalsIgnoreCase(sortBy)) {
            searchSort = new CreatedSort(SearchSort.Order.DESCENDING);
        }
        int startIndex = 0;
        if (pageSize == 0) {
            pageSize = 20;
        }
        if (previousTotalAttachments > 0) {
            startIndex = this.calculateStartIndex(pageNumber, pageSize);
        }
        query.addFilter((SearchQuery)new ContentPermissionsQuery.Builder().user(user).groupNames(groupNames).build());
        ContentSearch search = new ContentSearch(query.build(), (SearchSort)searchSort, startIndex, pageSize);
        ArrayList<Attachment> attachmentList = new ArrayList<Attachment>();
        try {
            SearchResults searchResults = this.searchManager.search((ISearch)search);
            int totalAttachments = searchResults.getUnfilteredResultsCount();
            int totalPage = this.calculateTotalPage(totalAttachments, pageSize);
            for (SearchResult searchResult : searchResults) {
                Handle handle = searchResult.getHandle();
                Attachment attachment = (Attachment)this.anyTypeDao.findByHandle(handle);
                attachmentList.add(attachment);
            }
            spaceAttachments.setAttachmentList(attachmentList);
            spaceAttachments.setTotalAttachments(totalAttachments);
            spaceAttachments.setTotalPage(totalPage);
        }
        catch (InvalidSearchException e) {
            LOG.error("Invalid search exception ", (Throwable)e);
            throw new InvalidSearchException(e.getMessage());
        }
        return spaceAttachments;
    }

    @VisibleForTesting
    protected int calculateTotalPage(int totalAttachments, int pageSize) {
        double dPageTotal = Math.ceil((double)totalAttachments / (double)pageSize);
        return (int)dPageTotal;
    }

    @VisibleForTesting
    protected int calculateStartIndex(int pageNumber, int pageSize) {
        return Math.max(0, (pageNumber - 1) * pageSize);
    }
}

