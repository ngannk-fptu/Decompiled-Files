/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.content.render.xhtml;

import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterCallback;
import com.atlassian.confluence.content.render.xhtml.XmlStreamWriterTemplate;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class DefaultXmlStreamWriterTemplate
implements XmlStreamWriterTemplate {
    private final XMLOutputFactory xmlFragmentOutputFactory;

    public DefaultXmlStreamWriterTemplate(XMLOutputFactory xmlFragmentOutputFactory) {
        this.xmlFragmentOutputFactory = xmlFragmentOutputFactory;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute(Writer writer, XmlStreamWriterCallback callback) throws XMLStreamException, IOException {
        XMLStreamWriter xmlStreamWriter = null;
        try {
            xmlStreamWriter = this.xmlFragmentOutputFactory.createXMLStreamWriter(writer);
            callback.withStreamWriter(xmlStreamWriter, writer);
        }
        catch (Throwable throwable) {
            StaxUtils.closeQuietly(xmlStreamWriter);
            throw throwable;
        }
        StaxUtils.closeQuietly(xmlStreamWriter);
    }
}

