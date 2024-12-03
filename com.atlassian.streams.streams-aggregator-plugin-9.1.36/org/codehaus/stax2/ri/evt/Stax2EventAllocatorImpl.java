/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.evt;

import java.util.ArrayList;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.stream.util.XMLEventConsumer;
import org.codehaus.stax2.DTDInfo;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.ri.EmptyIterator;
import org.codehaus.stax2.ri.evt.AttributeEventImpl;
import org.codehaus.stax2.ri.evt.CharactersEventImpl;
import org.codehaus.stax2.ri.evt.CommentEventImpl;
import org.codehaus.stax2.ri.evt.DTDEventImpl;
import org.codehaus.stax2.ri.evt.EndDocumentEventImpl;
import org.codehaus.stax2.ri.evt.EndElementEventImpl;
import org.codehaus.stax2.ri.evt.EntityReferenceEventImpl;
import org.codehaus.stax2.ri.evt.NamespaceEventImpl;
import org.codehaus.stax2.ri.evt.ProcInstrEventImpl;
import org.codehaus.stax2.ri.evt.StartDocumentEventImpl;
import org.codehaus.stax2.ri.evt.StartElementEventImpl;

public class Stax2EventAllocatorImpl
implements XMLEventAllocator,
XMLStreamConstants {
    public XMLEvent allocate(XMLStreamReader xMLStreamReader) throws XMLStreamException {
        Location location = this.getLocation(xMLStreamReader);
        switch (xMLStreamReader.getEventType()) {
            case 12: {
                return new CharactersEventImpl(location, xMLStreamReader.getText(), true);
            }
            case 4: {
                return new CharactersEventImpl(location, xMLStreamReader.getText(), false);
            }
            case 5: {
                return new CommentEventImpl(location, xMLStreamReader.getText());
            }
            case 11: {
                return this.createDTD(xMLStreamReader, location);
            }
            case 8: {
                return new EndDocumentEventImpl(location);
            }
            case 2: {
                return new EndElementEventImpl(location, xMLStreamReader);
            }
            case 3: {
                return new ProcInstrEventImpl(location, xMLStreamReader.getPITarget(), xMLStreamReader.getPIData());
            }
            case 6: {
                CharactersEventImpl charactersEventImpl = new CharactersEventImpl(location, xMLStreamReader.getText(), false);
                charactersEventImpl.setWhitespaceStatus(true);
                return charactersEventImpl;
            }
            case 7: {
                return new StartDocumentEventImpl(location, xMLStreamReader);
            }
            case 1: {
                return this.createStartElement(xMLStreamReader, location);
            }
            case 9: {
                return this.createEntityReference(xMLStreamReader, location);
            }
        }
        throw new XMLStreamException("Unrecognized event type " + xMLStreamReader.getEventType() + ".");
    }

    public void allocate(XMLStreamReader xMLStreamReader, XMLEventConsumer xMLEventConsumer) throws XMLStreamException {
        xMLEventConsumer.add(this.allocate(xMLStreamReader));
    }

    public XMLEventAllocator newInstance() {
        return new Stax2EventAllocatorImpl();
    }

    protected Location getLocation(XMLStreamReader xMLStreamReader) {
        return xMLStreamReader.getLocation();
    }

    protected EntityReference createEntityReference(XMLStreamReader xMLStreamReader, Location location) throws XMLStreamException {
        return new EntityReferenceEventImpl(location, xMLStreamReader.getLocalName());
    }

    protected DTD createDTD(XMLStreamReader xMLStreamReader, Location location) throws XMLStreamException {
        if (xMLStreamReader instanceof XMLStreamReader2) {
            XMLStreamReader2 xMLStreamReader2 = (XMLStreamReader2)xMLStreamReader;
            DTDInfo dTDInfo = xMLStreamReader2.getDTDInfo();
            return new DTDEventImpl(location, dTDInfo.getDTDRootName(), dTDInfo.getDTDSystemId(), dTDInfo.getDTDPublicId(), dTDInfo.getDTDInternalSubset(), dTDInfo.getProcessedDTD());
        }
        return new DTDEventImpl(location, null, xMLStreamReader.getText());
    }

    protected StartElement createStartElement(XMLStreamReader xMLStreamReader, Location location) throws XMLStreamException {
        ArrayList<NamespaceEventImpl> arrayList;
        int n;
        ArrayList<AttributeEventImpl> arrayList2;
        int n2;
        NamespaceContext namespaceContext = null;
        if (xMLStreamReader instanceof XMLStreamReader2) {
            namespaceContext = ((XMLStreamReader2)xMLStreamReader).getNonTransientNamespaceContext();
        }
        if ((n2 = xMLStreamReader.getAttributeCount()) < 1) {
            arrayList2 = null;
        } else {
            arrayList2 = new ArrayList<AttributeEventImpl>(n2);
            for (n = 0; n < n2; ++n) {
                QName qName = xMLStreamReader.getAttributeName(n);
                arrayList2.add(new AttributeEventImpl(location, qName, xMLStreamReader.getAttributeValue(n), xMLStreamReader.isAttributeSpecified(n)));
            }
        }
        n = xMLStreamReader.getNamespaceCount();
        if (n < 1) {
            arrayList = null;
        } else {
            arrayList = new ArrayList<NamespaceEventImpl>(n);
            for (int i = 0; i < n; ++i) {
                arrayList.add(NamespaceEventImpl.constructNamespace(location, xMLStreamReader.getNamespacePrefix(i), xMLStreamReader.getNamespaceURI(i)));
            }
        }
        return StartElementEventImpl.construct(location, xMLStreamReader.getName(), arrayList2 == null ? EmptyIterator.getInstance() : arrayList2.iterator(), arrayList == null ? EmptyIterator.getInstance() : arrayList.iterator(), namespaceContext);
    }
}

