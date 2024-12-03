/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.plugins.osgi.javaconfig.conditions;

import com.atlassian.annotations.PublicApi;
import com.atlassian.plugins.osgi.javaconfig.conditions.AbstractSystemPropertyCondition;

@PublicApi
public final class DevModeOnly
extends AbstractSystemPropertyCondition {
    public static final String ATLASSIAN_DEV_MODE_PROP = "atlassian.dev.mode";

    public DevModeOnly() {
        super(ATLASSIAN_DEV_MODE_PROP, "true");
    }
}

