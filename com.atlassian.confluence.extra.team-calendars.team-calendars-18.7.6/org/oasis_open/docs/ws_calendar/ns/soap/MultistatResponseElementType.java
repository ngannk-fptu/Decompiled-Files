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
import org.oasis_open.docs.ws_calendar.ns.soap.PropstatType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="MultistatResponseElementType", propOrder={"href", "changeToken", "propstat"})
public class MultistatResponseElementType {
    @XmlElement(required=true)
    protected String href;
    @XmlElement(required=true)
    protected String changeToken;
    protected List<PropstatType> propstat;

    public String getHref() {
        return this.href;
    }

    public void setHref(String value) {
        this.href = value;
    }

    public String getChangeToken() {
        return this.changeToken;
    }

    public void setChangeToken(String value) {
        this.changeToken = value;
    }

    public List<PropstatType> getPropstat() {
        if (this.propstat == null) {
            this.propstat = new ArrayList<PropstatType>();
        }
        return this.propstat;
    }
}

