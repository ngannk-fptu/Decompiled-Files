/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.map.JsonMappingException
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.business.insights.core.frontend.data;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.business.insights.core.frontend.data.ProductData;
import com.atlassian.business.insights.core.plugin.CorePluginInfo;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

public class ProductDataProvider
implements WebResourceDataProvider {
    @VisibleForTesting
    static final String BITBUCKET_PRODUCT_NAME = "Bitbucket";
    @VisibleForTesting
    static final String CONFLUENCE_PRODUCT_NAME = "Confluence";
    @VisibleForTesting
    static final String JIRA_PRODUCT_NAME = "Jira";
    @VisibleForTesting
    static final String UNSUPPORTED_PRODUCT = "Unknown product";
    private final ApplicationProperties applicationProperties;
    private final ObjectMapper objectMapper;
    private final TimeZoneManager timeZoneManager;
    private final LocaleResolver localeResolver;
    private final CorePluginInfo corePluginInfo;

    public ProductDataProvider(@Nonnull ObjectMapper objectMapper, @Nonnull ApplicationProperties applicationProperties, @Nonnull TimeZoneManager timeZoneManager, @Nonnull LocaleResolver localeResolver, @Nonnull CorePluginInfo corePluginInfo) {
        this.objectMapper = Objects.requireNonNull(objectMapper);
        this.applicationProperties = Objects.requireNonNull(applicationProperties);
        this.timeZoneManager = Objects.requireNonNull(timeZoneManager);
        this.localeResolver = Objects.requireNonNull(localeResolver);
        this.corePluginInfo = Objects.requireNonNull(corePluginInfo);
    }

    public Jsonable get() {
        return writer -> {
            try {
                this.objectMapper.writeValue(writer, (Object)this.getData());
            }
            catch (Exception e) {
                throw new JsonMappingException(e.getMessage(), (Throwable)e);
            }
        };
    }

    @VisibleForTesting
    ProductData getData() {
        return new ProductData(this.getProductName(), this.getServerTimeZone(), this.getUserLocale(), this.getPluginVersion());
    }

    private static String mapPlatformIdToProductName(String platformId) {
        switch (platformId) {
            case "jira": {
                return JIRA_PRODUCT_NAME;
            }
            case "conf": {
                return CONFLUENCE_PRODUCT_NAME;
            }
            case "bitbucket": 
            case "stash": {
                return BITBUCKET_PRODUCT_NAME;
            }
        }
        return UNSUPPORTED_PRODUCT;
    }

    private String getProductName() {
        String platformId = this.applicationProperties.getPlatformId();
        return ProductDataProvider.mapPlatformIdToProductName(platformId);
    }

    private String getServerTimeZone() {
        return this.timeZoneManager.getDefaultTimeZone().getID();
    }

    private String getUserLocale() {
        return this.localeResolver.getLocale().toLanguageTag();
    }

    private String getPluginVersion() {
        return this.corePluginInfo.getPluginVersion();
    }
}

