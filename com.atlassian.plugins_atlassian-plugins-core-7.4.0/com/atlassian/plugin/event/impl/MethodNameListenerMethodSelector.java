/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.plugin.event.impl;

import com.atlassian.plugin.event.impl.ListenerMethodSelector;
import java.lang.reflect.Method;
import org.apache.commons.lang3.StringUtils;

public class MethodNameListenerMethodSelector
implements ListenerMethodSelector {
    private final String methodName;

    public MethodNameListenerMethodSelector() {
        this("channel");
    }

    public MethodNameListenerMethodSelector(String s) {
        if (StringUtils.isEmpty((CharSequence)s)) {
            throw new IllegalArgumentException("Method name for the listener must be a valid method name");
        }
        this.methodName = s;
    }

    @Override
    public boolean isListenerMethod(Method method) {
        if (method == null) {
            throw new IllegalArgumentException("Method cannot be null");
        }
        return this.methodName.equals(method.getName());
    }
}

