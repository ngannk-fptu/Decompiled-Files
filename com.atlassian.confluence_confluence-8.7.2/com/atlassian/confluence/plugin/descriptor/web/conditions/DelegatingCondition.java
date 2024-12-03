/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.Map;

public class DelegatingCondition
implements Condition {
    private final Condition delegate;

    public DelegatingCondition(Condition delegate) {
        this.delegate = delegate;
    }

    public void init(Map map) throws PluginParseException {
        this.delegate.init(map);
    }

    public boolean shouldDisplay(Map map) {
        return this.delegate.shouldDisplay(map);
    }
}

