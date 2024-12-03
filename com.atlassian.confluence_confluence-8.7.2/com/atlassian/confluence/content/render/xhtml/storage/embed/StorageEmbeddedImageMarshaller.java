/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml.storage.embed;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.ImageAttributeWriter;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.MarshallingRegistry;
import com.atlassian.confluence.content.render.xhtml.MarshallingType;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.migration.UrlResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.ResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.storage.embed.StorageEmbeddedImageUnmarshaller;
import com.atlassian.confluence.content.render.xhtml.storage.embed.StorageImageAttributeWriter;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;

public class StorageEmbeddedImageMarshaller
implements Marshaller<EmbeddedImage> {
    private final XmlStreamWriterTemplate xmlStreamWriterTemplate;
    private final Marshaller<ResourceIdentifier> resourceIdentifierMarshaller;

    public StorageEmbeddedImageMarshaller(XmlStreamWriterTemplate xmlStreamWriterTemplate, Marshaller<ResourceIdentifier> resourceIdentifierMarshaller, MarshallingRegistry registry) {
        this.xmlStreamWriterTemplate = xmlStreamWriterTemplate;
        this.resourceIdentifierMarshaller = resourceIdentifierMarshaller;
        registry.register(this, EmbeddedImage.class, MarshallingType.STORAGE);
    }

    @Override
    public Streamable marshal(EmbeddedImage embeddedImage, ConversionContext conversionContext) throws XhtmlException {
        ResourceIdentifier resourceIdentifier = embeddedImage.getResourceIdentifier();
        Streamable marshalledResourceIdentifier = this.resourceIdentifierMarshaller.marshal(resourceIdentifier, conversionContext);
        return Streamables.from(this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
            xmlStreamWriter.writeStartElement(StorageEmbeddedImageUnmarshaller.IMAGE_ELEMENT.getPrefix(), StorageEmbeddedImageUnmarshaller.IMAGE_ELEMENT.getLocalPart(), StorageEmbeddedImageUnmarshaller.IMAGE_ELEMENT.getNamespaceURI());
            if (!(embeddedImage.getResourceIdentifier() instanceof AttachmentResourceIdentifier) && !(embeddedImage.getResourceIdentifier() instanceof UrlResourceIdentifier)) {
                throw new UnsupportedOperationException("Only attachments or external URLs can be embedded.");
            }
            StorageImageAttributeWriter attrWriter = new StorageImageAttributeWriter(xmlStreamWriter);
            ((ImageAttributeWriter)attrWriter).writeAttributes(embeddedImage);
            xmlStreamWriter.writeCharacters("");
            xmlStreamWriter.flush();
            marshalledResourceIdentifier.writeTo(underlyingWriter);
            xmlStreamWriter.writeEndElement();
        });
    }
}

