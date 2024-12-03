/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.diff.marshallers;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.links.UnresolvedLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.BlogPostResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ContentEntityResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.IdAndTypeResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.PageResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ShortcutResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.SpaceResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.UserResourceIdentifier;
import com.atlassian.confluence.xhtml.api.Link;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class DiffLinkMarshaller
implements Marshaller<Link> {
    private static final Set<Class> PERMISSION_DEPENDENT_LINK_CLASSES = ImmutableSet.of(AttachmentResourceIdentifier.class, SpaceResourceIdentifier.class, ContentEntityResourceIdentifier.class, IdAndTypeResourceIdentifier.class);
    private final Marshaller<Link> delegatingLinkMarshaller;
    private final Marshaller<UnresolvedLink> unresolvedLinkMarshaller;

    public DiffLinkMarshaller(Marshaller<Link> delegatingLinkMarshaller, Marshaller<UnresolvedLink> unresolvedLinkMarshaller) {
        this.delegatingLinkMarshaller = delegatingLinkMarshaller;
        this.unresolvedLinkMarshaller = unresolvedLinkMarshaller;
    }

    @Override
    public Streamable marshal(Link link, ConversionContext conversionContext) throws XhtmlException {
        ResourceIdentifier ri = link.getDestinationResourceIdentifier();
        if (ri == null || ri instanceof ShortcutResourceIdentifier || ri instanceof UserResourceIdentifier || ri instanceof PageResourceIdentifier || ri instanceof BlogPostResourceIdentifier) {
            return this.delegatingLinkMarshaller.marshal(link, conversionContext);
        }
        if (this.hasPermissionDependentType(ri)) {
            return this.unresolvedLinkMarshaller.marshal(new UnresolvedLink(link), conversionContext);
        }
        throw new UnsupportedOperationException("Cannot handle: " + link);
    }

    private boolean hasPermissionDependentType(ResourceIdentifier destinationResourceIdentifier) {
        for (Class permissionDependentLinkClass : PERMISSION_DEPENDENT_LINK_CLASSES) {
            if (!permissionDependentLinkClass.isInstance(destinationResourceIdentifier)) continue;
            return true;
        }
        return false;
    }
}

