/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.content.render.xhtml.view.embed;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.migration.UrlResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.model.resource.identifiers.AttachmentResourceIdentifier;
import com.atlassian.confluence.content.render.xhtml.view.embed.AttachedImageMarshaller;
import com.atlassian.confluence.content.render.xhtml.view.embed.ViewImageAttributeWriter;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class EmbeddedImageMarshaller
implements Marshaller<EmbeddedImage> {
    protected final XmlStreamWriterTemplate xmlStreamWriterTemplate;
    private final AttachedImageMarshaller attachedImageMarshaller;

    public EmbeddedImageMarshaller(XmlStreamWriterTemplate xmlStreamWriterTemplate, AttachedImageMarshaller attachedImageMarshaller) {
        this.xmlStreamWriterTemplate = xmlStreamWriterTemplate;
        this.attachedImageMarshaller = attachedImageMarshaller;
    }

    @Override
    public Streamable marshal(EmbeddedImage embeddedImage, ConversionContext conversionContext) throws XhtmlException {
        conversionContext.disableAsyncRenderSafe();
        if (embeddedImage.getResourceIdentifier() instanceof AttachmentResourceIdentifier) {
            AttachmentResourceIdentifier attachmentResourceIdentifier = (AttachmentResourceIdentifier)embeddedImage.getResourceIdentifier();
            return this.attachedImageMarshaller.marshal(this.xmlStreamWriterTemplate, embeddedImage, attachmentResourceIdentifier, conversionContext);
        }
        if (embeddedImage.getResourceIdentifier() instanceof UrlResourceIdentifier) {
            return Streamables.from(this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
                xmlStreamWriter.writeStartElement("img");
                new ViewImageAttributeWriter(xmlStreamWriter).writeAttributes(embeddedImage);
                String imageSrc = ((UrlResourceIdentifier)embeddedImage.getResourceIdentifier()).getUrl();
                xmlStreamWriter.writeAttribute("src", imageSrc);
                if (StringUtils.isNotBlank((CharSequence)embeddedImage.getTitle())) {
                    xmlStreamWriter.writeAttribute("data-element-title", embeddedImage.getTitle());
                }
                xmlStreamWriter.writeAttribute("data-image-src", imageSrc);
            });
        }
        throw new XhtmlException("Unsupported embedded resource: " + embeddedImage);
    }
}

