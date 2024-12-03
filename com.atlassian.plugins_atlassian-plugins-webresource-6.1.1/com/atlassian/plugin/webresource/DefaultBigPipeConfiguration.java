/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.webresource;

import com.atlassian.plugin.webresource.BigPipeConfiguration;
import com.atlassian.plugin.webresource.util.TimeSpan;
import java.util.concurrent.TimeUnit;

public class DefaultBigPipeConfiguration
implements BigPipeConfiguration {
    public static final String BIGPIPE_DEADLINE_SECONDS = new String("plugin.webresource.bigpipe.deadline.seconds");
    public static final String BIGPIPE_DEADLINE_DISABLED = new String("plugin.webresource.bigpipe.deadline.disabled");

    @Override
    public TimeSpan getDefaultBigPipeDeadline() {
        int secs = Integer.getInteger(BIGPIPE_DEADLINE_SECONDS, 30);
        return new TimeSpan(secs, TimeUnit.SECONDS);
    }

    @Override
    public boolean getBigPipeDeadlineDisabled() {
        return Boolean.getBoolean(BIGPIPE_DEADLINE_DISABLED);
    }
}

