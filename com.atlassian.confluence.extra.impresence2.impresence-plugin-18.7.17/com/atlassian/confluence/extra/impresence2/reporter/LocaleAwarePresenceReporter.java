/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.impresence2.reporter;

import com.atlassian.confluence.extra.impresence2.reporter.PresenceReporter;
import com.atlassian.confluence.extra.impresence2.util.LocaleSupport;
import java.util.List;

public abstract class LocaleAwarePresenceReporter
implements PresenceReporter {
    private final LocaleSupport localeSupport;

    public LocaleAwarePresenceReporter(LocaleSupport localeSupport) {
        this.localeSupport = localeSupport;
    }

    public LocaleSupport getLocaleSupport() {
        return this.localeSupport;
    }

    public String getText(String key) {
        return this.getLocaleSupport().getText(key);
    }

    public String getText(String key, Object[] substitutions) {
        return this.getLocaleSupport().getText(key, substitutions);
    }

    public String getText(String key, List list) {
        return this.getLocaleSupport().getText(key, list);
    }

    public String getTextStrict(String key) {
        return this.getLocaleSupport().getTextStrict(key);
    }
}

