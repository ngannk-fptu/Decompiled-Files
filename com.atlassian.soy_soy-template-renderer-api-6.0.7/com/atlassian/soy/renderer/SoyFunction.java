/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 */
package com.atlassian.soy.renderer;

import com.atlassian.annotations.PublicSpi;
import java.util.Set;

@PublicSpi
public interface SoyFunction {
    public String getName();

    public Set<Integer> validArgSizes();
}

