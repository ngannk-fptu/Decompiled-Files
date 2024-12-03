/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.cache;

import org.apache.xmlgraphics.image.loader.cache.TimeStampProvider;

public interface ExpirationPolicy {
    public boolean isExpired(TimeStampProvider var1, long var2);
}

