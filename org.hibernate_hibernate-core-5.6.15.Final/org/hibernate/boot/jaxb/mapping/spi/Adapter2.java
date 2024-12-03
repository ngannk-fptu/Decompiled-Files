/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.ConstraintMode
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package org.hibernate.boot.jaxb.mapping.spi;

import javax.persistence.ConstraintMode;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.hibernate.boot.jaxb.mapping.internal.ConstraintModeMarshalling;

public class Adapter2
extends XmlAdapter<String, ConstraintMode> {
    public ConstraintMode unmarshal(String value) {
        return ConstraintModeMarshalling.fromXml(value);
    }

    public String marshal(ConstraintMode value) {
        return ConstraintModeMarshalling.toXml(value);
    }
}

