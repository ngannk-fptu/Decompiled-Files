/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.soy.renderer.SoyClientFunction
 *  com.atlassian.soy.renderer.SoyFunction
 *  com.atlassian.soy.renderer.SoyServerFunction
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableSet
 *  com.google.inject.AbstractModule
 *  com.google.inject.Binder
 *  com.google.inject.multibindings.Multibinder
 *  com.google.template.soy.shared.restricted.SoyFunction
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.soy.impl.modules;

import com.atlassian.soy.impl.modules.CompositeFunctionAdaptor;
import com.atlassian.soy.impl.modules.CoreFunctionsModule;
import com.atlassian.soy.impl.modules.SoyJavaFunctionAdapter;
import com.atlassian.soy.impl.modules.SoyJsSrcFunctionAdapter;
import com.atlassian.soy.renderer.SoyClientFunction;
import com.atlassian.soy.renderer.SoyFunction;
import com.atlassian.soy.renderer.SoyServerFunction;
import com.atlassian.soy.spi.functions.SoyFunctionSupplier;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.multibindings.Multibinder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CustomFunctionsModule
extends AbstractModule {
    @VisibleForTesting
    static final Set<String> BUILT_IN_FUNCTION_NAMES = ImmutableSet.of((Object)"augmentMap", (Object)"ceiling", (Object)"bidiEndEdge", (Object)"bidiDirAttr", (Object)"bidiGlobalDir", (Object)"bidiMark", (Object[])new String[]{"bidiMarkAfter", "bidiStartEdge", "bidiTextDir", "floor", "isNonnull", "keys", "length", "max", "min", "randomInt", "round", "strContains", "strIndexOf", "strLen", "strSub"});
    private static final Logger log = LoggerFactory.getLogger(CustomFunctionsModule.class);
    private final SoyFunctionSupplier soyFunctionSupplier;

    public CustomFunctionsModule(SoyFunctionSupplier soyFunctionSupplier) {
        this.soyFunctionSupplier = soyFunctionSupplier;
    }

    public void configure() {
        HashMap<String, SoyFunctionHolder> holders = new HashMap<String, SoyFunctionHolder>();
        Iterator iterator = this.soyFunctionSupplier.get().iterator();
        while (iterator.hasNext()) {
            SoyFunction function = (SoyFunction)iterator.next();
            String name = function.getName();
            if (CustomFunctionsModule.isProvidedFunctionName(name)) {
                log.info("Ignoring {} as it is attempting to register with name {} but that soy function is already built-in", function.getClass(), (Object)name);
                continue;
            }
            SoyFunctionHolder holder = (SoyFunctionHolder)holders.get(name);
            if (holder == null) {
                holder = new SoyFunctionHolder();
                holders.put(name, holder);
            }
            if (holder.offer(function)) continue;
            log.info("Ignoring {} as as there is already a function with name {} registered", function.getClass(), (Object)name);
        }
        Multibinder binder = Multibinder.newSetBinder((Binder)this.binder(), com.google.template.soy.shared.restricted.SoyFunction.class);
        for (SoyFunctionHolder holder : holders.values()) {
            binder.addBinding().toInstance((Object)holder.createAdaptor());
        }
    }

    @VisibleForTesting
    static boolean isProvidedFunctionName(String name) {
        return CoreFunctionsModule.CORE_FUNCTION_NAMES.contains(name) || BUILT_IN_FUNCTION_NAMES.contains(name);
    }

    static class SoyFunctionHolder {
        private SoyServerFunction<?> serverFunction;
        private SoyClientFunction clientFunction;

        SoyFunctionHolder() {
        }

        public boolean offer(SoyFunction function) {
            boolean accepted = false;
            if (function instanceof SoyServerFunction && this.serverFunction == null) {
                this.serverFunction = (SoyServerFunction)function;
                accepted = true;
            }
            if (function instanceof SoyClientFunction && this.clientFunction == null) {
                this.clientFunction = (SoyClientFunction)function;
                accepted = true;
            }
            return accepted;
        }

        public com.google.template.soy.shared.restricted.SoyFunction createAdaptor() {
            if (this.serverFunction != null) {
                if (this.clientFunction != null) {
                    return new CompositeFunctionAdaptor(this.serverFunction, this.clientFunction);
                }
                return new SoyJavaFunctionAdapter(this.serverFunction);
            }
            if (this.clientFunction != null) {
                return new SoyJsSrcFunctionAdapter(this.clientFunction);
            }
            throw new IllegalStateException("No soy function offered to holder");
        }
    }
}

