/*
 * Decompiled with CFR 0.152.
 */
package org.kxml2.kdom;

import java.io.IOException;
import java.util.Vector;
import org.kxml2.kdom.Element;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class Node {
    public static final int DOCUMENT = 0;
    public static final int ELEMENT = 2;
    public static final int TEXT = 4;
    public static final int CDSECT = 5;
    public static final int ENTITY_REF = 6;
    public static final int IGNORABLE_WHITESPACE = 7;
    public static final int PROCESSING_INSTRUCTION = 8;
    public static final int COMMENT = 9;
    public static final int DOCDECL = 10;
    protected Vector children;
    protected StringBuffer types;

    public void addChild(int n, int n2, Object object) {
        if (object == null) {
            throw new NullPointerException();
        }
        if (this.children == null) {
            this.children = new Vector();
            this.types = new StringBuffer();
        }
        if (n2 == 2) {
            if (!(object instanceof Element)) {
                throw new RuntimeException("Element obj expected)");
            }
            ((Element)object).setParent(this);
        } else if (!(object instanceof String)) {
            throw new RuntimeException("String expected");
        }
        this.children.insertElementAt(object, n);
        this.types.insert(n, (char)n2);
    }

    public void addChild(int n, Object object) {
        this.addChild(this.getChildCount(), n, object);
    }

    public Element createElement(String string, String string2) {
        Element element = new Element();
        element.namespace = string == null ? "" : string;
        element.name = string2;
        return element;
    }

    public Object getChild(int n) {
        return this.children.elementAt(n);
    }

    public int getChildCount() {
        return this.children == null ? 0 : this.children.size();
    }

    public Element getElement(int n) {
        Object object = this.getChild(n);
        return object instanceof Element ? (Element)object : null;
    }

    public Element getElement(String string, String string2) {
        int n = this.indexOf(string, string2, 0);
        int n2 = this.indexOf(string, string2, n + 1);
        if (n == -1 || n2 != -1) {
            throw new RuntimeException("Element {" + string + "}" + string2 + (n == -1 ? " not found in " : " more than once in ") + this);
        }
        return this.getElement(n);
    }

    public String getText(int n) {
        return this.isText(n) ? (String)this.getChild(n) : null;
    }

    public int getType(int n) {
        return this.types.charAt(n);
    }

    public int indexOf(String string, String string2, int n) {
        int n2 = this.getChildCount();
        for (int i = n; i < n2; ++i) {
            Element element = this.getElement(i);
            if (element == null || !string2.equals(element.getName()) || string != null && !string.equals(element.getNamespace())) continue;
            return i;
        }
        return -1;
    }

    public boolean isText(int n) {
        int n2 = this.getType(n);
        return n2 == 4 || n2 == 7 || n2 == 5;
    }

    public void parse(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
        boolean bl = false;
        do {
            int n = xmlPullParser.getEventType();
            switch (n) {
                case 2: {
                    Element element = this.createElement(xmlPullParser.getNamespace(), xmlPullParser.getName());
                    this.addChild(2, element);
                    element.parse(xmlPullParser);
                    break;
                }
                case 1: 
                case 3: {
                    bl = true;
                    break;
                }
                default: {
                    if (xmlPullParser.getText() != null) {
                        this.addChild(n == 6 ? 4 : n, xmlPullParser.getText());
                    } else if (n == 6 && xmlPullParser.getName() != null) {
                        this.addChild(6, xmlPullParser.getName());
                    }
                    xmlPullParser.nextToken();
                }
            }
        } while (!bl);
    }

    public void removeChild(int n) {
        this.children.removeElementAt(n);
        int n2 = this.types.length() - 1;
        for (int i = n; i < n2; ++i) {
            this.types.setCharAt(i, this.types.charAt(i + 1));
        }
        this.types.setLength(n2);
    }

    public void write(XmlSerializer xmlSerializer) throws IOException {
        this.writeChildren(xmlSerializer);
        xmlSerializer.flush();
    }

    public void writeChildren(XmlSerializer xmlSerializer) throws IOException {
        if (this.children == null) {
            return;
        }
        int n = this.children.size();
        block10: for (int i = 0; i < n; ++i) {
            int n2 = this.getType(i);
            Object e = this.children.elementAt(i);
            switch (n2) {
                case 2: {
                    ((Element)e).write(xmlSerializer);
                    continue block10;
                }
                case 4: {
                    xmlSerializer.text((String)e);
                    continue block10;
                }
                case 7: {
                    xmlSerializer.ignorableWhitespace((String)e);
                    continue block10;
                }
                case 5: {
                    xmlSerializer.cdsect((String)e);
                    continue block10;
                }
                case 9: {
                    xmlSerializer.comment((String)e);
                    continue block10;
                }
                case 6: {
                    xmlSerializer.entityRef((String)e);
                    continue block10;
                }
                case 8: {
                    xmlSerializer.processingInstruction((String)e);
                    continue block10;
                }
                case 10: {
                    xmlSerializer.docdecl((String)e);
                    continue block10;
                }
                default: {
                    throw new RuntimeException("Illegal type: " + n2);
                }
            }
        }
    }
}

