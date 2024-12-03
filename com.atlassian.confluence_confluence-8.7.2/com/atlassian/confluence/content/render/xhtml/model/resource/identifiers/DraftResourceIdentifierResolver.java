/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.model.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.CannotResolveResourceIdentifierException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.DraftResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierResolver;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.pages.DraftManager;

public class DraftResourceIdentifierResolver
implements ResourceIdentifierResolver<DraftResourceIdentifier, Draft> {
    private final DraftManager draftManager;

    public DraftResourceIdentifierResolver(DraftManager draftManager) {
        this.draftManager = draftManager;
    }

    @Override
    public Draft resolve(DraftResourceIdentifier resourceIdentifier, ConversionContext conversionContext) throws CannotResolveResourceIdentifierException {
        Draft draft = this.draftManager.getDraft(resourceIdentifier.getDraftId());
        if (draft == null) {
            throw new CannotResolveResourceIdentifierException(resourceIdentifier, "Unable to resolve the resource identifier " + resourceIdentifier);
        }
        return draft;
    }
}

