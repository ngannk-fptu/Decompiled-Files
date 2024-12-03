/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  com.google.common.collect.ImmutableMap$Builder
 */
package com.atlassian.pats.entrypoint;

import com.atlassian.json.marshal.Jsonable;
import com.atlassian.pats.access.services.ReadOnlyModeService;
import com.atlassian.pats.core.properties.SystemProperty;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

public class PluginConstantsProvider
implements WebResourceDataProvider {
    private final LocaleResolver localeResolver;
    private final TimeZoneManager timeZoneManager;
    private final ReadOnlyModeService readOnlyModeService;

    public PluginConstantsProvider(LocaleResolver localeResolver, TimeZoneManager timeZoneManager, ReadOnlyModeService readOnlyModeService) {
        this.localeResolver = localeResolver;
        this.timeZoneManager = timeZoneManager;
        this.readOnlyModeService = readOnlyModeService;
    }

    public Jsonable get() {
        Gson gson = new Gson();
        return writer -> gson.toJson((Object)new ImmutableMap.Builder().put((Object)"readOnlyAccessMode", (Object)this.readOnlyModeService.isEnabled()).put((Object)"maximumTokenExpiryDays", (Object)SystemProperty.MAX_TOKEN_EXPIRY_DAYS.getValue()).put((Object)"isEternalTokensEnabled", (Object)SystemProperty.ETERNAL_TOKENS_ENABLED.getValue()).put((Object)"tokensEnabled", (Object)SystemProperty.PATS_ENABLED.getValue()).put((Object)"userLocale", (Object)this.localeResolver.getLocale().toLanguageTag()).put((Object)"userTimeZone", (Object)this.timeZoneManager.getUserTimeZone().getID()).put((Object)"tokenNameLength", (Object)SystemProperty.TOKEN_NAME_LENGTH.getValue()).build(), (Appendable)writer);
    }
}

