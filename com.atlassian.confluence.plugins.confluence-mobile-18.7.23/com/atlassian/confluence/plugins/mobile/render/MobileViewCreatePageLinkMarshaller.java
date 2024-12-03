/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Marshaller
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.content.render.xhtml.Streamables
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.content.render.xhtml.XmlStreamWriterCallback
 *  com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate
 *  com.atlassian.confluence.content.render.xhtml.model.links.CreatePageLink
 *  com.atlassian.confluence.xhtml.api.Link
 */
package com.atlassian.confluence.plugins.mobile.render;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Marshaller;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterCallback;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import com.atlassian.confluence.content.render.xhtml.model.links.CreatePageLink;
import com.atlassian.confluence.xhtml.api.Link;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class MobileViewCreatePageLinkMarshaller
implements Marshaller<CreatePageLink> {
    private final XmlStreamWriterTemplate xmlStreamWriterTemplate;
    private final Marshaller<Link> linkBodyMarshaller;

    public MobileViewCreatePageLinkMarshaller(XmlStreamWriterTemplate xmlStreamWriterTemplate, Marshaller<Link> linkBodyMarshaller) {
        this.xmlStreamWriterTemplate = xmlStreamWriterTemplate;
        this.linkBodyMarshaller = linkBodyMarshaller;
    }

    public Streamable marshal(CreatePageLink createPageLink, ConversionContext conversionContext) throws XhtmlException {
        final Streamable marshalledLinkBody = this.linkBodyMarshaller.marshal((Object)createPageLink.getDelegate(), conversionContext);
        return Streamables.from((XmlStreamWriterTemplate)this.xmlStreamWriterTemplate, (XmlStreamWriterCallback)new XmlStreamWriterCallback(){

            public void withStreamWriter(XMLStreamWriter xmlStreamWriter, Writer underlyingWriter) throws XMLStreamException, IOException {
                xmlStreamWriter.writeStartElement("span");
                xmlStreamWriter.writeAttribute("class", "create-page");
                xmlStreamWriter.writeCharacters("");
                xmlStreamWriter.flush();
                marshalledLinkBody.writeTo(underlyingWriter);
                xmlStreamWriter.writeEndElement();
            }
        });
    }
}

