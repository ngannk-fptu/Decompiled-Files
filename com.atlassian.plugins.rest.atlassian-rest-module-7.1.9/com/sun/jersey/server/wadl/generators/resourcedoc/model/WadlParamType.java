/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlType
 */
package com.sun.jersey.server.wadl.generators.resourcedoc.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="wadlParam", propOrder={})
public class WadlParamType {
    @XmlAttribute
    private String name;
    @XmlAttribute
    private String style;
    @XmlAttribute
    private QName type;
    private String doc;

    public String getDoc() {
        return this.doc;
    }

    public void setDoc(String commentText) {
        this.doc = commentText;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String paramName) {
        this.name = paramName;
    }

    public String getStyle() {
        return this.style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public QName getType() {
        return this.type;
    }

    public void setType(QName type) {
        this.type = type;
    }
}

