/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.util;

import java.util.ArrayList;
import org.htmlunit.cyberneko.xerces.xni.QName;
import org.htmlunit.cyberneko.xerces.xni.XMLAttributes;

public class XMLAttributesImpl
implements XMLAttributes {
    private final ArrayList<Attribute> attributes_ = new ArrayList();

    @Override
    public int addAttribute(QName name, String type, String value) {
        Attribute attribute = new Attribute();
        attribute.name_.setValues(name);
        attribute.type_ = type;
        attribute.value_ = value;
        attribute.specified_ = false;
        this.attributes_.add(attribute);
        return this.attributes_.size() - 1;
    }

    @Override
    public void removeAllAttributes() {
        this.attributes_.clear();
    }

    @Override
    public void removeAttributeAt(int attrIndex) {
        this.attributes_.remove(attrIndex);
    }

    @Override
    public void setName(int attrIndex, QName attrName) {
        this.attributes_.get(attrIndex).name_.setValues(attrName);
    }

    @Override
    public void getName(int attrIndex, QName attrName) {
        attrName.setValues(this.attributes_.get(attrIndex).name_);
    }

    @Override
    public void setType(int attrIndex, String attrType) {
        this.attributes_.get(attrIndex).type_ = attrType;
    }

    @Override
    public void setValue(int attrIndex, String attrValue) {
        Attribute attribute = this.attributes_.get(attrIndex);
        attribute.value_ = attrValue;
    }

    @Override
    public void setSpecified(int attrIndex, boolean specified) {
        this.attributes_.get(attrIndex).specified_ = specified;
    }

    @Override
    public boolean isSpecified(int attrIndex) {
        return this.attributes_.get(attrIndex).specified_;
    }

    @Override
    public int getLength() {
        return this.attributes_.size();
    }

    @Override
    public String getType(int index) {
        if (index < 0 || index >= this.getLength()) {
            return null;
        }
        return this.getReportableType(this.attributes_.get(index).type_);
    }

    @Override
    public String getType(String qname) {
        int index = this.getIndex(qname);
        return index != -1 ? this.getReportableType(this.attributes_.get(index).type_) : null;
    }

    @Override
    public String getValue(int index) {
        if (index < 0 || index >= this.getLength()) {
            return null;
        }
        return this.attributes_.get(index).value_;
    }

    @Override
    public String getValue(String qname) {
        int index = this.getIndex(qname);
        return index != -1 ? this.attributes_.get(index).value_ : null;
    }

    public String getName(int index) {
        if (index < 0 || index >= this.getLength()) {
            return null;
        }
        return ((Attribute)this.attributes_.get((int)index)).name_.rawname;
    }

    @Override
    public int getIndex(String qName) {
        for (int i = 0; i < this.getLength(); ++i) {
            Attribute attribute = this.attributes_.get(i);
            if (((Attribute)attribute).name_.rawname == null || !((Attribute)attribute).name_.rawname.equals(qName)) continue;
            return i;
        }
        return -1;
    }

    @Override
    public int getIndex(String uri, String localPart) {
        for (int i = 0; i < this.getLength(); ++i) {
            Attribute attribute = this.attributes_.get(i);
            if (((Attribute)attribute).name_.localpart == null || !((Attribute)attribute).name_.localpart.equals(localPart) || uri != ((Attribute)attribute).name_.uri && (uri == null || ((Attribute)attribute).name_.uri == null || !((Attribute)attribute).name_.uri.equals(uri))) continue;
            return i;
        }
        return -1;
    }

    @Override
    public String getLocalName(int index) {
        if (index < 0 || index >= this.getLength()) {
            return null;
        }
        return ((Attribute)this.attributes_.get((int)index)).name_.localpart;
    }

    @Override
    public String getQName(int index) {
        if (index < 0 || index >= this.getLength()) {
            return null;
        }
        String rawname = ((Attribute)this.attributes_.get((int)index)).name_.rawname;
        return rawname != null ? rawname : "";
    }

    @Override
    public String getType(String uri, String localName) {
        int index = this.getIndex(uri, localName);
        return index != -1 ? this.getReportableType(this.attributes_.get(index).type_) : null;
    }

    @Override
    public String getURI(int index) {
        if (index < 0 || index >= this.getLength()) {
            return null;
        }
        return ((Attribute)this.attributes_.get((int)index)).name_.uri;
    }

    @Override
    public String getValue(String uri, String localName) {
        int index = this.getIndex(uri, localName);
        return index != -1 ? this.getValue(index) : null;
    }

    public void addAttributeNS(QName name, String type, String value) {
        Attribute attribute = new Attribute();
        attribute.name_.setValues(name);
        attribute.type_ = type;
        attribute.value_ = value;
        attribute.specified_ = false;
        this.attributes_.add(attribute);
    }

    private String getReportableType(String type) {
        if (type.charAt(0) == '(') {
            return "NMTOKEN";
        }
        return type;
    }

    private static final class Attribute {
        private final QName name_ = new QName();
        private String type_;
        private String value_;
        private boolean specified_;

        private Attribute() {
        }
    }
}

