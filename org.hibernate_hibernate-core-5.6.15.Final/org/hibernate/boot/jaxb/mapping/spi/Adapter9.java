/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.ParameterMode
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package org.hibernate.boot.jaxb.mapping.spi;

import javax.persistence.ParameterMode;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.hibernate.boot.jaxb.mapping.internal.ParameterModeMarshalling;

public class Adapter9
extends XmlAdapter<String, ParameterMode> {
    public ParameterMode unmarshal(String value) {
        return ParameterModeMarshalling.fromXml(value);
    }

    public String marshal(ParameterMode value) {
        return ParameterModeMarshalling.toXml(value);
    }
}

