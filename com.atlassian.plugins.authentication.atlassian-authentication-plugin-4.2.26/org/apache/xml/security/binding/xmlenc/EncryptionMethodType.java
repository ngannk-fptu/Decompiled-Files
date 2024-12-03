/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAnyElement
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElementRef
 *  javax.xml.bind.annotation.XmlElementRefs
 *  javax.xml.bind.annotation.XmlMixed
 *  javax.xml.bind.annotation.XmlSchemaType
 *  javax.xml.bind.annotation.XmlType
 */
package org.apache.xml.security.binding.xmlenc;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="EncryptionMethodType", namespace="http://www.w3.org/2001/04/xmlenc#", propOrder={"content"})
public class EncryptionMethodType {
    @XmlElementRefs(value={@XmlElementRef(name="KeySize", namespace="http://www.w3.org/2001/04/xmlenc#", type=JAXBElement.class), @XmlElementRef(name="OAEPparams", namespace="http://www.w3.org/2001/04/xmlenc#", type=JAXBElement.class)})
    @XmlMixed
    @XmlAnyElement(lax=true)
    protected List<Object> content;
    @XmlAttribute(name="Algorithm", required=true)
    @XmlSchemaType(name="anyURI")
    protected String algorithm;

    public List<Object> getContent() {
        if (this.content == null) {
            this.content = new ArrayList<Object>();
        }
        return this.content;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }

    public void setAlgorithm(String value) {
        this.algorithm = value;
    }
}

