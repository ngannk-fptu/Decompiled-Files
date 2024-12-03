/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.GenerationType
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package org.hibernate.boot.jaxb.mapping.spi;

import javax.persistence.GenerationType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.hibernate.boot.jaxb.mapping.internal.GenerationTypeMarshalling;

public class Adapter6
extends XmlAdapter<String, GenerationType> {
    public GenerationType unmarshal(String value) {
        return GenerationTypeMarshalling.fromXml(value);
    }

    public String marshal(GenerationType value) {
        return GenerationTypeMarshalling.toXml(value);
    }
}

