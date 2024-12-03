/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.people.KnownUser
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.model.watch.ContentWatch
 *  com.atlassian.confluence.api.model.watch.SpaceWatch
 *  com.atlassian.confluence.api.service.accessmode.AccessModeService
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.api.service.content.SpaceService
 *  com.atlassian.confluence.api.service.exceptions.NotFoundException
 *  com.atlassian.confluence.api.service.exceptions.ReadOnlyException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.api.service.watch.WatchService
 *  com.atlassian.confluence.api.service.watch.WatchService$Validator
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.user.User
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.api.impl.service.watch;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.people.KnownUser;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.model.watch.ContentWatch;
import com.atlassian.confluence.api.model.watch.SpaceWatch;
import com.atlassian.confluence.api.service.accessmode.AccessModeService;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.ReadOnlyException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.watch.WatchService;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.mail.notification.NotificationManager;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserResolver;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public class WatchServiceImpl
implements WatchService {
    private static final Logger logger = LoggerFactory.getLogger(WatchServiceImpl.class);
    private final ConfluenceUserResolver confluenceUserResolver;
    private final PermissionManager permissionManager;
    private final NotificationManager notificationManager;
    private final SpaceManager spaceManager;
    private final SpaceService spaceService;
    private final ContentEntityManager contentEntityManager;
    private final ContentService contentService;
    private final AccessModeService accessModeService;

    public WatchServiceImpl(ConfluenceUserResolver confluenceUserResolver, PermissionManager permissionManager, NotificationManager notificationManager, SpaceManager spaceManager, SpaceService spaceService, ContentEntityManager contentEntityManager, ContentService contentService, AccessModeService accessModeService) {
        this.confluenceUserResolver = confluenceUserResolver;
        this.permissionManager = permissionManager;
        this.notificationManager = notificationManager;
        this.spaceManager = spaceManager;
        this.spaceService = spaceService;
        this.contentEntityManager = contentEntityManager;
        this.contentService = contentService;
        this.accessModeService = accessModeService;
    }

    public boolean isWatchingSpace(UserKey userKey, String spaceKey, ContentType contentType) {
        logger.debug("isWatchingContentInSpace: space: {} contentType: {}", (Object)spaceKey, (Object)contentType);
        User user = this.findUserByUserKey(userKey);
        if (user == null) {
            return false;
        }
        Space space = this.spaceManager.getSpace(spaceKey);
        if (space == null) {
            throw new NotFoundException();
        }
        this.canView(user, space).throwIfNotSuccessful("User not permitted to view space");
        Notification notification = this.notificationManager.getNotificationByUserAndSpaceAndType(user, space, ContentTypeEnum.getByRepresentation(contentType.getType()));
        return notification != null;
    }

    public final @NonNull SpaceWatch watchSpace(UserKey userKey, String spaceKey, List<ContentType> contentTypes) {
        logger.debug("watchSpace: space: {}", (Object)spaceKey);
        User user = this.findUserByUserKey(userKey);
        Space space = this.spaceManager.getSpace(spaceKey);
        this.enforceReadOnlyAccess();
        if (space == null) {
            throw new NotFoundException();
        }
        this.canView(user, space).throwIfNotSuccessful("User not permitted to view space");
        if (contentTypes.isEmpty()) {
            this.notificationManager.addSpaceNotification(user, space);
        } else if (contentTypes.contains(ContentType.BLOG_POST)) {
            this.notificationManager.addSpaceNotification(user, space, ContentTypeEnum.getByRepresentation(ContentType.BLOG_POST.getType()));
        } else {
            throw new ServiceException("The list must contain ContentType.BLOG_POST");
        }
        return new SpaceWatch(KnownUser.fromUserkey((UserKey)userKey), spaceKey, contentTypes);
    }

    public final @NonNull SpaceWatch watchSpace(UserKey userKey, String spaceKey) {
        return this.watchSpace(userKey, spaceKey, new ArrayList<ContentType>());
    }

    public final void unwatchSpace(UserKey userKey, String spaceKey, List<ContentType> contentTypes) {
        logger.debug("unwatchSpace: space: {}", (Object)spaceKey);
        User user = this.findUserByUserKey(userKey);
        Space space = this.spaceManager.getSpace(spaceKey);
        this.enforceReadOnlyAccess();
        if (space == null) {
            throw new NotFoundException();
        }
        this.canView(user, space).throwIfNotSuccessful("User not permitted to view space");
        if (contentTypes.isEmpty()) {
            this.notificationManager.removeSpaceNotification(user, space);
        } else if (contentTypes.contains(ContentType.BLOG_POST)) {
            Notification notification = this.notificationManager.getNotificationByUserAndSpaceAndType(user, space, ContentTypeEnum.getByRepresentation(ContentType.BLOG_POST.getType()));
            if (notification != null) {
                this.notificationManager.removeNotification(notification);
            }
        } else {
            throw new ServiceException("The list must contain ContentType.BLOG_POST");
        }
    }

    public final void unwatchSpace(UserKey userKey, String spaceKey) {
        this.unwatchSpace(userKey, spaceKey, new ArrayList<ContentType>());
    }

    public final boolean isWatchingSpace(UserKey userKey, String spaceKey) {
        logger.debug("isWatchingSpace: space: {}", (Object)spaceKey);
        User user = this.findUserByUserKey(userKey);
        if (user == null) {
            return false;
        }
        Space space = this.spaceManager.getSpace(spaceKey);
        if (space == null) {
            throw new NotFoundException();
        }
        this.canView(user, space).throwIfNotSuccessful("User not permitted to view space");
        return this.notificationManager.isUserWatchingPageOrSpace(user, space, null);
    }

    public final ContentWatch watchContent(UserKey userKey, ContentId contentId) {
        logger.debug("watchContent: content: {}", (Object)contentId);
        User user = this.findUserByUserKey(userKey);
        ContentEntityObject content = this.contentEntityManager.getById(contentId.asLong());
        this.enforceReadOnlyAccess();
        if (content == null) {
            throw new NotFoundException();
        }
        this.canView(user, content).throwIfNotSuccessful("User not permitted to view content");
        this.notificationManager.addContentNotification(user, content);
        return new ContentWatch(KnownUser.fromUserkey((UserKey)userKey), contentId);
    }

    public final void unwatchContent(UserKey userKey, ContentId contentId) {
        logger.debug("unwatchContent: content: {}", (Object)contentId);
        User user = this.findUserByUserKey(userKey);
        ContentEntityObject content = this.contentEntityManager.getById(contentId.asLong());
        this.enforceReadOnlyAccess();
        if (content == null) {
            throw new NotFoundException();
        }
        this.canView(user, content).throwIfNotSuccessful("User not permitted to view content");
        this.notificationManager.removeContentNotification(user, content);
    }

    public final boolean isWatchingContent(UserKey userKey, ContentId contentId) {
        logger.debug("isWatchingContent: content: {}", (Object)contentId);
        User user = this.findUserByUserKey(userKey);
        if (user == null) {
            return false;
        }
        ContentEntityObject content = this.contentEntityManager.getById(contentId.asLong());
        if (content == null) {
            throw new NotFoundException();
        }
        this.canView(user, content).throwIfNotSuccessful("User not permitted to view content");
        return this.notificationManager.isWatchingContent(user, content);
    }

    @Deprecated
    public WatchService.Validator validator() {
        return new ValidatorImpl();
    }

    private @Nullable User findUserByUserKey(UserKey userKey) {
        return this.confluenceUserResolver.getExistingUserByKey(userKey);
    }

    private ValidationResult canView(User user, Object target) {
        ConfluenceUser confluenceUser = AuthenticatedUserThreadLocal.get();
        if (confluenceUser == null || !confluenceUser.equals(user) && !this.permissionManager.isConfluenceAdministrator(confluenceUser) || !this.permissionManager.hasPermission(user, Permission.VIEW, target)) {
            return SimpleValidationResult.FORBIDDEN;
        }
        return SimpleValidationResult.VALID;
    }

    private void enforceReadOnlyAccess() {
        if (this.accessModeService.shouldEnforceReadOnlyAccess()) {
            throw new ReadOnlyException();
        }
    }

    @Deprecated
    public class ValidatorImpl
    implements WatchService.Validator {
        public final ValidationResult validateWatchSpace(UserKey userKey, String spaceKey) {
            return (ValidationResult)WatchServiceImpl.this.spaceService.find(new Expansion[0]).withKeys(new String[]{spaceKey}).fetch().flatMap(item -> this.checkPermissions(WatchServiceImpl.this.findUserByUserKey(userKey))).orElseThrow(NotFoundException::new);
        }

        public final ValidationResult validateWatchContent(UserKey userKey, ContentId contentId) {
            return (ValidationResult)WatchServiceImpl.this.contentService.find(new Expansion[0]).withId(contentId).fetch().flatMap(item -> this.checkPermissions(WatchServiceImpl.this.findUserByUserKey(userKey))).orElseThrow(NotFoundException::new);
        }

        private Optional<ValidationResult> checkPermissions(User user) {
            ConfluenceUser confluenceUser = AuthenticatedUserThreadLocal.get();
            if (confluenceUser == null || !confluenceUser.equals(user) && !WatchServiceImpl.this.permissionManager.isConfluenceAdministrator(confluenceUser)) {
                return Optional.of(SimpleValidationResult.FORBIDDEN);
            }
            if (WatchServiceImpl.this.accessModeService.shouldEnforceReadOnlyAccess()) {
                return Optional.of(SimpleValidationResult.builder().authorized(true).allowedInReadOnlyMode(false).build());
            }
            return Optional.of(SimpleValidationResult.VALID);
        }
    }
}

