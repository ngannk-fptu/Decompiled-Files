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
    final QName mName;
    final ArrayList mNamespaces;

    public EndElementEventImpl(Location location, XMLStreamReader xMLStreamReader) {
        super(location);
        this.mName = xMLStreamReader.getName();
        int n = xMLStreamReader.getNamespaceCount();
        if (n == 0) {
            this.mNamespaces = null;
        } else {
            ArrayList<NamespaceEventImpl> arrayList = new ArrayList<NamespaceEventImpl>(n);
            for (int i = 0; i < n; ++i) {
                arrayList.add(NamespaceEventImpl.constructNamespace(location, xMLStreamReader.getNamespacePrefix(i), xMLStreamReader.getNamespaceURI(i)));
            }
            this.mNamespaces = arrayList;
        }
    }

    public EndElementEventImpl(Location location, QName qName, Iterator iterator) {
        super(location);
        this.mName = qName;
        if (iterator == null || !iterator.hasNext()) {
            this.mNamespaces = null;
        } else {
            ArrayList<Namespace> arrayList = new ArrayList<Namespace>();
            while (iterator.hasNext()) {
                arrayList.add((Namespace)iterator.next());
            }
            this.mNamespaces = arrayList;
        }
    }

    public QName getName() {
        return this.mName;
    }

    public Iterator getNamespaces() {
        return this.mNamespaces == null ? EmptyIterator.getInstance() : this.mNamespaces.iterator();
    }

    public EndElement asEndElement() {
        return this;
    }

    public int getEventType() {
        return 2;
    }

    public boolean isEndElement() {
        return true;
    }

    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write("</");
            String string = this.mName.getPrefix();
            if (string != null && string.length() > 0) {
                writer.write(string);
                writer.write(58);
            }
            writer.write(this.mName.getLocalPart());
            writer.write(62);
        }
        catch (IOException iOException) {
            this.throwFromIOE(iOException);
        }
    }

    public void writeUsing(XMLStreamWriter2 xMLStreamWriter2) throws XMLStreamException {
        xMLStreamWriter2.writeEndElement();
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (!(object instanceof EndElement)) {
            return false;
        }
        EndElement endElement = (EndElement)object;
        return this.getName().equals(endElement.getName());
    }

    public int hashCode() {
        return this.getName().hashCode();
    }
}

