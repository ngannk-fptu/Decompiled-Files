/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.Lists
 *  com.google.inject.AbstractModule
 *  com.google.inject.BindingAnnotation
 *  com.google.inject.Module
 *  com.google.inject.Provides
 *  com.google.inject.Singleton
 *  com.google.inject.assistedinject.FactoryModuleBuilder
 */
package com.google.template.soy.tofu.internal;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.shared.internal.ModuleUtils;
import com.google.template.soy.shared.internal.SharedModule;
import com.google.template.soy.shared.restricted.SoyFunction;
import com.google.template.soy.shared.restricted.SoyJavaFunction;
import com.google.template.soy.shared.restricted.SoyJavaPrintDirective;
import com.google.template.soy.shared.restricted.SoyPrintDirective;
import com.google.template.soy.sharedpasses.SharedPassesModule;
import com.google.template.soy.tofu.internal.BaseTofu;
import com.google.template.soy.tofu.internal.TofuEvalVisitorFactory;
import com.google.template.soy.tofu.internal.TofuRenderVisitorFactory;
import com.google.template.soy.tofu.restricted.SoyTofuFunction;
import com.google.template.soy.tofu.restricted.SoyTofuPrintDirective;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TofuModule
extends AbstractModule {
    protected void configure() {
        this.install((Module)new SharedModule());
        this.install((Module)new SharedPassesModule());
        this.bind(TofuEvalVisitorFactory.class);
        this.bind(TofuRenderVisitorFactory.class);
        this.install(new FactoryModuleBuilder().build(BaseTofu.BaseTofuFactory.class));
    }

    @Provides
    @Singleton
    @Tofu
    Map<String, SoyJavaFunction> provideSoyJavaFunctionsMap(Set<SoyFunction> soyFunctionsSet) {
        return ModuleUtils.buildSpecificSoyFunctionsMapWithAdaptation(soyFunctionsSet, SoyJavaFunction.class, SoyTofuFunction.class, new Function<SoyTofuFunction, SoyJavaFunction>(){

            public SoyJavaFunction apply(SoyTofuFunction input) {
                return new SoyTofuFunctionAdapter(input);
            }
        });
    }

    @Provides
    @Singleton
    @Tofu
    Map<String, SoyJavaPrintDirective> provideSoyJavaDirectivesMap(Set<SoyPrintDirective> soyDirectivesSet) {
        return ModuleUtils.buildSpecificSoyDirectivesMapWithAdaptation(soyDirectivesSet, SoyJavaPrintDirective.class, SoyTofuPrintDirective.class, new Function<SoyTofuPrintDirective, SoyJavaPrintDirective>(){

            public SoyJavaPrintDirective apply(SoyTofuPrintDirective input) {
                return new SoyTofuPrintDirectiveAdapter(input);
            }
        });
    }

    private static class SoyTofuPrintDirectiveAdapter
    implements SoyJavaPrintDirective {
        private final SoyTofuPrintDirective adaptee;

        public SoyTofuPrintDirectiveAdapter(SoyTofuPrintDirective adaptee) {
            this.adaptee = adaptee;
        }

        @Override
        public SoyValue applyForJava(SoyValue value, List<SoyValue> args) {
            SoyData castValue = (SoyData)value;
            ArrayList castArgs = Lists.newArrayListWithCapacity((int)args.size());
            for (SoyValue arg : args) {
                castArgs.add((SoyData)arg);
            }
            return this.adaptee.applyForTofu(castValue, castArgs);
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

    private static class SoyTofuFunctionAdapter
    implements SoyJavaFunction {
        private final SoyTofuFunction adaptee;

        public SoyTofuFunctionAdapter(SoyTofuFunction adaptee) {
            this.adaptee = adaptee;
        }

        @Override
        public SoyValue computeForJava(List<SoyValue> args) {
            ArrayList castArgs = Lists.newArrayListWithCapacity((int)args.size());
            for (SoyValue arg : args) {
                castArgs.add((SoyData)arg);
            }
            return this.adaptee.computeForTofu(castArgs);
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
    public static @interface Tofu {
    }
}

