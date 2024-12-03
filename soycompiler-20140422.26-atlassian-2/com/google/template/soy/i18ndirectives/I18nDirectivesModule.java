/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.AbstractModule
 *  com.google.inject.Binder
 *  com.google.inject.multibindings.Multibinder
 */
package com.google.template.soy.i18ndirectives;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.multibindings.Multibinder;
import com.google.template.soy.i18ndirectives.FormatNumDirective;
import com.google.template.soy.shared.restricted.SoyPrintDirective;

public class I18nDirectivesModule
extends AbstractModule {
    public void configure() {
        Multibinder soyDirectivesSetBinder = Multibinder.newSetBinder((Binder)this.binder(), SoyPrintDirective.class);
        soyDirectivesSetBinder.addBinding().to(FormatNumDirective.class);
    }
}

