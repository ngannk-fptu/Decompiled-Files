/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.bididirectives;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.template.soy.bididirectives.BidiSpanWrapDirective;
import com.google.template.soy.bididirectives.BidiUnicodeWrapDirective;
import com.google.template.soy.shared.restricted.SoyPrintDirective;

public class BidiDirectivesModule
extends AbstractModule {
    @Override
    public void configure() {
        Multibinder<SoyPrintDirective> soyDirectivesSetBinder = Multibinder.newSetBinder(this.binder(), SoyPrintDirective.class);
        soyDirectivesSetBinder.addBinding().to(BidiSpanWrapDirective.class);
        soyDirectivesSetBinder.addBinding().to(BidiUnicodeWrapDirective.class);
    }
}

