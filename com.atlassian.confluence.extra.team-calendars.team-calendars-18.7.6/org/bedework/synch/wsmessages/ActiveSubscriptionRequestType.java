/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlSeeAlso
 *  javax.xml.bind.annotation.XmlType
 */
package org.bedework.synch.wsmessages;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.bedework.synch.wsmessages.BaseSynchRequestType;
import org.bedework.synch.wsmessages.ConnectorInfoType;
import org.bedework.synch.wsmessages.SubscriptionStatusRequestType;
import org.bedework.synch.wsmessages.SynchEndType;
import org.bedework.synch.wsmessages.UnsubscribeRequestType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ActiveSubscriptionRequestType", propOrder={"token", "principalHref", "subscriptionId", "end", "connectorInfo"})
@XmlSeeAlso(value={UnsubscribeRequestType.class, SubscriptionStatusRequestType.class})
public class ActiveSubscriptionRequestType
extends BaseSynchRequestType {
    @XmlElement(required=true)
    protected String token;
    @XmlElement(required=true)
    protected String principalHref;
    @XmlElement(name="subscription-id", required=true)
    protected String subscriptionId;
    @XmlElement(required=true)
    protected SynchEndType end;
    @XmlElement(required=true)
    protected ConnectorInfoType connectorInfo;

    public String getToken() {
        return this.token;
    }

    public void setToken(String value) {
        this.token = value;
    }

    public String getPrincipalHref() {
        return this.principalHref;
    }

    public void setPrincipalHref(String value) {
        this.principalHref = value;
    }

    public String getSubscriptionId() {
        return this.subscriptionId;
    }

    public void setSubscriptionId(String value) {
        this.subscriptionId = value;
    }

    public SynchEndType getEnd() {
        return this.end;
    }

    public void setEnd(SynchEndType value) {
        this.end = value;
    }

    public ConnectorInfoType getConnectorInfo() {
        return this.connectorInfo;
    }

    public void setConnectorInfo(ConnectorInfoType value) {
        this.connectorInfo = value;
    }
}

