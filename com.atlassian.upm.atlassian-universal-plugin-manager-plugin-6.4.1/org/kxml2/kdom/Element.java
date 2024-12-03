/*
 * Decompiled with CFR 0.152.
 */
package org.kxml2.kdom;

import java.io.IOException;
import java.util.Vector;
import org.kxml2.kdom.Node;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class Element
extends Node {
    protected String namespace;
    protected String name;
    protected Vector attributes;
    protected Node parent;
    protected Vector prefixes;

    public void init() {
    }

    public void clear() {
        this.attributes = null;
        this.children = null;
    }

    public Element createElement(String string, String string2) {
        return this.parent == null ? super.createElement(string, string2) : this.parent.createElement(string, string2);
    }

    public int getAttributeCount() {
        return this.attributes == null ? 0 : this.attributes.size();
    }

    public String getAttributeNamespace(int n) {
        return ((String[])this.attributes.elementAt(n))[0];
    }

    public String getAttributeName(int n) {
        return ((String[])this.attributes.elementAt(n))[1];
    }

    public String getAttributeValue(int n) {
        return ((String[])this.attributes.elementAt(n))[2];
    }

    public String getAttributeValue(String string, String string2) {
        for (int i = 0; i < this.getAttributeCount(); ++i) {
            if (!string2.equals(this.getAttributeName(i)) || string != null && !string.equals(this.getAttributeNamespace(i))) continue;
            return this.getAttributeValue(i);
        }
        return null;
    }

    public Node getRoot() {
        Element element = this;
        while (element.parent != null) {
            if (!(element.parent instanceof Element)) {
                return element.parent;
            }
            element = (Element)element.parent;
        }
        return element;
    }

    public String getName() {
        return this.name;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public String getNamespaceUri(String string) {
        int n = this.getNamespaceCount();
        for (int i = 0; i < n; ++i) {
            if (string != this.getNamespacePrefix(i) && (string == null || !string.equals(this.getNamespacePrefix(i)))) continue;
            return this.getNamespaceUri(i);
        }
        return this.parent instanceof Element ? ((Element)this.parent).getNamespaceUri(string) : null;
    }

    public int getNamespaceCount() {
        return this.prefixes == null ? 0 : this.prefixes.size();
    }

    public String getNamespacePrefix(int n) {
        return ((String[])this.prefixes.elementAt(n))[0];
    }

    public String getNamespaceUri(int n) {
        return ((String[])this.prefixes.elementAt(n))[1];
    }

    public Node getParent() {
        return this.parent;
    }

    public void parse(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        int n;
        for (n = xmlPullParser.getNamespaceCount(xmlPullParser.getDepth() - 1); n < xmlPullParser.getNamespaceCount(xmlPullParser.getDepth()); ++n) {
            this.setPrefix(xmlPullParser.getNamespacePrefix(n), xmlPullParser.getNamespaceUri(n));
        }
        for (n = 0; n < xmlPullParser.getAttributeCount(); ++n) {
            this.setAttribute(xmlPullParser.getAttributeNamespace(n), xmlPullParser.getAttributeName(n), xmlPullParser.getAttributeValue(n));
        }
        this.init();
        if (xmlPullParser.isEmptyElementTag()) {
            xmlPullParser.nextToken();
        } else {
            xmlPullParser.nextToken();
            super.parse(xmlPullParser);
            if (this.getChildCount() == 0) {
                this.addChild(7, "");
            }
        }
        xmlPullParser.require(3, this.getNamespace(), this.getName());
        xmlPullParser.nextToken();
    }

    public void setAttribute(String string, String string2, String string3) {
        if (this.attributes == null) {
            this.attributes = new Vector();
        }
        if (string == null) {
            string = "";
        }
        for (int i = this.attributes.size() - 1; i >= 0; --i) {
            String[] stringArray = (String[])this.attributes.elementAt(i);
            if (!stringArray[0].equals(string) || !stringArray[1].equals(string2)) continue;
            if (string3 == null) {
                this.attributes.removeElementAt(i);
            } else {
                stringArray[2] = string3;
            }
            return;
        }
        this.attributes.addElement(new String[]{string, string2, string3});
    }

    public void setPrefix(String string, String string2) {
        if (this.prefixes == null) {
            this.prefixes = new Vector();
        }
        this.prefixes.addElement(new String[]{string, string2});
    }

    public void setName(String string) {
        this.name = string;
    }

    public void setNamespace(String string) {
        if (string == null) {
            throw new NullPointerException("Use \"\" for empty namespace");
        }
        this.namespace = string;
    }

    protected void setParent(Node node) {
        this.parent = node;
    }

    public void write(XmlSerializer xmlSerializer) throws IOException {
        int n;
        if (this.prefixes != null) {
            for (n = 0; n < this.prefixes.size(); ++n) {
                xmlSerializer.setPrefix(this.getNamespacePrefix(n), this.getNamespaceUri(n));
            }
        }
        xmlSerializer.startTag(this.getNamespace(), this.getName());
        n = this.getAttributeCount();
        for (int i = 0; i < n; ++i) {
            xmlSerializer.attribute(this.getAttributeNamespace(i), this.getAttributeName(i), this.getAttributeValue(i));
        }
        this.writeChildren(xmlSerializer);
        xmlSerializer.endTag(this.getNamespace(), this.getName());
    }
}

