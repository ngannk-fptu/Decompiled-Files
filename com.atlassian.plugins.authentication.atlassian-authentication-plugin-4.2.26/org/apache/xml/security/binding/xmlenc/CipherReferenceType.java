/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlSchemaType
 *  javax.xml.bind.annotation.XmlType
 */
package org.apache.xml.security.binding.xmlenc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import org.apache.xml.security.binding.xmlenc.TransformsType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="CipherReferenceType", namespace="http://www.w3.org/2001/04/xmlenc#", propOrder={"transforms"})
public class CipherReferenceType {
    @XmlElement(name="Transforms", namespace="http://www.w3.org/2001/04/xmlenc#")
    protected TransformsType transforms;
    @XmlAttribute(name="URI", required=true)
    @XmlSchemaType(name="anyURI")
    protected String uri;

    public TransformsType getTransforms() {
        return this.transforms;
    }

    public void setTransforms(TransformsType value) {
        this.transforms = value;
    }

    public String getURI() {
        return this.uri;
    }

    public void setURI(String value) {
        this.uri = value;
    }
}

