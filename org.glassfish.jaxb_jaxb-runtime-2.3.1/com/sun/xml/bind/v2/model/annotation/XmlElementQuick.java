/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 */
package com.sun.xml.bind.v2.model.annotation;

import com.sun.xml.bind.v2.model.annotation.Locatable;
import com.sun.xml.bind.v2.model.annotation.Quick;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlElement;

final class XmlElementQuick
extends Quick
implements XmlElement {
    private final XmlElement core;

    public XmlElementQuick(Locatable upstream, XmlElement core) {
        super(upstream);
        this.core = core;
    }

    @Override
    protected Annotation getAnnotation() {
        return this.core;
    }

    @Override
    protected Quick newInstance(Locatable upstream, Annotation core) {
        return new XmlElementQuick(upstream, (XmlElement)core);
    }

    public Class<XmlElement> annotationType() {
        return XmlElement.class;
    }

    public String name() {
        return this.core.name();
    }

    public Class type() {
        return this.core.type();
    }

    public String namespace() {
        return this.core.namespace();
    }

    public String defaultValue() {
        return this.core.defaultValue();
    }

    public boolean required() {
        return this.core.required();
    }

    public boolean nillable() {
        return this.core.nillable();
    }
}

