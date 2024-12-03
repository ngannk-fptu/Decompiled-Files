/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  com.google.inject.AbstractModule
 *  com.google.inject.Binder
 *  com.google.inject.multibindings.Multibinder
 *  com.google.template.soy.shared.restricted.SoyFunction
 */
package com.atlassian.soy.impl.modules;

import com.atlassian.soy.impl.functions.ConcatFunction;
import com.atlassian.soy.impl.functions.ContextFunction;
import com.atlassian.soy.impl.functions.GetTextAsHtmlFunction;
import com.atlassian.soy.impl.functions.GetTextFunction;
import com.atlassian.soy.impl.functions.IsListFunction;
import com.atlassian.soy.impl.functions.IsMapFunction;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.multibindings.Multibinder;
import com.google.template.soy.shared.restricted.SoyFunction;
import java.util.Set;

class CoreFunctionsModule
extends AbstractModule {
    static final Set<String> CORE_FUNCTION_NAMES = ImmutableSet.of((Object)"concat", (Object)"contextPath", (Object)"getText", (Object)"getTextAsHtml", (Object)"isMap", (Object)"isList", (Object[])new String[0]);

    CoreFunctionsModule() {
    }

    public void configure() {
        Multibinder binder = Multibinder.newSetBinder((Binder)this.binder(), SoyFunction.class);
        binder.addBinding().to(ConcatFunction.class);
        binder.addBinding().to(ContextFunction.class);
        binder.addBinding().to(GetTextFunction.class);
        binder.addBinding().to(GetTextAsHtmlFunction.class);
        binder.addBinding().to(IsMapFunction.class);
        binder.addBinding().to(IsListFunction.class);
    }
}

