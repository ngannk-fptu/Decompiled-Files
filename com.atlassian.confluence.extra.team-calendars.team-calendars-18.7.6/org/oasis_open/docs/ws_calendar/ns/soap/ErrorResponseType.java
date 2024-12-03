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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.ws_calendar.ns.soap.ErrorCodeType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ErrorResponseType", propOrder={"error", "description"})
public class ErrorResponseType {
    @XmlElementRef(name="error", namespace="http://docs.oasis-open.org/ws-calendar/ns/soap", type=JAXBElement.class)
    protected JAXBElement<? extends ErrorCodeType> error;
    protected String description;

    public JAXBElement<? extends ErrorCodeType> getError() {
        return this.error;
    }

    public void setError(JAXBElement<? extends ErrorCodeType> value) {
        this.error = value;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String value) {
        this.description = value;
    }
}

