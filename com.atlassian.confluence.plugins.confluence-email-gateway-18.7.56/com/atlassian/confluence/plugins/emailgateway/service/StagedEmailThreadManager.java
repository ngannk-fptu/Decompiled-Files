/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.emailgateway.service;

import com.atlassian.confluence.plugins.emailgateway.api.StagedEmailThread;
import com.atlassian.confluence.plugins.emailgateway.api.StagedEmailThreadKey;
import java.util.Iterator;

public interface StagedEmailThreadManager
extends Iterable<StagedEmailThread> {
    public void storeStagedEmailThread(StagedEmailThread var1);

    public StagedEmailThread findStagedEmailThread(StagedEmailThreadKey var1);

    public void deleteStagedEmailThread(StagedEmailThreadKey var1);

    @Override
    public Iterator<StagedEmailThread> iterator();
}

