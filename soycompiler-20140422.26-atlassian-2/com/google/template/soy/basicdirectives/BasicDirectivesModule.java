/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.inject.AbstractModule
 *  com.google.inject.Binder
 *  com.google.inject.multibindings.Multibinder
 */
package com.google.template.soy.basicdirectives;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.multibindings.Multibinder;
import com.google.template.soy.basicdirectives.BasicEscapeDirective;
import com.google.template.soy.basicdirectives.ChangeNewlineToBrDirective;
import com.google.template.soy.basicdirectives.CleanHtmlDirective;
import com.google.template.soy.basicdirectives.FilterImageDataUriDirective;
import com.google.template.soy.basicdirectives.InsertWordBreaksDirective;
import com.google.template.soy.basicdirectives.TextDirective;
import com.google.template.soy.basicdirectives.TruncateDirective;
import com.google.template.soy.shared.restricted.SoyPrintDirective;

public class BasicDirectivesModule
extends AbstractModule {
    public void configure() {
        Multibinder soyDirectivesSetBinder = Multibinder.newSetBinder((Binder)this.binder(), SoyPrintDirective.class);
        soyDirectivesSetBinder.addBinding().toInstance((Object)new BasicEscapeDirective.EscapeCssString());
        soyDirectivesSetBinder.addBinding().toInstance((Object)new BasicEscapeDirective.FilterCssValue());
        soyDirectivesSetBinder.addBinding().toInstance((Object)new BasicEscapeDirective.EscapeHtmlRcdata());
        soyDirectivesSetBinder.addBinding().toInstance((Object)new BasicEscapeDirective.EscapeHtmlAttribute());
        soyDirectivesSetBinder.addBinding().toInstance((Object)new BasicEscapeDirective.EscapeHtmlAttributeNospace());
        soyDirectivesSetBinder.addBinding().toInstance((Object)new BasicEscapeDirective.FilterHtmlAttributes());
        soyDirectivesSetBinder.addBinding().toInstance((Object)new BasicEscapeDirective.FilterHtmlElementName());
        soyDirectivesSetBinder.addBinding().toInstance((Object)new BasicEscapeDirective.EscapeJsRegex());
        soyDirectivesSetBinder.addBinding().toInstance((Object)new BasicEscapeDirective.EscapeJsString());
        soyDirectivesSetBinder.addBinding().toInstance((Object)new BasicEscapeDirective.EscapeJsValue());
        soyDirectivesSetBinder.addBinding().toInstance((Object)new BasicEscapeDirective.FilterNormalizeUri());
        soyDirectivesSetBinder.addBinding().toInstance((Object)new BasicEscapeDirective.NormalizeUri());
        soyDirectivesSetBinder.addBinding().toInstance((Object)new BasicEscapeDirective.EscapeUri());
        soyDirectivesSetBinder.addBinding().to(ChangeNewlineToBrDirective.class);
        soyDirectivesSetBinder.addBinding().to(InsertWordBreaksDirective.class);
        soyDirectivesSetBinder.addBinding().to(TruncateDirective.class);
        soyDirectivesSetBinder.addBinding().to(TextDirective.class);
        soyDirectivesSetBinder.addBinding().to(CleanHtmlDirective.class);
        soyDirectivesSetBinder.addBinding().to(FilterImageDataUriDirective.class);
    }
}

