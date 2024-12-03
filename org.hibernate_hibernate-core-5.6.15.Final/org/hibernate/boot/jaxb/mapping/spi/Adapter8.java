/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.LockModeType
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package org.hibernate.boot.jaxb.mapping.spi;

import javax.persistence.LockModeType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.hibernate.boot.jaxb.mapping.internal.LockModeTypeMarshalling;

public class Adapter8
extends XmlAdapter<String, LockModeType> {
    public LockModeType unmarshal(String value) {
        return LockModeTypeMarshalling.fromXml(value);
    }

    public String marshal(LockModeType value) {
        return LockModeTypeMarshalling.toXml(value);
    }
}

