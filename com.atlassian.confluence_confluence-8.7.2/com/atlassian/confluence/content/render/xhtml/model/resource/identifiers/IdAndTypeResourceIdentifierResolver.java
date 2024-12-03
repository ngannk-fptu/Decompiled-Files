/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.CannotResolveResourceIdentifierException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.IdAndTypeResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierResolver;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.spring.container.ContainerManager;

public class IdAndTypeResourceIdentifierResolver
implements ResourceIdentifierResolver<IdAndTypeResourceIdentifier, Object> {
    private final AttachmentManager attachmentManager;
    private final SpaceManager spaceManager;

    public IdAndTypeResourceIdentifierResolver(AttachmentManager attachmentManager, SpaceManager spaceManager) {
        this.attachmentManager = attachmentManager;
        this.spaceManager = spaceManager;
    }

    @Override
    public Object resolve(IdAndTypeResourceIdentifier resourceIdentifier, ConversionContext conversionContext) throws CannotResolveResourceIdentifierException {
        ConfluenceEntityObject result = null;
        long resourceId = resourceIdentifier.getId();
        ContentTypeEnum resourceType = resourceIdentifier.getType();
        if (ContentTypeEnum.ATTACHMENT == resourceType) {
            result = this.attachmentManager.getAttachment(resourceId);
        } else if (resourceType == ContentTypeEnum.PAGE || resourceType == ContentTypeEnum.BLOG || resourceType == ContentTypeEnum.COMMENT || resourceType == ContentTypeEnum.PERSONAL_INFORMATION || resourceType == ContentTypeEnum.DRAFT || resourceType == ContentTypeEnum.CUSTOM) {
            result = this.getContentEntityManager().getById(resourceId);
        } else if (resourceType == ContentTypeEnum.SPACE) {
            result = this.spaceManager.getSpace(resourceId);
        }
        if (result == null) {
            throw new CannotResolveResourceIdentifierException(resourceIdentifier, "Unable to resolve the resource identifier " + resourceIdentifier);
        }
        return result;
    }

    private ContentEntityManager getContentEntityManager() {
        return (ContentEntityManager)ContainerManager.getComponent((String)"contentEntityManager");
    }
}

