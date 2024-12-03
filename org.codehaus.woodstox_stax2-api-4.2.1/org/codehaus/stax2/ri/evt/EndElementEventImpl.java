/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.evt;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.ri.EmptyIterator;
import org.codehaus.stax2.ri.evt.BaseEventImpl;
import org.codehaus.stax2.ri.evt.NamespaceEventImpl;

public class EndElementEventImpl
extends BaseEventImpl
implements EndElement {
    protected final QName mName;
    protected final ArrayList<Namespace> mNamespaces;

    public EndElementEventImpl(Location loc, XMLStreamReader r) {
        super(loc);
        this.mName = r.getName();
        int nsCount = r.getNamespaceCount();
        if (nsCount == 0) {
            this.mNamespaces = null;
        } else {
            ArrayList<NamespaceEventImpl> l = new ArrayList<NamespaceEventImpl>(nsCount);
            for (int i = 0; i < nsCount; ++i) {
                l.add(NamespaceEventImpl.constructNamespace(loc, r.getNamespacePrefix(i), r.getNamespaceURI(i)));
            }
            this.mNamespaces = l;
        }
    }

    public EndElementEventImpl(Location loc, QName name, Iterator<Namespace> namespaces) {
        super(loc);
        this.mName = name;
        if (namespaces == null || !namespaces.hasNext()) {
            this.mNamespaces = null;
        } else {
            ArrayList<Namespace> l = new ArrayList<Namespace>();
            while (namespaces.hasNext()) {
                l.add(namespaces.next());
            }
            this.mNamespaces = l;
        }
    }

    @Override
    public QName getName() {
        return this.mName;
    }

    @Override
    public Iterator<Namespace> getNamespaces() {
        if (this.mNamespaces == null) {
            return EmptyIterator.getInstance();
        }
        return this.mNamespaces.iterator();
    }

    @Override
    public EndElement asEndElement() {
        return this;
    }

    @Override
    public int getEventType() {
        return 2;
    }

    @Override
    public boolean isEndElement() {
        return true;
    }

    @Override
    public void writeAsEncodedUnicode(Writer w) throws XMLStreamException {
        try {
            w.write("</");
            String prefix = this.mName.getPrefix();
            if (prefix != null && prefix.length() > 0) {
                w.write(prefix);
                w.write(58);
            }
            w.write(this.mName.getLocalPart());
            w.write(62);
        }
        catch (IOException ie) {
            this.throwFromIOE(ie);
        }
    }

    @Override
    public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException {
        w.writeEndElement();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof EndElement)) {
            return false;
        }
        EndElement other = (EndElement)o;
        return this.getName().equals(other.getName());
    }

    @Override
    public int hashCode() {
        return this.getName().hashCode();
    }
}

