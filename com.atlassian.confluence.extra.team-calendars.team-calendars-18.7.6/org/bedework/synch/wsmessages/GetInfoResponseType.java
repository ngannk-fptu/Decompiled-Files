/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlType
 */
package org.bedework.synch.wsmessages;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.bedework.synch.wsmessages.SynchInfoType;
import org.oasis_open.docs.ws_calendar.ns.soap.BaseResponseType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="GetInfoResponseType", propOrder={"info"})
public class GetInfoResponseType
extends BaseResponseType {
    @XmlElement(required=true)
    protected SynchInfoType info;

    public SynchInfoType getInfo() {
        return this.info;
    }

    public void setInfo(SynchInfoType value) {
        this.info = value;
    }
}

