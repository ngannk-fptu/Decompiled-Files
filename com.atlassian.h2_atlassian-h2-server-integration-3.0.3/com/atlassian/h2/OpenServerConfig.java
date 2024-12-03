/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.h2;

import com.atlassian.h2.AbstractServerConfig;
import java.util.List;
import javax.annotation.Nonnull;

public class OpenServerConfig
extends AbstractServerConfig {
    @Override
    @Nonnull
    protected List<String> getOptions() {
        List<String> options = super.getOptions();
        if (Boolean.getBoolean("atlassian.dev.mode")) {
            options.add("-tcpAllowOthers");
        }
        return options;
    }
}

