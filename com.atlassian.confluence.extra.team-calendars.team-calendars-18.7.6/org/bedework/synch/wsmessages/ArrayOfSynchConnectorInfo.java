/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlType
 */
package org.bedework.synch.wsmessages;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.bedework.synch.wsmessages.SynchConnectorInfoType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ArrayOfSynchConnectorInfo", propOrder={"connector"})
public class ArrayOfSynchConnectorInfo {
    protected List<SynchConnectorInfoType> connector;

    public List<SynchConnectorInfoType> getConnector() {
        if (this.connector == null) {
            this.connector = new ArrayList<SynchConnectorInfoType>();
        }
        return this.connector;
    }
}

