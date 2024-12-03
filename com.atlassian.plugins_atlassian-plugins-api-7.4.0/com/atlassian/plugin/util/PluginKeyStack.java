/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.util;

import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public final class PluginKeyStack {
    private static final ThreadLocal<Deque<String>> PLUGIN_KEY_STACK = new ThreadLocal();

    private PluginKeyStack() {
    }

    public static void push(String pluginKey) {
        Deque<String> stack = PLUGIN_KEY_STACK.get();
        if (stack == null) {
            stack = new LinkedList<String>();
            PLUGIN_KEY_STACK.set(stack);
        }
        stack.push(pluginKey);
    }

    public static String pop() {
        Deque<String> stack = PLUGIN_KEY_STACK.get();
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        try {
            String string = stack.pop();
            return string;
        }
        finally {
            if (stack.isEmpty()) {
                PLUGIN_KEY_STACK.remove();
            }
        }
    }

    public static Set<String> getPluginKeys() {
        Deque<String> stack = PLUGIN_KEY_STACK.get();
        if (stack == null || stack.isEmpty()) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(new HashSet<String>(stack));
    }

    public static String getFirstPluginKey() {
        Deque<String> stack = PLUGIN_KEY_STACK.get();
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        return stack.getLast();
    }
}

