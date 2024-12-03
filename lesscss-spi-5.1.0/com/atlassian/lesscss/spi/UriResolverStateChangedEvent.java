/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lesscss.spi;

import java.net.URI;
import java.util.EventObject;

public abstract class UriResolverStateChangedEvent
extends EventObject {
    public UriResolverStateChangedEvent(Object source) {
        super(source);
    }

    public abstract boolean hasChanged(URI var1);
}

