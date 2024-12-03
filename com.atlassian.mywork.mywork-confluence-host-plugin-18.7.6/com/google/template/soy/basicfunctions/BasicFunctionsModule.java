/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.basicfunctions;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.template.soy.basicfunctions.AugmentMapFunction;
import com.google.template.soy.basicfunctions.CeilingFunction;
import com.google.template.soy.basicfunctions.FloorFunction;
import com.google.template.soy.basicfunctions.IsNonnullFunction;
import com.google.template.soy.basicfunctions.KeysFunction;
import com.google.template.soy.basicfunctions.LengthFunction;
import com.google.template.soy.basicfunctions.MaxFunction;
import com.google.template.soy.basicfunctions.MinFunction;
import com.google.template.soy.basicfunctions.RandomIntFunction;
import com.google.template.soy.basicfunctions.RoundFunction;
import com.google.template.soy.basicfunctions.StrContainsFunction;
import com.google.template.soy.basicfunctions.StrIndexOfFunction;
import com.google.template.soy.basicfunctions.StrLenFunction;
import com.google.template.soy.basicfunctions.StrSubFunction;
import com.google.template.soy.shared.restricted.SoyFunction;

public class BasicFunctionsModule
extends AbstractModule {
    @Override
    public void configure() {
        Multibinder<SoyFunction> soyFunctionsSetBinder = Multibinder.newSetBinder(this.binder(), SoyFunction.class);
        soyFunctionsSetBinder.addBinding().to(AugmentMapFunction.class);
        soyFunctionsSetBinder.addBinding().to(CeilingFunction.class);
        soyFunctionsSetBinder.addBinding().to(FloorFunction.class);
        soyFunctionsSetBinder.addBinding().to(IsNonnullFunction.class);
        soyFunctionsSetBinder.addBinding().to(KeysFunction.class);
        soyFunctionsSetBinder.addBinding().to(LengthFunction.class);
        soyFunctionsSetBinder.addBinding().to(MaxFunction.class);
        soyFunctionsSetBinder.addBinding().to(MinFunction.class);
        soyFunctionsSetBinder.addBinding().to(RandomIntFunction.class);
        soyFunctionsSetBinder.addBinding().to(RoundFunction.class);
        soyFunctionsSetBinder.addBinding().to(StrContainsFunction.class);
        soyFunctionsSetBinder.addBinding().to(StrIndexOfFunction.class);
        soyFunctionsSetBinder.addBinding().to(StrLenFunction.class);
        soyFunctionsSetBinder.addBinding().to(StrSubFunction.class);
    }
}

