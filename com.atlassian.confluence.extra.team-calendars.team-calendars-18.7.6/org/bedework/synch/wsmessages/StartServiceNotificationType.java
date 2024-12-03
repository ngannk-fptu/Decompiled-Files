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

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="StartServiceNotificationType", propOrder={"connectorId", "subscribeUrl", "id", "pw"})
public class StartServiceNotificationType {
    @XmlElement(required=true)
    protected String connectorId;
    @XmlElement(required=true)
    protected String subscribeUrl;
    protected String id;
    protected String pw;

    public String getConnectorId() {
        return this.connectorId;
    }

    public void setConnectorId(String value) {
        this.connectorId = value;
    }

    public String getSubscribeUrl() {
        return this.subscribeUrl;
    }

    public void setSubscribeUrl(String value) {
        this.subscribeUrl = value;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String value) {
        this.id = value;
    }

    public String getPw() {
        return this.pw;
    }

    public void setPw(String value) {
        this.pw = value;
    }
}

