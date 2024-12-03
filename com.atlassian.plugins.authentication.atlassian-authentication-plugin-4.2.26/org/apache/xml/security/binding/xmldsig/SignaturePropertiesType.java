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
import org.apache.xml.security.binding.xmldsig.SignaturePropertyType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="SignaturePropertiesType", namespace="http://www.w3.org/2000/09/xmldsig#", propOrder={"signatureProperty"})
public class SignaturePropertiesType {
    @XmlElement(name="SignatureProperty", namespace="http://www.w3.org/2000/09/xmldsig#", required=true)
    protected List<SignaturePropertyType> signatureProperty;
    @XmlAttribute(name="Id")
    @XmlJavaTypeAdapter(value=CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name="ID")
    protected String id;

    public List<SignaturePropertyType> getSignatureProperty() {
        if (this.signatureProperty == null) {
            this.signatureProperty = new ArrayList<SignaturePropertyType>();
        }
        return this.signatureProperty;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String value) {
        this.id = value;
    }
}

