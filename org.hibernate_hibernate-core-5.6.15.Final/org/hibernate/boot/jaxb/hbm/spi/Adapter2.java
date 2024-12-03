/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package org.hibernate.boot.jaxb.hbm.spi;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.hibernate.boot.jaxb.hbm.internal.CacheAccessTypeConverter;
import org.hibernate.cache.spi.access.AccessType;

public class Adapter2
extends XmlAdapter<String, AccessType> {
    public AccessType unmarshal(String value) {
        return CacheAccessTypeConverter.fromXml(value);
    }

    public String marshal(AccessType value) {
        return CacheAccessTypeConverter.toXml(value);
    }
}

