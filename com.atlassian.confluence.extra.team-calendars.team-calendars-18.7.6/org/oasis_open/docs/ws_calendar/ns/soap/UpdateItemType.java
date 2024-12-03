/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlType
 */
package org.oasis_open.docs.ws_calendar.ns.soap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.ws_calendar.ns.soap.BaseRequestType;
import org.oasis_open.docs.ws_calendar.ns.soap.ComponentSelectionType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="UpdateItemType", propOrder={"changeToken", "select"})
public class UpdateItemType
extends BaseRequestType {
    @XmlElement(required=true)
    protected String changeToken;
    @XmlElement(required=true)
    protected List<ComponentSelectionType> select;

    public String getChangeToken() {
        return this.changeToken;
    }

    public void setChangeToken(String value) {
        this.changeToken = value;
    }

    public List<ComponentSelectionType> getSelect() {
        if (this.select == null) {
            this.select = new ArrayList<ComponentSelectionType>();
        }
        return this.select;
    }
}

