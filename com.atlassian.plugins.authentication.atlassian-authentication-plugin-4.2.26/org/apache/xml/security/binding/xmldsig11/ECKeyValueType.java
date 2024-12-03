/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlID
 *  javax.xml.bind.annotation.XmlSchemaType
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.adapters.CollapsedStringAdapter
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package org.apache.xml.security.binding.xmldsig11;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.xml.security.binding.xmldsig11.ECParametersType;
import org.apache.xml.security.binding.xmldsig11.NamedCurveType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ECKeyValueType", namespace="http://www.w3.org/2009/xmldsig11#", propOrder={"ecParameters", "namedCurve", "publicKey"})
public class ECKeyValueType {
    @XmlElement(name="ECParameters", namespace="http://www.w3.org/2009/xmldsig11#")
    protected ECParametersType ecParameters;
    @XmlElement(name="NamedCurve", namespace="http://www.w3.org/2009/xmldsig11#")
    protected NamedCurveType namedCurve;
    @XmlElement(name="PublicKey", namespace="http://www.w3.org/2009/xmldsig11#", required=true)
    protected byte[] publicKey;
    @XmlAttribute(name="Id")
    @XmlJavaTypeAdapter(value=CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name="ID")
    protected String id;

    public ECParametersType getECParameters() {
        return this.ecParameters;
    }

    public void setECParameters(ECParametersType value) {
        this.ecParameters = value;
    }

    public NamedCurveType getNamedCurve() {
        return this.namedCurve;
    }

    public void setNamedCurve(NamedCurveType value) {
        this.namedCurve = value;
    }

    public byte[] getPublicKey() {
        return this.publicKey;
    }

    public void setPublicKey(byte[] value) {
        this.publicKey = value;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String value) {
        this.id = value;
    }
}

