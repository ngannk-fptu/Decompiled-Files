/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.setup.settings.Settings
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.setup.settings.SpaceSettings
 *  com.atlassian.confluence.spaces.SpaceLogo
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.util.profiling.UtilTimerStack
 */
package com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.AbstractSubCalendarEventTransformer;
import com.atlassian.confluence.extra.calendar3.calendarstore.event.transformers.SubCalendarEventTransformerFactory;
import com.atlassian.confluence.extra.calendar3.model.SubCalendarEvent;
import com.atlassian.confluence.extra.calendar3.rest.validators.event.impl.UrlFieldValidator;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.setup.settings.SpaceSettings;
import com.atlassian.confluence.spaces.SpaceLogo;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.util.profiling.UtilTimerStack;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

public class DefaultSubCalendarEventTransformer
extends AbstractSubCalendarEventTransformer<SubCalendarEventTransformerFactory.TransformParameters>
implements SubCalendarEventTransformerFactory.SubCalendarEventTransformer<SubCalendarEventTransformerFactory.TransformParameters> {
    private static final String CACHE_NAME = DefaultSubCalendarEventTransformer.class.getName();
    private static final Pattern PATTERN_BLOGPOST_URL = Pattern.compile("^.+/display/.+/\\d{4}/\\d{2}/\\d{2}/(.*)$");
    private static final Pattern PATTERN_PAGE_URL = Pattern.compile("^.+/display/.+/([^/]+)$");
    private static final Pattern PATTERN_VIEW_PAGE_URL = Pattern.compile("^.+/pages/viewpage.action\\?pageId=(\\d+)");
    private final SettingsManager settingsManager;
    private final SpaceManager spaceManager;
    private final PageManager pageManager;
    private final UrlFieldValidator urlFieldValidator;
    private final Cache<String, String> cache;

    public DefaultSubCalendarEventTransformer(LocaleManager localeManager, I18NBeanFactory i18NBeanFactory, SettingsManager settingsManager, SpaceManager spaceManager, PageManager pageManager, CacheFactory cacheFactory, UrlFieldValidator urlFieldValidator) {
        super(localeManager, i18NBeanFactory);
        this.settingsManager = settingsManager;
        this.spaceManager = spaceManager;
        this.pageManager = pageManager;
        this.urlFieldValidator = urlFieldValidator;
        CacheSettings cacheSettings = new CacheSettingsBuilder().maxEntries(100).replicateViaInvalidation().replicateAsynchronously().build();
        this.cache = cacheFactory.getCache(CACHE_NAME, null, cacheSettings);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public SubCalendarEvent transform(SubCalendarEvent toBeTransformed, ConfluenceUser forUser, SubCalendarEventTransformerFactory.TransformParameters transformParameters) {
        String methodSignature = "DefaultSubCalendarEventTransformer.transform(SubCalendarEvent, SubCalendarEventTransformerFactory.TransformParameters)";
        UtilTimerStack.push((String)methodSignature);
        try {
            HashMap<String, List<String>> errors;
            Object url;
            String spaceKey = toBeTransformed.getSubCalendar().getSpaceKey();
            if (StringUtils.isNotBlank(spaceKey) && StringUtils.isBlank(toBeTransformed.getCustomEventTypeId())) {
                toBeTransformed.setIconUrl((String)this.cache.get((Object)spaceKey, () -> this.getSpaceLogo(spaceKey)));
            }
            if (!this.urlFieldValidator.isValid((String)(url = toBeTransformed.getUrl()), errors = new HashMap<String, List<String>>())) {
                String possiblyFixedUrl = "https://" + (String)url;
                if (this.urlFieldValidator.isValid(possiblyFixedUrl, errors)) {
                    toBeTransformed.setUrl(possiblyFixedUrl);
                    url = possiblyFixedUrl;
                } else {
                    toBeTransformed.setUrl("");
                    url = "";
                }
            }
            if (StringUtils.isNotBlank((String)url)) {
                Matcher m = PATTERN_BLOGPOST_URL.matcher((CharSequence)url);
                if (m.matches()) {
                    toBeTransformed.setUrlAlias(GeneralUtil.urlDecode((String)m.group(1)));
                } else {
                    m = PATTERN_PAGE_URL.matcher((CharSequence)url);
                    if (m.matches()) {
                        toBeTransformed.setUrlAlias(GeneralUtil.urlDecode((String)m.group(1)));
                    } else {
                        ContentEntityObject linkedContent;
                        m = PATTERN_VIEW_PAGE_URL.matcher((CharSequence)url);
                        if (m.matches() && null != (linkedContent = this.pageManager.getById(Long.parseLong(m.group(1))))) {
                            toBeTransformed.setUrlAlias(StringUtils.defaultIfEmpty(linkedContent.getTitle(), (String)url));
                        }
                    }
                }
            }
            toBeTransformed.setEditable(!transformParameters.isReadOnly());
            SubCalendarEvent subCalendarEvent = toBeTransformed;
            return subCalendarEvent;
        }
        finally {
            UtilTimerStack.pop((String)methodSignature);
        }
    }

    private String getSpaceLogo(String spaceKey) {
        SpaceLogo spaceLogo = this.spaceManager.getLogoForSpace(spaceKey);
        SpaceSettings spaceSettings = this.settingsManager.getSpaceSettings(spaceKey);
        if (null == spaceSettings || spaceSettings.isDisableLogo()) {
            spaceLogo = this.spaceManager.getLogoForGlobalcontext();
        }
        Settings globalSettings = this.settingsManager.getGlobalSettings();
        if (spaceLogo.isGlobalLogo() && globalSettings.isDisableLogo()) {
            spaceLogo = SpaceLogo.DEFAULT_SPACE_LOGO;
        }
        return this.settingsManager.getGlobalSettings().getBaseUrl() + spaceLogo.getDownloadPath();
    }
}

