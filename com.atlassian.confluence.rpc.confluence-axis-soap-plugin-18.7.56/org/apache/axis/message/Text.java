/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.message;

import javax.xml.parsers.ParserConfigurationException;
import org.apache.axis.InternalException;
import org.apache.axis.message.NodeImpl;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.CharacterData;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Text
extends NodeImpl
implements javax.xml.soap.Text {
    public Text(CharacterData data) {
        if (data == null) {
            throw new IllegalArgumentException("Text value may not be null.");
        }
        this.textRep = data;
    }

    public Text(String s) {
        try {
            Document doc = XMLUtils.newDocument();
            this.textRep = doc.createTextNode(s);
        }
        catch (ParserConfigurationException e) {
            throw new InternalException(e);
        }
    }

    public Text() {
        this((String)null);
    }

    public boolean isComment() {
        String temp = this.textRep.getNodeValue().trim();
        return temp.startsWith("<!--") && temp.endsWith("-->");
    }

    public String getNodeValue() throws DOMException {
        return this.textRep.getNodeValue();
    }

    public void setNodeValue(String nodeValue) throws DOMException {
        this.setDirty(true);
        this.textRep.setNodeValue(nodeValue);
    }

    public org.w3c.dom.Text splitText(int offset) throws DOMException {
        int length = this.textRep.getLength();
        String tailData = this.textRep.substringData(offset, length);
        this.textRep.deleteData(offset, length);
        Text tailText = new Text(tailData);
        Node myParent = this.getParentNode();
        if (myParent != null) {
            NodeList brothers = myParent.getChildNodes();
            for (int i = 0; i < brothers.getLength(); ++i) {
                if (!brothers.item(i).equals(this)) continue;
                myParent.insertBefore(tailText, this);
                return tailText;
            }
        }
        return tailText;
    }

    public String getData() throws DOMException {
        return this.textRep.getData();
    }

    public void setData(String data) throws DOMException {
        this.textRep.setData(data);
    }

    public int getLength() {
        return this.textRep.getLength();
    }

    public String substringData(int offset, int count) throws DOMException {
        return this.textRep.substringData(offset, count);
    }

    public void appendData(String arg) throws DOMException {
        this.textRep.appendData(arg);
    }

    public void insertData(int offset, String arg) throws DOMException {
        this.textRep.insertData(offset, arg);
    }

    public void replaceData(int offset, int count, String arg) throws DOMException {
        this.textRep.replaceData(offset, count, arg);
    }

    public void deleteData(int offset, int count) throws DOMException {
        this.textRep.deleteData(offset, count);
    }

    public String toString() {
        return this.textRep.getNodeValue();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Text)) {
            return false;
        }
        return this == obj || this.hashCode() == obj.hashCode();
    }

    public int hashCode() {
        if (this.textRep == null) {
            return -1;
        }
        return this.textRep.getData() != null ? this.textRep.getData().hashCode() : 0;
    }
}

