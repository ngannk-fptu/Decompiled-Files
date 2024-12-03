/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.storage.link;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.migration.UrlLinkMarshaller;
import com.atlassian.confluence.content.render.xhtml.migration.UrlResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.links.EmptyLink;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.LinkBody;
import org.apache.commons.lang3.StringUtils;

public class StorageLinkMarshaller
implements Marshaller<Link> {
    private final XmlStreamWriterTemplate xmlStreamWriterTemplate;
    private final Marshaller<ResourceIdentifier> resourceIdentifierMarshaller;
    private final Marshaller<LinkBody> linkBodyMarshaller;
    private final Marshaller<Link> urlLinkMarshaller;

    public StorageLinkMarshaller(XmlStreamWriterTemplate xmlStreamWriterTemplate, Marshaller<ResourceIdentifier> resourceIdentifierMarshaller, Marshaller<LinkBody> linkBodyMarshaller, MarshallingRegistry marshallingRegistry) {
        this.xmlStreamWriterTemplate = xmlStreamWriterTemplate;
        this.resourceIdentifierMarshaller = resourceIdentifierMarshaller;
        this.linkBodyMarshaller = linkBodyMarshaller;
        this.urlLinkMarshaller = new UrlLinkMarshaller(xmlStreamWriterTemplate);
        marshallingRegistry.register(this, Link.class, MarshallingType.STORAGE);
    }

    @Override
    public Streamable marshal(Link link, ConversionContext conversionContext) throws XhtmlException {
        if (link.getDestinationResourceIdentifier() instanceof UrlResourceIdentifier) {
            return this.urlLinkMarshaller.marshal(link, conversionContext);
        }
        if (link instanceof EmptyLink) {
            return Streamables.empty();
        }
        ResourceIdentifier resourceId = link.getDestinationResourceIdentifier();
        Streamable marshalledResourceIdentifier = this.resourceIdentifierMarshaller.marshal(resourceId, conversionContext);
        Streamable marshalledBody = link.getBody() == null ? null : this.linkBodyMarshaller.marshal(link.getBody(), conversionContext);
        return Streamables.from(this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
            xmlStreamWriter.writeStartElement("ac", "link", "http://atlassian.com/content");
            if (StringUtils.isNotBlank((CharSequence)link.getAnchor())) {
                xmlStreamWriter.writeAttribute("ac", "http://atlassian.com/content", "anchor", link.getAnchor());
            }
            if (StringUtils.isNotBlank((CharSequence)link.getTooltip())) {
                xmlStreamWriter.writeAttribute("ac", "http://atlassian.com/content", "tooltip", link.getTooltip());
            }
            if (link.getTarget().isPresent()) {
                xmlStreamWriter.writeAttribute("ac", "http://atlassian.com/content", "target", link.getTarget().get());
            }
            if (marshalledResourceIdentifier != null) {
                xmlStreamWriter.writeCharacters("");
                xmlStreamWriter.flush();
                marshalledResourceIdentifier.writeTo(underlyingWriter);
            }
            if (marshalledBody != null) {
                xmlStreamWriter.writeCharacters("");
                xmlStreamWriter.flush();
                marshalledBody.writeTo(underlyingWriter);
            }
            xmlStreamWriter.writeEndElement();
        });
    }
}

