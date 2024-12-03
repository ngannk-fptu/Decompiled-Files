/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Depth
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentType
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.PageResponseImpl
 *  com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 */
package com.atlassian.confluence.content.custom;

import com.atlassian.confluence.api.model.Depth;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentType;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.PageResponseImpl;
import com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.content.CustomContentEntityObject;
import com.atlassian.confluence.content.apisupport.ApiSupportProvider;
import com.atlassian.confluence.content.apisupport.BaseContentTypeApiSupport;
import com.atlassian.confluence.pages.ContentConvertible;

class NullContentTypeApiSupport
extends BaseContentTypeApiSupport<CustomContentEntityObject> {
    private final ContentType contentType;

    NullContentTypeApiSupport(ContentType contentType, ApiSupportProvider provider) {
        super(provider);
        this.contentType = contentType;
    }

    @Override
    public ContentType getHandledType() {
        return this.contentType;
    }

    @Override
    protected PageResponse<Content> getChildrenForThisType(CustomContentEntityObject content, LimitedRequest limitedRequest, Expansions expansions, Depth depth) {
        return PageResponseImpl.empty((boolean)false);
    }

    @Override
    protected PageResponse<Content> getChildrenOfThisTypeForOtherType(ContentConvertible otherTypeParent, LimitedRequest limitedRequest, Expansions expansions, Depth depth) {
        return PageResponseImpl.empty((boolean)false);
    }

    @Override
    public boolean supportsChildrenOfType(ContentType otherType) {
        return false;
    }

    @Override
    public boolean supportsChildrenForParentType(ContentType parentType) {
        return false;
    }

    @Override
    public Class<CustomContentEntityObject> getEntityClass() {
        return CustomContentEntityObject.class;
    }

    @Override
    public ValidationResult validateCreate(Content newContent) {
        return this.noApiSupportValidationResult();
    }

    @Override
    public ValidationResult validateUpdate(Content updatedContent, CustomContentEntityObject existingEntity) {
        return this.noApiSupportValidationResult();
    }

    private ValidationResult noApiSupportValidationResult() {
        return SimpleValidationResult.builder().addError("Not supported, no ApiSupport available for " + this.getHandledType(), new Object[0]).withExceptionSupplier(ServiceExceptionSupplier.notImplementedSupplier()).build();
    }
}

