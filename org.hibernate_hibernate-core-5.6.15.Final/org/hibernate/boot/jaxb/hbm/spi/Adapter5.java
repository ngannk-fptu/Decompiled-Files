/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package org.hibernate.boot.jaxb.hbm.spi;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.hibernate.FlushMode;
import org.hibernate.boot.jaxb.hbm.internal.FlushModeConverter;

public class Adapter5
extends XmlAdapter<String, FlushMode> {
    public FlushMode unmarshal(String value) {
        return FlushModeConverter.fromXml(value);
    }

    public String marshal(FlushMode value) {
        return FlushModeConverter.toXml(value);
    }
}

