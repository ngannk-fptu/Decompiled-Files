/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlValue
 */
package com.sun.xml.bind.v2.model.annotation;

import com.sun.xml.bind.v2.model.annotation.Locatable;
import com.sun.xml.bind.v2.model.annotation.Quick;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlValue;

final class XmlValueQuick
extends Quick
implements XmlValue {
    private final XmlValue core;

    public XmlValueQuick(Locatable upstream, XmlValue core) {
        super(upstream);
        this.core = core;
    }

    @Override
    protected Annotation getAnnotation() {
        return this.core;
    }

    @Override
    protected Quick newInstance(Locatable upstream, Annotation core) {
        return new XmlValueQuick(upstream, (XmlValue)core);
    }

    public Class<XmlValue> annotationType() {
        return XmlValue.class;
    }
}

