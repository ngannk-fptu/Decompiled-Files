/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.storage.link;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.storage.link.StorageLinkConstants;
import com.atlassian.confluence.xhtml.api.EmbeddedImage;
import com.atlassian.confluence.xhtml.api.EmbeddedImageLinkBody;
import com.atlassian.confluence.xhtml.api.LinkBody;
import com.atlassian.confluence.xhtml.api.PlainTextLinkBody;
import com.atlassian.confluence.xhtml.api.RichTextLinkBody;
import java.text.MessageFormat;
import org.apache.commons.lang3.StringUtils;

public class StorageLinkBodyMarshaller
implements Marshaller<LinkBody<?>> {
    private final Marshaller<EmbeddedImage> embeddedImageMarshaller;
    private final XmlStreamWriterTemplate xmlStreamWriterTemplate;
    private static final String LINK_BODY_EXPANDED_NAME = "ac:link-body";
    private static final MessageFormat RICH_AND_IMAGE_BODY_FRAGMENT = new MessageFormat("<ac:link-body>{0}</ac:link-body>");

    public StorageLinkBodyMarshaller(Marshaller<EmbeddedImage> embeddedImageMarshaller, XmlStreamWriterTemplate xmlStreamWriterTemplate) {
        this.embeddedImageMarshaller = embeddedImageMarshaller;
        this.xmlStreamWriterTemplate = xmlStreamWriterTemplate;
    }

    @Override
    public Streamable marshal(LinkBody<?> linkBody, ConversionContext conversionContext) throws XhtmlException {
        if (linkBody == null) {
            return null;
        }
        if (linkBody instanceof EmbeddedImageLinkBody || linkBody instanceof RichTextLinkBody) {
            String bodyString = this.getBodyString(linkBody, conversionContext);
            return writer -> {
                if (StringUtils.isNotBlank((CharSequence)bodyString)) {
                    writer.write(RICH_AND_IMAGE_BODY_FRAGMENT.format(new String[]{bodyString}));
                }
            };
        }
        if (linkBody instanceof PlainTextLinkBody) {
            return this.marshallPlainTextLinkBody((PlainTextLinkBody)linkBody);
        }
        throw new UnsupportedOperationException("Unsupported link body: " + linkBody);
    }

    private String getBodyString(LinkBody<?> linkBody, ConversionContext conversionContext) throws XhtmlException {
        String bodyString = linkBody instanceof EmbeddedImageLinkBody ? Streamables.writeToString(this.embeddedImageMarshaller.marshal(((EmbeddedImageLinkBody)linkBody).getBody(), conversionContext)) : ((RichTextLinkBody)linkBody).getBody();
        return bodyString;
    }

    private Streamable marshallPlainTextLinkBody(PlainTextLinkBody body) throws XhtmlException {
        if (body == null || StringUtils.isBlank((CharSequence)body.getBody())) {
            return Streamables.empty();
        }
        return Streamables.from(this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
            xmlStreamWriter.writeStartElement(StorageLinkConstants.PLAIN_TEXT_LINK_BODY_ELEMENT_QNAME.getPrefix(), StorageLinkConstants.PLAIN_TEXT_LINK_BODY_ELEMENT_QNAME.getLocalPart(), StorageLinkConstants.PLAIN_TEXT_LINK_BODY_ELEMENT_QNAME.getNamespaceURI());
            xmlStreamWriter.writeCData(body.getBody());
            xmlStreamWriter.writeEndElement();
        });
    }
}

