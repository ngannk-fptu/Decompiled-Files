/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AccessType
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package org.hibernate.boot.jaxb.mapping.spi;

import javax.persistence.AccessType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.hibernate.boot.jaxb.mapping.internal.AccessTypeMarshalling;

public class Adapter1
extends XmlAdapter<String, AccessType> {
    public AccessType unmarshal(String value) {
        return AccessTypeMarshalling.fromXml(value);
    }

    public String marshal(AccessType value) {
        return AccessTypeMarshalling.toXml(value);
    }
}

