/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crucible.spi.services.UserService
 *  com.google.common.base.Supplier
 */
package com.atlassian.upm.core.impl;

import com.atlassian.crucible.spi.services.UserService;
import com.atlassian.upm.core.impl.AbstractApplicationDescriptor;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.google.common.base.Supplier;

public class FecruApplicationDescriptor
extends AbstractApplicationDescriptor {
    public FecruApplicationDescriptor(UpmAppManager upmAppManager, UserService userService) {
        super(upmAppManager, (Supplier<Integer>)((Supplier)() -> {
            try {
                return userService.getActiveUserCount();
            }
            catch (Exception e) {
                return 0;
            }
        }));
    }
}

