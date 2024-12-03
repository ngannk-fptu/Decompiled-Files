/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 */
package com.atlassian.confluence.impl.plugin.web.readonly;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import java.util.Map;

public class ReadOnlyContextProvider
implements ContextProvider {
    private final ContextProvider delegate;

    public ReadOnlyContextProvider(ContextProvider delegate) {
        this.delegate = delegate;
    }

    public void init(Map<String, String> map) throws PluginParseException {
        throw new UnsupportedOperationException();
    }

    public Map<String, Object> getContextMap(Map<String, Object> map) {
        return this.delegate.getContextMap(map);
    }
}

