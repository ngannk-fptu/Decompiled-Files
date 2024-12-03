/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.xml;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import org.apache.jackrabbit.commons.NamespaceHelper;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public abstract class Exporter {
    private final AttributesImpl attributes = new AttributesImpl();
    private final LinkedList stack = new LinkedList();
    private final Set shareables = new HashSet();
    private boolean share = false;
    private final Session session;
    protected final NamespaceHelper helper;
    private final ContentHandler handler;
    private final boolean recurse;
    private final boolean binary;

    protected Exporter(Session session, ContentHandler handler, boolean recurse, boolean binary) {
        this.session = session;
        this.helper = new NamespaceHelper(session);
        this.handler = handler;
        this.recurse = recurse;
        this.binary = binary;
        this.stack.add(new HashMap());
    }

    public void export(Node node) throws RepositoryException, SAXException {
        this.handler.startDocument();
        String[] prefixes = this.session.getNamespacePrefixes();
        for (int i = 0; i < prefixes.length; ++i) {
            if (prefixes[i].length() <= 0 || prefixes[i].equals("xml")) continue;
            this.addNamespace(prefixes[i], this.session.getNamespaceURI(prefixes[i]));
        }
        this.exportNode(node);
        this.handler.endDocument();
    }

    protected abstract void exportNode(String var1, String var2, Node var3) throws RepositoryException, SAXException;

    protected abstract void exportProperty(String var1, String var2, Value var3) throws RepositoryException, SAXException;

    protected abstract void exportProperty(String var1, String var2, int var3, Value[] var4) throws RepositoryException, SAXException;

    protected void exportNodes(Node node) throws RepositoryException, SAXException {
        if (this.recurse && !this.share) {
            NodeIterator iterator = node.getNodes();
            while (iterator.hasNext()) {
                Node child = iterator.nextNode();
                this.exportNode(child);
            }
        }
    }

    protected void exportProperties(Node node) throws RepositoryException, SAXException {
        if (this.share) {
            ValueFactory factory = this.session.getValueFactory();
            this.exportProperty("http://www.jcp.org/jcr/1.0", "primaryType", factory.createValue(this.helper.getJcrName("nt:share"), 7));
            this.exportProperty("http://www.jcp.org/jcr/1.0", "uuid", factory.createValue(node.getUUID()));
        } else {
            SortedMap properties = this.getProperties(node);
            this.exportProperty(properties, this.helper.getJcrName("jcr:primaryType"));
            this.exportProperty(properties, this.helper.getJcrName("jcr:mixinTypes"));
            this.exportProperty(properties, this.helper.getJcrName("jcr:uuid"));
            for (Map.Entry entry : properties.entrySet()) {
                String name = (String)entry.getKey();
                this.exportProperty(name, (Property)entry.getValue());
            }
        }
    }

    private void exportNode(Node node) throws RepositoryException, SAXException {
        boolean bl = this.share = node.isNodeType(this.helper.getJcrName("mix:shareable")) && !this.shareables.add(node.getUUID());
        if (node.getDepth() == 0) {
            this.exportNode("http://www.jcp.org/jcr/1.0", "root", node);
        } else {
            String name = node.getName();
            int colon = name.indexOf(58);
            if (colon == -1) {
                this.exportNode("", name, node);
            } else {
                String uri = this.session.getNamespaceURI(name.substring(0, colon));
                this.exportNode(uri, name.substring(colon + 1), node);
            }
        }
    }

    private SortedMap getProperties(Node node) throws RepositoryException {
        TreeMap<String, Property> properties = new TreeMap<String, Property>();
        PropertyIterator iterator = node.getProperties();
        while (iterator.hasNext()) {
            Property property = iterator.nextProperty();
            properties.put(property.getName(), property);
        }
        return properties;
    }

    private void exportProperty(Map properties, String name) throws RepositoryException, SAXException {
        Property property = (Property)properties.remove(name);
        if (property != null) {
            this.exportProperty(name, property);
        }
    }

    private void exportProperty(String name, Property property) throws RepositoryException, SAXException {
        int type;
        String uri = "";
        String local = name;
        int colon = name.indexOf(58);
        if (colon != -1) {
            uri = this.session.getNamespaceURI(name.substring(0, colon));
            local = name.substring(colon + 1);
        }
        if ((type = property.getType()) != 2 || this.binary) {
            if (property.isMultiple()) {
                this.exportProperty(uri, local, type, property.getValues());
            } else {
                this.exportProperty(uri, local, property.getValue());
            }
        } else {
            ValueFactory factory = this.session.getValueFactory();
            Value value = factory.createValue("", 2);
            if (property.isMultiple()) {
                this.exportProperty(uri, local, type, new Value[]{value});
            } else {
                this.exportProperty(uri, local, value);
            }
        }
    }

    protected void characters(char[] ch, int start, int length) throws SAXException {
        this.handler.characters(ch, start, length);
    }

    protected void addAttribute(String uri, String local, String value) throws RepositoryException {
        this.attributes.addAttribute(uri, local, this.getXMLName(uri, local), "CDATA", value);
    }

    protected void startElement(String uri, String local) throws SAXException, RepositoryException {
        String name = this.getXMLName(uri, local);
        Map namespaces = (Map)this.stack.getFirst();
        for (Map.Entry entry : namespaces.entrySet()) {
            String namespace = (String)entry.getKey();
            String prefix = (String)entry.getValue();
            this.handler.startPrefixMapping(prefix, namespace);
            this.attributes.addAttribute("http://www.w3.org/2000/xmlns/", prefix, "xmlns:" + prefix, "CDATA", namespace);
        }
        this.handler.startElement(uri, local, name, this.attributes);
        this.attributes.clear();
        this.stack.addFirst(new HashMap());
    }

    protected void endElement(String uri, String local) throws SAXException, RepositoryException {
        this.stack.removeFirst();
        this.handler.endElement(uri, local, this.getXMLName(uri, local));
        Map namespaces = (Map)this.stack.getFirst();
        Iterator iterator = namespaces.values().iterator();
        while (iterator.hasNext()) {
            this.handler.endPrefixMapping((String)iterator.next());
        }
        namespaces.clear();
    }

    protected String getXMLName(String uri, String local) throws RepositoryException {
        if (uri.length() == 0) {
            return local;
        }
        String prefix = this.getPrefix(uri);
        if (prefix == null) {
            prefix = this.getUniquePrefix(this.session.getNamespacePrefix(uri));
            ((Map)this.stack.getFirst()).put(uri, prefix);
        }
        return prefix + ":" + local;
    }

    protected String addNamespace(String hint, String uri) {
        String prefix = this.getPrefix(uri);
        if (prefix == null) {
            prefix = this.getUniquePrefix(hint);
            ((Map)this.stack.getFirst()).put(uri, prefix);
        }
        return prefix;
    }

    private String getPrefix(String uri) {
        Iterator iterator = this.stack.iterator();
        while (iterator.hasNext()) {
            String prefix = (String)((Map)iterator.next()).get(uri);
            if (prefix == null) continue;
            return prefix;
        }
        return null;
    }

    private String getUniquePrefix(String hint) {
        String prefix = hint;
        int i = 2;
        while (this.prefixExists(prefix)) {
            prefix = hint + i;
            ++i;
        }
        return prefix;
    }

    private boolean prefixExists(String prefix) {
        Iterator iterator = this.stack.iterator();
        while (iterator.hasNext()) {
            if (!((Map)iterator.next()).containsValue(prefix)) continue;
            return true;
        }
        return false;
    }
}

