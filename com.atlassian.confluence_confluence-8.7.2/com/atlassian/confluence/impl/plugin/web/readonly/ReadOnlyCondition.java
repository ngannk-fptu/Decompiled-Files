/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.confluence.impl.plugin.web.readonly;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.Map;

public class ReadOnlyCondition
implements Condition {
    private final Condition delegate;

    public ReadOnlyCondition(Condition delegate) {
        this.delegate = delegate;
    }

    public void init(Map<String, String> map) throws PluginParseException {
        throw new UnsupportedOperationException();
    }

    public boolean shouldDisplay(Map<String, Object> map) {
        return this.delegate.shouldDisplay(map);
    }
}

