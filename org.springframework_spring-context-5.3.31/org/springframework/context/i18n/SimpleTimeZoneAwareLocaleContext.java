/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.context.i18n;

import java.util.Locale;
import java.util.TimeZone;
import org.springframework.context.i18n.SimpleLocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.lang.Nullable;

public class SimpleTimeZoneAwareLocaleContext
extends SimpleLocaleContext
implements TimeZoneAwareLocaleContext {
    @Nullable
    private final TimeZone timeZone;

    public SimpleTimeZoneAwareLocaleContext(@Nullable Locale locale, @Nullable TimeZone timeZone) {
        super(locale);
        this.timeZone = timeZone;
    }

    @Override
    @Nullable
    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    @Override
    public String toString() {
        return super.toString() + " " + (this.timeZone != null ? this.timeZone.toString() : "-");
    }
}

