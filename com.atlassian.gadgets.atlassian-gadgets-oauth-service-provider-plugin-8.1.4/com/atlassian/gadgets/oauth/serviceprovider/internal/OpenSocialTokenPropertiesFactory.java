/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetParsingException
 *  com.atlassian.gadgets.GadgetRequestContext
 *  com.atlassian.gadgets.spec.GadgetSpec
 *  com.atlassian.gadgets.spec.GadgetSpecFactory
 *  com.atlassian.oauth.Request
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken
 *  com.atlassian.oauth.serviceprovider.TokenPropertiesFactory
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.oauth.serviceprovider.internal;

import com.atlassian.gadgets.GadgetParsingException;
import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.gadgets.spec.GadgetSpec;
import com.atlassian.gadgets.spec.GadgetSpecFactory;
import com.atlassian.oauth.Request;
import com.atlassian.oauth.serviceprovider.ServiceProviderToken;
import com.atlassian.oauth.serviceprovider.TokenPropertiesFactory;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService
public class OpenSocialTokenPropertiesFactory
implements TokenPropertiesFactory {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final GadgetSpecFactory gadgetSpecFactory;

    @Autowired
    public OpenSocialTokenPropertiesFactory(@ComponentImport GadgetSpecFactory gadgetSpecFactory) {
        this.gadgetSpecFactory = (GadgetSpecFactory)Preconditions.checkNotNull((Object)gadgetSpecFactory, (Object)"gadgetSpecFactory");
    }

    public Map<String, String> newRequestTokenProperties(Request request) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        String appUrl = this.getAppUrl(request);
        if (appUrl != null) {
            try {
                URI uri = URI.create(appUrl.trim());
                builder.put((Object)"xoauth_app_url", (Object)uri.toASCIIString());
            }
            catch (IllegalArgumentException e) {
                this.logger.warn("appUrl is not a valid URI: " + appUrl, (Throwable)e);
            }
        }
        return builder.build();
    }

    public Map<String, String> newAccessTokenProperties(ServiceProviderToken requestToken) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        String appUrl = requestToken.getProperty("xoauth_app_url");
        if (appUrl != null) {
            try {
                builder.put((Object)"alternate.consumer.name", (Object)this.getGadgetName(appUrl));
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        return builder.build();
    }

    private String getGadgetName(String appUrl) {
        try {
            GadgetSpec spec = this.gadgetSpecFactory.getGadgetSpec(new URI(appUrl), GadgetRequestContext.NO_CURRENT_REQUEST);
            return spec.getDirectoryTitle() != null ? spec.getDirectoryTitle() : spec.getTitle();
        }
        catch (URISyntaxException e) {
            throw new IllegalArgumentException("appUrl is not a valid URI");
        }
        catch (GadgetParsingException e) {
            if (this.logger.isDebugEnabled()) {
                this.logger.warn("Unable to parse gadget spec", (Throwable)e);
            } else {
                this.logger.warn("Unable to parse gadget spec at '" + appUrl + "': " + e.getMessage());
            }
            throw new IllegalArgumentException("Unable to parse gadget spec");
        }
    }

    private String getAppUrl(Request request) {
        if (request.getParameter("xoauth_app_url") != null) {
            return request.getParameter("xoauth_app_url");
        }
        if (request.getParameter("opensocial_app_url") != null) {
            return request.getParameter("opensocial_app_url");
        }
        return null;
    }
}

