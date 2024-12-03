/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.bandana;

import java.io.Serializable;

public interface BandanaContext
extends Serializable {
    public BandanaContext getParentContext();

    public boolean hasParentContext();
}

