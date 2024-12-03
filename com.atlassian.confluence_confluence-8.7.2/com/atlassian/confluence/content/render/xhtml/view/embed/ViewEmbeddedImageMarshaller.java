/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.v2.components.HtmlEscaper
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.view.embed;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.view.embed.AttachedImageMarshaller;
import com.atlassian.confluence.content.render.xhtml.view.embed.EmbeddedImageMarshaller;
import com.atlassian.confluence.content.render.xhtml.view.embed.EmbeddedWrapperAttributeWriter;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import com.atlassian.renderer.v2.components.HtmlEscaper;
import org.apache.commons.lang3.StringUtils;

public class ViewEmbeddedImageMarshaller
extends EmbeddedImageMarshaller {
    public ViewEmbeddedImageMarshaller(XmlStreamWriterTemplate xmlStreamWriterTemplate, AttachedImageMarshaller attachedImageMarshaller) {
        super(xmlStreamWriterTemplate, attachedImageMarshaller);
    }

    @Override
    public Streamable marshal(EmbeddedImage embeddedImage, ConversionContext conversionContext) throws XhtmlException {
        Streamable original = super.marshal(embeddedImage, conversionContext);
        Streamable beginSpanStream = Streamables.from(this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
            xmlStreamWriter.writeStartElement("span");
            new EmbeddedWrapperAttributeWriter(xmlStreamWriter).writeAttributes(embeddedImage);
        });
        Streamable endSpanStream = Streamables.from("</span>");
        String title = embeddedImage.getTitle();
        if (StringUtils.isNotBlank((CharSequence)title)) {
            String widthAttribute = StringUtils.isNotBlank((CharSequence)embeddedImage.getWidth()) ? " style='width: " + embeddedImage.getWidth() + "px;'" : "";
            Streamable imageTitle = Streamables.from("<span class='confluence-embedded-image-title' aria-hidden='true'" + widthAttribute + ">" + HtmlEscaper.escapeAll((String)title, (boolean)false) + "</span>");
            endSpanStream = Streamables.combine(imageTitle, endSpanStream);
        }
        return Streamables.combine(beginSpanStream, original, endSpanStream);
    }
}

