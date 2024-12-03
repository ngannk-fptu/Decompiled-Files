/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.codehaus.jettison.Convention;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class Node {
    JSONObject object;
    Map attributes;
    Map namespaces;
    Iterator keys;
    QName name;
    JSONArray array;
    int arrayIndex;
    String currentKey;
    Node parent;

    public Node(Node parent, String name, JSONObject object, Convention con) throws JSONException, XMLStreamException {
        this.parent = parent;
        this.object = object;
        this.namespaces = new LinkedHashMap();
        this.attributes = new LinkedHashMap();
        con.processAttributesAndNamespaces(this, object);
        this.keys = object.keys();
        this.name = con.createQName(name, this);
    }

    public Node(String name, Convention con) throws XMLStreamException {
        this.name = con.createQName(name, this);
        this.namespaces = new HashMap();
        this.attributes = new HashMap();
    }

    public Node(JSONObject object) {
        this.object = object;
        this.namespaces = new HashMap();
        this.attributes = new HashMap();
    }

    public int getNamespaceCount() {
        return this.namespaces.size();
    }

    public String getNamespaceURI(String prefix) {
        String result = (String)this.namespaces.get(prefix);
        if (result == null && this.parent != null) {
            result = this.parent.getNamespaceURI(prefix);
        }
        return result;
    }

    public String getNamespaceURI(int index) {
        if (index < 0 || index >= this.getNamespaceCount()) {
            throw new IllegalArgumentException("Illegal index: element has " + this.getNamespaceCount() + " namespace declarations");
        }
        Iterator itr = this.namespaces.values().iterator();
        while (--index >= 0) {
            itr.next();
        }
        Object ns = itr.next();
        return ns == null ? "" : ns.toString();
    }

    public String getNamespacePrefix(String URI2) {
        String result = null;
        for (Map.Entry e : this.namespaces.entrySet()) {
            if (!e.getValue().equals(URI2)) continue;
            result = (String)e.getKey();
        }
        if (result == null && this.parent != null) {
            result = this.parent.getNamespacePrefix(URI2);
        }
        return result;
    }

    public String getNamespacePrefix(int index) {
        if (index < 0 || index >= this.getNamespaceCount()) {
            throw new IllegalArgumentException("Illegal index: element has " + this.getNamespaceCount() + " namespace declarations");
        }
        Iterator itr = this.namespaces.keySet().iterator();
        while (--index >= 0) {
            itr.next();
        }
        return itr.next().toString();
    }

    public void setNamespaces(Map namespaces) {
        this.namespaces = namespaces;
    }

    public void setNamespace(String prefix, String uri) {
        this.namespaces.put(prefix, uri);
    }

    public Map getAttributes() {
        return this.attributes;
    }

    public void setAttribute(QName name, String value) {
        this.attributes.put(name, value);
    }

    public Iterator getKeys() {
        return this.keys;
    }

    public QName getName() {
        return this.name;
    }

    public JSONObject getObject() {
        return this.object;
    }

    public void setObject(JSONObject object) {
        this.object = object;
    }

    public JSONArray getArray() {
        return this.array;
    }

    public void setArray(JSONArray array) {
        this.array = array;
    }

    public int getArrayIndex() {
        return this.arrayIndex;
    }

    public void setArrayIndex(int arrayIndex) {
        this.arrayIndex = arrayIndex;
    }

    public String getCurrentKey() {
        return this.currentKey;
    }

    public void setCurrentKey(String currentKey) {
        this.currentKey = currentKey;
    }

    public String toString() {
        if (this.name != null) {
            return this.name.toString();
        }
        return super.toString();
    }
}

