/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.analytics.jobs.api;

import com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEvent;
import java.util.concurrent.Callable;

public interface PeriodicEventSupplier
extends Callable<PeriodicEvent> {
}

