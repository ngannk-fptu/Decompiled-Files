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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.ws_calendar.ns.soap.ArrayOfOperations;
import org.oasis_open.docs.ws_calendar.ns.soap.BaseRequestType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="MultiOpType", propOrder={"operations"})
public class MultiOpType
extends BaseRequestType {
    @XmlElement(required=true)
    protected ArrayOfOperations operations;

    public ArrayOfOperations getOperations() {
        return this.operations;
    }

    public void setOperations(ArrayOfOperations value) {
        this.operations = value;
    }
}

