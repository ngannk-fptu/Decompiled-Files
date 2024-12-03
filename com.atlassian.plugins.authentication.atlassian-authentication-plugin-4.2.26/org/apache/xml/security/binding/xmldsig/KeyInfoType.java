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
 *  javax.xml.bind.annotation.XmlID
 *  javax.xml.bind.annotation.XmlMixed
 *  javax.xml.bind.annotation.XmlSchemaType
 *  javax.xml.bind.annotation.XmlType
 *  javax.xml.bind.annotation.adapters.CollapsedStringAdapter
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package org.apache.xml.security.binding.xmldsig;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="KeyInfoType", namespace="http://www.w3.org/2000/09/xmldsig#", propOrder={"content"})
public class KeyInfoType {
    @XmlElementRefs(value={@XmlElementRef(name="KeyName", namespace="http://www.w3.org/2000/09/xmldsig#", type=JAXBElement.class), @XmlElementRef(name="KeyValue", namespace="http://www.w3.org/2000/09/xmldsig#", type=JAXBElement.class), @XmlElementRef(name="RetrievalMethod", namespace="http://www.w3.org/2000/09/xmldsig#", type=JAXBElement.class), @XmlElementRef(name="X509Data", namespace="http://www.w3.org/2000/09/xmldsig#", type=JAXBElement.class), @XmlElementRef(name="PGPData", namespace="http://www.w3.org/2000/09/xmldsig#", type=JAXBElement.class), @XmlElementRef(name="SPKIData", namespace="http://www.w3.org/2000/09/xmldsig#", type=JAXBElement.class), @XmlElementRef(name="MgmtData", namespace="http://www.w3.org/2000/09/xmldsig#", type=JAXBElement.class)})
    @XmlMixed
    @XmlAnyElement(lax=true)
    protected List<Object> content;
    @XmlAttribute(name="Id")
    @XmlJavaTypeAdapter(value=CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name="ID")
    protected String id;

    public List<Object> getContent() {
        if (this.content == null) {
            this.content = new ArrayList<Object>();
        }
        return this.content;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String value) {
        this.id = value;
    }
}

