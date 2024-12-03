/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult$Builder
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.api.service.content.ContentService$ContentFinder
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.api.service.exceptions.ReadOnlyException
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.persistence.EntityManagerProvider
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport
 *  com.atlassian.user.User
 *  com.google.common.annotations.VisibleForTesting
 *  javax.persistence.EntityManager
 *  javax.persistence.Query
 *  org.apache.commons.lang3.ObjectUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.pagehierarchy.validation;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.api.service.exceptions.ReadOnlyException;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.persistence.EntityManagerProvider;
import com.atlassian.confluence.plugins.pagehierarchy.rest.CopyPageHierarchyRequest;
import com.atlassian.confluence.plugins.pagehierarchy.rest.CopyPageHierarchyTitleOptions;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport;
import com.atlassian.user.User;
import com.google.common.annotations.VisibleForTesting;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CopyPageHierarchyValidator {
    private static final Logger log = LoggerFactory.getLogger(CopyPageHierarchyValidator.class);
    private static final String subtreeTitlesQuery;
    private static final String spacePagesLowerTitleQuery;
    private static final String prefixOnlyOption;
    private static final String searchReplaceOnlyOption;
    private static final String prefixAndSearchReplaceOption;
    private static final String defaultOption;
    private final ContentService contentService;
    private final PermissionManager permissionManager;
    private final SpaceManager spaceManager;
    private final AccessModeService accessModeService;
    private final EntityManagerProvider entityManagerProvider;
    private static final String MAX_PROCESSED_ENTRIES_PROPERTY = "confluence.cph.max.entries";
    public static final int MAX_PAGES;
    public static final int MAX_TITLE_LENGTH = 255;

    @Autowired
    public CopyPageHierarchyValidator(@ConfluenceImport ContentService contentService, @ConfluenceImport EntityManagerProvider entityManagerProvider, @ConfluenceImport PermissionManager permissionManager, @ConfluenceImport SpaceManager spaceManager, AccessModeService accessModeService) {
        this.contentService = contentService;
        this.entityManagerProvider = entityManagerProvider;
        this.permissionManager = permissionManager;
        this.spaceManager = spaceManager;
        this.accessModeService = accessModeService;
    }

    public void validate(CopyPageHierarchyRequest request) {
        this.validateAccessMode();
        this.validateRequestPermissions(request);
        this.validateCopyLimits(request);
        this.validatePageTitles(request);
    }

    private Query generatePageTitlesQuery(CopyPageHierarchyRequest request) {
        Optional destinationPage = this.contentService.find(new Expansion[]{new Expansion("space")}).withId(request.getDestinationPageId()).fetch();
        Optional originalPage = this.contentService.find(new Expansion[]{new Expansion("space")}).withId(request.getOriginalPageId()).fetch();
        this.validatePagesExistAndAreViewable(originalPage, destinationPage);
        long originalSpaceId = ((Content)originalPage.get()).getSpace().getId();
        long destinationSpaceId = ((Content)destinationPage.get()).getSpace().getId();
        long originalPageId = request.getOriginalPageId().asLong();
        CopyPageHierarchyTitleOptions titleOptions = request.getTitleOptions();
        String prefix = titleOptions.getPrefix();
        String search = titleOptions.getSearch();
        String replace = titleOptions.getReplace();
        StringBuilder queryBuilder = new StringBuilder(subtreeTitlesQuery).append("and ");
        if (ObjectUtils.isNotEmpty((Object)prefix) && ObjectUtils.isNotEmpty((Object)search)) {
            queryBuilder.append(prefixAndSearchReplaceOption);
        } else if (ObjectUtils.isNotEmpty((Object)prefix) && StringUtils.isEmpty((CharSequence)search)) {
            queryBuilder.append(prefixOnlyOption);
        } else if (StringUtils.isEmpty((CharSequence)prefix) && ObjectUtils.isNotEmpty((Object)search)) {
            queryBuilder.append(searchReplaceOnlyOption);
        } else if (StringUtils.isEmpty((CharSequence)prefix) && StringUtils.isEmpty((CharSequence)search)) {
            queryBuilder.append(defaultOption);
        }
        queryBuilder.append(String.format(" in (%s)", spacePagesLowerTitleQuery));
        EntityManager entityManager = this.entityManagerProvider.getEntityManager();
        Query titleQuery = entityManager.createQuery(queryBuilder.toString());
        titleQuery.setParameter("originalPageId", (Object)originalPageId).setParameter("destinationSpaceId", (Object)destinationSpaceId).setParameter("originalSpaceId", (Object)originalSpaceId);
        if (ObjectUtils.isNotEmpty((Object)prefix)) {
            titleQuery.setParameter("prefix", (Object)GeneralUtil.specialToLowerCase((String)prefix));
        }
        if (ObjectUtils.isNotEmpty((Object)search)) {
            titleQuery.setParameter("search", (Object)GeneralUtil.specialToLowerCase((String)search)).setParameter("replace", (Object)GeneralUtil.specialToLowerCase((String)replace));
        }
        return titleQuery;
    }

    @VisibleForTesting
    void validatePageTitles(CopyPageHierarchyRequest request) {
        Query titleQuery = this.generatePageTitlesQuery(request);
        List results = titleQuery.getResultList();
        log.debug(results.toString());
        if (!results.isEmpty()) {
            ValidationResult validationResult = SimpleValidationResult.builder().addError("copy.page.hierarchy.dialog.page.title.conflict", results.toArray()).build();
            throw new BadRequestException("The following pages have conflicting page titles.", validationResult);
        }
    }

    @VisibleForTesting
    void validateCopyLimits(CopyPageHierarchyRequest request) {
        CopyPageHierarchyTitleOptions titleOptions = request.getTitleOptions();
        Optional originalPage = this.contentService.find(new Expansion[]{new Expansion("space")}).withId(request.getOriginalPageId()).fetch();
        this.checkValidationResult(this.checkOriginalPage(originalPage, SimpleValidationResult.builder().authorized(true)));
        long originalSpaceId = ((Content)originalPage.get()).getSpace().getId();
        EntityManager entityManager = this.entityManagerProvider.getEntityManager();
        Query titlesQuery = entityManager.createQuery(subtreeTitlesQuery).setParameter("originalPageId", (Object)request.getOriginalPageId().asLong()).setParameter("originalSpaceId", (Object)originalSpaceId).setMaxResults(MAX_PAGES + 1);
        List titles = titlesQuery.getResultList();
        if (titles.size() > MAX_PAGES) {
            ValidationResult validationResult = SimpleValidationResult.builder().addError("copy.page.hierarchy.dialog.over.page.limit", new Object[]{MAX_PAGES}).build();
            throw new BadRequestException("You are trying to copy too many pages, the maximum is " + MAX_PAGES + ".", validationResult);
        }
        Pattern pattern = Pattern.compile(Pattern.quote(titleOptions.getSearch()), 2);
        List invalidTitles = titles.stream().filter(title -> {
            if (StringUtils.isNotBlank((CharSequence)titleOptions.getPrefix())) {
                title = titleOptions.getPrefix() + (String)title;
            }
            if (StringUtils.isNotBlank((CharSequence)titleOptions.getSearch())) {
                title = pattern.matcher((CharSequence)title).replaceAll(Pattern.quote(titleOptions.getReplace()));
            }
            return ((String)title).length() > 255;
        }).collect(Collectors.toList());
        if (!invalidTitles.isEmpty()) {
            ValidationResult validationResult = SimpleValidationResult.builder().addError("copy.page.hierarchy.dialog.error.title.length", invalidTitles.toArray()).build();
            String message = "The following page titles will exceed the maximum allowed length of 255 characters.";
            throw new BadRequestException(message, validationResult);
        }
    }

    @VisibleForTesting
    void validateAccessMode() {
        if (this.accessModeService.isReadOnlyAccessModeEnabled()) {
            throw new ReadOnlyException();
        }
    }

    @VisibleForTesting
    void validateRequestPermissions(CopyPageHierarchyRequest request) {
        ContentService.ContentFinder contentFinder = this.contentService.find(new Expansion[]{new Expansion("space")});
        Optional originalPage = contentFinder.withId(request.getOriginalPageId()).fetch();
        Optional destinationPage = contentFinder.withId(request.getDestinationPageId()).fetch();
        this.validatePagesExistAndAreViewable(originalPage, destinationPage);
        if (destinationPage.isPresent()) {
            Content destination = (Content)destinationPage.get();
            Space space = this.spaceManager.getSpace(destination.getSpace().getKey());
            boolean hasPermission = this.permissionManager.hasCreatePermission((User)AuthenticatedUserThreadLocal.get(), (Object)space, Page.class);
            if (!hasPermission) {
                ValidationResult validationResult = SimpleValidationResult.builder().addError("copy.page.hierarchy.validation.noCreatePagePermission", new Object[0]).build();
                throw new PermissionException("You are not allowed to perform that action.", validationResult);
            }
        }
    }

    private void validatePagesExistAndAreViewable(Optional<Content> originalPage, Optional<Content> destinationPage) {
        SimpleValidationResult.Builder validationResultBuilder = SimpleValidationResult.builder().authorized(true);
        this.checkOriginalPage(originalPage, validationResultBuilder);
        this.checkDestinationPage(destinationPage, validationResultBuilder);
        this.checkValidationResult(validationResultBuilder);
    }

    private void checkValidationResult(SimpleValidationResult.Builder builder) {
        ValidationResult validationResult = builder.build();
        if (validationResult.isNotSuccessful()) {
            throw new BadRequestException("The supplied parameters are invalid.", validationResult);
        }
    }

    private SimpleValidationResult.Builder checkOriginalPage(Optional<Content> page, SimpleValidationResult.Builder builder) {
        if (!page.isPresent()) {
            builder.addFieldError("originalPageId", "copy.page.hierarchy.dialog.invalid.origin", new Object[0]);
        }
        return builder;
    }

    private SimpleValidationResult.Builder checkDestinationPage(Optional<Content> page, SimpleValidationResult.Builder builder) {
        if (!page.isPresent()) {
            builder.addFieldError("destinationPageId", "copy.page.hierarchy.dialog.invalid.destination", new Object[0]);
        }
        return builder;
    }

    static {
        MAX_PAGES = Integer.getInteger(MAX_PROCESSED_ENTRIES_PROPERTY, 2000);
        subtreeTitlesQuery = "select distinct page.title from Page page left join page.ancestors as ancestor where (ancestor.id = :originalPageId or page.id = :originalPageId) and page.space.id = :originalSpaceId and page.originalVersion is null and page.contentStatus = 'current' ";
        spacePagesLowerTitleQuery = "select page.lowerTitle from Page page where (page.space.id = :destinationSpaceId) and page.originalVersion is null and page.contentStatus = 'current' ";
        prefixOnlyOption = "ltrim(rtrim(concat(:prefix, page.lowerTitle)))";
        searchReplaceOnlyOption = "ltrim(rtrim(replace(page.lowerTitle, :search, :replace)))";
        prefixAndSearchReplaceOption = "ltrim(rtrim(concat(:prefix, replace(page.lowerTitle, :search, :replace))))";
        defaultOption = "page.lowerTitle";
    }
}

