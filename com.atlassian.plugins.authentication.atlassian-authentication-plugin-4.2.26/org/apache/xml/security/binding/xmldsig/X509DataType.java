/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAnyElement
 *  javax.xml.bind.annotation.XmlElementRef
 *  javax.xml.bind.annotation.XmlElementRefs
 *  javax.xml.bind.annotation.XmlType
 */
package org.apache.xml.security.binding.xmldsig;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="X509DataType", namespace="http://www.w3.org/2000/09/xmldsig#", propOrder={"x509IssuerSerialOrX509SKIOrX509SubjectName"})
public class X509DataType {
    @XmlElementRefs(value={@XmlElementRef(name="X509IssuerSerial", namespace="http://www.w3.org/2000/09/xmldsig#", type=JAXBElement.class), @XmlElementRef(name="X509SKI", namespace="http://www.w3.org/2000/09/xmldsig#", type=JAXBElement.class), @XmlElementRef(name="X509SubjectName", namespace="http://www.w3.org/2000/09/xmldsig#", type=JAXBElement.class), @XmlElementRef(name="X509Certificate", namespace="http://www.w3.org/2000/09/xmldsig#", type=JAXBElement.class), @XmlElementRef(name="X509CRL", namespace="http://www.w3.org/2000/09/xmldsig#", type=JAXBElement.class)})
    @XmlAnyElement(lax=true)
    protected List<Object> x509IssuerSerialOrX509SKIOrX509SubjectName;

    public List<Object> getX509IssuerSerialOrX509SKIOrX509SubjectName() {
        if (this.x509IssuerSerialOrX509SKIOrX509SubjectName == null) {
            this.x509IssuerSerialOrX509SKIOrX509SubjectName = new ArrayList<Object>();
        }
        return this.x509IssuerSerialOrX509SKIOrX509SubjectName;
    }
}

