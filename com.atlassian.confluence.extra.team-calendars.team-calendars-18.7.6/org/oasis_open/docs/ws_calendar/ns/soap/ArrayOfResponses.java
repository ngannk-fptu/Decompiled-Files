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
package org.oasis_open.docs.ws_calendar.ns.soap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.ws_calendar.ns.soap.BaseResponseType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ArrayOfResponses", propOrder={"baseResponse"})
public class ArrayOfResponses {
    @XmlElementRef(name="baseResponse", namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", type=JAXBElement.class)
    protected List<JAXBElement<? extends BaseResponseType>> baseResponse;

    public List<JAXBElement<? extends BaseResponseType>> getBaseResponse() {
        if (this.baseResponse == null) {
            this.baseResponse = new ArrayList<JAXBElement<? extends BaseResponseType>>();
        }
        return this.baseResponse;
    }
}

