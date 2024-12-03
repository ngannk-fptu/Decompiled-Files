/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.adapters.XmlAdapter
 */
package org.hibernate.boot.jaxb.hbm.spi;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import org.hibernate.boot.jaxb.hbm.internal.GenerationTimingConverter;
import org.hibernate.tuple.GenerationTiming;

public class Adapter7
extends XmlAdapter<String, GenerationTiming> {
    public GenerationTiming unmarshal(String value) {
        return GenerationTimingConverter.fromXml(value);
    }

    public String marshal(GenerationTiming value) {
        return GenerationTimingConverter.toXml(value);
    }
}

