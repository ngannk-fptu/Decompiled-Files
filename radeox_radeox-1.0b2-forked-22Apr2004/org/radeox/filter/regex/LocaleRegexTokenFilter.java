/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.radeox.filter.regex;

import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.radeox.api.engine.context.InitialRenderContext;
import org.radeox.filter.regex.RegexTokenFilter;

public abstract class LocaleRegexTokenFilter
extends RegexTokenFilter {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$filter$regex$LocaleRegexTokenFilter == null ? (class$org$radeox$filter$regex$LocaleRegexTokenFilter = LocaleRegexTokenFilter.class$("org.radeox.filter.regex.LocaleRegexTokenFilter")) : class$org$radeox$filter$regex$LocaleRegexTokenFilter));
    protected ResourceBundle inputMessages;
    protected ResourceBundle outputMessages;
    static /* synthetic */ Class class$org$radeox$filter$regex$LocaleRegexTokenFilter;

    protected boolean isSingleLine() {
        return false;
    }

    protected ResourceBundle getInputBundle() {
        Locale inputLocale = (Locale)this.initialContext.get("RenderContext.input_locale");
        String inputName = (String)this.initialContext.get("RenderContext.input_bundle_name");
        return ResourceBundle.getBundle(inputName, inputLocale);
    }

    protected ResourceBundle getOutputBundle() {
        Locale outputLocale = (Locale)this.initialContext.get("RenderContext.output_locale");
        String outputName = (String)this.initialContext.get("RenderContext.output_bundle_name");
        return ResourceBundle.getBundle(outputName, outputLocale);
    }

    public void setInitialContext(InitialRenderContext context) {
        super.setInitialContext(context);
        this.clearRegex();
        this.outputMessages = this.getOutputBundle();
        this.inputMessages = this.getInputBundle();
        String match = this.inputMessages.getString(this.getLocaleKey() + ".match");
        this.addRegex(match, "", !this.isSingleLine());
    }

    protected abstract String getLocaleKey();

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

