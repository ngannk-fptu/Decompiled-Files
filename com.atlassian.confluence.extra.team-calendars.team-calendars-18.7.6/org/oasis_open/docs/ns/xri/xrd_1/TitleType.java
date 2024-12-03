/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlSchemaType
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.adapters.CollapsedStringAdapter
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package org.oasis_open.docs.ns.xri.xrd_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.oasis_open.docs.ns.xri.xrd_1.String;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="TitleType")
public class TitleType
extends String {
    @XmlAttribute(namespace="http://www.w3.org/XML/1998/namespace")
    @XmlJavaTypeAdapter(value=CollapsedStringAdapter.class)
    @XmlSchemaType(name="language")
    protected java.lang.String lang;

    public java.lang.String getLang() {
        return this.lang;
    }

    public void setLang(java.lang.String value) {
        this.lang = value;
    }
}

