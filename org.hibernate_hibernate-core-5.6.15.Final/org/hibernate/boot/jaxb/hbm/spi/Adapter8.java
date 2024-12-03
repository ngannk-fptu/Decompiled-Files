/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package org.hibernate.boot.jaxb.hbm.spi;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.hibernate.LockMode;
import org.hibernate.boot.jaxb.hbm.internal.LockModeConverter;

public class Adapter8
extends XmlAdapter<String, LockMode> {
    public LockMode unmarshal(String value) {
        return LockModeConverter.fromXml(value);
    }

    public String marshal(LockMode value) {
        return LockModeConverter.toXml(value);
    }
}

