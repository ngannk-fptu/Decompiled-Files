/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.TemporalType
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package org.hibernate.boot.jaxb.mapping.spi;

import javax.persistence.TemporalType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.hibernate.boot.jaxb.mapping.internal.TemporalTypeMarshalling;

public class Adapter10
extends XmlAdapter<String, TemporalType> {
    public TemporalType unmarshal(String value) {
        return TemporalTypeMarshalling.fromXml(value);
    }

    public String marshal(TemporalType value) {
        return TemporalTypeMarshalling.toXml(value);
    }
}

