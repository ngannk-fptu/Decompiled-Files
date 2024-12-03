/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.spi;

import org.hibernate.boot.jaxb.Origin;
import org.hibernate.boot.jaxb.spi.Binder;
import org.hibernate.boot.jaxb.spi.Binding;

public abstract class XmlSource {
    private final Origin origin;

    protected XmlSource(Origin origin) {
        this.origin = origin;
    }

    public Origin getOrigin() {
        return this.origin;
    }

    public abstract Binding doBind(Binder var1);
}

