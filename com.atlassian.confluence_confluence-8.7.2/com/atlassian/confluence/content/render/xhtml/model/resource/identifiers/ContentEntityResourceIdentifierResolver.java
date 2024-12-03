/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.migration.ContentDao;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.CannotResolveResourceIdentifierException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ContentEntityResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierResolver;
import com.atlassian.confluence.core.ContentEntityObject;

public class ContentEntityResourceIdentifierResolver
implements ResourceIdentifierResolver<ContentEntityResourceIdentifier, ContentEntityObject> {
    private final ContentDao contentDao;

    public ContentEntityResourceIdentifierResolver(ContentDao contentDao) {
        this.contentDao = contentDao;
    }

    @Override
    public ContentEntityObject resolve(ContentEntityResourceIdentifier resourceIdentifier, ConversionContext conversionContext) throws CannotResolveResourceIdentifierException {
        try {
            return this.contentDao.getById(resourceIdentifier.getContentId());
        }
        catch (Exception e) {
            throw new CannotResolveResourceIdentifierException(resourceIdentifier, "Unable to resolve the resource identifier " + resourceIdentifier, e);
        }
    }
}

