/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.FetchType
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package org.hibernate.boot.jaxb.mapping.spi;

import javax.persistence.FetchType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.hibernate.boot.jaxb.mapping.internal.FetchTypeMarshalling;

public class Adapter5
extends XmlAdapter<String, FetchType> {
    public FetchType unmarshal(String value) {
        return FetchTypeMarshalling.fromXml(value);
    }

    public String marshal(FetchType value) {
        return FetchTypeMarshalling.toXml(value);
    }
}

