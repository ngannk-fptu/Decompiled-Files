/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 */
package com.atlassian.soy.renderer;

import com.atlassian.annotations.PublicSpi;
import com.atlassian.soy.renderer.SoyFunction;

@PublicSpi
public interface SoyServerFunction<T>
extends SoyFunction {
    public T apply(Object ... var1);
}

