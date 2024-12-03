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
import org.bedework.synch.wsmessages.ArrayOfSynchPropertyInfo;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="SynchConnectorInfoType", propOrder={"name", "manager", "readOnly", "properties"})
public class SynchConnectorInfoType {
    @XmlElement(required=true)
    protected String name;
    protected boolean manager;
    protected boolean readOnly;
    @XmlElement(required=true)
    protected ArrayOfSynchPropertyInfo properties;

    public String getName() {
        return this.name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public boolean isManager() {
        return this.manager;
    }

    public void setManager(boolean value) {
        this.manager = value;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public void setReadOnly(boolean value) {
        this.readOnly = value;
    }

    public ArrayOfSynchPropertyInfo getProperties() {
        return this.properties;
    }

    public void setProperties(ArrayOfSynchPropertyInfo value) {
        this.properties = value;
    }
}

