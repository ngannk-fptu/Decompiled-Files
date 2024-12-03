/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElementDecl
 */
package com.sun.xml.bind.v2.model.annotation;

import com.sun.xml.bind.v2.model.annotation.Locatable;
import com.sun.xml.bind.v2.model.annotation.Quick;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlElementDecl;

final class XmlElementDeclQuick
extends Quick
implements XmlElementDecl {
    private final XmlElementDecl core;

    public XmlElementDeclQuick(Locatable upstream, XmlElementDecl core) {
        super(upstream);
        this.core = core;
    }

    @Override
    protected Annotation getAnnotation() {
        return this.core;
    }

    @Override
    protected Quick newInstance(Locatable upstream, Annotation core) {
        return new XmlElementDeclQuick(upstream, (XmlElementDecl)core);
    }

    public Class<XmlElementDecl> annotationType() {
        return XmlElementDecl.class;
    }

    public String name() {
        return this.core.name();
    }

    public Class scope() {
        return this.core.scope();
    }

    public String namespace() {
        return this.core.namespace();
    }

    public String defaultValue() {
        return this.core.defaultValue();
    }

    public String substitutionHeadNamespace() {
        return this.core.substitutionHeadNamespace();
    }

    public String substitutionHeadName() {
        return this.core.substitutionHeadName();
    }
}

