/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.view.link;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxStreamMarshaller;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifierResolver;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.CannotResolveResourceIdentifierException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.xhtml.api.Link;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class ViewAttachmentLinkMarshallingStrategy
implements StaxStreamMarshaller<Link> {
    private final AttachmentResourceIdentifierResolver attachmentResourceIdentifierResolver;

    public ViewAttachmentLinkMarshallingStrategy(AttachmentResourceIdentifierResolver attachmentResourceIdentifierResolver) {
        this.attachmentResourceIdentifierResolver = attachmentResourceIdentifierResolver;
    }

    @Override
    public void marshal(Link attachmentLink, XMLStreamWriter xmlStreamWriter, ConversionContext context) throws XMLStreamException {
        Attachment attachment;
        AttachmentResourceIdentifier ri = (AttachmentResourceIdentifier)attachmentLink.getDestinationResourceIdentifier();
        try {
            attachment = this.attachmentResourceIdentifierResolver.resolve(ri, context);
        }
        catch (CannotResolveResourceIdentifierException e) {
            throw new XMLStreamException(e);
        }
        if (attachment != null) {
            xmlStreamWriter.writeAttribute("data-linked-resource-id", Long.toString(attachment.getId()));
            xmlStreamWriter.writeAttribute("data-linked-resource-version", String.valueOf(attachment.getVersion()));
            xmlStreamWriter.writeAttribute("data-linked-resource-type", attachment.getType());
            xmlStreamWriter.writeAttribute("data-linked-resource-default-alias", attachment.getFileName());
            if (attachment.getNiceType() != null) {
                xmlStreamWriter.writeAttribute("data-nice-type", attachment.getNiceType());
            }
            xmlStreamWriter.writeAttribute("data-linked-resource-content-type", attachment.getMediaType());
            ContentEntityObject content = attachment.getContainer();
            if (content != null) {
                xmlStreamWriter.writeAttribute("data-linked-resource-container-id", content.getIdAsString());
                xmlStreamWriter.writeAttribute("data-linked-resource-container-version", String.valueOf(content.getVersion()));
            }
        }
    }
}

