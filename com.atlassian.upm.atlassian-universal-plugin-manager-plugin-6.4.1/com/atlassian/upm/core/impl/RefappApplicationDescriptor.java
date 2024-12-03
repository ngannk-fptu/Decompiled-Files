/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 */
package com.atlassian.upm.core.impl;

import com.atlassian.upm.core.impl.AbstractApplicationDescriptor;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.google.common.base.Supplier;

public class RefappApplicationDescriptor
extends AbstractApplicationDescriptor {
    public RefappApplicationDescriptor(UpmAppManager upmAppManager) {
        super(upmAppManager, (Supplier<Integer>)((Supplier)() -> 1));
    }
}

