/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.AbstractModule
 *  com.google.inject.Binder
 *  com.google.inject.multibindings.Multibinder
 */
package com.google.template.soy.bidifunctions;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.multibindings.Multibinder;
import com.google.template.soy.bidifunctions.BidiDirAttrFunction;
import com.google.template.soy.bidifunctions.BidiEndEdgeFunction;
import com.google.template.soy.bidifunctions.BidiGlobalDirFunction;
import com.google.template.soy.bidifunctions.BidiMarkAfterFunction;
import com.google.template.soy.bidifunctions.BidiMarkFunction;
import com.google.template.soy.bidifunctions.BidiStartEdgeFunction;
import com.google.template.soy.bidifunctions.BidiTextDirFunction;
import com.google.template.soy.shared.restricted.SoyFunction;

public class BidiFunctionsModule
extends AbstractModule {
    public void configure() {
        Multibinder soyFunctionsSetBinder = Multibinder.newSetBinder((Binder)this.binder(), SoyFunction.class);
        soyFunctionsSetBinder.addBinding().to(BidiDirAttrFunction.class);
        soyFunctionsSetBinder.addBinding().to(BidiEndEdgeFunction.class);
        soyFunctionsSetBinder.addBinding().to(BidiGlobalDirFunction.class);
        soyFunctionsSetBinder.addBinding().to(BidiMarkAfterFunction.class);
        soyFunctionsSetBinder.addBinding().to(BidiMarkFunction.class);
        soyFunctionsSetBinder.addBinding().to(BidiStartEdgeFunction.class);
        soyFunctionsSetBinder.addBinding().to(BidiTextDirFunction.class);
    }
}

