/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http;

import java.util.Set;

public interface EntityDetails {
    public long getContentLength();

    public String getContentType();

    public String getContentEncoding();

    public boolean isChunked();

    public Set<String> getTrailerNames();
}

