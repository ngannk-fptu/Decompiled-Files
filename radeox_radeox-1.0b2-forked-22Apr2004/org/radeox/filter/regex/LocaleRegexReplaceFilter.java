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
import org.radeox.filter.regex.RegexReplaceFilter;

public abstract class LocaleRegexReplaceFilter
extends RegexReplaceFilter {
    private static Log log = LogFactory.getLog((Class)(class$org$radeox$filter$regex$LocaleRegexReplaceFilter == null ? (class$org$radeox$filter$regex$LocaleRegexReplaceFilter = LocaleRegexReplaceFilter.class$("org.radeox.filter.regex.LocaleRegexReplaceFilter")) : class$org$radeox$filter$regex$LocaleRegexReplaceFilter));
    static /* synthetic */ Class class$org$radeox$filter$regex$LocaleRegexReplaceFilter;

    protected abstract String getLocaleKey();

    protected boolean isSingleLine() {
        return false;
    }

    protected ResourceBundle getInputBundle() {
        Locale inputLocale = (Locale)this.initialContext.get("RenderContext.input_locale");
        String inputName = (String)this.initialContext.get("RenderContext.input_bundle_name");
        return ResourceBundle.getBundle(inputName, inputLocale);
    }

    protected ResourceBundle getOutputBundle() {
        String outputName = (String)this.initialContext.get("RenderContext.output_bundle_name");
        Locale outputLocale = (Locale)this.initialContext.get("RenderContext.output_locale");
        return ResourceBundle.getBundle(outputName, outputLocale);
    }

    public void setInitialContext(InitialRenderContext context) {
        super.setInitialContext(context);
        this.clearRegex();
        ResourceBundle outputMessages = this.getOutputBundle();
        ResourceBundle inputMessages = this.getInputBundle();
        String match = inputMessages.getString(this.getLocaleKey() + ".match");
        String print = outputMessages.getString(this.getLocaleKey() + ".print");
        this.addRegex(match, print, !this.isSingleLine());
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

