/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.opensymphony.module.sitemesh.scalability;

import com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorage;
import javax.servlet.http.HttpServletRequest;

public interface ScalabilitySupportFactory {
    public int getInitialBufferSize();

    public long getMaximumOutputLength();

    public int getMaximumOutputExceededHttpCode();

    public boolean isMaxOutputLengthExceededThrown();

    public long getSecondaryStorageLimit();

    public boolean hasCustomSecondaryStorage();

    public SecondaryStorage getSecondaryStorage(HttpServletRequest var1);
}

