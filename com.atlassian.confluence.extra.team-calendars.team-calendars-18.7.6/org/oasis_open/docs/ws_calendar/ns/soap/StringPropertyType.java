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
package org.oasis_open.docs.ws_calendar.ns.soap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.ws_calendar.ns.soap.DisplayNameType;
import org.oasis_open.docs.ws_calendar.ns.soap.GetPropertiesBasePropertyType;
import org.oasis_open.docs.ws_calendar.ns.soap.PrincipalHomeType;
import org.oasis_open.docs.ws_calendar.ns.soap.ResourceDescriptionType;
import org.oasis_open.docs.ws_calendar.ns.soap.ResourceOwnerType;
import org.oasis_open.docs.ws_calendar.ns.soap.ResourceTimezoneIdType;
import org.oasis_open.docs.ws_calendar.ns.soap.TimezoneServerType;
import org.oasis_open.docs.ws_calendar.ns.soap.XresourceType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="StringPropertyType", propOrder={"string"})
@XmlSeeAlso(value={ResourceTimezoneIdType.class, XresourceType.class, DisplayNameType.class, ResourceOwnerType.class, TimezoneServerType.class, PrincipalHomeType.class, ResourceDescriptionType.class})
public class StringPropertyType
extends GetPropertiesBasePropertyType {
    @XmlElement(required=true)
    protected String string;

    public String getString() {
        return this.string;
    }

    public void setString(String value) {
        this.string = value;
    }
}

