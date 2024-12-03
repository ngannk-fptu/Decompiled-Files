/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.i18n;

import java.util.TimeZone;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.lang.Nullable;

public interface TimeZoneAwareLocaleContext
extends LocaleContext {
    @Nullable
    public TimeZone getTimeZone();
}

