/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl.stax;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecNamespace;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.stax.XMLSecEventBaseImpl;
import org.apache.xml.security.stax.impl.stax.XMLSecNamespaceImpl;

public class XMLSecStartElementImpl
extends XMLSecEventBaseImpl
implements XMLSecStartElement {
    private final QName elementName;
    private XMLSecNamespace elementNamespace;
    private List<XMLSecAttribute> attributes = Collections.emptyList();
    private List<XMLSecNamespace> namespaces = Collections.emptyList();

    public XMLSecStartElementImpl(QName elementName, List<XMLSecAttribute> attributes, List<XMLSecNamespace> namespaces, XMLSecStartElement parentXmlSecStartElement) {
        this.elementName = elementName;
        this.setParentXMLSecStartElement(parentXmlSecStartElement);
        if (attributes != null) {
            this.attributes = attributes;
        }
        if (namespaces != null) {
            this.namespaces = namespaces;
        }
    }

    public XMLSecStartElementImpl(QName elementName, Collection<XMLSecAttribute> attributes, Collection<XMLSecNamespace> namespaces) {
        this.elementName = elementName;
        if (attributes != null && !attributes.isEmpty()) {
            this.attributes = new ArrayList<XMLSecAttribute>(attributes);
        }
        if (namespaces != null && !namespaces.isEmpty()) {
            this.namespaces = new ArrayList<XMLSecNamespace>(namespaces);
        }
    }

    @Override
    public QName getName() {
        return this.elementName;
    }

    @Override
    public XMLSecNamespace getElementNamespace() {
        if (this.elementNamespace == null) {
            this.elementNamespace = XMLSecNamespaceImpl.getInstance(this.elementName.getPrefix(), this.elementName.getNamespaceURI());
        }
        return this.elementNamespace;
    }

    @Override
    public Iterator<Attribute> getAttributes() {
        if (this.attributes.isEmpty()) {
            return XMLSecStartElementImpl.getEmptyIterator();
        }
        return this.attributes.iterator();
    }

    @Override
    public void getAttributesFromCurrentScope(List<XMLSecAttribute> comparableAttributeList) {
        comparableAttributeList.addAll(this.attributes);
        if (this.parentXMLSecStartELement != null) {
            this.parentXMLSecStartELement.getAttributesFromCurrentScope(comparableAttributeList);
        }
    }

    @Override
    public List<XMLSecAttribute> getOnElementDeclaredAttributes() {
        return this.attributes;
    }

    @Override
    public void addAttribute(XMLSecAttribute xmlSecAttribute) {
        if (this.attributes == Collections.emptyList()) {
            this.attributes = new ArrayList<XMLSecAttribute>(1);
        }
        this.attributes.add(xmlSecAttribute);
    }

    @Override
    public int getDocumentLevel() {
        return super.getDocumentLevel() + 1;
    }

    @Override
    public void getElementPath(List<QName> list) {
        super.getElementPath(list);
        list.add(this.getName());
    }

    @Override
    public XMLSecStartElement getStartElementAtLevel(int level) {
        int thisLevel = this.getDocumentLevel();
        if (thisLevel < level) {
            return null;
        }
        if (thisLevel == level) {
            return this;
        }
        return this.parentXMLSecStartELement.getStartElementAtLevel(level);
    }

    @Override
    public Iterator<Namespace> getNamespaces() {
        if (this.namespaces.isEmpty()) {
            return XMLSecStartElementImpl.getEmptyIterator();
        }
        return this.namespaces.iterator();
    }

    @Override
    public void getNamespacesFromCurrentScope(List<XMLSecNamespace> comparableNamespaceList) {
        if (this.parentXMLSecStartELement != null) {
            this.parentXMLSecStartELement.getNamespacesFromCurrentScope(comparableNamespaceList);
        }
        comparableNamespaceList.addAll(this.namespaces);
    }

    @Override
    public List<XMLSecNamespace> getOnElementDeclaredNamespaces() {
        return this.namespaces;
    }

    @Override
    public void addNamespace(XMLSecNamespace xmlSecNamespace) {
        if (this.namespaces == Collections.emptyList()) {
            this.namespaces = new ArrayList<XMLSecNamespace>(1);
        }
        this.namespaces.add(xmlSecNamespace);
    }

    @Override
    public Attribute getAttributeByName(QName name) {
        for (int i = 0; i < this.attributes.size(); ++i) {
            Attribute comparableAttribute = this.attributes.get(i);
            if (!name.equals(comparableAttribute.getName())) continue;
            return comparableAttribute;
        }
        return null;
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return new NamespaceContext(){

            @Override
            public String getNamespaceURI(String prefix) {
                for (int i = 0; i < XMLSecStartElementImpl.this.namespaces.size(); ++i) {
                    Namespace comparableNamespace = (Namespace)XMLSecStartElementImpl.this.namespaces.get(i);
                    if (!prefix.equals(comparableNamespace.getPrefix())) continue;
                    return comparableNamespace.getNamespaceURI();
                }
                if (XMLSecStartElementImpl.this.parentXMLSecStartELement != null) {
                    return XMLSecStartElementImpl.this.parentXMLSecStartELement.getNamespaceURI(prefix);
                }
                return null;
            }

            @Override
            public String getPrefix(String namespaceURI) {
                for (int i = 0; i < XMLSecStartElementImpl.this.namespaces.size(); ++i) {
                    Namespace comparableNamespace = (Namespace)XMLSecStartElementImpl.this.namespaces.get(i);
                    if (!namespaceURI.equals(comparableNamespace.getNamespaceURI())) continue;
                    return comparableNamespace.getPrefix();
                }
                if (XMLSecStartElementImpl.this.parentXMLSecStartELement != null) {
                    return XMLSecStartElementImpl.this.parentXMLSecStartELement.getNamespaceContext().getPrefix(namespaceURI);
                }
                return null;
            }

            public Iterator getPrefixes(String namespaceURI) {
                HashSet<String> prefixes = new HashSet<String>();
                ArrayList<XMLSecNamespace> xmlSecNamespaces = new ArrayList<XMLSecNamespace>();
                XMLSecStartElementImpl.this.getNamespacesFromCurrentScope(xmlSecNamespaces);
                for (int i = 0; i < xmlSecNamespaces.size(); ++i) {
                    Namespace xmlSecNamespace = (Namespace)xmlSecNamespaces.get(i);
                    if (!namespaceURI.equals(xmlSecNamespace.getNamespaceURI())) continue;
                    prefixes.add(xmlSecNamespace.getPrefix());
                }
                return prefixes.iterator();
            }
        };
    }

    @Override
    public String getNamespaceURI(String prefix) {
        for (int i = 0; i < this.namespaces.size(); ++i) {
            Namespace comparableNamespace = this.namespaces.get(i);
            if (!prefix.equals(comparableNamespace.getPrefix())) continue;
            return comparableNamespace.getNamespaceURI();
        }
        if (this.parentXMLSecStartELement != null) {
            return this.parentXMLSecStartELement.getNamespaceURI(prefix);
        }
        return null;
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
    public XMLSecStartElement asStartElement() {
        return this;
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write(60);
            String prefix = this.getName().getPrefix();
            if (prefix != null && !prefix.isEmpty()) {
                writer.write(prefix);
                writer.write(58);
            }
            writer.write(this.getName().getLocalPart());
            for (Namespace namespace : this.namespaces) {
                writer.write(" xmlns");
                String nsPrefix = namespace.getPrefix();
                if (nsPrefix != null && !nsPrefix.isEmpty()) {
                    writer.write(58);
                    writer.write(nsPrefix);
                }
                writer.write("=\"");
                writer.write(namespace.getValue());
                writer.write(34);
            }
            for (Attribute attribute : this.attributes) {
                writer.write(32);
                String attrPrefix = attribute.getName().getPrefix();
                if (attrPrefix != null && !attrPrefix.isEmpty()) {
                    writer.write(attrPrefix);
                    writer.write(58);
                }
                writer.write(attribute.getName().getLocalPart());
                writer.write("=\"");
                writer.write(attribute.getValue());
                writer.write(34);
            }
            writer.write(62);
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }
}

