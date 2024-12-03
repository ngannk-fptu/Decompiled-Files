/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.spi.application;

import com.atlassian.annotations.PublicSpi;
import java.net.URI;
import javax.annotation.Nullable;

@PublicSpi
public interface IconizedType {
    @Nullable
    public URI getIconUri();
}

