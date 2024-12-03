/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.cache.spi.access.SoftLock
 */
package com.hazelcast.hibernate.serialization;

import com.hazelcast.hibernate.serialization.ExpiryMarker;
import org.hibernate.cache.spi.access.SoftLock;

public class MarkerWrapper
implements SoftLock {
    private final ExpiryMarker marker;

    public MarkerWrapper(ExpiryMarker marker) {
        this.marker = marker;
    }

    public ExpiryMarker getMarker() {
        return this.marker;
    }
}

