/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.scalability;

import com.opensymphony.module.sitemesh.scalability.ScalabilitySupport;
import com.opensymphony.module.sitemesh.scalability.outputlength.NoopOutputLengthObserver;
import com.opensymphony.module.sitemesh.scalability.outputlength.OutputLengthObserver;
import com.opensymphony.module.sitemesh.scalability.secondarystorage.NoopSecondaryStorage;
import com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorage;

public class NoopScalabilitySupport
implements ScalabilitySupport {
    private OutputLengthObserver outputLengthObserver = new NoopOutputLengthObserver();
    private SecondaryStorage secondaryStorage = new NoopSecondaryStorage();

    public OutputLengthObserver getOutputLengthObserver() {
        return this.outputLengthObserver;
    }

    public SecondaryStorage getSecondaryStorage() {
        return this.secondaryStorage;
    }

    public int getInitialBufferSize() {
        return 8192;
    }

    public boolean isMaxOutputLengthExceededThrown() {
        return false;
    }
}

