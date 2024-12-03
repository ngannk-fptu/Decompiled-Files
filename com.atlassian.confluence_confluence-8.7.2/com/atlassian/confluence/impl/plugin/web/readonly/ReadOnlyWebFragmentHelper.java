/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.plugin.web.WebFragmentHelper
 */
package com.atlassian.confluence.impl.plugin.web.readonly;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.web.Condition;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.plugin.web.WebFragmentHelper;
import java.util.List;
import java.util.Map;

public class ReadOnlyWebFragmentHelper
implements WebFragmentHelper {
    private final WebFragmentHelper delegate;

    public ReadOnlyWebFragmentHelper(WebFragmentHelper delegate) {
        this.delegate = delegate;
    }

    public Condition loadCondition(String s, Plugin plugin) {
        throw new UnsupportedOperationException();
    }

    public ContextProvider loadContextProvider(String s, Plugin plugin) {
        throw new UnsupportedOperationException();
    }

    public String getI18nValue(String s, List<?> list, Map<String, Object> map) {
        return this.delegate.getI18nValue(s, list, map);
    }

    public String renderVelocityFragment(String s, Map<String, Object> map) {
        throw new UnsupportedOperationException();
    }
}

