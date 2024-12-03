/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.user.User
 *  org.apache.commons.collections.CollectionUtils
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.api.impl.service.content.finder;

import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.CommentStatus;
import com.atlassian.confluence.pages.Contained;
import com.atlassian.confluence.pages.ContentConvertible;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import org.apache.commons.collections.CollectionUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class FinderPredicates {
    public static Predicate<? super ContentEntityObject> createContentTypePredicate(ContentType ... types) {
        final List<ContentType> contentTypes = Arrays.asList(types);
        return new Predicate<ContentEntityObject>(){

            @Override
            public boolean test(@Nullable ContentEntityObject input) {
                ContentType type = null;
                if (input instanceof ContentConvertible) {
                    type = ((ContentConvertible)((Object)input)).getContentTypeObject();
                }
                return type != null && contentTypes.contains(type);
            }

            public String toString() {
                return "ContentType IN " + contentTypes;
            }
        };
    }

    public static Predicate<? super ContentEntityObject> createTitlePredicate(final String title) {
        return new Predicate<ContentEntityObject>(){

            @Override
            public boolean test(@Nullable ContentEntityObject input) {
                return input != null && title.equalsIgnoreCase(input.getTitle());
            }

            public String toString() {
                return "Title = " + title;
            }
        };
    }

    public static Predicate<? super ContentEntityObject> createSpaceKeysPredicate(final List<String> spaceKeys) {
        return new Predicate<ContentEntityObject>(){

            @Override
            public boolean test(@Nullable ContentEntityObject input) {
                Space space;
                if (input instanceof Spaced && (space = ((Spaced)((Object)input)).getSpace()) != null) {
                    return spaceKeys.contains(space.getKey());
                }
                return false;
            }

            public String toString() {
                return "SpaceKey IN " + spaceKeys;
            }
        };
    }

    public static Predicate<? super ContentEntityObject> createCreationDatePredicate(final LocalDate createdDate) {
        return new Predicate<ContentEntityObject>(){

            @Override
            public boolean test(@Nullable ContentEntityObject input) {
                if (input == null) {
                    return false;
                }
                if (input.getCreationDate() == null) {
                    return false;
                }
                ZonedDateTime date = input.getCreationDate().toInstant().atZone(ZoneId.systemDefault());
                return date.toLocalDate().isEqual(createdDate);
            }

            public String toString() {
                return "CreatedDate = " + createdDate;
            }
        };
    }

    public static Predicate<? super ContentEntityObject> createFileNamePredicate(final String filename) {
        final Predicate<? super ContentEntityObject> titlePredicate = FinderPredicates.createTitlePredicate(filename);
        return new Predicate<ContentEntityObject>(){

            @Override
            public boolean test(@Nullable ContentEntityObject input) {
                return input instanceof Attachment && titlePredicate.test(input);
            }

            public String toString() {
                return "Filename = " + filename;
            }
        };
    }

    public static Predicate<? super ContentEntityObject> createMediaTypePredicate(final String mediaType) {
        return new Predicate<ContentEntityObject>(){

            @Override
            public boolean test(@Nullable ContentEntityObject input) {
                return input instanceof Attachment && mediaType.equals(((Attachment)input).getMediaType());
            }

            public String toString() {
                return "MediaType = " + mediaType;
            }
        };
    }

    public static Predicate<? super ContentEntityObject> createCommentLocationPredicate(final Collection<String> location) {
        return new Predicate<ContentEntityObject>(){

            @Override
            public boolean test(@Nullable ContentEntityObject input) {
                if (CollectionUtils.isEmpty((Collection)location)) {
                    return true;
                }
                if (input instanceof Comment) {
                    Comment comment = (Comment)input;
                    return location.contains(CommentStatus.Value.RESOLVED.getStringValue()) && comment.getStatus().isResolved() || location.contains("inline") && comment.isInlineComment() || location.contains("footer") && !comment.isInlineComment() && !comment.getStatus().isResolved();
                }
                return false;
            }

            public String toString() {
                return "Location = " + location.toString();
            }
        };
    }

    public static Predicate<? super ContentEntityObject> createContentIdPredicate(List<ContentId> contentIds) {
        return input -> input instanceof ContentConvertible && contentIds.contains(input.getContentId());
    }

    public static Predicate<? super ContentEntityObject> statusPredicate(List<ContentStatus> statuses) {
        return input -> input != null && statuses.contains(input.getContentStatusObject());
    }

    public static Predicate<Object> permissionPredicate(Permission permission, PermissionManager permissionManager) {
        return FinderPredicates.permissionPredicate(AuthenticatedUserThreadLocal.get(), permission, permissionManager);
    }

    public static Predicate<Object> permissionPredicate(User user, Permission permission, PermissionManager permissionManager) {
        return target -> permissionManager.hasPermission(user, permission, target);
    }

    public static Predicate<? super ContentEntityObject> containerPredicate(final @NonNull ContentId containerId) {
        return new Predicate<ContentEntityObject>(){

            @Override
            public boolean test(@Nullable ContentEntityObject input) {
                if (input instanceof Contained) {
                    Object container = ((Contained)((Object)input)).getContainer();
                    return container != null && containerId.asLong() == container.getId();
                }
                return false;
            }

            public String toString() {
                return "container = " + containerId.asLong();
            }
        };
    }
}

