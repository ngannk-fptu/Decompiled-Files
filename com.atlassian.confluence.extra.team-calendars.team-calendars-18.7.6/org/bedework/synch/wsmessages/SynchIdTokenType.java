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
@XmlType(name="SynchIdTokenType", propOrder={"principalHref", "subscribeUrl", "synchToken", "opaqueData"})
public class SynchIdTokenType {
    @XmlElement(required=true)
    protected String principalHref;
    @XmlElement(required=true)
    protected String subscribeUrl;
    @XmlElement(required=true)
    protected String synchToken;
    protected String opaqueData;

    public String getPrincipalHref() {
        return this.principalHref;
    }

    public void setPrincipalHref(String value) {
        this.principalHref = value;
    }

    public String getSubscribeUrl() {
        return this.subscribeUrl;
    }

    public void setSubscribeUrl(String value) {
        this.subscribeUrl = value;
    }

    public String getSynchToken() {
        return this.synchToken;
    }

    public void setSynchToken(String value) {
        this.synchToken = value;
    }

    public String getOpaqueData() {
        return this.opaqueData;
    }

    public void setOpaqueData(String value) {
        this.opaqueData = value;
    }
}

