/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.InheritanceType
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package org.hibernate.boot.jaxb.mapping.spi;

import javax.persistence.InheritanceType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.hibernate.boot.jaxb.mapping.internal.InheritanceTypeMarshalling;

public class Adapter7
extends XmlAdapter<String, InheritanceType> {
    public InheritanceType unmarshal(String value) {
        return InheritanceTypeMarshalling.fromXml(value);
    }

    public String marshal(InheritanceType value) {
        return InheritanceTypeMarshalling.toXml(value);
    }
}

