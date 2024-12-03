/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.ctc.wstx.evt.SimpleStartElement
 *  org.codehaus.stax2.ri.evt.AttributeEventImpl
 */
package com.atlassian.confluence.content.render.xhtml.view;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.StaxUtils;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.XmlEventReaderFactory;
import com.atlassian.confluence.content.render.xhtml.transformers.FragmentTransformer;
import com.ctc.wstx.evt.SimpleStartElement;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.codehaus.stax2.ri.evt.AttributeEventImpl;

public class ViewTableWrappingFragmentTransformer
implements FragmentTransformer {
    private final XmlEventReaderFactory xmlEventReaderFactory;

    public ViewTableWrappingFragmentTransformer(XmlEventReaderFactory xmlEventReaderFactory) {
        this.xmlEventReaderFactory = xmlEventReaderFactory;
    }

    @Override
    public boolean handles(StartElement startElementEvent, ConversionContext conversionContext) {
        return "table".equalsIgnoreCase(startElementEvent.getName().getLocalPart());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Streamable transform(XMLEventReader reader, FragmentTransformer mainFragmentTransformer, ConversionContext conversionContext) throws XhtmlException {
        try {
            Streamable body;
            XMLEvent tableEvent = reader.peek();
            if (!tableEvent.isStartElement() || !this.handles(tableEvent.asStartElement(), conversionContext)) {
                throw new XhtmlException("The next event is not an expected table StartElement.");
            }
            StringWriter writer = new StringWriter(80);
            if (tableEvent.isStartElement()) {
                StartElement startTableEvent = tableEvent.asStartElement();
                this.writeStartElementIncludeAttributes(startTableEvent, writer);
            } else {
                tableEvent.writeAsEncodedUnicode(writer);
            }
            String tableStart = writer.toString();
            XMLEventReader bodyReader = this.xmlEventReaderFactory.createXmlFragmentBodyEventReader(reader);
            try {
                body = mainFragmentTransformer.transform(bodyReader, mainFragmentTransformer, conversionContext);
            }
            finally {
                StaxUtils.closeQuietly(bodyReader);
            }
            return writer1 -> {
                writer1.write("<div class=\"table-wrap\">");
                writer1.write(tableStart);
                body.writeTo(writer1);
                writer1.write("</table></div>");
            };
        }
        catch (XMLStreamException ex) {
            throw new XhtmlException("Exception while wrapping a table.", ex);
        }
    }

    private void writeStartElementIncludeAttributes(StartElement startTableEvent, StringWriter writer) throws XMLStreamException {
        ArrayList<Object> attrs = new ArrayList<Object>(2);
        Iterator<Attribute> attrItr = startTableEvent.getAttributes();
        while (attrItr.hasNext()) {
            Attribute attr = attrItr.next();
            if (!attr.isSpecified()) {
                AttributeEventImpl hrefAttr = new AttributeEventImpl(attr.getLocation(), attr.getName(), attr.getValue(), true);
                attrs.add(hrefAttr);
                continue;
            }
            attrs.add(attr);
        }
        SimpleStartElement newStartTableElement = SimpleStartElement.construct((Location)startTableEvent.getLocation(), (QName)startTableEvent.getName(), attrs.iterator(), startTableEvent.getNamespaces(), (NamespaceContext)startTableEvent.getNamespaceContext());
        newStartTableElement.writeAsEncodedUnicode(writer);
    }
}

