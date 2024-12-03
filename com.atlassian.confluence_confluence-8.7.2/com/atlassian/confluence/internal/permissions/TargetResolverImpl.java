/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.Container
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.permissions.Target
 *  com.atlassian.confluence.api.model.permissions.Target$ContainerTarget
 *  com.atlassian.confluence.api.model.permissions.Target$IdTarget
 *  com.atlassian.confluence.api.model.permissions.Target$ModelObjectTarget
 *  com.atlassian.confluence.api.model.permissions.TargetType
 *  com.atlassian.confluence.api.model.permissions.spi.UnsupportedTargetException
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.fugue.Option
 *  com.google.common.collect.ImmutableSet
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.internal.permissions;

import com.atlassian.confluence.api.model.content.Container;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.permissions.Target;
import com.atlassian.confluence.api.model.permissions.TargetType;
import com.atlassian.confluence.api.model.permissions.spi.UnsupportedTargetException;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.persistence.AnyTypeDao;
import com.atlassian.confluence.internal.permissions.TargetResolver;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.pages.BlogPost;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.fugue.Option;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class TargetResolverImpl
implements TargetResolver {
    private final AnyTypeDao anyTypeDao;
    private static final Set<TargetType> CONTAINED_IN_SPACE_TARGET_TYPES = ImmutableSet.builder().add((Object[])new TargetType[]{TargetType.BLOG_POST, TargetType.PAGE}).build();

    public TargetResolverImpl(AnyTypeDao anyTypeDao) {
        this.anyTypeDao = anyTypeDao;
    }

    @Override
    public <T> @NonNull T resolveModelObject(Target target, Class<T> expectedClass) {
        if (target instanceof Target.ModelObjectTarget) {
            Object modelObject = ((Target.ModelObjectTarget)target).getModelObject();
            if (expectedClass.isAssignableFrom(modelObject.getClass())) {
                return expectedClass.cast(modelObject);
            }
            throw new ClassCastException("Unsupported model object type " + modelObject.getClass() + " for this operation on target: " + target);
        }
        throw new UnsupportedTargetException("Unsupported Target class: " + target.getClass());
    }

    @Override
    public <T> @NonNull Option<T> resolveHibernateObject(Target target, Class<T> expectedType) {
        TargetType targetType = target.getTargetType();
        if (TargetType.SPACE.equals((Object)targetType)) {
            long id = this.resolveModelObject(target, com.atlassian.confluence.api.model.content.Space.class).getId();
            return this.getHibernateObject(id, targetType, expectedType);
        }
        ContentId contentId = target instanceof Target.IdTarget ? ((Target.IdTarget)target).getId() : this.resolveModelObject(target, Content.class).getId();
        return this.getHibernateObject(contentId, targetType, expectedType);
    }

    @Override
    public <T> @NonNull Option<T> resolveContainerHibernateObject(Target target, Class<T> expectedType) {
        if (target instanceof Target.ModelObjectTarget) {
            Container container;
            Class<? extends Container> expectedContainerType = TargetResolverImpl.getExpectedContainerType(target.getTargetType());
            Content modelObject = this.resolveModelObject(target, Content.class);
            try {
                container = modelObject.getContainer();
            }
            catch (IllegalStateException e) {
                throw new RuntimeException("Operation requires Content's container info to be expanded, but received a collapsed container for this operation on model object: " + modelObject + ", target: " + target + ": " + e.getMessage());
            }
            if (container == null) {
                throw new BadRequestException("Null container for model object: " + modelObject + " for this operation on target: " + target);
            }
            return this.resolveContainerHibernateObject(container, expectedContainerType, target, expectedType);
        }
        if (target instanceof Target.ContainerTarget) {
            Class<? extends Container> expectedContainerType = TargetResolverImpl.getExpectedContainerType(target.getTargetType());
            Container container = this.resolveModelObject((Target)((Target.ContainerTarget)target).getContainer(), Container.class);
            return this.resolveContainerHibernateObject(container, expectedContainerType, target, expectedType);
        }
        throw new UnsupportedTargetException("Unsupported Target: " + target.getClass() + ": " + target);
    }

    private <T> Option<T> resolveContainerHibernateObject(Container container, Class<? extends Container> expectedContainerType, Target target, Class<T> callerExpectedType) {
        if (!expectedContainerType.isAssignableFrom(container.getClass())) {
            throw new BadRequestException("Unsupported container object type " + container.getClass() + " for this operation on target: " + target);
        }
        if (container instanceof Content) {
            Content containerContent = (Content)container;
            return this.getHibernateObject(containerContent.getId(), TargetType.valueOf((ContentType)containerContent.getType()), callerExpectedType);
        }
        if (container instanceof com.atlassian.confluence.api.model.content.Space) {
            com.atlassian.confluence.api.model.content.Space containerSpace = (com.atlassian.confluence.api.model.content.Space)container;
            return this.getHibernateObject(containerSpace.getId(), TargetType.SPACE, callerExpectedType);
        }
        throw new UnsupportedTargetException("Unsupported Target: " + target.getClass() + ": " + target);
    }

    @Override
    public boolean isContainerTarget(Target target) {
        return target instanceof Target.ContainerTarget;
    }

    private <T> @NonNull Option<T> getHibernateObject(@Nullable ContentId contentId, TargetType targetType, Class<T> callerExpectedClass) {
        Class<? extends ConfluenceEntityObject> expectedHibernateType = TargetResolverImpl.mapTargetTypeToHibernateClass(targetType);
        return contentId == null ? Option.none() : this.getHibernateObject(contentId.asLong(), targetType, expectedHibernateType, callerExpectedClass);
    }

    private <T> @NonNull Option<T> getHibernateObject(long id, TargetType targetType, Class<T> callerExpectedClass) {
        return this.getHibernateObject(id, targetType, TargetResolverImpl.mapTargetTypeToHibernateClass(targetType), callerExpectedClass);
    }

    private <T> Option<T> getHibernateObject(long id, TargetType targetType, Class<? extends ConfluenceEntityObject> expectedHibernateType, Class<T> callerExpectedClass) {
        Object hibernateObject = this.anyTypeDao.getByIdAndType(id, expectedHibernateType);
        if (hibernateObject == null) {
            return Option.none();
        }
        if (!expectedHibernateType.isAssignableFrom(hibernateObject.getClass())) {
            throw new BadRequestException("Received TargetType " + targetType + ", but the retrieved hibernate object was a " + hibernateObject.getClass());
        }
        if (!callerExpectedClass.isAssignableFrom(hibernateObject.getClass())) {
            throw new ClassCastException("Requested type was " + callerExpectedClass + ", but the retrieved hibernate object was a " + hibernateObject.getClass());
        }
        return Option.some(callerExpectedClass.cast(hibernateObject));
    }

    private static @NonNull Class<? extends Container> getExpectedContainerType(TargetType targetType) {
        if (TargetType.COMMENT.equals((Object)targetType) || TargetType.ATTACHMENT.equals((Object)targetType)) {
            return Content.class;
        }
        if (CONTAINED_IN_SPACE_TARGET_TYPES.contains(targetType)) {
            return com.atlassian.confluence.api.model.content.Space.class;
        }
        throw new BadRequestException("Unsupported target type to be in a container: " + targetType);
    }

    private static @NonNull Class<? extends ConfluenceEntityObject> mapTargetTypeToHibernateClass(TargetType targetType) {
        if (TargetType.COMMENT.equals((Object)targetType)) {
            return Comment.class;
        }
        if (TargetType.PAGE.equals((Object)targetType)) {
            return Page.class;
        }
        if (TargetType.BLOG_POST.equals((Object)targetType)) {
            return BlogPost.class;
        }
        if (TargetType.ATTACHMENT.equals((Object)targetType)) {
            return Attachment.class;
        }
        if (TargetType.SPACE.equals((Object)targetType)) {
            return Space.class;
        }
        if (!TargetType.BUILT_IN.contains(targetType)) {
            return CustomContentEntityObject.class;
        }
        throw new BadRequestException("Unsupported target type: " + targetType);
    }
}

