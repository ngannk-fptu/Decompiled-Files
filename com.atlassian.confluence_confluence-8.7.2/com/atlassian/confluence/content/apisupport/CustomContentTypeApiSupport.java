/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalSpi
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Container
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.InternalServerException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.apisupport;

import com.atlassian.annotations.ExperimentalSpi;
import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Container;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.InternalServerException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.CustomContentManager;
import com.atlassian.confluence.content.apisupport.BaseContentTypeApiSupport;
import com.atlassian.confluence.content.apisupport.ContentCreator;
import com.atlassian.confluence.content.apisupport.CustomContentApiSupportParams;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.ContentConvertible;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ExperimentalSpi
public abstract class CustomContentTypeApiSupport
extends BaseContentTypeApiSupport<CustomContentEntityObject> {
    private final ContentCreator contentCreator;
    private final CustomContentManager customContentManager;
    private final ContentEntityManager contentEntityManager;
    private static final Logger log = LoggerFactory.getLogger(CustomContentTypeApiSupport.class);

    public CustomContentTypeApiSupport(CustomContentApiSupportParams params) {
        super(params.getProvider());
        this.customContentManager = params.getCustomContentManager();
        this.contentCreator = params.getContentCreator();
        this.contentEntityManager = params.getContentEntityManager();
    }

    @Override
    public Class<CustomContentEntityObject> getEntityClass() {
        return CustomContentEntityObject.class;
    }

    @Override
    public CustomContentEntityObject create(Content content) {
        CustomContentEntityObject entity = this.customContentManager.newPluginContentEntityObject(this.getHandledType().serialise());
        this.contentCreator.setCommonPropertiesForCreate(content, entity, AuthenticatedUserThreadLocal.get());
        this.setContentContainer(content, entity);
        this.executeWithExceptionWrapping(() -> {
            this.createCustomContentEntity(content, entity);
            return null;
        });
        return this.contentCreator.saveNewContent(entity, content.getVersion(), null);
    }

    private boolean setContentContainer(Content content, CustomContentEntityObject customContentEntityObject) {
        Container container = content.getContainer();
        if (container instanceof Content) {
            Content containerContent = (Content)container;
            long containerId = containerContent.getId().asLong();
            ContentEntityObject existingContainer = customContentEntityObject.getContainer();
            if (existingContainer == null || existingContainer.getId() != containerId) {
                customContentEntityObject.setContainer((ContentEntityObject)Preconditions.checkNotNull((Object)this.contentEntityManager.getById(containerId)));
                return true;
            }
        }
        return false;
    }

    @Override
    public CustomContentEntityObject update(Content content, CustomContentEntityObject entity) {
        CustomContentEntityObject original = this.contentCreator.cloneForUpdate(entity);
        boolean storeRequired = this.contentCreator.setCommonPropertiesForUpdate(content, entity);
        storeRequired |= this.setContentContainer(content, entity);
        return (storeRequired |= this.executeWithExceptionWrapping(() -> this.updateCustomContentEntity(content, entity, original)).booleanValue()) ? this.contentCreator.saveNewVersion(entity, original, content.getVersion()) : entity;
    }

    @Override
    protected PageResponse<Content> getChildrenOfThisTypeForOtherType(ContentConvertible otherTypeParent, LimitedRequest limitedRequest, Expansions expansions, Depth depth) {
        return this.getFilteredChildrenOfThisTypeForOtherType(otherTypeParent, limitedRequest, expansions, depth, t -> true);
    }

    @Override
    @Deprecated
    protected PageResponse<Content> getChildrenOfThisTypeForOtherType(ContentConvertible otherTypeParent, LimitedRequest limitedRequest, Expansions expansions, Depth depth, Predicate<? super ContentEntityObject> predicate) {
        ContentEntityObject other = this.contentEntityManager.getById(otherTypeParent.getContentId().asLong());
        if (other == null) {
            return PageResponseImpl.empty((boolean)false);
        }
        return this.customContentManager.getChildrenOfTypeAndFilter(other, this.getHandledType().getType(), limitedRequest, expansions, depth, arg_0 -> predicate.apply(arg_0));
    }

    private <S> S executeWithExceptionWrapping(Callable<S> callable) {
        try {
            return callable.call();
        }
        catch (ServiceException ex) {
            throw ex;
        }
        catch (IllegalArgumentException ex) {
            log.warn("API Support : {} for type {}, threw an IllegalArgumentException, wrapping in a BadRequestException", this.getClass(), (Object)this.getHandledType());
            throw new BadRequestException((Throwable)ex);
        }
        catch (Exception ex) {
            throw new InternalServerException(String.format("Mapping for content type %s threw a non ServiceException.", this.getHandledType()), (Throwable)ex);
        }
    }

    protected abstract boolean updateCustomContentEntity(Content var1, CustomContentEntityObject var2, CustomContentEntityObject var3);

    protected abstract void createCustomContentEntity(Content var1, CustomContentEntityObject var2);
}

