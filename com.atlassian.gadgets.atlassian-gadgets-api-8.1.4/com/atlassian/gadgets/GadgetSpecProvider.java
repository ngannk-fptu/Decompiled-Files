/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets;

import java.net.URI;

public interface GadgetSpecProvider {
    public Iterable<URI> entries();

    public boolean contains(URI var1);
}

