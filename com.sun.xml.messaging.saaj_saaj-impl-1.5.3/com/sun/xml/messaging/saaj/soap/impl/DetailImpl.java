/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.Detail
 *  javax.xml.soap.DetailEntry
 *  javax.xml.soap.Name
 *  javax.xml.soap.Node
 *  javax.xml.soap.SOAPElement
 *  javax.xml.soap.SOAPException
 */
package com.sun.xml.messaging.saaj.soap.impl;

import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.messaging.saaj.soap.impl.ElementImpl;
import com.sun.xml.messaging.saaj.soap.impl.FaultElementImpl;
import com.sun.xml.messaging.saaj.soap.name.NameImpl;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import org.w3c.dom.Element;

public abstract class DetailImpl
extends FaultElementImpl
implements Detail {
    public DetailImpl(SOAPDocumentImpl ownerDoc, NameImpl detailName) {
        super(ownerDoc, detailName);
    }

    public DetailImpl(SOAPDocumentImpl ownerDoc, Element domElement) {
        super(ownerDoc, domElement);
    }

    protected abstract DetailEntry createDetailEntry(Name var1);

    protected abstract DetailEntry createDetailEntry(QName var1);

    public DetailEntry addDetailEntry(Name name) throws SOAPException {
        DetailEntry entry = this.createDetailEntry(name);
        this.addNode((org.w3c.dom.Node)entry);
        return entry;
    }

    public DetailEntry addDetailEntry(QName qname) throws SOAPException {
        DetailEntry entry = this.createDetailEntry(qname);
        this.addNode((org.w3c.dom.Node)entry);
        return entry;
    }

    @Override
    protected SOAPElement addElement(Name name) throws SOAPException {
        return this.addDetailEntry(name);
    }

    @Override
    protected SOAPElement addElement(QName name) throws SOAPException {
        return this.addDetailEntry(name);
    }

    @Override
    protected SOAPElement convertToSoapElement(Element element) {
        Node soapNode = this.getSoapDocument().find(element);
        if (soapNode instanceof DetailEntry) {
            return (SOAPElement)soapNode;
        }
        DetailEntry detailEntry = this.createDetailEntry(NameImpl.copyElementName(element));
        return this.replaceElementWithSOAPElement(element, (ElementImpl)detailEntry);
    }

    public Iterator<DetailEntry> getDetailEntries() {
        return new Iterator<DetailEntry>(){
            Iterator<org.w3c.dom.Node> eachNode;
            SOAPElement next;
            SOAPElement last;
            {
                this.eachNode = DetailImpl.this.getChildElementNodes();
                this.next = null;
                this.last = null;
            }

            @Override
            public boolean hasNext() {
                if (this.next == null) {
                    while (this.eachNode.hasNext()) {
                        this.next = (SOAPElement)this.eachNode.next();
                        if (this.next instanceof DetailEntry) break;
                        this.next = null;
                    }
                }
                return this.next != null;
            }

            @Override
            public DetailEntry next() {
                if (!this.hasNext()) {
                    throw new NoSuchElementException();
                }
                this.last = this.next;
                this.next = null;
                return (DetailEntry)this.last;
            }

            @Override
            public void remove() {
                if (this.last == null) {
                    throw new IllegalStateException();
                }
                SOAPElement target = this.last;
                DetailImpl.this.removeChild((org.w3c.dom.Node)target);
                this.last = null;
            }
        };
    }

    @Override
    protected boolean isStandardFaultElement() {
        return true;
    }
}

