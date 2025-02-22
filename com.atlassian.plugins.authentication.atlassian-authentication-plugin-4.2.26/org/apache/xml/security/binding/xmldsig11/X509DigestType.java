/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlSchemaType
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.XmlValue
 */
package org.apache.xml.security.binding.xmldsig11;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="X509DigestType", namespace="http://www.w3.org/2009/xmldsig11#", propOrder={"value"})
public class X509DigestType {
    @XmlValue
    protected byte[] value;
    @XmlAttribute(name="Algorithm", required=true)
    @XmlSchemaType(name="anyURI")
    protected String algorithm;

    public byte[] getValue() {
        return this.value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }

    public void setAlgorithm(String value) {
        this.algorithm = value;
    }
}

