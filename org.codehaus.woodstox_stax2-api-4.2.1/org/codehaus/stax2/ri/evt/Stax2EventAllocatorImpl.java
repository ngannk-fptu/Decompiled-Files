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
    @Override
    public XMLEvent allocate(XMLStreamReader r) throws XMLStreamException {
        Location loc = this.getLocation(r);
        switch (r.getEventType()) {
            case 12: {
                return new CharactersEventImpl(loc, r.getText(), true);
            }
            case 4: {
                return new CharactersEventImpl(loc, r.getText(), false);
            }
            case 5: {
                return new CommentEventImpl(loc, r.getText());
            }
            case 11: {
                return this.createDTD(r, loc);
            }
            case 8: {
                return new EndDocumentEventImpl(loc);
            }
            case 2: {
                return new EndElementEventImpl(loc, r);
            }
            case 3: {
                return new ProcInstrEventImpl(loc, r.getPITarget(), r.getPIData());
            }
            case 6: {
                CharactersEventImpl ch = new CharactersEventImpl(loc, r.getText(), false);
                ch.setWhitespaceStatus(true);
                return ch;
            }
            case 7: {
                return new StartDocumentEventImpl(loc, r);
            }
            case 1: {
                return this.createStartElement(r, loc);
            }
            case 9: {
                return this.createEntityReference(r, loc);
            }
        }
        throw new XMLStreamException("Unrecognized event type " + r.getEventType() + ".");
    }

    @Override
    public void allocate(XMLStreamReader r, XMLEventConsumer consumer) throws XMLStreamException {
        consumer.add(this.allocate(r));
    }

    @Override
    public XMLEventAllocator newInstance() {
        return new Stax2EventAllocatorImpl();
    }

    protected Location getLocation(XMLStreamReader r) {
        return r.getLocation();
    }

    protected EntityReference createEntityReference(XMLStreamReader r, Location loc) throws XMLStreamException {
        return new EntityReferenceEventImpl(loc, r.getLocalName());
    }

    protected DTD createDTD(XMLStreamReader r, Location loc) throws XMLStreamException {
        if (r instanceof XMLStreamReader2) {
            XMLStreamReader2 sr2 = (XMLStreamReader2)r;
            DTDInfo dtd = sr2.getDTDInfo();
            return new DTDEventImpl(loc, dtd.getDTDRootName(), dtd.getDTDSystemId(), dtd.getDTDPublicId(), dtd.getDTDInternalSubset(), dtd.getProcessedDTD());
        }
        return new DTDEventImpl(loc, null, r.getText());
    }

    protected StartElement createStartElement(XMLStreamReader r, Location loc) throws XMLStreamException {
        ArrayList<NamespaceEventImpl> ns;
        ArrayList<AttributeEventImpl> attrs;
        int attrCount;
        NamespaceContext nsCtxt = null;
        if (r instanceof XMLStreamReader2) {
            nsCtxt = ((XMLStreamReader2)r).getNonTransientNamespaceContext();
        }
        if ((attrCount = r.getAttributeCount()) < 1) {
            attrs = null;
        } else {
            attrs = new ArrayList<AttributeEventImpl>(attrCount);
            for (int i = 0; i < attrCount; ++i) {
                QName aname = r.getAttributeName(i);
                attrs.add(new AttributeEventImpl(loc, aname, r.getAttributeValue(i), r.isAttributeSpecified(i)));
            }
        }
        int nsCount = r.getNamespaceCount();
        if (nsCount < 1) {
            ns = null;
        } else {
            ns = new ArrayList<NamespaceEventImpl>(nsCount);
            for (int i = 0; i < nsCount; ++i) {
                ns.add(NamespaceEventImpl.constructNamespace(loc, r.getNamespacePrefix(i), r.getNamespaceURI(i)));
            }
        }
        return StartElementEventImpl.construct(loc, r.getName(), attrs == null ? EmptyIterator.getInstance() : attrs.iterator(), ns == null ? EmptyIterator.getInstance() : ns.iterator(), nsCtxt);
    }
}

