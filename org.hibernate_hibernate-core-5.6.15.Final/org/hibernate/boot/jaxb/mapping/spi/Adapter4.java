/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EnumType
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package org.hibernate.boot.jaxb.mapping.spi;

import javax.persistence.EnumType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.hibernate.boot.jaxb.mapping.internal.EnumTypeMarshalling;

public class Adapter4
extends XmlAdapter<String, EnumType> {
    public EnumType unmarshal(String value) {
        return EnumTypeMarshalling.fromXml(value);
    }

    public String marshal(EnumType value) {
        return EnumTypeMarshalling.toXml(value);
    }
}

