/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 */
package com.atlassian.upm.core.impl;

import com.atlassian.upm.core.impl.AbstractApplicationDescriptor;
import com.atlassian.upm.core.impl.ConfUserAccessor;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.google.common.base.Supplier;

public class ConfluenceApplicationDescriptor
extends AbstractApplicationDescriptor {
    public ConfluenceApplicationDescriptor(UpmAppManager upmAppManager, ConfUserAccessor userAccessor) {
        super(upmAppManager, (Supplier<Integer>)((Supplier)userAccessor::getActiveUserCount));
    }
}

