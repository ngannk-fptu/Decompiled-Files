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
    protected final QName _name;
    protected final ArrayList<Attribute> _attrs;
    protected final ArrayList<Namespace> _nsDecls;
    protected NamespaceContext _parentNsCtxt;
    NamespaceContext _actualNsCtxt = null;

    protected StartElementEventImpl(Location loc, QName name, ArrayList<Attribute> attrs, ArrayList<Namespace> nsDecls, NamespaceContext parentNsCtxt) {
        super(loc);
        this._name = name;
        this._attrs = attrs;
        this._nsDecls = nsDecls;
        this._parentNsCtxt = parentNsCtxt == null ? EmptyNamespaceContext.getInstance() : parentNsCtxt;
    }

    public static StartElementEventImpl construct(Location loc, QName name, Iterator<?> attrIt, Iterator<?> nsDeclIt, NamespaceContext nsCtxt) {
        ArrayList<Namespace> nsDecls;
        ArrayList<Attribute> attrs;
        if (attrIt == null || !attrIt.hasNext()) {
            attrs = null;
        } else {
            attrs = new ArrayList<Attribute>();
            do {
                attrs.add((Attribute)attrIt.next());
            } while (attrIt.hasNext());
        }
        if (nsDeclIt == null || !nsDeclIt.hasNext()) {
            nsDecls = null;
        } else {
            nsDecls = new ArrayList<Namespace>();
            do {
                nsDecls.add((Namespace)nsDeclIt.next());
            } while (nsDeclIt.hasNext());
        }
        return new StartElementEventImpl(loc, name, attrs, nsDecls, nsCtxt);
    }

    @Override
    public StartElement asStartElement() {
        return this;
    }

    @Override
    public int getEventType() {
        return 1;
    }

    @Override
    public boolean isStartElement() {
        return true;
    }

    @Override
    public void writeAsEncodedUnicode(Writer w) throws XMLStreamException {
        try {
            int i;
            int len;
            w.write(60);
            String prefix = this._name.getPrefix();
            if (prefix != null && prefix.length() > 0) {
                w.write(prefix);
                w.write(58);
            }
            w.write(this._name.getLocalPart());
            if (this._nsDecls != null) {
                len = this._nsDecls.size();
                for (i = 0; i < len; ++i) {
                    w.write(32);
                    this._nsDecls.get(i).writeAsEncodedUnicode(w);
                }
            }
            if (this._attrs != null) {
                len = this._attrs.size();
                for (i = 0; i < len; ++i) {
                    Attribute attr = this._attrs.get(i);
                    if (!attr.isSpecified()) continue;
                    w.write(32);
                    attr.writeAsEncodedUnicode(w);
                }
            }
            w.write(62);
        }
        catch (IOException ie) {
            throw new XMLStreamException(ie);
        }
    }

    @Override
    public void writeUsing(XMLStreamWriter2 sw) throws XMLStreamException {
        int i;
        int len;
        QName n = this._name;
        sw.writeStartElement(n.getPrefix(), n.getLocalPart(), n.getNamespaceURI());
        if (this._nsDecls != null) {
            len = this._nsDecls.size();
            for (i = 0; i < len; ++i) {
                Namespace ns = this._nsDecls.get(i);
                String prefix = ns.getPrefix();
                String uri = ns.getNamespaceURI();
                if (prefix == null || prefix.length() == 0) {
                    sw.writeDefaultNamespace(uri);
                    continue;
                }
                sw.writeNamespace(prefix, uri);
            }
        }
        if (this._attrs != null) {
            len = this._attrs.size();
            for (i = 0; i < len; ++i) {
                Attribute attr = this._attrs.get(i);
                if (!attr.isSpecified()) continue;
                QName name = attr.getName();
                sw.writeAttribute(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart(), attr.getValue());
            }
        }
    }

    @Override
    public final QName getName() {
        return this._name;
    }

    @Override
    public Iterator<Namespace> getNamespaces() {
        if (this._nsDecls == null) {
            return EmptyIterator.getInstance();
        }
        return this._nsDecls.iterator();
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        if (this._actualNsCtxt == null) {
            this._actualNsCtxt = this._nsDecls == null ? this._parentNsCtxt : MergedNsContext.construct(this._parentNsCtxt, this._nsDecls);
        }
        return this._actualNsCtxt;
    }

    @Override
    public String getNamespaceURI(String prefix) {
        if (this._nsDecls != null) {
            if (prefix == null) {
                prefix = "";
            }
            int len = this._nsDecls.size();
            for (int i = 0; i < len; ++i) {
                Namespace ns = this._nsDecls.get(i);
                String thisPrefix = ns.getPrefix();
                if (thisPrefix == null) {
                    thisPrefix = "";
                }
                if (!prefix.equals(thisPrefix)) continue;
                return ns.getNamespaceURI();
            }
        }
        return null;
    }

    @Override
    public Attribute getAttributeByName(QName nameIn) {
        if (this._attrs == null) {
            return null;
        }
        String ln = nameIn.getLocalPart();
        String uri = nameIn.getNamespaceURI();
        int len = this._attrs.size();
        boolean notInNs = uri == null || uri.length() == 0;
        for (int i = 0; i < len; ++i) {
            Attribute attr = this._attrs.get(i);
            QName name = attr.getName();
            if (!name.getLocalPart().equals(ln)) continue;
            String thisUri = name.getNamespaceURI();
            if (!(notInNs ? thisUri == null || thisUri.length() == 0 : uri.equals(thisUri))) continue;
            return attr;
        }
        return null;
    }

    @Override
    public Iterator<Attribute> getAttributes() {
        if (this._attrs == null) {
            return EmptyIterator.getInstance();
        }
        return this._attrs.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof StartElement)) {
            return false;
        }
        StartElement other = (StartElement)o;
        if (this._name.equals(other.getName()) && StartElementEventImpl.iteratedEquals(this.getNamespaces(), other.getNamespaces())) {
            return StartElementEventImpl.iteratedEquals(this.getAttributes(), other.getAttributes());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = this._name.hashCode();
        hash = StartElementEventImpl.addHash(this.getNamespaces(), hash);
        hash = StartElementEventImpl.addHash(this.getAttributes(), hash);
        return hash;
    }
}

