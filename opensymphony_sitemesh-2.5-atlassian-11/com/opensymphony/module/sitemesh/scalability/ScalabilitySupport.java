/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.scalability;

import com.opensymphony.module.sitemesh.scalability.outputlength.OutputLengthObserver;
import com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorage;

public interface ScalabilitySupport {
    public OutputLengthObserver getOutputLengthObserver();

    public SecondaryStorage getSecondaryStorage();

    public int getInitialBufferSize();

    public boolean isMaxOutputLengthExceededThrown();
}

