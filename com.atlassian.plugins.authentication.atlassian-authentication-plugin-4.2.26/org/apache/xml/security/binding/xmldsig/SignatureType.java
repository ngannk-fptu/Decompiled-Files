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
package org.apache.xml.security.binding.xmldsig;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.xml.security.binding.xmldsig.KeyInfoType;
import org.apache.xml.security.binding.xmldsig.ObjectType;
import org.apache.xml.security.binding.xmldsig.SignatureValueType;
import org.apache.xml.security.binding.xmldsig.SignedInfoType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="SignatureType", namespace="http://www.w3.org/2000/09/xmldsig#", propOrder={"signedInfo", "signatureValue", "keyInfo", "object"})
public class SignatureType {
    @XmlElement(name="SignedInfo", namespace="http://www.w3.org/2000/09/xmldsig#", required=true)
    protected SignedInfoType signedInfo;
    @XmlElement(name="SignatureValue", namespace="http://www.w3.org/2000/09/xmldsig#", required=true)
    protected SignatureValueType signatureValue;
    @XmlElement(name="KeyInfo", namespace="http://www.w3.org/2000/09/xmldsig#")
    protected KeyInfoType keyInfo;
    @XmlElement(name="Object", namespace="http://www.w3.org/2000/09/xmldsig#")
    protected List<ObjectType> object;
    @XmlAttribute(name="Id")
    @XmlJavaTypeAdapter(value=CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name="ID")
    protected String id;

    public SignedInfoType getSignedInfo() {
        return this.signedInfo;
    }

    public void setSignedInfo(SignedInfoType value) {
        this.signedInfo = value;
    }

    public SignatureValueType getSignatureValue() {
        return this.signatureValue;
    }

    public void setSignatureValue(SignatureValueType value) {
        this.signatureValue = value;
    }

    public KeyInfoType getKeyInfo() {
        return this.keyInfo;
    }

    public void setKeyInfo(KeyInfoType value) {
        this.keyInfo = value;
    }

    public List<ObjectType> getObject() {
        if (this.object == null) {
            this.object = new ArrayList<ObjectType>();
        }
        return this.object;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String value) {
        this.id = value;
    }
}

