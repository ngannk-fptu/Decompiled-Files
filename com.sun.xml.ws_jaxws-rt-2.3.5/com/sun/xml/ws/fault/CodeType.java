/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlTransient
 *  javax.xml.bind.annotation.XmlType
 */
package com.sun.xml.ws.fault;

import com.sun.xml.ws.fault.SubcodeType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="CodeType", namespace="http://www.w3.org/2003/05/soap-envelope", propOrder={"Value", "Subcode"})
class CodeType {
    @XmlTransient
    private static final String ns = "http://www.w3.org/2003/05/soap-envelope";
    @XmlElement(namespace="http://www.w3.org/2003/05/soap-envelope")
    private QName Value;
    @XmlElement(namespace="http://www.w3.org/2003/05/soap-envelope")
    private SubcodeType Subcode;

    CodeType(QName value) {
        this.Value = value;
    }

    CodeType() {
    }

    QName getValue() {
        return this.Value;
    }

    SubcodeType getSubcode() {
        return this.Subcode;
    }

    void setSubcode(SubcodeType subcode) {
        this.Subcode = subcode;
    }
}

