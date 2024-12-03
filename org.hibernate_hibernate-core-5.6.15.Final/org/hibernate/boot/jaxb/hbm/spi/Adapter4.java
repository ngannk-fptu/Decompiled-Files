/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package org.hibernate.boot.jaxb.hbm.spi;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.hibernate.EntityMode;
import org.hibernate.boot.jaxb.hbm.internal.EntityModeConverter;

public class Adapter4
extends XmlAdapter<String, EntityMode> {
    public EntityMode unmarshal(String value) {
        return EntityModeConverter.fromXml(value);
    }

    public String marshal(EntityMode value) {
        return EntityModeConverter.toXml(value);
    }
}

