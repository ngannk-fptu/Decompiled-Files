/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.analytics.api.annotations.PrivacyPolicySafe
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.util.i18n.event;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.analytics.api.annotations.PrivacyPolicySafe;
import com.atlassian.event.api.AsynchronousPreferred;
import java.util.Locale;

@PrivacyPolicySafe
@EventName(value="confluence.i18n.cacheInit")
@AsynchronousPreferred
public class I18NCacheInitEvent {
    @PrivacyPolicySafe
    private final String localeCode;

    public I18NCacheInitEvent(Locale locale) {
        this.localeCode = locale.toString();
    }

    public String getLocaleCode() {
        return this.localeCode;
    }
}

