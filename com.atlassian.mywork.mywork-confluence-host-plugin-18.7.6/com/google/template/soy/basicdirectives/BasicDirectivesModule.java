/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.basicdirectives;

import com.google.inject.AbstractModule;
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
    @Override
    public void configure() {
        Multibinder<SoyPrintDirective> soyDirectivesSetBinder = Multibinder.newSetBinder(this.binder(), SoyPrintDirective.class);
        soyDirectivesSetBinder.addBinding().toInstance(new BasicEscapeDirective.EscapeCssString());
        soyDirectivesSetBinder.addBinding().toInstance(new BasicEscapeDirective.FilterCssValue());
        soyDirectivesSetBinder.addBinding().toInstance(new BasicEscapeDirective.EscapeHtmlRcdata());
        soyDirectivesSetBinder.addBinding().toInstance(new BasicEscapeDirective.EscapeHtmlAttribute());
        soyDirectivesSetBinder.addBinding().toInstance(new BasicEscapeDirective.EscapeHtmlAttributeNospace());
        soyDirectivesSetBinder.addBinding().toInstance(new BasicEscapeDirective.FilterHtmlAttributes());
        soyDirectivesSetBinder.addBinding().toInstance(new BasicEscapeDirective.FilterHtmlElementName());
        soyDirectivesSetBinder.addBinding().toInstance(new BasicEscapeDirective.EscapeJsRegex());
        soyDirectivesSetBinder.addBinding().toInstance(new BasicEscapeDirective.EscapeJsString());
        soyDirectivesSetBinder.addBinding().toInstance(new BasicEscapeDirective.EscapeJsValue());
        soyDirectivesSetBinder.addBinding().toInstance(new BasicEscapeDirective.FilterNormalizeUri());
        soyDirectivesSetBinder.addBinding().toInstance(new BasicEscapeDirective.NormalizeUri());
        soyDirectivesSetBinder.addBinding().toInstance(new BasicEscapeDirective.EscapeUri());
        soyDirectivesSetBinder.addBinding().to(ChangeNewlineToBrDirective.class);
        soyDirectivesSetBinder.addBinding().to(InsertWordBreaksDirective.class);
        soyDirectivesSetBinder.addBinding().to(TruncateDirective.class);
        soyDirectivesSetBinder.addBinding().to(TextDirective.class);
        soyDirectivesSetBinder.addBinding().to(CleanHtmlDirective.class);
        soyDirectivesSetBinder.addBinding().to(FilterImageDataUriDirective.class);
    }
}

