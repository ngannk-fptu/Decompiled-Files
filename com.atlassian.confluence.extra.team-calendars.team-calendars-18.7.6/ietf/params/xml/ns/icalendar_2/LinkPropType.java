/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlType
 */
package ietf.params.xml.ns.icalendar_2;

import ietf.params.xml.ns.icalendar_2.BasePropertyType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="LinkPropType", propOrder={"uri", "uid", "reference"})
public class LinkPropType
extends BasePropertyType {
    protected String uri;
    protected String uid;
    protected String reference;

    public String getUri() {
        return this.uri;
    }

    public void setUri(String value) {
        this.uri = value;
    }

    public String getUid() {
        return this.uid;
    }

    public void setUid(String value) {
        this.uid = value;
    }

    public String getReference() {
        return this.reference;
    }

    public void setReference(String value) {
        this.reference = value;
    }
}

