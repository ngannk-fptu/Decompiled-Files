/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElementRef
 *  javax.xml.bind.annotation.XmlElementRefs
 *  javax.xml.bind.annotation.XmlType
 */
package ietf.params.xml.ns.icalendar_2;

import ietf.params.xml.ns.icalendar_2.BasePropertyType;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ArrayOfProperties", propOrder={"basePropertyOrTzid"})
public class ArrayOfProperties {
    @XmlElementRefs(value={@XmlElementRef(name="baseProperty", namespace="urn:ietf:params:xml:ns:icalendar-2.0", type=JAXBElement.class), @XmlElementRef(name="tzid", namespace="urn:ietf:params:xml:ns:icalendar-2.0", type=JAXBElement.class)})
    protected List<JAXBElement<? extends BasePropertyType>> basePropertyOrTzid;

    public List<JAXBElement<? extends BasePropertyType>> getBasePropertyOrTzid() {
        if (this.basePropertyOrTzid == null) {
            this.basePropertyOrTzid = new ArrayList<JAXBElement<? extends BasePropertyType>>();
        }
        return this.basePropertyOrTzid;
    }
}

