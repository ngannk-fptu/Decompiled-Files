/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.i18ndirectives;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.template.soy.i18ndirectives.FormatNumDirective;
import com.google.template.soy.shared.restricted.SoyPrintDirective;

public class I18nDirectivesModule
extends AbstractModule {
    @Override
    public void configure() {
        Multibinder<SoyPrintDirective> soyDirectivesSetBinder = Multibinder.newSetBinder(this.binder(), SoyPrintDirective.class);
        soyDirectivesSetBinder.addBinding().to(FormatNumDirective.class);
    }
}

