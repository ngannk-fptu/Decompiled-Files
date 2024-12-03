/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.DefaultSubCalendarEventTransformer;
import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.SubCalendarEventTransformerFactory;
import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.WebResourceDependentSubCalendarEventTransformer;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.util.BuildInformationManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import org.apache.commons.lang.StringUtils;

public class SubCalendarEventNotSupportingInviteesTransformer
extends WebResourceDependentSubCalendarEventTransformer<SubCalendarEventTransformerFactory.TransformParameters> {
    private static final String CACHE_NAME = DefaultSubCalendarEventTransformer.class.getName();
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final Cache<String, String> cache;

    public SubCalendarEventNotSupportingInviteesTransformer(LocaleManager localeManager, I18NBeanFactory i18NBeanFactory, BuildInformationManager buildInformationManager, WebResourceUrlProvider webResourceUrlProvider, CacheFactory cacheFactory) {
        super(localeManager, i18NBeanFactory, buildInformationManager);
        this.webResourceUrlProvider = webResourceUrlProvider;
        CacheSettings cacheSettings = new CacheSettingsBuilder().maxEntries(200).replicateViaInvalidation().replicateAsynchronously().build();
        this.cache = cacheFactory.getCache(CACHE_NAME, null, cacheSettings);
    }

    @Override
    public SubCalendarEvent transform(SubCalendarEvent toBeTransformed, ConfluenceUser forUser, SubCalendarEventTransformerFactory.TransformParameters transformParameters) {
        toBeTransformed.setInvitees(null);
        if (StringUtils.isBlank(toBeTransformed.getCustomEventTypeId())) {
            String event48 = "img/events_48.png";
            String event24 = "img/events_24.png";
            toBeTransformed.setIconUrl((String)this.cache.get((Object)event48, () -> this.webResourceUrlProvider.getStaticPluginResourceUrl(this.getCalendarResourceModuleKey(), event48, UrlMode.ABSOLUTE)));
            toBeTransformed.setMediumIconUrl((String)this.cache.get((Object)event24, () -> this.webResourceUrlProvider.getStaticPluginResourceUrl(this.getCalendarResourceModuleKey(), event24, UrlMode.ABSOLUTE)));
        }
        return toBeTransformed;
    }
}

