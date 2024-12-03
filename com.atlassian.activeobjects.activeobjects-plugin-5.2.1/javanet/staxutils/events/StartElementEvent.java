/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javanet.staxutils.NamespaceContextAdapter;
import javanet.staxutils.StaticNamespaceContext;
import javanet.staxutils.events.AbstractXMLEvent;
import javanet.staxutils.events.AttributeEvent;
import javanet.staxutils.events.NamespaceEvent;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;

public class StartElementEvent
extends AbstractXMLEvent
implements StartElement {
    protected QName name;
    protected Map attributes;
    protected Map namespaces;
    protected NamespaceContext namespaceCtx;

    public StartElementEvent(QName name, NamespaceContext namespaceCtx, Location location) {
        super(location);
        this.name = name;
        this.namespaceCtx = new StartElementContext(namespaceCtx);
    }

    public StartElementEvent(QName name, Iterator attributes, Iterator namespaces, NamespaceContext namespaceCtx, Location location, QName schemaType) {
        super(location, schemaType);
        this.namespaceCtx = new StartElementContext(namespaceCtx);
        this.mergeNamespaces(namespaces);
        this.mergeAttributes(attributes);
        QName newName = this.processQName(name);
        this.name = newName == null ? name : newName;
    }

    public StartElementEvent(StartElement that) {
        this(that.getName(), that.getAttributes(), that.getNamespaces(), that.getNamespaceContext(), that.getLocation(), that.getSchemaType());
    }

    public int getEventType() {
        return 1;
    }

    public QName getName() {
        return this.name;
    }

    public Attribute getAttributeByName(QName name) {
        if (this.attributes != null) {
            return (Attribute)this.attributes.get(name);
        }
        return null;
    }

    public Iterator getAttributes() {
        if (this.attributes != null) {
            return this.attributes.values().iterator();
        }
        return Collections.EMPTY_LIST.iterator();
    }

    public NamespaceContext getNamespaceContext() {
        return this.namespaceCtx;
    }

    public Iterator getNamespaces() {
        if (this.namespaces != null) {
            return this.namespaces.values().iterator();
        }
        return Collections.EMPTY_LIST.iterator();
    }

    public String getNamespaceURI(String prefix) {
        return this.getNamespaceContext().getNamespaceURI(prefix);
    }

    private void mergeAttributes(Iterator iter) {
        if (iter == null) {
            return;
        }
        while (iter.hasNext()) {
            QName attrName;
            QName newName;
            Attribute attr = (Attribute)iter.next();
            if (this.attributes == null) {
                this.attributes = new HashMap();
            }
            if ((newName = this.processQName(attrName = attr.getName())) != null) {
                AttributeEvent newAttr = new AttributeEvent(newName, null, attr);
                this.attributes.put(newName, newAttr);
                continue;
            }
            this.attributes.put(attrName, attr);
        }
    }

    private void mergeNamespaces(Iterator iter) {
        if (iter == null) {
            return;
        }
        while (iter.hasNext()) {
            Namespace ns = (Namespace)iter.next();
            String prefix = ns.getPrefix();
            if (this.namespaces == null) {
                this.namespaces = new HashMap();
            }
            if (this.namespaces.containsKey(prefix)) continue;
            this.namespaces.put(prefix, ns);
        }
    }

    private QName processQName(QName name) {
        String nsURI = name.getNamespaceURI();
        String prefix = name.getPrefix();
        if (nsURI == null || nsURI.length() == 0) {
            if (prefix != null && prefix.length() > 0) {
                return new QName(name.getLocalPart());
            }
            return name;
        }
        String resolvedNS = this.namespaceCtx.getNamespaceURI(prefix);
        if (resolvedNS == null) {
            if (prefix != null && prefix.length() > 0) {
                if (this.namespaces == null) {
                    this.namespaces = new HashMap();
                }
                this.namespaces.put(prefix, new NamespaceEvent(prefix, nsURI));
            }
            return null;
        }
        if (!resolvedNS.equals(nsURI)) {
            String newPrefix = this.namespaceCtx.getPrefix(nsURI);
            if (newPrefix == null) {
                newPrefix = this.generatePrefix(nsURI);
            }
            return new QName(nsURI, name.getLocalPart(), newPrefix);
        }
        return null;
    }

    private String generatePrefix(String nsURI) {
        String newPrefix;
        int nsCount = 0;
        do {
            newPrefix = "ns" + nsCount;
            ++nsCount;
        } while (this.namespaceCtx.getNamespaceURI(newPrefix) != null);
        if (this.namespaces == null) {
            this.namespaces = new HashMap();
        }
        this.namespaces.put(newPrefix, new NamespaceEvent(newPrefix, nsURI));
        return newPrefix;
    }

    private final class StartElementContext
    extends NamespaceContextAdapter
    implements StaticNamespaceContext {
        public StartElementContext(NamespaceContext namespaceCtx) {
            super(namespaceCtx);
        }

        public String getNamespaceURI(String prefix) {
            if (StartElementEvent.this.namespaces != null && StartElementEvent.this.namespaces.containsKey(prefix)) {
                Namespace namespace = (Namespace)StartElementEvent.this.namespaces.get(prefix);
                return namespace.getNamespaceURI();
            }
            return super.getNamespaceURI(prefix);
        }

        public String getPrefix(String nsURI) {
            Iterator i = StartElementEvent.this.getNamespaces();
            while (i.hasNext()) {
                Namespace ns = (Namespace)i.next();
                if (!ns.getNamespaceURI().equals(nsURI)) continue;
                return ns.getPrefix();
            }
            return super.getPrefix(nsURI);
        }

        public Iterator getPrefixes(String nsURI) {
            ArrayList<String> prefixes = null;
            if (StartElementEvent.this.namespaces != null) {
                Iterator i = StartElementEvent.this.namespaces.values().iterator();
                while (i.hasNext()) {
                    Namespace ns = (Namespace)i.next();
                    if (!ns.getNamespaceURI().equals(nsURI)) continue;
                    if (prefixes == null) {
                        prefixes = new ArrayList<String>();
                    }
                    String prefix = ns.getPrefix();
                    prefixes.add(prefix);
                }
            }
            Iterator parentPrefixes = super.getPrefixes(nsURI);
            while (parentPrefixes.hasNext()) {
                String prefix = (String)parentPrefixes.next();
                if (StartElementEvent.this.namespaces == null || StartElementEvent.this.namespaces.containsKey(prefix)) continue;
                if (prefixes == null) {
                    prefixes = new ArrayList();
                }
                prefixes.add(prefix);
            }
            return prefixes == null ? Collections.EMPTY_LIST.iterator() : prefixes.iterator();
        }
    }
}

