/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools;

import java.util.HashMap;
import java.util.Map;
import org.aspectj.weaver.tools.MatchingContext;

public class DefaultMatchingContext
implements MatchingContext {
    private Map contextMap = new HashMap();

    @Override
    public boolean hasContextBinding(String contextParameterName) {
        return this.contextMap.containsKey(contextParameterName);
    }

    @Override
    public Object getBinding(String contextParameterName) {
        return this.contextMap.get(contextParameterName);
    }

    public void addContextBinding(String name, Object value) {
        this.contextMap.put(name, value);
    }

    public void removeContextBinding(String name) {
        this.contextMap.remove(name);
    }
}

