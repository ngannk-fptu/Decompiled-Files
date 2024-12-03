/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.DiscriminatorType
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package org.hibernate.boot.jaxb.mapping.spi;

import javax.persistence.DiscriminatorType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.hibernate.boot.jaxb.mapping.internal.DiscriminatorTypeMarshalling;

public class Adapter3
extends XmlAdapter<String, DiscriminatorType> {
    public DiscriminatorType unmarshal(String value) {
        return DiscriminatorTypeMarshalling.fromXml(value);
    }

    public String marshal(DiscriminatorType value) {
        return DiscriminatorTypeMarshalling.toXml(value);
    }
}

