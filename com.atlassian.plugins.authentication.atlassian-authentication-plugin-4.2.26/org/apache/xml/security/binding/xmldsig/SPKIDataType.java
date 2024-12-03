/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAnyElement
 *  javax.xml.bind.annotation.XmlElementRef
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
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="SPKIDataType", namespace="http://www.w3.org/2000/09/xmldsig#", propOrder={"spkiSexpAndAny"})
public class SPKIDataType {
    @XmlElementRef(name="SPKISexp", namespace="http://www.w3.org/2000/09/xmldsig#", type=JAXBElement.class)
    @XmlAnyElement(lax=true)
    protected List<Object> spkiSexpAndAny;

    public List<Object> getSPKISexpAndAny() {
        if (this.spkiSexpAndAny == null) {
            this.spkiSexpAndAny = new ArrayList<Object>();
        }
        return this.spkiSexpAndAny;
    }
}

