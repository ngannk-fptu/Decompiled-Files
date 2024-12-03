/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package org.hibernate.boot.jaxb.hbm.spi;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.hibernate.CacheMode;
import org.hibernate.boot.jaxb.hbm.internal.CacheModeConverter;

public class Adapter1
extends XmlAdapter<String, CacheMode> {
    public CacheMode unmarshal(String value) {
        return CacheModeConverter.fromXml(value);
    }

    public String marshal(CacheMode value) {
        return CacheModeConverter.toXml(value);
    }
}

