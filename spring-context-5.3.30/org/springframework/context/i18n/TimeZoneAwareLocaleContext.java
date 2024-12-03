/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
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

