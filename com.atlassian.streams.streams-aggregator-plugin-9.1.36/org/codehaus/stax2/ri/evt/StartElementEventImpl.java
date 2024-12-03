/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.evt;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.ri.EmptyIterator;
import org.codehaus.stax2.ri.EmptyNamespaceContext;
import org.codehaus.stax2.ri.evt.BaseEventImpl;
import org.codehaus.stax2.ri.evt.MergedNsContext;

public class StartElementEventImpl
extends BaseEventImpl
implements StartElement {
    protected final QName mName;
    protected final ArrayList mAttrs;
    protected final ArrayList mNsDecls;
    protected NamespaceContext mParentNsCtxt;
    NamespaceContext mActualNsCtxt = null;

    protected StartElementEventImpl(Location location, QName qName, ArrayList arrayList, ArrayList arrayList2, NamespaceContext namespaceContext) {
        super(location);
        this.mName = qName;
        this.mAttrs = arrayList;
        this.mNsDecls = arrayList2;
        this.mParentNsCtxt = namespaceContext == null ? EmptyNamespaceContext.getInstance() : namespaceContext;
    }

    public static StartElementEventImpl construct(Location location, QName qName, Iterator iterator, Iterator iterator2, NamespaceContext namespaceContext) {
        ArrayList<Namespace> arrayList;
        ArrayList<Attribute> arrayList2;
        if (iterator == null || !iterator.hasNext()) {
            arrayList2 = null;
        } else {
            arrayList2 = new ArrayList<Attribute>();
            do {
                arrayList2.add((Attribute)iterator.next());
            } while (iterator.hasNext());
        }
        if (iterator2 == null || !iterator2.hasNext()) {
            arrayList = null;
        } else {
            arrayList = new ArrayList<Namespace>();
            do {
                arrayList.add((Namespace)iterator2.next());
            } while (iterator2.hasNext());
        }
        return new StartElementEventImpl(location, qName, arrayList2, arrayList, namespaceContext);
    }

    public StartElement asStartElement() {
        return this;
    }

    public int getEventType() {
        return 1;
    }

    public boolean isStartElement() {
        return true;
    }

    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            int n;
            int n2;
            writer.write(60);
            String string = this.mName.getPrefix();
            if (string != null && string.length() > 0) {
                writer.write(string);
                writer.write(58);
            }
            writer.write(this.mName.getLocalPart());
            if (this.mNsDecls != null) {
                n2 = this.mNsDecls.size();
                for (n = 0; n < n2; ++n) {
                    writer.write(32);
                    ((Namespace)this.mNsDecls.get(n)).writeAsEncodedUnicode(writer);
                }
            }
            if (this.mAttrs != null) {
                n2 = this.mAttrs.size();
                for (n = 0; n < n2; ++n) {
                    Attribute attribute = (Attribute)this.mAttrs.get(n);
                    if (!attribute.isSpecified()) continue;
                    writer.write(32);
                    attribute.writeAsEncodedUnicode(writer);
                }
            }
            writer.write(62);
        }
        catch (IOException iOException) {
            throw new XMLStreamException(iOException);
        }
    }

    public void writeUsing(XMLStreamWriter2 xMLStreamWriter2) throws XMLStreamException {
        Object object;
        Attribute attribute;
        int n;
        int n2;
        QName qName = this.mName;
        xMLStreamWriter2.writeStartElement(qName.getPrefix(), qName.getLocalPart(), qName.getNamespaceURI());
        if (this.mNsDecls != null) {
            n2 = this.mNsDecls.size();
            for (n = 0; n < n2; ++n) {
                attribute = (Namespace)this.mNsDecls.get(n);
                object = attribute.getPrefix();
                String string = attribute.getNamespaceURI();
                if (object == null || ((String)object).length() == 0) {
                    xMLStreamWriter2.writeDefaultNamespace(string);
                    continue;
                }
                xMLStreamWriter2.writeNamespace((String)object, string);
            }
        }
        if (this.mAttrs != null) {
            n2 = this.mAttrs.size();
            for (n = 0; n < n2; ++n) {
                attribute = (Attribute)this.mAttrs.get(n);
                if (!attribute.isSpecified()) continue;
                object = attribute.getName();
                xMLStreamWriter2.writeAttribute(((QName)object).getPrefix(), ((QName)object).getNamespaceURI(), ((QName)object).getLocalPart(), attribute.getValue());
            }
        }
    }

    public final QName getName() {
        return this.mName;
    }

    public Iterator getNamespaces() {
        return this.mNsDecls == null ? EmptyIterator.getInstance() : this.mNsDecls.iterator();
    }

    public NamespaceContext getNamespaceContext() {
        if (this.mActualNsCtxt == null) {
            this.mActualNsCtxt = this.mNsDecls == null ? this.mParentNsCtxt : MergedNsContext.construct(this.mParentNsCtxt, this.mNsDecls);
        }
        return this.mActualNsCtxt;
    }

    public String getNamespaceURI(String string) {
        if (this.mNsDecls != null) {
            if (string == null) {
                string = "";
            }
            int n = this.mNsDecls.size();
            for (int i = 0; i < n; ++i) {
                Namespace namespace = (Namespace)this.mNsDecls.get(i);
                String string2 = namespace.getPrefix();
                if (string2 == null) {
                    string2 = "";
                }
                if (!string.equals(string2)) continue;
                return namespace.getNamespaceURI();
            }
        }
        return null;
    }

    public Attribute getAttributeByName(QName qName) {
        if (this.mAttrs == null) {
            return null;
        }
        String string = qName.getLocalPart();
        String string2 = qName.getNamespaceURI();
        int n = this.mAttrs.size();
        boolean bl = string2 == null || string2.length() == 0;
        for (int i = 0; i < n; ++i) {
            Attribute attribute = (Attribute)this.mAttrs.get(i);
            QName qName2 = attribute.getName();
            if (!qName2.getLocalPart().equals(string)) continue;
            String string3 = qName2.getNamespaceURI();
            if (!(bl ? string3 == null || string3.length() == 0 : string2.equals(string3))) continue;
            return attribute;
        }
        return null;
    }

    public Iterator getAttributes() {
        if (this.mAttrs == null) {
            return EmptyIterator.getInstance();
        }
        return this.mAttrs.iterator();
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (!(object instanceof StartElement)) {
            return false;
        }
        StartElement startElement = (StartElement)object;
        if (this.mName.equals(startElement.getName()) && StartElementEventImpl.iteratedEquals(this.getNamespaces(), startElement.getNamespaces())) {
            return StartElementEventImpl.iteratedEquals(this.getAttributes(), startElement.getAttributes());
        }
        return false;
    }

    public int hashCode() {
        int n = this.mName.hashCode();
        n = StartElementEventImpl.addHash(this.getNamespaces(), n);
        n = StartElementEventImpl.addHash(this.getAttributes(), n);
        return n;
    }
}

