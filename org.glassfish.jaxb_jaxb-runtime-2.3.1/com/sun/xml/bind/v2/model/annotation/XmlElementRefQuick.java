/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElementRef
 */
package com.sun.xml.bind.v2.model.annotation;

import com.sun.xml.bind.v2.model.annotation.Locatable;
import com.sun.xml.bind.v2.model.annotation.Quick;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlElementRef;

final class XmlElementRefQuick
extends Quick
implements XmlElementRef {
    private final XmlElementRef core;

    public XmlElementRefQuick(Locatable upstream, XmlElementRef core) {
        super(upstream);
        this.core = core;
    }

    @Override
    protected Annotation getAnnotation() {
        return this.core;
    }

    @Override
    protected Quick newInstance(Locatable upstream, Annotation core) {
        return new XmlElementRefQuick(upstream, (XmlElementRef)core);
    }

    public Class<XmlElementRef> annotationType() {
        return XmlElementRef.class;
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

    public boolean required() {
        return this.core.required();
    }
}

