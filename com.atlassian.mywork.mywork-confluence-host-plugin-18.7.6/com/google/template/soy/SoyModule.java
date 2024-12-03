/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.template.soy.GuiceInitializer;
import com.google.template.soy.SoyFileSet;
import com.google.template.soy.basicdirectives.BasicDirectivesModule;
import com.google.template.soy.basicfunctions.BasicFunctionsModule;
import com.google.template.soy.bididirectives.BidiDirectivesModule;
import com.google.template.soy.bidifunctions.BidiFunctionsModule;
import com.google.template.soy.data.SoyValueHelper;
import com.google.template.soy.i18ndirectives.I18nDirectivesModule;
import com.google.template.soy.jssrc.internal.JsSrcModule;
import com.google.template.soy.parsepasses.CheckFunctionCallsVisitor;
import com.google.template.soy.parsepasses.PerformAutoescapeVisitor;
import com.google.template.soy.parsepasses.contextautoesc.ContextualAutoescaper;
import com.google.template.soy.shared.internal.SharedModule;
import com.google.template.soy.tofu.internal.TofuModule;
import com.google.template.soy.types.SoyTypeOps;

public class SoyModule
extends AbstractModule {
    @Override
    protected void configure() {
        this.install(new SharedModule());
        this.install(new TofuModule());
        this.install(new JsSrcModule());
        this.bind(ContextualAutoescaper.class);
        this.bind(PerformAutoescapeVisitor.class);
        this.bind(SoyFileSet.Builder.class);
        this.bind(SoyTypeOps.class);
        this.bind(SoyValueHelper.class);
        this.install(new BasicDirectivesModule());
        this.install(new BidiDirectivesModule());
        this.install(new BasicFunctionsModule());
        this.install(new BidiFunctionsModule());
        this.install(new I18nDirectivesModule());
        this.install(new FactoryModuleBuilder().build(CheckFunctionCallsVisitor.CheckFunctionCallsVisitorFactory.class));
        this.install(new FactoryModuleBuilder().build(SoyFileSet.SoyFileSetFactory.class));
        this.requestStaticInjection(GuiceInitializer.class);
    }
}

