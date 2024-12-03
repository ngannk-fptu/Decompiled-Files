/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 */
package com.atlassian.applinks.internal.common.web.data;

import com.atlassian.applinks.core.plugin.AuthenticationProviderModuleDescriptor;
import com.atlassian.applinks.internal.common.json.JacksonJsonableMarshaller;
import com.atlassian.applinks.internal.rest.model.BaseRestEntity;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import java.util.List;

public class AuthenticationTypesDataProvider
implements WebResourceDataProvider {
    private final I18nResolver i18nResolver;
    private final PluginAccessor pluginAccessor;

    public AuthenticationTypesDataProvider(I18nResolver i18nResolver, PluginAccessor pluginAccessor) {
        this.i18nResolver = i18nResolver;
        this.pluginAccessor = pluginAccessor;
    }

    public Jsonable get() {
        return JacksonJsonableMarshaller.INSTANCE.marshal((Object)this.getAllTypes());
    }

    private BaseRestEntity getAllTypes() {
        BaseRestEntity.Builder allTypes = new BaseRestEntity.Builder();
        for (AuthenticationProviderModuleDescriptor authProvider : this.getEnabledAuthModules()) {
            String key = authProvider.getModule().getAuthenticationProviderClass().getName();
            allTypes.add(key, this.i18nResolver.getText(authProvider.getI18nNameKey()));
        }
        return allTypes.build();
    }

    private List<AuthenticationProviderModuleDescriptor> getEnabledAuthModules() {
        return this.pluginAccessor.getEnabledModuleDescriptorsByClass(AuthenticationProviderModuleDescriptor.class);
    }
}

