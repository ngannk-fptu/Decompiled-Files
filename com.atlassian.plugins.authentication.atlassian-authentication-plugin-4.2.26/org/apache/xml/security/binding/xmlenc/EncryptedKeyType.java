/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlType
 */
package org.apache.xml.security.binding.xmlenc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.xml.security.binding.xmlenc.EncryptedType;
import org.apache.xml.security.binding.xmlenc.ReferenceList;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="EncryptedKeyType", namespace="http://www.w3.org/2001/04/xmlenc#", propOrder={"referenceList", "carriedKeyName"})
public class EncryptedKeyType
extends EncryptedType {
    @XmlElement(name="ReferenceList", namespace="http://www.w3.org/2001/04/xmlenc#")
    protected ReferenceList referenceList;
    @XmlElement(name="CarriedKeyName", namespace="http://www.w3.org/2001/04/xmlenc#")
    protected String carriedKeyName;
    @XmlAttribute(name="Recipient")
    protected String recipient;

    public ReferenceList getReferenceList() {
        return this.referenceList;
    }

    public void setReferenceList(ReferenceList value) {
        this.referenceList = value;
    }

    public String getCarriedKeyName() {
        return this.carriedKeyName;
    }

    public void setCarriedKeyName(String value) {
        this.carriedKeyName = value;
    }

    public String getRecipient() {
        return this.recipient;
    }

    public void setRecipient(String value) {
        this.recipient = value;
    }
}

