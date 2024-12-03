/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 */
package com.atlassian.soy.renderer;

import com.atlassian.annotations.PublicSpi;

@PublicSpi
public interface SoyDataMapper<I, O> {
    public String getName();

    public O convert(I var1);
}

