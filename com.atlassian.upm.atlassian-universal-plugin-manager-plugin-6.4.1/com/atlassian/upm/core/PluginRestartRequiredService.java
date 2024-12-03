/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Change;
import com.atlassian.upm.core.Plugin;

public interface PluginRestartRequiredService {
    public Iterable<Change> getRestartRequiredChanges();

    public boolean hasChangesRequiringRestart();

    public Option<Change> getRestartRequiredChange(Plugin var1);

    public void revertRestartRequiredChange(Plugin var1);
}

