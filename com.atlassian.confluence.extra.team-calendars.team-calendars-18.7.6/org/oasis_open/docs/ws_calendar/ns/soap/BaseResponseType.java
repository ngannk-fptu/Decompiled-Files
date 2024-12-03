/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlAttribute
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlSeeAlso
 *  javax.xml.bind.annotation.XmlType
 */
package org.oasis_open.docs.ws_calendar.ns.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.ws_calendar.ns.soap.AddItemResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.DeleteItemResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.ErrorResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.FetchItemResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.FreebusyReportResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.GetPropertiesResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.MultiOpResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.MultistatusType;
import org.oasis_open.docs.ws_calendar.ns.soap.PropstatType;
import org.oasis_open.docs.ws_calendar.ns.soap.StatusType;
import org.oasis_open.docs.ws_calendar.ns.soap.UpdateItemResponseType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="BaseResponseType", propOrder={"status", "message", "errorResponse"})
@XmlSeeAlso(value={GetPropertiesResponseType.class, UpdateItemResponseType.class, FreebusyReportResponseType.class, FetchItemResponseType.class, MultiOpResponseType.class, AddItemResponseType.class, PropstatType.class, DeleteItemResponseType.class, MultistatusType.class})
public abstract class BaseResponseType {
    @XmlElement(required=true)
    protected StatusType status;
    protected String message;
    protected ErrorResponseType errorResponse;
    @XmlAttribute
    protected Integer id;

    public StatusType getStatus() {
        return this.status;
    }

    public void setStatus(StatusType value) {
        this.status = value;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String value) {
        this.message = value;
    }

    public ErrorResponseType getErrorResponse() {
        return this.errorResponse;
    }

    public void setErrorResponse(ErrorResponseType value) {
        this.errorResponse = value;
    }

    public int getId() {
        if (this.id == null) {
            return 0;
        }
        return this.id;
    }

    public void setId(Integer value) {
        this.id = value;
    }
}

