/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetState
 */
package com.atlassian.gadgets.dashboard.spi;

import com.atlassian.gadgets.GadgetState;
import java.net.URI;

@Deprecated
public interface GadgetStateFactory {
    @Deprecated
    public GadgetState createGadgetState(URI var1);
}

