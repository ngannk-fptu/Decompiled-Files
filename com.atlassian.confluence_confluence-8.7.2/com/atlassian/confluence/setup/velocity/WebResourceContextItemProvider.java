/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  com.atlassian.webresource.api.assembler.WebResourceAssemblerFactory
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Maps
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.setup.velocity;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugin.webresource.ConfluenceWebResourceService;
import com.atlassian.confluence.setup.velocity.VelocityContextItemProvider;
import com.atlassian.confluence.setup.velocity.VelocityFriendlyPageBuilderService;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.atlassian.webresource.api.assembler.WebResourceAssemblerFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import org.checkerframework.checker.nullness.qual.NonNull;

public class WebResourceContextItemProvider
implements VelocityContextItemProvider {
    public static final String STATIC_RESOURCE_URL_PREFIX = "staticResourceUrlPrefix";
    public static final String WEB_RESOURCE_HELPER = "webResourceHelper";
    private final VelocityFriendlyPageBuilderService webResourceHelper;
    private final ConcurrentMap<String, Map<String, Object>> localeToContext = Maps.newConcurrentMap();
    private final LocaleManager localeManager;
    private final WebResourceUrlProvider webResourceUrlProvider;

    public WebResourceContextItemProvider(PageBuilderService pageBuilderService, WebResourceAssemblerFactory webResourceAssemblerFactory, WebResourceUrlProvider webResourceUrlProvider, ConfluenceWebResourceService confluenceWebResourceService, LocaleManager localeManager) {
        this.localeManager = localeManager;
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.webResourceHelper = new VelocityFriendlyPageBuilderService(pageBuilderService, webResourceAssemblerFactory, webResourceUrlProvider, confluenceWebResourceService);
    }

    @Override
    public @NonNull Map<String, Object> getContextMap() {
        String locale = this.localeManager.getLocale(AuthenticatedUserThreadLocal.get()).toString();
        return this.localeToContext.computeIfAbsent(locale, k -> {
            ImmutableMap.Builder map = ImmutableMap.builder();
            map.put((Object)WEB_RESOURCE_HELPER, (Object)this.webResourceHelper);
            map.put((Object)STATIC_RESOURCE_URL_PREFIX, (Object)this.webResourceUrlProvider.getStaticResourcePrefix(UrlMode.AUTO));
            return map.build();
        });
    }
}

