/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets.directory.spi;

import com.atlassian.gadgets.directory.spi.ExternalGadgetSpec;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpecId;
import java.net.URI;

public interface ExternalGadgetSpecStore {
    public Iterable<ExternalGadgetSpec> entries();

    public ExternalGadgetSpec add(URI var1);

    public void remove(ExternalGadgetSpecId var1);

    public boolean contains(URI var1);
}

