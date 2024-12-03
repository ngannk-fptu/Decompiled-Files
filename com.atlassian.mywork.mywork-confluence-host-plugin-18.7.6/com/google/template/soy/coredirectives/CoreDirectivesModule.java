/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.coredirectives;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.template.soy.coredirectives.EscapeHtmlDirective;
import com.google.template.soy.coredirectives.IdDirective;
import com.google.template.soy.coredirectives.NoAutoescapeDirective;
import com.google.template.soy.shared.restricted.SoyPrintDirective;

public class CoreDirectivesModule
extends AbstractModule {
    @Override
    public void configure() {
        Multibinder<SoyPrintDirective> soyDirectivesSetBinder = Multibinder.newSetBinder(this.binder(), SoyPrintDirective.class);
        soyDirectivesSetBinder.addBinding().to(NoAutoescapeDirective.class);
        soyDirectivesSetBinder.addBinding().to(IdDirective.class);
        soyDirectivesSetBinder.addBinding().to(EscapeHtmlDirective.class);
    }
}

