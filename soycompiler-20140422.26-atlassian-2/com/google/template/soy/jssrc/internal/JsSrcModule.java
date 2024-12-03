/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.AbstractModule
 *  com.google.inject.Module
 *  com.google.inject.Provides
 *  com.google.inject.Singleton
 *  com.google.inject.assistedinject.FactoryModuleBuilder
 */
package com.google.template.soy.jssrc.internal;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.template.soy.jssrc.SoyJsSrcOptions;
import com.google.template.soy.jssrc.internal.CanInitOutputVarVisitor;
import com.google.template.soy.jssrc.internal.GenCallCodeUtils;
import com.google.template.soy.jssrc.internal.GenDirectivePluginRequiresVisitor;
import com.google.template.soy.jssrc.internal.GenFunctionPluginRequiresVisitor;
import com.google.template.soy.jssrc.internal.GenJsCodeVisitor;
import com.google.template.soy.jssrc.internal.GenJsExprsVisitor;
import com.google.template.soy.jssrc.internal.IsComputableAsJsExprsVisitor;
import com.google.template.soy.jssrc.internal.JsExprTranslator;
import com.google.template.soy.jssrc.internal.JsSrcMain;
import com.google.template.soy.jssrc.internal.OptimizeBidiCodeGenVisitor;
import com.google.template.soy.jssrc.internal.TranslateToJsExprVisitor;
import com.google.template.soy.jssrc.restricted.SoyJsSrcFunction;
import com.google.template.soy.jssrc.restricted.SoyJsSrcPrintDirective;
import com.google.template.soy.jssrc.restricted.SoyLibraryAssistedJsSrcFunction;
import com.google.template.soy.jssrc.restricted.SoyLibraryAssistedJsSrcPrintDirective;
import com.google.template.soy.shared.internal.ApiCallScope;
import com.google.template.soy.shared.internal.GuiceSimpleScope;
import com.google.template.soy.shared.internal.ModuleUtils;
import com.google.template.soy.shared.internal.SharedModule;
import com.google.template.soy.shared.restricted.SoyFunction;
import com.google.template.soy.shared.restricted.SoyPrintDirective;
import com.google.template.soy.sharedpasses.SharedPassesModule;
import java.util.Map;
import java.util.Set;

public class JsSrcModule
extends AbstractModule {
    protected void configure() {
        this.install((Module)new SharedModule());
        this.install((Module)new SharedPassesModule());
        this.bind(JsSrcMain.class);
        this.bind(GenJsCodeVisitor.class);
        this.bind(OptimizeBidiCodeGenVisitor.class);
        this.bind(CanInitOutputVarVisitor.class);
        this.bind(GenCallCodeUtils.class);
        this.bind(IsComputableAsJsExprsVisitor.class);
        this.bind(JsExprTranslator.class);
        this.bind(GenDirectivePluginRequiresVisitor.class);
        this.bind(GenFunctionPluginRequiresVisitor.class);
        this.install(new FactoryModuleBuilder().build(GenJsExprsVisitor.GenJsExprsVisitorFactory.class));
        this.install(new FactoryModuleBuilder().build(TranslateToJsExprVisitor.TranslateToJsExprVisitorFactory.class));
        this.bind(SoyJsSrcOptions.class).toProvider(GuiceSimpleScope.getUnscopedProvider()).in(ApiCallScope.class);
    }

    @Provides
    @Singleton
    Map<String, SoyJsSrcFunction> provideSoyJsSrcFunctionsMap(Set<SoyFunction> soyFunctionsSet) {
        return ModuleUtils.buildSpecificSoyFunctionsMap(soyFunctionsSet, SoyJsSrcFunction.class);
    }

    @Provides
    @Singleton
    Map<String, SoyLibraryAssistedJsSrcFunction> provideSoyLibraryAssistedJsSrcFunctionsMap(Set<SoyFunction> soyFunctionsSet) {
        return ModuleUtils.buildSpecificSoyFunctionsMap(soyFunctionsSet, SoyLibraryAssistedJsSrcFunction.class);
    }

    @Provides
    @Singleton
    Map<String, SoyJsSrcPrintDirective> provideSoyJsSrcDirectivesMap(Set<SoyPrintDirective> soyDirectivesSet) {
        return ModuleUtils.buildSpecificSoyDirectivesMap(soyDirectivesSet, SoyJsSrcPrintDirective.class);
    }

    @Provides
    @Singleton
    Map<String, SoyLibraryAssistedJsSrcPrintDirective> provideSoyLibraryAssistedJsSrcDirectivesMap(Set<SoyPrintDirective> soyDirectivesSet) {
        return ModuleUtils.buildSpecificSoyDirectivesMap(soyDirectivesSet, SoyLibraryAssistedJsSrcPrintDirective.class);
    }
}

