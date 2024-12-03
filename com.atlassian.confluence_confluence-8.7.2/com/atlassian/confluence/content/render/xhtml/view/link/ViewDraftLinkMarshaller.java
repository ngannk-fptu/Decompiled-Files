/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.content.render.xhtml.view.link;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.StaxStreamMarshaller;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.view.link.CommonLinkAttributesWriter;
import com.atlassian.confluence.xhtml.api.Link;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ViewDraftLinkMarshaller
implements Marshaller<Link> {
    private final CommonLinkAttributesWriter commonLinkAttributesWriter;
    private final Marshaller<Link> linkBodyMarshaller;
    private final XmlStreamWriterTemplate xmlStreamWriterTemplate;
    private final StaxStreamMarshaller<Link> linkStaxStreamMarshaller;

    public ViewDraftLinkMarshaller(CommonLinkAttributesWriter commonLinkAttributesWriter, Marshaller<Link> linkBodyMarshaller, XmlStreamWriterTemplate xmlStreamWriterTemplate, @Nullable StaxStreamMarshaller<Link> linkStaxStreamMarshaller) {
        this.commonLinkAttributesWriter = Objects.requireNonNull(commonLinkAttributesWriter);
        this.linkBodyMarshaller = Objects.requireNonNull(linkBodyMarshaller);
        this.xmlStreamWriterTemplate = Objects.requireNonNull(xmlStreamWriterTemplate);
        this.linkStaxStreamMarshaller = linkStaxStreamMarshaller;
    }

    @Override
    public Streamable marshal(Link link, ConversionContext conversionContext) throws XhtmlException {
        Streamable marshalledLinkBody = this.linkBodyMarshaller.marshal(link, conversionContext);
        return Streamables.from(this.xmlStreamWriterTemplate, (xmlStreamWriter, underlyingWriter) -> {
            xmlStreamWriter.writeStartElement("a");
            Object anchor = "#";
            if (StringUtils.isNotBlank((CharSequence)link.getAnchor())) {
                anchor = (String)anchor + link.getAnchor();
            }
            this.commonLinkAttributesWriter.writeCommonAttributes(link, xmlStreamWriter, conversionContext);
            xmlStreamWriter.writeAttribute("href", (String)anchor);
            if (this.linkStaxStreamMarshaller != null) {
                this.linkStaxStreamMarshaller.marshal(link, xmlStreamWriter, conversionContext);
            }
            StaxUtils.writeRawXML(xmlStreamWriter, underlyingWriter, marshalledLinkBody);
            xmlStreamWriter.writeEndElement();
        });
    }
}

