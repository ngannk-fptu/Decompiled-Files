/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.runtime.log.Log
 *  org.dom4j.Attribute
 *  org.dom4j.Document
 *  org.dom4j.DocumentHelper
 *  org.dom4j.Element
 *  org.dom4j.Node
 *  org.dom4j.io.SAXReader
 */
package org.apache.velocity.tools.generic;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.velocity.runtime.log.Log;
import org.apache.velocity.tools.ConversionUtils;
import org.apache.velocity.tools.config.DefaultKey;
import org.apache.velocity.tools.generic.SafeConfig;
import org.apache.velocity.tools.generic.ValueParser;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

@DefaultKey(value="xml")
public class XmlTool
extends SafeConfig {
    public static final String FILE_KEY = "file";
    protected Log LOG;
    private List<Node> nodes;

    public XmlTool() {
    }

    public XmlTool(Node node) {
        this(Collections.singletonList(node));
    }

    public XmlTool(List<Node> nodes) {
        this.nodes = nodes;
    }

    @Override
    protected void configure(ValueParser parser) {
        this.LOG = (Log)parser.getValue("log");
        String file = parser.getString(FILE_KEY);
        if (file != null) {
            try {
                this.read(file);
            }
            catch (IllegalArgumentException iae) {
                throw iae;
            }
            catch (Exception e) {
                throw new RuntimeException("Could not read XML file at: " + file, e);
            }
        }
    }

    protected void setRoot(Node node) {
        if (node instanceof Document) {
            node = ((Document)node).getRootElement();
        }
        this.nodes = new ArrayList<Node>(1);
        this.nodes.add(node);
    }

    private void log(Object o, Throwable t) {
        if (this.LOG != null) {
            this.LOG.debug((Object)("XmlTool - " + o), t);
        }
    }

    protected void read(String file) throws Exception {
        URL url = ConversionUtils.toURL(file, this);
        if (url == null) {
            throw new IllegalArgumentException("Could not find file, classpath resource or standard URL for '" + file + "'.");
        }
        this.read(url);
    }

    protected void read(URL url) throws Exception {
        SAXReader reader = new SAXReader();
        this.setRoot((Node)reader.read(url));
    }

    protected void parse(String xml) throws Exception {
        this.setRoot((Node)DocumentHelper.parseText((String)xml));
    }

    public XmlTool read(Object o) {
        if (this.isSafeMode() || o == null) {
            return null;
        }
        try {
            XmlTool xml = new XmlTool();
            if (o instanceof URL) {
                xml.read((URL)o);
            } else {
                String file = String.valueOf(o);
                xml.read(file);
            }
            return xml;
        }
        catch (Exception e) {
            this.log("Failed to read XML from : " + o, e);
            return null;
        }
    }

    public XmlTool parse(Object o) {
        if (o == null) {
            return null;
        }
        String s = String.valueOf(o);
        try {
            XmlTool xml = new XmlTool();
            xml.parse(s);
            return xml;
        }
        catch (Exception e) {
            this.log("Failed to parse XML from : " + o, e);
            return null;
        }
    }

    public Object get(Object o) {
        if (this.isEmpty() || o == null) {
            return null;
        }
        String attr = this.attr(o);
        if (attr != null) {
            return attr;
        }
        Number i = ConversionUtils.toNumber(o);
        if (i != null) {
            return this.get(i);
        }
        String s = String.valueOf(o);
        if (s.length() == 0) {
            return null;
        }
        if (s.indexOf(47) < 0) {
            s = this.getPath() + '/' + s;
        }
        return this.find(s);
    }

    public Object getName() {
        Object name = this.get("name");
        if (name != null) {
            return name;
        }
        return this.getNodeName();
    }

    public String getNodeName() {
        if (this.isEmpty()) {
            return null;
        }
        return this.node().getName();
    }

    public String getPath() {
        if (this.isEmpty()) {
            return null;
        }
        return this.node().getPath();
    }

    public String attr(Object o) {
        if (o == null) {
            return null;
        }
        String key = String.valueOf(o);
        Node node = this.node();
        if (node instanceof Element) {
            return ((Element)node).attributeValue(key);
        }
        return null;
    }

    public Map<String, String> attributes() {
        Node node = this.node();
        if (node instanceof Element) {
            HashMap<String, String> attrs = new HashMap<String, String>();
            Iterator i = ((Element)node).attributeIterator();
            while (i.hasNext()) {
                Attribute a = (Attribute)i.next();
                attrs.put(a.getName(), a.getValue());
            }
            return attrs;
        }
        return null;
    }

    public boolean isEmpty() {
        return this.nodes == null || this.nodes.isEmpty();
    }

    public int size() {
        if (this.isEmpty()) {
            return 0;
        }
        return this.nodes.size();
    }

    public Iterator<XmlTool> iterator() {
        if (this.isEmpty()) {
            return null;
        }
        return new NodeIterator(this.nodes.iterator());
    }

    public XmlTool getFirst() {
        if (this.size() == 1) {
            return this;
        }
        return new XmlTool(this.node());
    }

    public XmlTool getLast() {
        if (this.size() == 1) {
            return this;
        }
        return new XmlTool(this.nodes.get(this.size() - 1));
    }

    public XmlTool get(Number n) {
        if (n == null) {
            return null;
        }
        int i = n.intValue();
        if (i < 0 || i > this.size() - 1) {
            return null;
        }
        return new XmlTool(this.nodes.get(i));
    }

    public Node node() {
        if (this.isEmpty()) {
            return null;
        }
        return this.nodes.get(0);
    }

    public XmlTool find(Object o) {
        if (o == null || this.isEmpty()) {
            return null;
        }
        return this.find(String.valueOf(o));
    }

    public XmlTool find(String xpath) {
        if (xpath == null || xpath.length() == 0) {
            return null;
        }
        if (xpath.indexOf(47) < 0) {
            xpath = "//" + xpath;
        }
        ArrayList<Node> found = new ArrayList<Node>();
        for (Node n : this.nodes) {
            found.addAll(n.selectNodes(xpath));
        }
        if (found.isEmpty()) {
            return null;
        }
        return new XmlTool(found);
    }

    public XmlTool getParent() {
        if (this.isEmpty()) {
            return null;
        }
        Element parent = this.node().getParent();
        if (parent == null) {
            return null;
        }
        return new XmlTool((Node)parent);
    }

    public XmlTool parents() {
        if (this.isEmpty()) {
            return null;
        }
        if (this.size() == 1) {
            return this.getParent();
        }
        ArrayList<Node> parents = new ArrayList<Node>(this.size());
        for (Node n : this.nodes) {
            Element parent = n.getParent();
            if (parent == null || parents.contains(parent)) continue;
            parents.add((Node)parent);
        }
        if (parents.isEmpty()) {
            return null;
        }
        return new XmlTool(parents);
    }

    public XmlTool children() {
        if (this.isEmpty()) {
            return null;
        }
        ArrayList<Node> kids = new ArrayList<Node>();
        for (Node n : this.nodes) {
            if (!(n instanceof Element)) continue;
            kids.addAll(((Element)n).elements());
        }
        return new XmlTool(kids);
    }

    public String getText() {
        if (this.isEmpty()) {
            return null;
        }
        StringBuilder out = new StringBuilder();
        for (Node n : this.nodes) {
            String text = n.getText();
            if (text == null) continue;
            out.append(text);
        }
        String result = out.toString().trim();
        if (result.length() > 0) {
            return result;
        }
        return null;
    }

    public String toString() {
        if (this.isEmpty()) {
            return super.toString();
        }
        StringBuilder out = new StringBuilder();
        for (Node n : this.nodes) {
            if (n instanceof Attribute) {
                out.append(n.getText().trim());
                continue;
            }
            out.append(n.asXML());
        }
        return out.toString();
    }

    public static class NodeIterator
    implements Iterator<XmlTool> {
        private Iterator<Node> i;

        public NodeIterator(Iterator<Node> i) {
            this.i = i;
        }

        @Override
        public boolean hasNext() {
            return this.i.hasNext();
        }

        @Override
        public XmlTool next() {
            return new XmlTool(this.i.next());
        }

        @Override
        public void remove() {
            this.i.remove();
        }
    }
}

