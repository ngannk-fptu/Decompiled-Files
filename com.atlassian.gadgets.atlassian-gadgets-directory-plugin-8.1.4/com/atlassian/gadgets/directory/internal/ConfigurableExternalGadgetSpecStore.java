/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.directory.spi.ExternalGadgetSpecId
 */
package com.atlassian.gadgets.directory.internal;

import com.atlassian.gadgets.directory.spi.ExternalGadgetSpecId;
import java.net.URI;

public interface ConfigurableExternalGadgetSpecStore {
    public void add(URI var1);

    public void remove(ExternalGadgetSpecId var1);
}

