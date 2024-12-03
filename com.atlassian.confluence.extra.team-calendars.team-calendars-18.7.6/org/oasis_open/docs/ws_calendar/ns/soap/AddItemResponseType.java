/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlType
 */
package org.oasis_open.docs.ws_calendar.ns.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.ws_calendar.ns.soap.BaseResponseType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="AddItemResponseType", propOrder={"href", "changeToken"})
public class AddItemResponseType
extends BaseResponseType {
    protected String href;
    protected String changeToken;

    public String getHref() {
        return this.href;
    }

    public void setHref(String value) {
        this.href = value;
    }

    public String getChangeToken() {
        return this.changeToken;
    }

    public void setChangeToken(String value) {
        this.changeToken = value;
    }
}

