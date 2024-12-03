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
import org.bedework.synch.wsmessages.SynchPropertyType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ArrayOfSynchProperties", propOrder={"property"})
public class ArrayOfSynchProperties {
    protected List<SynchPropertyType> property;

    public List<SynchPropertyType> getProperty() {
        if (this.property == null) {
            this.property = new ArrayList<SynchPropertyType>();
        }
        return this.property;
    }
}

