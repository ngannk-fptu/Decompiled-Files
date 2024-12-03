/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource;

import com.atlassian.plugin.webresource.util.TimeSpan;

public interface BigPipeConfiguration {
    public TimeSpan getDefaultBigPipeDeadline();

    public boolean getBigPipeDeadlineDisabled();
}

