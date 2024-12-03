/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.bytebuddy.ByteBuddy
 *  net.bytebuddy.asm.Advice
 *  net.bytebuddy.asm.AsmVisitorWrapper
 *  net.bytebuddy.description.modifier.ModifierContributor$ForField
 *  net.bytebuddy.description.type.TypeDescription
 *  net.bytebuddy.dynamic.ClassFileLocator
 *  net.bytebuddy.dynamic.ClassFileLocator$Simple
 *  net.bytebuddy.matcher.ElementMatcher
 *  net.bytebuddy.matcher.ElementMatchers
 *  net.bytebuddy.pool.TypePool
 *  net.bytebuddy.pool.TypePool$Default
 *  org.osgi.framework.hooks.weaving.WeavingHook
 *  org.osgi.framework.hooks.weaving.WovenClass
 */
package com.atlassian.plugin.osgi.container.felix;

import com.atlassian.plugin.osgi.advice.ConstructorAdvice;
import com.atlassian.plugin.osgi.advice.MethodInvocationAdvice;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.modifier.ModifierContributor;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;

public class PluginKeyWeaver
implements WeavingHook {
    public void weave(WovenClass wovenClass) {
        String wovenClassName = wovenClass.getClassName();
        if (wovenClassName.equals("org.eclipse.gemini.blueprint.service.importer.support.LocalBundleContextAdvice")) {
            TypePool typePool = TypePool.Default.of((ClassLoader)wovenClass.getBundleWiring().getClassLoader());
            TypeDescription def = typePool.describe(wovenClassName).resolve();
            ClassFileLocator locator = ClassFileLocator.Simple.of((String)wovenClassName, (byte[])wovenClass.getBytes());
            byte[] target = new ByteBuddy().rebase(def, locator).defineField("pluginKey", String.class, new ModifierContributor.ForField[0]).visit((AsmVisitorWrapper)Advice.to(ConstructorAdvice.class).on((ElementMatcher)ElementMatchers.isConstructor())).visit((AsmVisitorWrapper)Advice.to(MethodInvocationAdvice.class).on((ElementMatcher)ElementMatchers.named((String)"invoke"))).make().getBytes();
            wovenClass.setBytes(target);
            wovenClass.getDynamicImports().add("com.atlassian.plugin.util");
            wovenClass.getDynamicImports().add("com.atlassian.plugin.osgi.util");
            wovenClass.getDynamicImports().add("com.atlassian.plugin.osgi.container.felix");
        }
    }
}

