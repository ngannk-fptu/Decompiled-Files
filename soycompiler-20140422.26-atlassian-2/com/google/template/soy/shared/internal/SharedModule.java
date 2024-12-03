/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Lists
 *  com.google.inject.AbstractModule
 *  com.google.inject.Binder
 *  com.google.inject.BindingAnnotation
 *  com.google.inject.Module
 *  com.google.inject.Provides
 *  com.google.inject.Singleton
 *  com.google.inject.multibindings.Multibinder
 */
package com.google.template.soy.shared.internal;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.BindingAnnotation;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import com.google.template.soy.coredirectives.CoreDirectivesModule;
import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.internal.i18n.BidiGlobalDir;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.shared.internal.ApiCallScope;
import com.google.template.soy.shared.internal.GuiceSimpleScope;
import com.google.template.soy.shared.internal.ModuleUtils;
import com.google.template.soy.shared.restricted.ApiCallScopeBindingAnnotations;
import com.google.template.soy.shared.restricted.SoyFunction;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import com.google.template.soy.shared.restricted.SoyJavaPrintDirective;
import com.google.template.soy.shared.restricted.SoyJavaRuntimeFunction;
import com.google.template.soy.shared.restricted.SoyJavaRuntimePrintDirective;
import com.google.template.soy.shared.restricted.SoyPrintDirective;
import com.google.template.soy.types.SoyTypeProvider;
import com.google.template.soy.types.SoyTypeRegistry;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SharedModule
extends AbstractModule {
    protected void configure() {
        this.install((Module)new CoreDirectivesModule());
        Multibinder.newSetBinder((Binder)this.binder(), SoyFunction.class);
        Multibinder.newSetBinder((Binder)this.binder(), SoyPrintDirective.class);
        GuiceSimpleScope apiCallScope = new GuiceSimpleScope();
        this.bindScope(ApiCallScope.class, apiCallScope);
        this.bind(GuiceSimpleScope.class).annotatedWith(ApiCallScopeBindingAnnotations.ApiCall.class).toInstance((Object)apiCallScope);
        this.bind(Boolean.class).annotatedWith(ApiCallScopeBindingAnnotations.IsUsingIjData.class).toProvider(GuiceSimpleScope.getUnscopedProvider()).in(ApiCallScope.class);
        this.bind(SoyMsgBundle.class).toProvider(GuiceSimpleScope.getUnscopedProvider()).in(ApiCallScope.class);
        this.bind(String.class).annotatedWith(ApiCallScopeBindingAnnotations.LocaleString.class).toProvider(GuiceSimpleScope.getUnscopedProvider()).in(ApiCallScope.class);
        this.bind(BidiGlobalDir.class).toProvider(GuiceSimpleScope.getUnscopedProvider()).in(ApiCallScope.class);
        Multibinder.newSetBinder((Binder)this.binder(), SoyTypeProvider.class);
        this.bind(SoyTypeRegistry.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    Map<String, SoyFunction> provideSoyFunctionsMap(Set<SoyFunction> soyFunctionsSet) {
        ImmutableMap.Builder mapBuilder = ImmutableMap.builder();
        for (SoyFunction function : soyFunctionsSet) {
            mapBuilder.put((Object)function.getName(), (Object)function);
        }
        return mapBuilder.build();
    }

    @Provides
    @Singleton
    Map<String, SoyPrintDirective> provideSoyDirectivesMap(Set<SoyPrintDirective> soyDirectivesSet) {
        ImmutableMap.Builder mapBuilder = ImmutableMap.builder();
        for (SoyPrintDirective directive : soyDirectivesSet) {
            mapBuilder.put((Object)directive.getName(), (Object)directive);
        }
        return mapBuilder.build();
    }

    @Provides
    @Singleton
    @Shared
    Map<String, SoyJavaFunction> provideSoyJavaFunctionsMap(Set<SoyFunction> soyFunctionsSet) {
        return ModuleUtils.buildSpecificSoyFunctionsMapWithAdaptation(soyFunctionsSet, SoyJavaFunction.class, SoyJavaRuntimeFunction.class, new Function<SoyJavaRuntimeFunction, SoyJavaFunction>(){

            public SoyJavaFunction apply(SoyJavaRuntimeFunction input) {
                return new SoyJavaRuntimeFunctionAdapter(input);
            }
        });
    }

    @Provides
    @Singleton
    @Shared
    Map<String, SoyJavaPrintDirective> provideSoyJavaDirectivesMap(Set<SoyPrintDirective> soyDirectivesSet) {
        return ModuleUtils.buildSpecificSoyDirectivesMapWithAdaptation(soyDirectivesSet, SoyJavaPrintDirective.class, SoyJavaRuntimePrintDirective.class, new Function<SoyJavaRuntimePrintDirective, SoyJavaPrintDirective>(){

            public SoyJavaPrintDirective apply(SoyJavaRuntimePrintDirective input) {
                return new SoyJavaRuntimePrintDirectiveAdapter(input);
            }
        });
    }

    public boolean equals(Object other) {
        return other != null && ((Object)((Object)this)).getClass().equals(other.getClass());
    }

    public int hashCode() {
        return ((Object)((Object)this)).getClass().hashCode();
    }

    private static class SoyJavaRuntimePrintDirectiveAdapter
    implements SoyJavaPrintDirective {
        private final SoyJavaRuntimePrintDirective adaptee;

        public SoyJavaRuntimePrintDirectiveAdapter(SoyJavaRuntimePrintDirective adaptee) {
            this.adaptee = adaptee;
        }

        @Override
        public SoyValue applyForJava(SoyValue value, List<SoyValue> args) {
            SoyData castValue = (SoyData)value;
            ArrayList castArgs = Lists.newArrayListWithCapacity((int)args.size());
            for (SoyValue arg : args) {
                castArgs.add((SoyData)arg);
            }
            return this.adaptee.apply(castValue, castArgs);
        }

        @Override
        public String getName() {
            return this.adaptee.getName();
        }

        @Override
        public Set<Integer> getValidArgsSizes() {
            return this.adaptee.getValidArgsSizes();
        }

        @Override
        public boolean shouldCancelAutoescape() {
            return this.adaptee.shouldCancelAutoescape();
        }
    }

    private static class SoyJavaRuntimeFunctionAdapter
    implements SoyJavaFunction {
        private final SoyJavaRuntimeFunction adaptee;

        public SoyJavaRuntimeFunctionAdapter(SoyJavaRuntimeFunction adaptee) {
            this.adaptee = adaptee;
        }

        @Override
        public SoyValue computeForJava(List<SoyValue> args) {
            ArrayList castArgs = Lists.newArrayListWithCapacity((int)args.size());
            for (SoyValue arg : args) {
                castArgs.add((SoyData)arg);
            }
            return this.adaptee.compute(castArgs);
        }

        @Override
        public String getName() {
            return this.adaptee.getName();
        }

        @Override
        public Set<Integer> getValidArgsSizes() {
            return this.adaptee.getValidArgsSizes();
        }
    }

    @BindingAnnotation
    @Target(value={ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface Shared {
    }
}

