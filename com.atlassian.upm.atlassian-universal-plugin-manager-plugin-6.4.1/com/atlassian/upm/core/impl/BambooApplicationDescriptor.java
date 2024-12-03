/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bamboo.buildqueue.manager.AgentManager
 *  com.google.common.base.Supplier
 */
package com.atlassian.upm.core.impl;

import com.atlassian.bamboo.buildqueue.manager.AgentManager;
import com.atlassian.upm.core.impl.AbstractApplicationDescriptor;
import com.atlassian.upm.core.impl.UpmAppManager;
import com.atlassian.upm.core.test.rest.resources.ActiveEditionResource;
import com.google.common.base.Supplier;

public class BambooApplicationDescriptor
extends AbstractApplicationDescriptor {
    public BambooApplicationDescriptor(UpmAppManager upmAppManager, AgentManager agentManager) {
        super(upmAppManager, (Supplier<Integer>)((Supplier)() -> agentManager.getAllRemoteAgents().size()));
    }

    @Override
    public int getActiveUserCount() {
        return 0;
    }

    @Override
    public int getActiveEditionCount() {
        return ActiveEditionResource.getActiveEdition().getOrElse(super.getActiveUserCount());
    }
}

