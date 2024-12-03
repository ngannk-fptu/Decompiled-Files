/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.multibindings;

import com.google.inject.multibindings.Element;
import java.lang.annotation.Annotation;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class RealElement
implements Element {
    private static final AtomicInteger nextUniqueId = new AtomicInteger(1);
    private final int uniqueId = nextUniqueId.getAndIncrement();
    private final String setName;

    RealElement(String setName) {
        this.setName = setName;
    }

    @Override
    public String setName() {
        return this.setName;
    }

    @Override
    public int uniqueId() {
        return this.uniqueId;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Element.class;
    }

    @Override
    public String toString() {
        return "@" + Element.class.getName() + "(setName=" + this.setName + ",uniqueId=" + this.uniqueId + ")";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Element && ((Element)o).setName().equals(this.setName()) && ((Element)o).uniqueId() == this.uniqueId();
    }

    @Override
    public int hashCode() {
        return 127 * ("setName".hashCode() ^ this.setName.hashCode()) + 127 * ("uniqueId".hashCode() ^ this.uniqueId);
    }
}

