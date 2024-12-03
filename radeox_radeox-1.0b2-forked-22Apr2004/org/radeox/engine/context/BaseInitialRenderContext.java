/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.engine.context;

import java.util.Locale;
import org.radeox.api.engine.context.InitialRenderContext;
import org.radeox.engine.context.BaseRenderContext;

public class BaseInitialRenderContext
extends BaseRenderContext
implements InitialRenderContext {
    public BaseInitialRenderContext() {
        Locale languageLocale = Locale.getDefault();
        Locale locale = new Locale("Basic", "basic");
        this.set("RenderContext.input_locale", locale);
        this.set("RenderContext.output_locale", locale);
        this.set("RenderContext.language_locale", languageLocale);
        this.set("RenderContext.input_bundle_name", "radeox_markup");
        this.set("RenderContext.output_bundle_name", "radeox_markup");
        this.set("RenderContext.language_bundle_name", "radeox_messages");
        this.set("RenderContext.default_formatter", "java");
    }
}

