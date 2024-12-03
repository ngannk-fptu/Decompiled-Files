/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierContextUtility;

public class MakeRelativeAndDelegateResourceIdentifierMarshaller
implements Marshaller<ResourceIdentifier> {
    private final Marshaller<ResourceIdentifier> delegate;
    private final ResourceIdentifierContextUtility resourceIdentifierContextUtility;

    public MakeRelativeAndDelegateResourceIdentifierMarshaller(Marshaller<ResourceIdentifier> delegate, ResourceIdentifierContextUtility resourceIdentifierContextUtility) {
        this.delegate = delegate;
        this.resourceIdentifierContextUtility = resourceIdentifierContextUtility;
    }

    @Override
    public Streamable marshal(ResourceIdentifier resourceId, ConversionContext conversionContext) throws XhtmlException {
        if (conversionContext != null) {
            resourceId = this.resourceIdentifierContextUtility.convertToRelative(resourceId, conversionContext.getEntity());
        }
        if (resourceId == null) {
            return Streamables.empty();
        }
        return this.delegate.marshal(resourceId, conversionContext);
    }
}

