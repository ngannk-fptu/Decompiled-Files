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

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.bedework.synch.wsmessages.ArrayOfSynchProperties;
import org.bedework.synch.wsmessages.ConnectorInfoType;
import org.bedework.synch.wsmessages.SynchDirectionType;
import org.bedework.synch.wsmessages.SynchMasterType;
import org.oasis_open.docs.ws_calendar.ns.soap.BaseResponseType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="SubscriptionStatusResponseType", propOrder={"subscriptionId", "principalHref", "direction", "master", "endAConnector", "endBConnector", "lastRefresh", "errorCt", "missingTarget", "info"})
public class SubscriptionStatusResponseType
extends BaseResponseType {
    @XmlElement(required=true)
    protected String subscriptionId;
    @XmlElement(required=true)
    protected String principalHref;
    @XmlElement(required=true)
    protected SynchDirectionType direction;
    @XmlElement(required=true)
    protected SynchMasterType master;
    @XmlElement(required=true)
    protected ConnectorInfoType endAConnector;
    @XmlElement(required=true)
    protected ConnectorInfoType endBConnector;
    @XmlElement(required=true)
    protected String lastRefresh;
    @XmlElement(required=true)
    protected BigInteger errorCt;
    protected boolean missingTarget;
    @XmlElement(required=true)
    protected ArrayOfSynchProperties info;

    public String getSubscriptionId() {
        return this.subscriptionId;
    }

    public void setSubscriptionId(String value) {
        this.subscriptionId = value;
    }

    public String getPrincipalHref() {
        return this.principalHref;
    }

    public void setPrincipalHref(String value) {
        this.principalHref = value;
    }

    public SynchDirectionType getDirection() {
        return this.direction;
    }

    public void setDirection(SynchDirectionType value) {
        this.direction = value;
    }

    public SynchMasterType getMaster() {
        return this.master;
    }

    public void setMaster(SynchMasterType value) {
        this.master = value;
    }

    public ConnectorInfoType getEndAConnector() {
        return this.endAConnector;
    }

    public void setEndAConnector(ConnectorInfoType value) {
        this.endAConnector = value;
    }

    public ConnectorInfoType getEndBConnector() {
        return this.endBConnector;
    }

    public void setEndBConnector(ConnectorInfoType value) {
        this.endBConnector = value;
    }

    public String getLastRefresh() {
        return this.lastRefresh;
    }

    public void setLastRefresh(String value) {
        this.lastRefresh = value;
    }

    public BigInteger getErrorCt() {
        return this.errorCt;
    }

    public void setErrorCt(BigInteger value) {
        this.errorCt = value;
    }

    public boolean isMissingTarget() {
        return this.missingTarget;
    }

    public void setMissingTarget(boolean value) {
        this.missingTarget = value;
    }

    public ArrayOfSynchProperties getInfo() {
        return this.info;
    }

    public void setInfo(ArrayOfSynchProperties value) {
        this.info = value;
    }
}

