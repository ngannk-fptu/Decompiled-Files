/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.view.embed;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.editor.macro.PlaceholderUrlFactory;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.view.embed.UnidentifiedAttachmentMarshaller;
import com.atlassian.confluence.content.render.xhtml.view.embed.ViewImageAttributeWriter;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import org.apache.commons.lang3.StringUtils;

public class ViewUnidentifiedAttachmentMarshaller
implements UnidentifiedAttachmentMarshaller {
    private final PlaceholderUrlFactory placeholderUrlFactory;

    public ViewUnidentifiedAttachmentMarshaller(PlaceholderUrlFactory placeholderUrlFactory) {
        this.placeholderUrlFactory = placeholderUrlFactory;
    }

    @Override
    public Streamable marshalPlaceholder(XmlStreamWriterTemplate xmlStreamWriterTemplate, EmbeddedImage embeddedImage, AttachmentResourceIdentifier ri, ConversionContext context) throws XhtmlException {
        return Streamables.from(xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
            xmlStreamWriter.writeStartElement("img");
            new ViewImageAttributeWriter(xmlStreamWriter, true).writeAttributes(embeddedImage);
            xmlStreamWriter.writeAttribute("src", this.placeholderUrlFactory.getUrlForUnknownAttachment());
            if (StringUtils.isNotBlank((CharSequence)ri.getFilename())) {
                xmlStreamWriter.writeAttribute("title", ri.getFilename());
            }
        });
    }
}

