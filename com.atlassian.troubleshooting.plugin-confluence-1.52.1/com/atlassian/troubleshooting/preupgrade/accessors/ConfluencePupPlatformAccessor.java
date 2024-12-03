/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.preupgrade.accessors;

import com.atlassian.troubleshooting.preupgrade.accessors.PupPlatformAccessor;

public interface ConfluencePupPlatformAccessor
extends PupPlatformAccessor {
    public boolean isSynchronyStandalone();

    public String getVersion();
}

