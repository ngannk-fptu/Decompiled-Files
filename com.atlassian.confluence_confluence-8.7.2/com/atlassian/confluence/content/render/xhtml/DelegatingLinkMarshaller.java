/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.links.DefaultLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifierContextUtility;
import com.atlassian.confluence.renderer.PageTemplateContext;
import com.atlassian.confluence.xhtml.api.Link;
import java.util.Map;

public class DelegatingLinkMarshaller
implements Marshaller<Link> {
    private Map<String, Marshaller<Link>> marshallerByClassSimpleName;
    private final ResourceIdentifierContextUtility riContextUtil;

    public DelegatingLinkMarshaller(Map<String, Marshaller<Link>> marshallersByClassSimpleName, ResourceIdentifierContextUtility resourceIdentifierContextUtility) {
        this.marshallerByClassSimpleName = marshallersByClassSimpleName;
        this.riContextUtil = resourceIdentifierContextUtility;
    }

    @Override
    public Streamable marshal(Link link, ConversionContext conversionContext) throws XhtmlException {
        ResourceIdentifier ri = this.getResourceIdentifier(link, conversionContext);
        Marshaller<Link> marshaller = this.getMarshallerForResourceIdentifier(ri);
        if (marshaller != null) {
            DefaultLink linkToMarshall = DefaultLink.builder(link).withDestinationResourceIdentifier(ri).build();
            return marshaller.marshal(linkToMarshall, conversionContext);
        }
        throw new UnsupportedOperationException("Cannot handle: " + link);
    }

    protected ResourceIdentifier getResourceIdentifier(Link link, ConversionContext conversionContext) throws XhtmlException {
        ResourceIdentifier ri = link.getDestinationResourceIdentifier();
        if (ri == null) {
            if (conversionContext == null || conversionContext.getEntity() == null && !(conversionContext.getRenderContext() instanceof PageTemplateContext)) {
                throw new XhtmlException("A relative link cannot be marshalled without a ConversionContext containing a ContentEntityObject unless it is on a Page Template");
            }
            if (conversionContext.getRenderContext() instanceof PageTemplateContext) {
                return this.riContextUtil.createAbsolutePageTemplateResourceIdentifier(conversionContext.getTemplate());
            }
            try {
                ri = this.riContextUtil.createAbsoluteResourceIdentifier(conversionContext.getEntity());
            }
            catch (IllegalStateException ex) {
                throw new XhtmlException(ex);
            }
        }
        return ri;
    }

    protected Marshaller<Link> getMarshallerForResourceIdentifier(ResourceIdentifier ri) {
        String simpleName = ri.getClass().getSimpleName();
        return this.marshallerByClassSimpleName.get(simpleName);
    }
}

