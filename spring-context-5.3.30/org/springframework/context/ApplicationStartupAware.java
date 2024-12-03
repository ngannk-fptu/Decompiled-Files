/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.Aware
 *  org.springframework.core.metrics.ApplicationStartup
 */
package org.springframework.context;

import org.springframework.beans.factory.Aware;
import org.springframework.core.metrics.ApplicationStartup;

public interface ApplicationStartupAware
extends Aware {
    public void setApplicationStartup(ApplicationStartup var1);
}

