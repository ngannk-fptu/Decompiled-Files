/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlNs
 *  javax.xml.bind.annotation.XmlNsForm
 *  javax.xml.bind.annotation.XmlSchema
 */
package com.sun.xml.bind.v2.model.annotation;

import com.sun.xml.bind.v2.model.annotation.Locatable;
import com.sun.xml.bind.v2.model.annotation.Quick;
import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;

final class XmlSchemaQuick
extends Quick
implements XmlSchema {
    private final XmlSchema core;

    public XmlSchemaQuick(Locatable upstream, XmlSchema core) {
        super(upstream);
        this.core = core;
    }

    @Override
    protected Annotation getAnnotation() {
        return this.core;
    }

    @Override
    protected Quick newInstance(Locatable upstream, Annotation core) {
        return new XmlSchemaQuick(upstream, (XmlSchema)core);
    }

    public Class<XmlSchema> annotationType() {
        return XmlSchema.class;
    }

    public String location() {
        return this.core.location();
    }

    public String namespace() {
        return this.core.namespace();
    }

    public XmlNs[] xmlns() {
        return this.core.xmlns();
    }

    public XmlNsForm elementFormDefault() {
        return this.core.elementFormDefault();
    }

    public XmlNsForm attributeFormDefault() {
        return this.core.attributeFormDefault();
    }
}

