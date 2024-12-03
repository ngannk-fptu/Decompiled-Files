/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison.badgerfish;

import java.io.IOException;
import java.io.Writer;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import org.codehaus.jettison.AbstractXMLStreamWriter;
import org.codehaus.jettison.Node;
import org.codehaus.jettison.XsonNamespaceContext;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.util.FastStack;

public class BadgerFishXMLStreamWriter
extends AbstractXMLStreamWriter {
    private JSONObject root;
    private JSONObject currentNode;
    private Writer writer;
    private FastStack nodes;
    private String currentKey;
    private NamespaceContext ctx;

    public BadgerFishXMLStreamWriter(Writer writer) {
        this(writer, new JSONObject());
    }

    public BadgerFishXMLStreamWriter(Writer writer, JSONObject currentNode) {
        this(writer, new JSONObject(), new FastStack());
    }

    public BadgerFishXMLStreamWriter(Writer writer, JSONObject currentNode, FastStack nodes) {
        this.currentNode = currentNode;
        this.root = currentNode;
        this.writer = writer;
        this.nodes = nodes;
        this.ctx = new XsonNamespaceContext(nodes);
    }

    @Override
    public void close() throws XMLStreamException {
    }

    @Override
    public void flush() throws XMLStreamException {
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return this.ctx;
    }

    @Override
    public String getPrefix(String ns) throws XMLStreamException {
        return this.getNamespaceContext().getPrefix(ns);
    }

    @Override
    public Object getProperty(String arg0) throws IllegalArgumentException {
        return null;
    }

    @Override
    public void setDefaultNamespace(String arg0) throws XMLStreamException {
    }

    @Override
    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        this.ctx = context;
    }

    @Override
    public void setPrefix(String arg0, String arg1) throws XMLStreamException {
    }

    @Override
    public void writeAttribute(String p, String ns, String local, String value) throws XMLStreamException {
        String key = this.createAttributeKey(p, ns, local);
        try {
            this.getCurrentNode().put(key, value);
        }
        catch (JSONException e) {
            throw new XMLStreamException(e);
        }
    }

    private String createAttributeKey(String p, String ns, String local) {
        return "@" + this.createKey(p, ns, local);
    }

    private String createKey(String p, String ns, String local) {
        if (p == null || p.equals("")) {
            return local;
        }
        return p + ":" + local;
    }

    @Override
    public void writeAttribute(String ns, String local, String value) throws XMLStreamException {
        this.writeAttribute(null, ns, local, value);
    }

    @Override
    public void writeAttribute(String local, String value) throws XMLStreamException {
        this.writeAttribute(null, local, value);
    }

    @Override
    public void writeCharacters(String text) throws XMLStreamException {
        if ((text = text.trim()).length() == 0) {
            return;
        }
        try {
            Object o = this.getCurrentNode().opt("$");
            if (o instanceof JSONArray) {
                ((JSONArray)o).put(text);
            } else if (o instanceof String) {
                JSONArray arr = new JSONArray();
                arr.put(o);
                arr.put(text);
                this.getCurrentNode().put("$", arr);
            } else {
                this.getCurrentNode().put("$", text);
            }
        }
        catch (JSONException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public void writeDefaultNamespace(String ns) throws XMLStreamException {
        this.writeNamespace("", ns);
    }

    @Override
    public void writeEndElement() throws XMLStreamException {
        if (this.getNodes().size() > 1) {
            this.getNodes().pop();
            this.currentNode = ((Node)this.getNodes().peek()).getObject();
        }
    }

    @Override
    public void writeEntityRef(String arg0) throws XMLStreamException {
    }

    @Override
    public void writeNamespace(String prefix, String ns) throws XMLStreamException {
        ((Node)this.getNodes().peek()).setNamespace(prefix, ns);
        try {
            JSONObject nsObj = this.getCurrentNode().optJSONObject("@xmlns");
            if (nsObj == null) {
                nsObj = new JSONObject();
                this.getCurrentNode().put("@xmlns", nsObj);
            }
            if (prefix.equals("")) {
                prefix = "$";
            }
            nsObj.put(prefix, ns);
        }
        catch (JSONException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public void writeProcessingInstruction(String arg0, String arg1) throws XMLStreamException {
    }

    @Override
    public void writeProcessingInstruction(String arg0) throws XMLStreamException {
    }

    @Override
    public void writeStartDocument() throws XMLStreamException {
    }

    @Override
    public void writeEndDocument() throws XMLStreamException {
        try {
            this.root.write(this.writer);
            this.writer.flush();
        }
        catch (JSONException e) {
            throw new XMLStreamException(e);
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public void writeStartElement(String prefix, String local, String ns) throws XMLStreamException {
        try {
            this.currentKey = this.createKey(prefix, ns, local);
            Object existing = this.getCurrentNode().opt(this.currentKey);
            if (existing instanceof JSONObject) {
                JSONArray array = new JSONArray();
                array.put(existing);
                JSONObject newCurrent = new JSONObject();
                array.put(newCurrent);
                this.getCurrentNode().put(this.currentKey, array);
                this.currentNode = newCurrent;
                Node node = new Node(this.currentNode);
                this.getNodes().push(node);
            } else {
                JSONObject newCurrent = new JSONObject();
                if (existing instanceof JSONArray) {
                    ((JSONArray)existing).put(newCurrent);
                } else {
                    this.getCurrentNode().put(this.currentKey, newCurrent);
                }
                this.currentNode = newCurrent;
                Node node = new Node(this.currentNode);
                this.getNodes().push(node);
            }
        }
        catch (JSONException e) {
            throw new XMLStreamException("Could not write start element!", e);
        }
    }

    protected JSONObject getCurrentNode() {
        return this.currentNode;
    }

    protected FastStack getNodes() {
        return this.nodes;
    }
}

