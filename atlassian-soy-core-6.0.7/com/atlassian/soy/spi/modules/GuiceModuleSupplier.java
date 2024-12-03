/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  com.google.inject.Module
 */
package com.atlassian.soy.spi.modules;

import com.google.common.base.Supplier;
import com.google.inject.Module;

public interface GuiceModuleSupplier
extends Supplier<Iterable<Module>> {
    public Iterable<Module> get();
}

