/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlSchemaType
 *  javax.xml.bind.annotation.XmlType
 */
package org.oasis_open.docs.ns.xri.xrd_1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.ns.xri.xrd_1.String;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="PropertyType")
public class PropertyType
extends String {
    @XmlAttribute(required=true)
    @XmlSchemaType(name="anyURI")
    protected java.lang.String type;

    public java.lang.String getType() {
        return this.type;
    }

    public void setType(java.lang.String value) {
        this.type = value;
    }
}

