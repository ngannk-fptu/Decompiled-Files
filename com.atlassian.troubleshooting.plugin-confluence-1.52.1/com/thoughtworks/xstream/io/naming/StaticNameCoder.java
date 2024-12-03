/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.io.naming;

import com.thoughtworks.xstream.io.naming.NameCoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class StaticNameCoder
implements NameCoder {
    private final Map java2Node;
    private final Map java2Attribute;
    private transient Map node2Java;
    private transient Map attribute2Java;

    public StaticNameCoder(Map java2Node, Map java2Attribute) {
        this.java2Node = new HashMap(java2Node);
        this.java2Attribute = java2Node == java2Attribute || java2Attribute == null ? this.java2Node : new HashMap(java2Attribute);
        this.readResolve();
    }

    public String decodeAttribute(String attributeName) {
        String name = (String)this.attribute2Java.get(attributeName);
        return name == null ? attributeName : name;
    }

    public String decodeNode(String nodeName) {
        String name = (String)this.node2Java.get(nodeName);
        return name == null ? nodeName : name;
    }

    public String encodeAttribute(String name) {
        String friendlyName = (String)this.java2Attribute.get(name);
        return friendlyName == null ? name : friendlyName;
    }

    public String encodeNode(String name) {
        String friendlyName = (String)this.java2Node.get(name);
        return friendlyName == null ? name : friendlyName;
    }

    private Object readResolve() {
        this.node2Java = this.invertMap(this.java2Node);
        this.attribute2Java = this.java2Node == this.java2Attribute ? this.node2Java : this.invertMap(this.java2Attribute);
        return this;
    }

    private Map invertMap(Map map) {
        HashMap<String, String> inverseMap = new HashMap<String, String>(map.size());
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = iter.next();
            inverseMap.put((String)entry.getValue(), (String)entry.getKey());
        }
        return inverseMap;
    }
}

