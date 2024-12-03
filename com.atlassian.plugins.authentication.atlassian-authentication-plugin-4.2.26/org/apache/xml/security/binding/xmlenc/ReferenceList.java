/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElementRef
 *  javax.xml.bind.annotation.XmlElementRefs
 *  javax.xml.bind.annotation.XmlRootElement
 *  javax.xml.bind.annotation.XmlType
 */
package org.apache.xml.security.binding.xmlenc;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.xml.security.binding.xmlenc.ReferenceType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="", propOrder={"dataReferenceOrKeyReference"})
@XmlRootElement(name="ReferenceList", namespace="http://www.w3.org/2001/04/xmlenc#")
public class ReferenceList {
    @XmlElementRefs(value={@XmlElementRef(name="DataReference", namespace="http://www.w3.org/2001/04/xmlenc#", type=JAXBElement.class), @XmlElementRef(name="KeyReference", namespace="http://www.w3.org/2001/04/xmlenc#", type=JAXBElement.class)})
    protected List<JAXBElement<ReferenceType>> dataReferenceOrKeyReference;

    public List<JAXBElement<ReferenceType>> getDataReferenceOrKeyReference() {
        if (this.dataReferenceOrKeyReference == null) {
            this.dataReferenceOrKeyReference = new ArrayList<JAXBElement<ReferenceType>>();
        }
        return this.dataReferenceOrKeyReference;
    }
}

