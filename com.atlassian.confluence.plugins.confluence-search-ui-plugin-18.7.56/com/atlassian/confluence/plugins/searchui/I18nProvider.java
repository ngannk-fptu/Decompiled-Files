/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.util.i18n.PluginI18NResource
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.user.User
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.confluence.plugins.searchui;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.i18n.PluginI18NResource;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.user.User;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;

public class I18nProvider
implements WebResourceDataProvider {
    private static final String PLUGIN_KEY = "com.atlassian.confluence.plugins.confluence-search-ui-plugin";
    private final PluginAccessor pluginAccessor;
    private final LocaleManager localeManager;
    private final I18NBeanFactory i18NBeanFactory;
    private final ObjectMapper jackson = new ObjectMapper();

    public I18nProvider(PluginAccessor pluginAccessor, LocaleManager localeManager, I18NBeanFactory i18NBeanFactory) {
        this.pluginAccessor = pluginAccessor;
        this.localeManager = localeManager;
        this.i18NBeanFactory = i18NBeanFactory;
    }

    public Jsonable get() {
        return writer -> this.jackson.writeValue(writer, this.loadI18nProperties());
    }

    private Map<String, String> loadI18nProperties() {
        Plugin plugin = this.pluginAccessor.getEnabledPlugin(PLUGIN_KEY);
        HashMap<String, String> i18nProperties = new HashMap<String, String>();
        I18NBean i18NBean = this.getI18nBean();
        plugin.getResourceDescriptors().stream().filter(resourceDescriptor -> "i18n".equals(resourceDescriptor.getType())).map(resourceDescriptor -> new PluginI18NResource(plugin, resourceDescriptor)).forEach(pluginI18NResource -> pluginI18NResource.getBundle().keySet().forEach(key -> i18nProperties.put((String)key, i18NBean.getText(key, null, true))));
        return i18nProperties;
    }

    private I18NBean getI18nBean() {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        Locale locale = this.localeManager.getLocale((User)user);
        return this.i18NBeanFactory.getI18NBean(locale);
    }
}

