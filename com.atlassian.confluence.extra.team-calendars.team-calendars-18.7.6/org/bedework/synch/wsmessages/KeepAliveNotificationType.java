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
@XmlType(name="KeepAliveNotificationType", propOrder={"subscribeUrl", "token"})
public class KeepAliveNotificationType {
    @XmlElement(required=true)
    protected String subscribeUrl;
    @XmlElement(required=true)
    protected String token;

    public String getSubscribeUrl() {
        return this.subscribeUrl;
    }

    public void setSubscribeUrl(String value) {
        this.subscribeUrl = value;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String value) {
        this.token = value;
    }
}

