/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.spi;

import java.io.Serializable;
import org.hibernate.boot.jaxb.Origin;

public class Binding<T>
implements Serializable {
    private final T root;
    private final Origin origin;

    public Binding(T root, Origin origin) {
        this.root = root;
        this.origin = origin;
    }

    public T getRoot() {
        return this.root;
    }

    public Origin getOrigin() {
        return this.origin;
    }
}

