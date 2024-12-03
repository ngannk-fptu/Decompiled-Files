/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package org.hibernate.boot.jaxb.hbm.spi;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.hibernate.boot.jaxb.hbm.internal.ExecuteUpdateResultCheckStyleConverter;
import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;

public class Adapter3
extends XmlAdapter<String, ExecuteUpdateResultCheckStyle> {
    public ExecuteUpdateResultCheckStyle unmarshal(String value) {
        return ExecuteUpdateResultCheckStyleConverter.fromXml(value);
    }

    public String marshal(ExecuteUpdateResultCheckStyle value) {
        return ExecuteUpdateResultCheckStyleConverter.toXml(value);
    }
}

