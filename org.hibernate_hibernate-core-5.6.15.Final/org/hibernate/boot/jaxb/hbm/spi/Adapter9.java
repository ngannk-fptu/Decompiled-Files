/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package org.hibernate.boot.jaxb.hbm.spi;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.hibernate.boot.jaxb.hbm.internal.OptimisticLockStyleConverter;
import org.hibernate.engine.OptimisticLockStyle;

public class Adapter9
extends XmlAdapter<String, OptimisticLockStyle> {
    public OptimisticLockStyle unmarshal(String value) {
        return OptimisticLockStyleConverter.fromXml(value);
    }

    public String marshal(OptimisticLockStyle value) {
        return OptimisticLockStyleConverter.toXml(value);
    }
}

