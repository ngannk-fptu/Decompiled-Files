/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.utils.process.ExternalProcess
 */
package com.atlassian.confluence.plugins.synchrony.bootstrap;

import com.atlassian.confluence.plugins.synchrony.bootstrap.DelegatingExternalProcess;
import com.atlassian.utils.process.ExternalProcess;

class NonIdlingExternalProcess
extends DelegatingExternalProcess {
    NonIdlingExternalProcess(ExternalProcess externalProcess) {
        super(externalProcess);
    }

    @Override
    public boolean isTimedOut() {
        return false;
    }
}

