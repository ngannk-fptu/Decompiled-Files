/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.io.xml.xppdom;

import com.thoughtworks.xstream.io.xml.xppdom.Xpp3Dom;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class XppDom
implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String value;
    private Map attributes;
    private List childList;
    private transient Map childMap;
    private XppDom parent;

    public XppDom(String name) {
        this.name = name;
        this.childList = new ArrayList();
        this.childMap = new HashMap();
    }

    public String getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String[] getAttributeNames() {
        if (null == this.attributes) {
            return new String[0];
        }
        return this.attributes.keySet().toArray(new String[0]);
    }

    public String getAttribute(String name) {
        return null != this.attributes ? (String)this.attributes.get(name) : null;
    }

    public void setAttribute(String name, String value) {
        if (null == this.attributes) {
            this.attributes = new HashMap();
        }
        this.attributes.put(name, value);
    }

    public XppDom getChild(int i) {
        return (XppDom)this.childList.get(i);
    }

    public XppDom getChild(String name) {
        return (XppDom)this.childMap.get(name);
    }

    public void addChild(XppDom xpp3Dom) {
        xpp3Dom.setParent(this);
        this.childList.add(xpp3Dom);
        this.childMap.put(xpp3Dom.getName(), xpp3Dom);
    }

    public XppDom[] getChildren() {
        if (null == this.childList) {
            return new XppDom[0];
        }
        return this.childList.toArray(new XppDom[0]);
    }

    public XppDom[] getChildren(String name) {
        if (null == this.childList) {
            return new XppDom[0];
        }
        ArrayList<XppDom> children = new ArrayList<XppDom>();
        int size = this.childList.size();
        for (int i = 0; i < size; ++i) {
            XppDom configuration = (XppDom)this.childList.get(i);
            if (!name.equals(configuration.getName())) continue;
            children.add(configuration);
        }
        return children.toArray(new XppDom[0]);
    }

    public int getChildCount() {
        if (null == this.childList) {
            return 0;
        }
        return this.childList.size();
    }

    public XppDom getParent() {
        return this.parent;
    }

    public void setParent(XppDom parent) {
        this.parent = parent;
    }

    Object readResolve() {
        this.childMap = new HashMap();
        Iterator iter = this.childList.iterator();
        while (iter.hasNext()) {
            XppDom element = (XppDom)iter.next();
            this.childMap.put(element.getName(), element);
        }
        return this;
    }

    public static XppDom build(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Xpp3Dom> elements = new ArrayList<Xpp3Dom>();
        ArrayList<StringBuffer> values = new ArrayList<StringBuffer>();
        XppDom node = null;
        int eventType = parser.getEventType();
        while (eventType != 1) {
            if (eventType == 2) {
                String rawName = parser.getName();
                Xpp3Dom child = new Xpp3Dom(rawName);
                int depth = elements.size();
                if (depth > 0) {
                    XppDom parent = (XppDom)elements.get(depth - 1);
                    parent.addChild(child);
                }
                elements.add(child);
                values.add(new StringBuffer());
                int attributesSize = parser.getAttributeCount();
                for (int i = 0; i < attributesSize; ++i) {
                    String name = parser.getAttributeName(i);
                    String value = parser.getAttributeValue(i);
                    child.setAttribute(name, value);
                }
            } else if (eventType == 4) {
                int depth = values.size() - 1;
                StringBuffer valueBuffer = (StringBuffer)values.get(depth);
                valueBuffer.append(parser.getText());
            } else if (eventType == 3) {
                int depth = elements.size() - 1;
                XppDom finalNode = (XppDom)elements.remove(depth);
                String accumulatedValue = values.remove(depth).toString();
                String finishedValue = 0 == accumulatedValue.length() ? null : accumulatedValue;
                finalNode.setValue(finishedValue);
                if (0 == depth) {
                    node = finalNode;
                }
            }
            eventType = parser.next();
        }
        return node;
    }
}

