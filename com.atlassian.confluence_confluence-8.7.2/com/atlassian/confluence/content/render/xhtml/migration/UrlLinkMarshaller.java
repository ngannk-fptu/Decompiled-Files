/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.migration.UrlResourceIdentifier;
import com.atlassian.confluence.xhtml.api.Link;
import com.atlassian.confluence.xhtml.api.PlainTextLinkBody;
import com.atlassian.confluence.xhtml.api.RichTextLinkBody;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class UrlLinkMarshaller
implements Marshaller<Link> {
    private final XmlStreamWriterTemplate xmlStreamWriterTemplate;

    public UrlLinkMarshaller(XmlStreamWriterTemplate xmlStreamWriterTemplate) {
        this.xmlStreamWriterTemplate = xmlStreamWriterTemplate;
    }

    @Override
    public Streamable marshal(Link link, ConversionContext conversionContext) throws XhtmlException {
        return Streamables.from(this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
            String bodyText;
            PlainTextLinkBody body;
            UrlResourceIdentifier urlResourceIdentifier = (UrlResourceIdentifier)link.getDestinationResourceIdentifier();
            String href = StringUtils.defaultString((String)urlResourceIdentifier.getUrl()) + (String)(StringUtils.isNotBlank((CharSequence)link.getAnchor()) ? "#" + link.getAnchor() : "");
            xmlStreamWriter.writeStartElement("a");
            xmlStreamWriter.writeAttribute("href", href);
            if (StringUtils.isNotBlank((CharSequence)link.getTooltip())) {
                xmlStreamWriter.writeAttribute("title", link.getTooltip());
            }
            if ((body = link.getBody()) == null) {
                body = new PlainTextLinkBody(href);
            }
            if (!(body instanceof RichTextLinkBody) && !(body instanceof PlainTextLinkBody)) {
                throw new UnsupportedOperationException("UrlResourceIdentifier links should only occur during migration and will only have RichTextLinkBody or PlainTextLinkBody bodies.");
            }
            if (body instanceof RichTextLinkBody) {
                bodyText = ((RichTextLinkBody)((Object)body)).getBody();
                if (StringUtils.isBlank((CharSequence)bodyText)) {
                    body = new PlainTextLinkBody(href);
                }
            } else {
                bodyText = body.getBody();
                if (StringUtils.isBlank((CharSequence)bodyText)) {
                    body = new PlainTextLinkBody(href);
                }
            }
            String marshalledBody = body instanceof RichTextLinkBody ? ((RichTextLinkBody)((Object)body)).getBody() : StringEscapeUtils.escapeHtml4((String)body.getBody());
            xmlStreamWriter.writeCharacters("");
            xmlStreamWriter.flush();
            underlyingWriter.append(marshalledBody);
            xmlStreamWriter.writeEndElement();
        });
    }
}

