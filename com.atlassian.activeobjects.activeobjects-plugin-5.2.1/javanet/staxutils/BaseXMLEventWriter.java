/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javanet.staxutils.SimpleNamespaceContext;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public abstract class BaseXMLEventWriter
implements XMLEventWriter {
    protected XMLEventFactory factory;
    protected List nsStack = new ArrayList();
    protected StartElement lastStart;
    protected Map attrBuff = new LinkedHashMap();
    protected Map nsBuff = new LinkedHashMap();
    protected boolean closed;

    protected BaseXMLEventWriter() {
        this(null, null);
    }

    protected BaseXMLEventWriter(XMLEventFactory eventFactory, NamespaceContext nsCtx) {
        if (nsCtx != null) {
            this.nsStack.add(new SimpleNamespaceContext(nsCtx));
        } else {
            this.nsStack.add(new SimpleNamespaceContext());
        }
        this.factory = eventFactory != null ? eventFactory : XMLEventFactory.newInstance();
    }

    public synchronized void flush() throws XMLStreamException {
        if (!this.closed) {
            this.sendCachedEvents();
        }
    }

    private void sendCachedEvents() throws XMLStreamException {
        if (this.lastStart != null) {
            SimpleNamespaceContext nsCtx = this.pushNamespaceStack();
            ArrayList namespaces = new ArrayList();
            this.mergeNamespaces(this.lastStart.getNamespaces(), namespaces);
            this.mergeNamespaces(this.nsBuff.values().iterator(), namespaces);
            this.nsBuff.clear();
            ArrayList attributes = new ArrayList();
            this.mergeAttributes(this.lastStart.getAttributes(), namespaces, attributes);
            this.mergeAttributes(this.attrBuff.values().iterator(), namespaces, attributes);
            this.attrBuff.clear();
            QName tagName = this.lastStart.getName();
            QName newName = this.processQName(tagName, namespaces);
            StartElement newStart = this.factory.createStartElement(newName.getPrefix(), newName.getNamespaceURI(), newName.getLocalPart(), attributes.iterator(), namespaces.iterator(), nsCtx);
            this.lastStart = null;
            this.sendEvent(newStart);
        } else {
            XMLEvent evt;
            Iterator i = this.nsBuff.values().iterator();
            while (i.hasNext()) {
                evt = (XMLEvent)i.next();
                this.sendEvent(evt);
            }
            this.nsBuff.clear();
            i = this.attrBuff.values().iterator();
            while (i.hasNext()) {
                evt = (XMLEvent)i.next();
                this.sendEvent(evt);
            }
            this.attrBuff.clear();
        }
    }

    private void mergeAttributes(Iterator iter, List namespaces, List attributes) {
        while (iter.hasNext()) {
            QName newName;
            Attribute attr = (Attribute)iter.next();
            QName attrName = attr.getName();
            if (!attrName.equals(newName = this.processQName(attrName, namespaces))) {
                Attribute newAttr = this.factory.createAttribute(newName, attr.getValue());
                attributes.add(newAttr);
                continue;
            }
            attributes.add(attr);
        }
    }

    private void mergeNamespaces(Iterator iter, List namespaces) throws XMLStreamException {
        while (iter.hasNext()) {
            Namespace ns = (Namespace)iter.next();
            String prefix = ns.getPrefix();
            String nsURI = ns.getNamespaceURI();
            SimpleNamespaceContext nsCtx = this.peekNamespaceStack();
            if (!nsCtx.isPrefixDeclared(prefix)) {
                if (prefix == null || prefix.length() == 0) {
                    nsCtx.setDefaultNamespace(nsURI);
                } else {
                    nsCtx.setPrefix(prefix, nsURI);
                }
                namespaces.add(ns);
                continue;
            }
            if (nsCtx.getNamespaceURI(prefix).equals(nsURI)) continue;
            throw new XMLStreamException("Prefix already declared: " + ns, ns.getLocation());
        }
    }

    private QName processQName(QName name, List namespaces) {
        SimpleNamespaceContext nsCtx = this.peekNamespaceStack();
        String nsURI = name.getNamespaceURI();
        String prefix = name.getPrefix();
        if (prefix != null && prefix.length() > 0) {
            String resolvedNS = nsCtx.getNamespaceURI(prefix);
            if (resolvedNS != null) {
                if (!resolvedNS.equals(nsURI)) {
                    String newPrefix = nsCtx.getPrefix(nsURI);
                    if (newPrefix == null) {
                        newPrefix = this.generatePrefix(nsURI, nsCtx, namespaces);
                    }
                    return new QName(nsURI, name.getLocalPart(), newPrefix);
                }
            } else if (nsURI != null && nsURI.length() > 0) {
                nsCtx.setPrefix(prefix, nsURI);
                namespaces.add(this.factory.createNamespace(prefix, nsURI));
            }
            return name;
        }
        if (nsURI != null && nsURI.length() > 0) {
            String newPrefix = nsCtx.getPrefix(nsURI);
            if (newPrefix == null) {
                newPrefix = this.generatePrefix(nsURI, nsCtx, namespaces);
            }
            return new QName(nsURI, name.getLocalPart(), newPrefix);
        }
        return name;
    }

    private String generatePrefix(String nsURI, SimpleNamespaceContext nsCtx, List namespaces) {
        String newPrefix;
        int nsCount = 0;
        do {
            newPrefix = "ns" + nsCount;
            ++nsCount;
        } while (nsCtx.getNamespaceURI(newPrefix) != null);
        nsCtx.setPrefix(newPrefix, nsURI);
        namespaces.add(this.factory.createNamespace(newPrefix, nsURI));
        return newPrefix;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public synchronized void close() throws XMLStreamException {
        if (this.closed) {
            return;
        }
        try {
            this.flush();
        }
        finally {
            this.closed = true;
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public synchronized void add(XMLEvent event) throws XMLStreamException {
        if (this.closed) {
            throw new XMLStreamException("Writer has been closed");
        }
        switch (event.getEventType()) {
            case 13: {
                this.cacheNamespace((Namespace)event);
                return;
            }
            case 10: {
                this.cacheAttribute((Attribute)event);
                return;
            }
        }
        this.sendCachedEvents();
        if (event.isStartElement()) {
            this.lastStart = event.asStartElement();
            return;
        }
        if (event.isEndElement()) {
            if (this.nsStack.isEmpty()) {
                throw new XMLStreamException("Mismatched end element event: " + event);
            }
            SimpleNamespaceContext nsCtx = this.peekNamespaceStack();
            EndElement endTag = event.asEndElement();
            QName endElemName = endTag.getName();
            String prefix = endElemName.getPrefix();
            String nsURI = endElemName.getNamespaceURI();
            if (nsURI != null && nsURI.length() > 0) {
                String boundURI = nsCtx.getNamespaceURI(prefix);
                if (!nsURI.equals(boundURI)) {
                    String newPrefix = nsCtx.getPrefix(nsURI);
                    if (newPrefix == null) throw new XMLStreamException("EndElement namespace (" + nsURI + ") isn't bound [" + endTag + "]");
                    QName newName = new QName(nsURI, endElemName.getLocalPart(), newPrefix);
                    event = this.factory.createEndElement(newName, endTag.getNamespaces());
                }
            } else {
                String defaultURI = nsCtx.getNamespaceURI("");
                if (defaultURI != null && defaultURI.length() > 0) {
                    throw new XMLStreamException("Unable to write " + event + " because default namespace is occluded by " + defaultURI);
                }
            }
            this.popNamespaceStack();
        }
        this.sendEvent(event);
    }

    public void add(XMLEventReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            this.add(reader.nextEvent());
        }
    }

    public synchronized String getPrefix(String nsURI) throws XMLStreamException {
        return this.getNamespaceContext().getPrefix(nsURI);
    }

    public synchronized void setPrefix(String prefix, String nsURI) throws XMLStreamException {
        this.peekNamespaceStack().setPrefix(prefix, nsURI);
    }

    public synchronized void setDefaultNamespace(String nsURI) throws XMLStreamException {
        this.peekNamespaceStack().setDefaultNamespace(nsURI);
    }

    public synchronized void setNamespaceContext(NamespaceContext root) throws XMLStreamException {
        SimpleNamespaceContext parent = (SimpleNamespaceContext)this.nsStack.get(0);
        parent.setParent(root);
    }

    public synchronized NamespaceContext getNamespaceContext() {
        return this.peekNamespaceStack();
    }

    protected SimpleNamespaceContext popNamespaceStack() {
        return (SimpleNamespaceContext)this.nsStack.remove(this.nsStack.size() - 1);
    }

    protected SimpleNamespaceContext peekNamespaceStack() {
        return (SimpleNamespaceContext)this.nsStack.get(this.nsStack.size() - 1);
    }

    protected SimpleNamespaceContext pushNamespaceStack() {
        SimpleNamespaceContext parent = this.peekNamespaceStack();
        SimpleNamespaceContext nsCtx = parent != null ? new SimpleNamespaceContext(parent) : new SimpleNamespaceContext();
        this.nsStack.add(nsCtx);
        return nsCtx;
    }

    protected void cacheAttribute(Attribute attr) {
        this.attrBuff.put(attr.getName(), attr);
    }

    protected void cacheNamespace(Namespace ns) {
        this.nsBuff.put(ns.getPrefix(), ns);
    }

    protected abstract void sendEvent(XMLEvent var1) throws XMLStreamException;
}

