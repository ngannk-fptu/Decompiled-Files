/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.radeox.macro;

import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.radeox.api.engine.context.InitialRenderContext;
import org.radeox.macro.BaseMacro;
import org.radeox.macro.LocaleMacro;

public abstract class BaseLocaleMacro
extends BaseMacro
implements LocaleMacro {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$macro$BaseLocaleMacro == null ? (class$org$radeox$macro$BaseLocaleMacro = BaseLocaleMacro.class$("org.radeox.macro.BaseLocaleMacro")) : class$org$radeox$macro$BaseLocaleMacro));
    private String name;
    static /* synthetic */ Class class$org$radeox$macro$BaseLocaleMacro;

    public String getName() {
        return this.name;
    }

    public void setInitialContext(InitialRenderContext context) {
        super.setInitialContext(context);
        Locale languageLocale = (Locale)context.get("RenderContext.language_locale");
        String languageName = (String)context.get("RenderContext.language_bundle_name");
        ResourceBundle messages = ResourceBundle.getBundle(languageName, languageLocale);
        Locale inputLocale = (Locale)context.get("RenderContext.input_locale");
        String inputName = (String)context.get("RenderContext.input_bundle_name");
        ResourceBundle inputMessages = ResourceBundle.getBundle(inputName, inputLocale);
        this.name = inputMessages.getString(this.getLocaleKey() + ".name");
        try {
            this.description = messages.getString(this.getLocaleKey() + ".description");
        }
        catch (Exception e) {
            log.warn((Object)("Cannot read description from properties " + inputName + " for " + this.getLocaleKey()));
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

