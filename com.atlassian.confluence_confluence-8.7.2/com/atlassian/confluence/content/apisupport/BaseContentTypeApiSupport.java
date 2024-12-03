/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalSpi
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.link.Link
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 */
package com.atlassian.confluence.content.apisupport;

import com.atlassian.annotations.ExperimentalSpi;
import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.link.Link;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.content.apisupport.ApiSupportProvider;
import com.atlassian.confluence.content.apisupport.ContentTypeApiSupport;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Contained;
import com.atlassian.confluence.pages.ContentConvertible;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.Spaced;
import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@ExperimentalSpi
public abstract class BaseContentTypeApiSupport<T extends ContentConvertible>
implements ContentTypeApiSupport<T> {
    private final ApiSupportProvider apiSupportProvider;

    protected BaseContentTypeApiSupport(ApiSupportProvider provider) {
        this.apiSupportProvider = provider;
    }

    @Override
    public final PageResponse<Content> getFilteredChildren(ContentConvertible content, LimitedRequest limitedRequest, Expansions expansions, Depth depth, Predicate<? super ContentEntityObject> predicate) {
        ContentType type = content.getContentTypeObject();
        if (type.equals((Object)this.getHandledType())) {
            if (predicate != null) {
                return this.getFilteredChildrenForThisType(content, limitedRequest, expansions, depth, predicate);
            }
            return this.getChildrenForThisType(content, limitedRequest, expansions, depth);
        }
        if (this.apiSupportProvider.getForType(type).supportsChildrenOfType(this.getHandledType())) {
            if (predicate != null) {
                return this.getFilteredChildrenOfThisTypeForOtherType(content, limitedRequest, expansions, depth, predicate);
            }
            return this.getChildrenOfThisTypeForOtherType(content, limitedRequest, expansions, depth);
        }
        throw new ServiceException("Cannot get children for unsupported contentEntity type ");
    }

    protected abstract PageResponse<Content> getChildrenForThisType(T var1, LimitedRequest var2, Expansions var3, Depth var4);

    @Deprecated
    protected PageResponse<Content> getChildrenForThisType(T content, LimitedRequest limitedRequest, Expansions expansions, Depth depth, com.google.common.base.Predicate<? super ContentEntityObject> predicate) {
        throw new NotImplementedServiceException("Predicate filtering not implemented for this content type " + this.getEntityClass());
    }

    protected PageResponse<Content> getFilteredChildrenForThisType(T content, LimitedRequest limitedRequest, Expansions expansions, Depth depth, Predicate<? super ContentEntityObject> predicate) {
        return this.getChildrenForThisType(content, limitedRequest, expansions, depth, (com.google.common.base.Predicate<ContentEntityObject>)((com.google.common.base.Predicate)predicate::test));
    }

    protected abstract PageResponse<Content> getChildrenOfThisTypeForOtherType(ContentConvertible var1, LimitedRequest var2, Expansions var3, Depth var4);

    @Deprecated
    protected PageResponse<Content> getChildrenOfThisTypeForOtherType(ContentConvertible otherTypeParent, LimitedRequest limitedRequest, Expansions expansions, Depth depth, com.google.common.base.Predicate<? super ContentEntityObject> predicate) {
        throw new NotImplementedServiceException("Predicate filtering of children not implemented for this content type : " + this.getEntityClass());
    }

    protected PageResponse<Content> getFilteredChildrenOfThisTypeForOtherType(ContentConvertible otherTypeParent, LimitedRequest limitedRequest, Expansions expansions, Depth depth, Predicate<? super ContentEntityObject> predicate) {
        return this.getChildrenOfThisTypeForOtherType(otherTypeParent, limitedRequest, expansions, depth, (com.google.common.base.Predicate<ContentEntityObject>)((com.google.common.base.Predicate)predicate::test));
    }

    @Override
    public Map<ContentId, Map<String, Object>> getExtensions(Iterable<T> contentEntities, Expansions expansions) {
        return Collections.emptyMap();
    }

    @Override
    public List<Link> getLinks(T contentEntity) {
        return Collections.emptyList();
    }

    @Override
    public Optional<Object> container(T contentEntity, Expansions expansions) {
        Space container = null;
        if (contentEntity instanceof Contained) {
            container = (Space)((Contained)contentEntity).getContainer();
        }
        if (container == null && contentEntity instanceof Spaced) {
            container = ((Spaced)contentEntity).getSpace();
        }
        return Optional.ofNullable(container);
    }

    @Override
    public T create(Content content) {
        Preconditions.checkNotNull((Object)content);
        throw new NotImplementedServiceException("Cannot create content of type " + content.getType());
    }

    @Override
    public ValidationResult validateCreate(Content newContent) {
        throw new NotImplementedServiceException("Cannot validate create of content of type " + newContent.getType());
    }

    @Override
    public T update(Content content, T entity) {
        Preconditions.checkNotNull((Object)content);
        throw new NotImplementedServiceException("Cannot update content of type " + content.getType());
    }

    @Override
    public ValidationResult validateUpdate(Content updatedContent, T existingEntity) {
        throw new NotImplementedServiceException("Cannot validate update of content of type " + updatedContent.getType());
    }
}

