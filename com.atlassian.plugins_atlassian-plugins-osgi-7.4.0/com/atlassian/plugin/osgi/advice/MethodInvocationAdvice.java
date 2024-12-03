/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.PluginKeyStack
 *  net.bytebuddy.asm.Advice$FieldValue
 *  net.bytebuddy.asm.Advice$OnMethodEnter
 *  net.bytebuddy.asm.Advice$OnMethodExit
 */
package com.atlassian.plugin.osgi.advice;

import com.atlassian.plugin.util.PluginKeyStack;
import net.bytebuddy.asm.Advice;

public class MethodInvocationAdvice {
    @Advice.OnMethodEnter
    public static void onEnter(@Advice.FieldValue(value="pluginKey") String pluginKey) {
        PluginKeyStack.push((String)pluginKey);
    }

    @Advice.OnMethodExit(onThrowable=Throwable.class)
    public static void onExit() {
        PluginKeyStack.pop();
    }
}

