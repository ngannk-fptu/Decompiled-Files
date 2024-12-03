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
import org.bedework.synch.wsmessages.SynchPropertyInfoType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ArrayOfSynchPropertyInfo", propOrder={"property"})
public class ArrayOfSynchPropertyInfo {
    protected List<SynchPropertyInfoType> property;

    public List<SynchPropertyInfoType> getProperty() {
        if (this.property == null) {
            this.property = new ArrayList<SynchPropertyInfoType>();
        }
        return this.property;
    }
}

