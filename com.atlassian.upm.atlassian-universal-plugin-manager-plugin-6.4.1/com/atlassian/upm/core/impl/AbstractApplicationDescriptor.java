/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.base.Supplier
 */
package com.atlassian.upm.core.impl;

import com.atlassian.upm.core.HostApplicationDescriptor;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.core.test.rest.resources.ActiveEditionResource;
import com.google.common.base.Function;
import com.google.common.base.Supplier;
import java.util.Objects;

public class AbstractApplicationDescriptor
implements HostApplicationDescriptor {
    private final UpmAppManager upmAppManager;
    private final Supplier<Integer> activeUserCount;
    private static final Function<UpmAppManager.ApplicationInfo, Integer> numberOfActiveUsers = app -> app.numberOfActiveUsers;

    public AbstractApplicationDescriptor(UpmAppManager upmAppManager, Supplier<Integer> activeUserCount) {
        this.upmAppManager = Objects.requireNonNull(upmAppManager, "upmAppManager");
        this.activeUserCount = Objects.requireNonNull(activeUserCount, "activeUserCount");
    }

    @Override
    public int getActiveEditionCount() {
        return this.getActiveUserCount();
    }

    @Override
    public int getActiveUserCount() {
        return ActiveEditionResource.getActiveEdition().orElse(this.upmAppManager.getApplicationWithMostActiveUsers().map(numberOfActiveUsers)).getOrElse(this.activeUserCount);
    }
}

