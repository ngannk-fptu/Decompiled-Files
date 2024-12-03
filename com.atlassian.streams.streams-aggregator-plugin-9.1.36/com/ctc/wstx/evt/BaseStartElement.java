/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.evt;

import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.util.BaseNsContext;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.ri.EmptyIterator;
import org.codehaus.stax2.ri.evt.BaseEventImpl;

abstract class BaseStartElement
extends BaseEventImpl
implements StartElement {
    protected final QName mName;
    protected final BaseNsContext mNsCtxt;

    protected BaseStartElement(Location loc, QName name, BaseNsContext nsCtxt) {
        super(loc);
        this.mName = name;
        this.mNsCtxt = nsCtxt;
    }

    public abstract Attribute getAttributeByName(QName var1);

    public abstract Iterator getAttributes();

    public final QName getName() {
        return this.mName;
    }

    public Iterator getNamespaces() {
        if (this.mNsCtxt == null) {
            return EmptyIterator.getInstance();
        }
        return this.mNsCtxt.getNamespaces();
    }

    public NamespaceContext getNamespaceContext() {
        return this.mNsCtxt;
    }

    public String getNamespaceURI(String prefix) {
        return this.mNsCtxt == null ? null : this.mNsCtxt.getNamespaceURI(prefix);
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

    public void writeAsEncodedUnicode(Writer w) throws XMLStreamException {
        try {
            w.write(60);
            String prefix = this.mName.getPrefix();
            if (prefix != null && prefix.length() > 0) {
                w.write(prefix);
                w.write(58);
            }
            w.write(this.mName.getLocalPart());
            this.outputNsAndAttr(w);
            w.write(62);
        }
        catch (IOException ie) {
            throw new WstxIOException(ie);
        }
    }

    public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException {
        QName n = this.mName;
        w.writeStartElement(n.getPrefix(), n.getLocalPart(), n.getNamespaceURI());
        this.outputNsAndAttr(w);
    }

    protected abstract void outputNsAndAttr(Writer var1) throws IOException;

    protected abstract void outputNsAndAttr(XMLStreamWriter var1) throws XMLStreamException;

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
        if (this.mName.equals(other.getName()) && BaseStartElement.iteratedEquals(this.getNamespaces(), other.getNamespaces())) {
            return BaseStartElement.iteratedEquals(this.getAttributes(), other.getAttributes());
        }
        return false;
    }

    public int hashCode() {
        int hash = this.mName.hashCode();
        hash = BaseStartElement.addHash(this.getNamespaces(), hash);
        hash = BaseStartElement.addHash(this.getAttributes(), hash);
        return hash;
    }
}

