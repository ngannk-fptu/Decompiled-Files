/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.AbstractModule
 *  com.google.inject.Binder
 *  com.google.inject.multibindings.Multibinder
 *  com.google.template.soy.shared.restricted.SoyFunction
 */
package com.atlassian.soy.impl.modules;

import com.atlassian.soy.impl.functions.IncludeResourcesFunction;
import com.atlassian.soy.impl.functions.RequireResourceFunction;
import com.atlassian.soy.impl.functions.RequireResourcesForContextFunction;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.multibindings.Multibinder;
import com.google.template.soy.shared.restricted.SoyFunction;

class WebResourceFunctionsModule
extends AbstractModule {
    WebResourceFunctionsModule() {
    }

    protected void configure() {
        Multibinder binder = Multibinder.newSetBinder((Binder)this.binder(), SoyFunction.class);
        binder.addBinding().to(IncludeResourcesFunction.class);
        binder.addBinding().to(RequireResourceFunction.class);
        binder.addBinding().to(RequireResourcesForContextFunction.class);
    }
}

