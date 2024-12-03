/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElementRef
 *  javax.xml.bind.annotation.XmlMixed
 *  javax.xml.bind.annotation.XmlType
 */
package org.apache.xml.security.binding.xmlenc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="CipherValueType", namespace="http://www.w3.org/2001/04/xmlenc#", propOrder={"content"})
public class CipherValueType {
    @XmlElementRef(name="Include", namespace="http://www.w3.org/2004/08/xop/include", type=JAXBElement.class)
    @XmlMixed
    protected List<Serializable> content;

    public List<Serializable> getContent() {
        if (this.content == null) {
            this.content = new ArrayList<Serializable>();
        }
        return this.content;
    }
}

