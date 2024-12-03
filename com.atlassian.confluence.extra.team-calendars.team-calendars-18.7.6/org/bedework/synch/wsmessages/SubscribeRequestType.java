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
import org.bedework.synch.wsmessages.ArrayOfSynchProperties;
import org.bedework.synch.wsmessages.BaseSynchRequestType;
import org.bedework.synch.wsmessages.ConnectorInfoType;
import org.bedework.synch.wsmessages.SynchDirectionType;
import org.bedework.synch.wsmessages.SynchMasterType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="SubscribeRequestType", propOrder={"token", "principalHref", "direction", "master", "endAConnector", "endBConnector", "info", "opaqueData"})
public class SubscribeRequestType
extends BaseSynchRequestType {
    @XmlElement(required=true)
    protected String token;
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
    protected ArrayOfSynchProperties info;
    protected String opaqueData;

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

    public ArrayOfSynchProperties getInfo() {
        return this.info;
    }

    public void setInfo(ArrayOfSynchProperties value) {
        this.info = value;
    }

    public String getOpaqueData() {
        return this.opaqueData;
    }

    public void setOpaqueData(String value) {
        this.opaqueData = value;
    }
}

