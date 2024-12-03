/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.util.Base64Encoder
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.editor.embed;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.editor.macro.PlaceholderUrlFactory;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.storage.resource.identifiers.StorageAttachmentResourceIdentifierMarshaller;
import com.atlassian.confluence.content.render.xhtml.view.embed.UnidentifiedAttachmentMarshaller;
import com.atlassian.confluence.content.render.xhtml.view.embed.ViewImageAttributeWriter;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import com.atlassian.user.util.Base64Encoder;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.commons.lang3.StringUtils;

public class EditorUnidentifiedAttachmentMarshaller
implements UnidentifiedAttachmentMarshaller {
    private final StorageAttachmentResourceIdentifierMarshaller storageAttachmentResourceIdentifierMarshaller;
    private final PlaceholderUrlFactory placeholderUrlFactory;

    public EditorUnidentifiedAttachmentMarshaller(StorageAttachmentResourceIdentifierMarshaller storageAttachmentResourceIdentifierMarshaller, PlaceholderUrlFactory placeholderUrlFactory) {
        this.storageAttachmentResourceIdentifierMarshaller = storageAttachmentResourceIdentifierMarshaller;
        this.placeholderUrlFactory = placeholderUrlFactory;
    }

    @Override
    public Streamable marshalPlaceholder(XmlStreamWriterTemplate xmlStreamWriterTemplate, EmbeddedImage embeddedImage, AttachmentResourceIdentifier attachmentResourceIdentifier, ConversionContext conversionContext) throws XhtmlException {
        String unresolvedResource = Streamables.writeToString(this.storageAttachmentResourceIdentifierMarshaller.marshal(attachmentResourceIdentifier, conversionContext));
        return Streamables.from(xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
            xmlStreamWriter.writeStartElement("img");
            new ViewImageAttributeWriter(xmlStreamWriter, true).writeAttributes(embeddedImage);
            xmlStreamWriter.writeAttribute("src", this.placeholderUrlFactory.getUrlForUnknownAttachment());
            if (StringUtils.isNotBlank((CharSequence)attachmentResourceIdentifier.getFilename())) {
                xmlStreamWriter.writeAttribute("title", attachmentResourceIdentifier.getFilename());
            }
            try {
                byte[] encoded = Base64Encoder.encode((byte[])unresolvedResource.getBytes("UTF-8"));
                xmlStreamWriter.writeAttribute("data-resource-id", new String(encoded, "UTF-8"));
            }
            catch (UnsupportedEncodingException ex) {
                throw new IOException("The UTF-8 charset is required.", ex);
            }
        });
    }
}

