/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.bytebuddy.asm.Advice$FieldValue
 *  net.bytebuddy.asm.Advice$OnMethodExit
 *  org.osgi.framework.BundleContext
 */
package com.atlassian.plugin.osgi.advice;

import com.atlassian.plugin.osgi.util.OsgiHeaderUtil;
import net.bytebuddy.asm.Advice;
import org.osgi.framework.BundleContext;

public class ConstructorAdvice {
    @Advice.OnMethodExit
    public static void afterConstructor(@Advice.FieldValue(value="pluginKey", readOnly=false) String pluginKey, @Advice.FieldValue(value="context") BundleContext context) {
        if (context != null) {
            pluginKey = OsgiHeaderUtil.getPluginKey(context.getBundle());
        }
    }
}

