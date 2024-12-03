/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult$Builder
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.api.service.content.ContentService$ContentFinder
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.api.service.exceptions.ReadOnlyException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport
 *  com.atlassian.user.User
 *  com.google.common.annotations.VisibleForTesting
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.pagehierarchy.validation;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.api.service.exceptions.ReadOnlyException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.plugins.pagehierarchy.rest.DeletePageHierarchyRequest;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport;
import com.atlassian.user.User;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeletePageHierarchyValidator {
    private final ContentService contentService;
    private final PageManager pageManager;
    private final PermissionManager permissionManager;
    private final SpacePermissionManager spacePermissionManager;
    private final AccessModeService accessModeService;
    private static final String MAX_PROCESSED_ENTRIES_PROPERTY = "confluence.dph.max.entries";
    public static final int MAX_PAGES = Integer.getInteger("confluence.dph.max.entries", 2000);

    @Autowired
    public DeletePageHierarchyValidator(@ConfluenceImport ContentService contentService, @ConfluenceImport PageManager pageManager, @ConfluenceImport PermissionManager permissionManager, @ConfluenceImport SpacePermissionManager spacePermissionManager, AccessModeService accessModeService) {
        this.contentService = contentService;
        this.pageManager = pageManager;
        this.permissionManager = permissionManager;
        this.spacePermissionManager = spacePermissionManager;
        this.accessModeService = accessModeService;
    }

    public void validateRequest(DeletePageHierarchyRequest request) throws ServiceException {
        this.validateAccessMode();
        this.validateRequestPermissions(request);
    }

    @VisibleForTesting
    void validateAccessMode() throws ServiceException {
        if (this.accessModeService.isReadOnlyAccessModeEnabled()) {
            throw new ReadOnlyException();
        }
    }

    @VisibleForTesting
    void validateRequestPermissions(DeletePageHierarchyRequest request) throws ServiceException {
        ValidationResult validationResult;
        ContentService.ContentFinder contentFinder = this.contentService.find(new Expansion[]{new Expansion("space")});
        SimpleValidationResult.Builder validationResultBuilder = SimpleValidationResult.builder().authorized(true);
        Option potentialTargetPage = contentFinder.withId(request.getTargetPageId()).fetchOne();
        if (potentialTargetPage.isEmpty()) {
            validationResultBuilder.addFieldError("targetPageId", "delete.page.hierarchy.dialog.invalid.target", new Object[0]);
        }
        if (potentialTargetPage.isDefined()) {
            Page target = this.pageManager.getPage(request.getTargetPageId().asLong());
            boolean hasDeletePermission = this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.REMOVE, (Object)target);
            if (!hasDeletePermission) {
                ValidationResult validationResult2 = SimpleValidationResult.builder().addError("delete.page.hierarchy.validation.noDeletePagePermission", new Object[0]).build();
                throw new PermissionException("delete.page.hierarchy.validation.noDeletePagePermission", validationResult2);
            }
            boolean hasSpaceLevelRemovePermission = this.spacePermissionManager.hasPermission("REMOVEPAGE", target.getSpace(), (User)AuthenticatedUserThreadLocal.get());
            if (!hasSpaceLevelRemovePermission && request.isDeleteHierarchy()) {
                validationResultBuilder.addFieldError("deleteHierarchy", "delete.page.hierarchy.dialog.invalid.deleteHierarchy", new Object[0]);
            }
            if (request.isDeleteHierarchy() && this.pageManager.countPagesInSubtree(this.pageManager.getPage(request.getTargetPageId().asLong())) > MAX_PAGES) {
                ValidationResult validationResult3 = SimpleValidationResult.builder().addError("delete.page.hierarchy.validation.maxPagesExceeded", new Object[]{MAX_PAGES}).build();
                throw new BadRequestException("You are trying to delete too many pages, the maximum is " + MAX_PAGES + ".", validationResult3);
            }
        }
        if ((validationResult = validationResultBuilder.build()).isNotSuccessful()) {
            throw new BadRequestException("The supplied parameters are invalid.", validationResult);
        }
    }
}

