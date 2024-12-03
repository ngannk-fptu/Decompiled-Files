/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package com.atlassian.confluence.plugins.emailgateway.service;

import com.atlassian.confluence.plugins.emailgateway.api.StagedEmailThread;
import com.atlassian.confluence.plugins.emailgateway.api.StagedEmailThreadKey;
import com.atlassian.confluence.plugins.emailgateway.service.StagedEmailThreadManager;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;

public class InMemoryStagedEmailThreadManager
implements StagedEmailThreadManager {
    private final Map<StagedEmailThreadKey, StagedEmailThread> stagedEmailThreadMap = Maps.newConcurrentMap();

    @Override
    public void storeStagedEmailThread(StagedEmailThread stagedEmailThread) {
        this.stagedEmailThreadMap.put(stagedEmailThread.getKey(), stagedEmailThread);
    }

    @Override
    public StagedEmailThread findStagedEmailThread(StagedEmailThreadKey stagedEmailThreadKey) {
        return this.stagedEmailThreadMap.get(stagedEmailThreadKey);
    }

    @Override
    public void deleteStagedEmailThread(StagedEmailThreadKey stagedEmailThreadKey) {
        this.stagedEmailThreadMap.remove(stagedEmailThreadKey);
    }

    @Override
    public Iterator<StagedEmailThread> iterator() {
        return this.stagedEmailThreadMap.values().iterator();
    }
}

