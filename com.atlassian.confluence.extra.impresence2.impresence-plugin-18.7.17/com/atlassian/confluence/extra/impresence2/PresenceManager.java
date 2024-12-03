/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.impresence2;

import com.atlassian.confluence.extra.impresence2.reporter.PresenceReporter;
import java.util.Collection;

public interface PresenceManager {
    public PresenceReporter getReporter(String var1);

    public Collection<PresenceReporter> getReporters();
}

