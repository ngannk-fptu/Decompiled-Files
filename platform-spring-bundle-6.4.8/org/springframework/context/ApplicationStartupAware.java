/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context;

import org.springframework.beans.factory.Aware;
import org.springframework.core.metrics.ApplicationStartup;

public interface ApplicationStartupAware
extends Aware {
    public void setApplicationStartup(ApplicationStartup var1);
}

