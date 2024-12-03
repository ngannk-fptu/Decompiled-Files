/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElementRef
 *  javax.xml.bind.annotation.XmlType
 */
package ietf.params.xml.ns.icalendar_2;

import ietf.params.xml.ns.icalendar_2.BaseParameterType;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ArrayOfParameters", propOrder={"baseParameter"})
public class ArrayOfParameters {
    @XmlElementRef(name="baseParameter", namespace="urn:ietf:params:xml:ns:icalendar-2.0", type=JAXBElement.class)
    protected List<JAXBElement<? extends BaseParameterType>> baseParameter;

    public List<JAXBElement<? extends BaseParameterType>> getBaseParameter() {
        if (this.baseParameter == null) {
            this.baseParameter = new ArrayList<JAXBElement<? extends BaseParameterType>>();
        }
        return this.baseParameter;
    }
}

