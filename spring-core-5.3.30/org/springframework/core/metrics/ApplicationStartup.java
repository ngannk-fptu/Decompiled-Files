/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.metrics;

import org.springframework.core.metrics.DefaultApplicationStartup;
import org.springframework.core.metrics.StartupStep;

public interface ApplicationStartup {
    public static final ApplicationStartup DEFAULT = new DefaultApplicationStartup();

    public StartupStep start(String var1);
}

