/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.user.crowd;

import java.io.Serializable;

public class CachedOption<T>
implements Serializable {
    private final Availability availabilty;
    private final T object;

    public CachedOption(Availability availabilty) {
        this.availabilty = availabilty;
        this.object = null;
    }

    public CachedOption(Availability availabilty, T object) {
        this.availabilty = availabilty;
        this.object = object;
    }

    public Availability getAvailabilty() {
        return this.availabilty;
    }

    public T getObject() {
        return this.object;
    }

    static enum Availability {
        ONE,
        NONE;

    }
}

