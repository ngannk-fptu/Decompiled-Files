/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlType
 */
package org.apache.xml.security.binding.xmldsig11;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="PrimeFieldParamsType", namespace="http://www.w3.org/2009/xmldsig11#", propOrder={"p"})
public class PrimeFieldParamsType {
    @XmlElement(name="P", namespace="http://www.w3.org/2009/xmldsig11#", required=true)
    protected byte[] p;

    public byte[] getP() {
        return this.p;
    }

    public void setP(byte[] value) {
        this.p = value;
    }
}

