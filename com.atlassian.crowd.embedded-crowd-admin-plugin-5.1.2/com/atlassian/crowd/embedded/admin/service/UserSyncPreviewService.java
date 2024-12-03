/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.directory.RemoteDirectory
 *  com.atlassian.crowd.directory.loader.DirectoryInstanceLoader
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.SearchRestriction
 *  com.atlassian.crowd.embedded.api.User
 *  com.atlassian.crowd.exception.OperationFailedException
 *  com.atlassian.crowd.model.user.User
 *  com.atlassian.crowd.search.EntityDescriptor
 *  com.atlassian.crowd.search.builder.Combine
 *  com.atlassian.crowd.search.builder.QueryBuilder
 *  com.atlassian.crowd.search.builder.Restriction
 *  com.atlassian.crowd.search.query.entity.EntityQuery
 *  com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction
 *  com.atlassian.crowd.search.query.entity.restriction.Property
 *  com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys
 *  com.atlassian.sal.api.message.I18nResolver
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Service
 *  org.springframework.util.StringUtils
 */
package com.atlassian.crowd.embedded.admin.service;

import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.directory.loader.DirectoryInstanceLoader;
import com.atlassian.crowd.embedded.admin.dto.PageLink;
import com.atlassian.crowd.embedded.admin.dto.UserSyncPreviewRequest;
import com.atlassian.crowd.embedded.admin.dto.UserSyncPreviewResult;
import com.atlassian.crowd.embedded.admin.dto.UserSyncPreviewUserDto;
import com.atlassian.crowd.embedded.admin.service.GravatarService;
import com.atlassian.crowd.embedded.admin.service.PaginationService;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.crowd.exception.OperationFailedException;
import com.atlassian.crowd.search.EntityDescriptor;
import com.atlassian.crowd.search.builder.Combine;
import com.atlassian.crowd.search.builder.QueryBuilder;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.EntityQuery;
import com.atlassian.crowd.search.query.entity.restriction.BooleanRestriction;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import com.atlassian.sal.api.message.I18nResolver;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class UserSyncPreviewService {
    private static final Logger log = LoggerFactory.getLogger(UserSyncPreviewService.class);
    protected static final int USERS_PER_PAGE = 50;
    protected static final int MAX_DISPLAY_USERS_COUNT = 10000;
    protected static final int MAX_USERS_COUNT = 10001;
    @Autowired
    private DirectoryInstanceLoader directoryInstanceLoader;
    @Autowired
    private I18nResolver i18nResolver;
    @Autowired
    private PaginationService paginationService;
    @Autowired
    private GravatarService gravatarService;

    public UserSyncPreviewResult getUserPreviewResult(Directory directory, UserSyncPreviewRequest request) throws Exception {
        int totalUsersCount;
        RemoteDirectory remoteDirectory = this.directoryInstanceLoader.getRawDirectory(null, directory.getImplementationClass(), directory.getAttributes());
        String usersPreviewFilter = request.getFilter();
        int pageNumberRequestParam = request.getPageNumber();
        UserSyncPreviewResult result = new UserSyncPreviewResult();
        log.debug("Calculating total users count");
        if (request.getTotalUsersCount() >= 0) {
            totalUsersCount = request.getTotalUsersCount();
            log.debug("Taken from Request. Total count = " + totalUsersCount);
        } else {
            totalUsersCount = this.queryForTotalUsersCount(remoteDirectory, usersPreviewFilter);
            log.debug("Queried in Remote Directory with filter='" + usersPreviewFilter + "'. Total count = " + totalUsersCount);
        }
        result.setTotalUsersCount(totalUsersCount);
        int displayTotalUsersCount = Math.min(totalUsersCount, 10000);
        int pagesCount = this.paginationService.getPagesCount(displayTotalUsersCount, 50);
        int pageNumber = Math.min(pageNumberRequestParam, pagesCount);
        log.debug("Fetching users from RemoteDirectory. Page=" + pageNumber + ", Filter='" + usersPreviewFilter + "'");
        List<com.atlassian.crowd.model.user.User> userList = this.searchUsers(remoteDirectory, usersPreviewFilter, pageNumber);
        int displayUsersCount = userList.size();
        log.debug("Fetched " + displayUsersCount + " users");
        ArrayList<UserSyncPreviewUserDto> userListResult = new ArrayList<UserSyncPreviewUserDto>();
        for (com.atlassian.crowd.model.user.User user : userList) {
            UserSyncPreviewUserDto userDto = new UserSyncPreviewUserDto((User)user);
            String emailHash = this.gravatarService.calculateEmailHash(user.getEmailAddress());
            userDto.setEmailHash(emailHash);
            userListResult.add(userDto);
        }
        result.setUsers(userListResult);
        String usersCountSubtitle = this.getUsersCountSubtitle(totalUsersCount, displayUsersCount);
        result.setUsersCountSubtitle(usersCountSubtitle);
        List<PageLink> pageLinks = this.paginationService.getPageLinks(displayTotalUsersCount, 50, pageNumber);
        result.setPageLinks(pageLinks);
        return result;
    }

    protected int queryForTotalUsersCount(RemoteDirectory remoteDirectory, String usersPreviewFilter) throws OperationFailedException {
        EntityQuery<String> query = this.getPagedQueryForUsersCount(usersPreviewFilter);
        return remoteDirectory.searchUsers(query).size();
    }

    protected EntityQuery<com.atlassian.crowd.model.user.User> getSearchQuery(String usersPreviewFilter, int pageNumber) {
        int pageIndex = pageNumber - 1;
        if (StringUtils.isEmpty((Object)usersPreviewFilter)) {
            return this.getPagedQuery(pageIndex);
        }
        return this.getPagedQueryWithFilter(pageIndex, usersPreviewFilter);
    }

    private List<com.atlassian.crowd.model.user.User> searchUsers(RemoteDirectory remoteDirectory, String usersPreviewFilter, int pageNumber) throws OperationFailedException {
        EntityQuery<com.atlassian.crowd.model.user.User> searchQuery = this.getSearchQuery(usersPreviewFilter, pageNumber);
        return remoteDirectory.searchUsers(searchQuery);
    }

    private EntityQuery<com.atlassian.crowd.model.user.User> getPagedQuery(int pageIndex) {
        int startIndex = pageIndex * 50;
        return QueryBuilder.queryFor(com.atlassian.crowd.model.user.User.class, (EntityDescriptor)EntityDescriptor.user()).startingAt(startIndex).returningAtMost(50);
    }

    private EntityQuery<com.atlassian.crowd.model.user.User> getPagedQueryWithFilter(int pageIndex, String usersPreviewFilter) {
        int startIndex = pageIndex * 50;
        return QueryBuilder.queryFor(com.atlassian.crowd.model.user.User.class, (EntityDescriptor)EntityDescriptor.user()).with((SearchRestriction)this.filter(usersPreviewFilter)).startingAt(startIndex).returningAtMost(50);
    }

    private BooleanRestriction filter(String usersPreviewFilter) {
        return Combine.anyOf((SearchRestriction[])new SearchRestriction[]{Restriction.on((Property)UserTermKeys.USERNAME).containing((Object)usersPreviewFilter), Restriction.on((Property)UserTermKeys.DISPLAY_NAME).containing((Object)usersPreviewFilter), Restriction.on((Property)UserTermKeys.FIRST_NAME).containing((Object)usersPreviewFilter), Restriction.on((Property)UserTermKeys.LAST_NAME).containing((Object)usersPreviewFilter), Restriction.on((Property)UserTermKeys.EMAIL).containing((Object)usersPreviewFilter)});
    }

    private EntityQuery<String> getPagedQueryForUsersCount(String usersPreviewFilter) {
        if (usersPreviewFilter.isEmpty()) {
            return this.getPagedQueryForUsersCount();
        }
        return QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.user()).with((SearchRestriction)this.filter(usersPreviewFilter)).returningAtMost(10001);
    }

    private EntityQuery<String> getPagedQueryForUsersCount() {
        return QueryBuilder.queryFor(String.class, (EntityDescriptor)EntityDescriptor.user()).returningAtMost(10001);
    }

    private String getUsersCountSubtitle(int totalUsersCount, int displayUsersCount) {
        if (totalUsersCount > 10000) {
            return this.i18nResolver.getText("embedded.crowd.directory.users.preview.subtitle.excess", new Serializable[]{Integer.valueOf(displayUsersCount), Integer.valueOf(10000)});
        }
        return this.i18nResolver.getText("embedded.crowd.directory.users.preview.subtitle.norm", new Serializable[]{Integer.valueOf(displayUsersCount), Integer.valueOf(totalUsersCount)});
    }

    public void setDirectoryInstanceLoader(DirectoryInstanceLoader directoryInstanceLoader) {
        this.directoryInstanceLoader = directoryInstanceLoader;
    }

    public void setI18nResolver(I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }

    public void setPaginationService(PaginationService paginationService) {
        this.paginationService = paginationService;
    }

    public void setGravatarService(GravatarService gravatarService) {
        this.gravatarService = gravatarService;
    }
}

