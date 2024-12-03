/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.inject.Inject
 *  javax.inject.Named
 */
package com.atlassian.plugins.authentication.data;

import com.atlassian.json.marshal.Jsonable;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.impl.util.JsmUrlChecker;
import com.atlassian.sal.api.ApplicationProperties;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import javax.inject.Inject;
import javax.inject.Named;

@Named
public class LoginGatewayDataProvider {
    private static final Gson GSON = new Gson();
    private static final String JSM_PLATFORM_ID = "jsm";
    private final ApplicationProperties applicationProperties;
    private final JsmUrlChecker jsmUrlChecker;

    @Inject
    public LoginGatewayDataProvider(@ComponentImport ApplicationProperties applicationProperties, JsmUrlChecker jsmUrlChecker) {
        this.applicationProperties = applicationProperties;
        this.jsmUrlChecker = jsmUrlChecker;
    }

    public Jsonable get(String refererUrl, Object destinationUrl) {
        return writer -> {
            ImmutableMap.Builder dataBuilder = ImmutableMap.builder().put((Object)"product", (Object)this.getProductId(refererUrl));
            if (destinationUrl != null) {
                dataBuilder.put((Object)"destination", (Object)destinationUrl.toString());
            }
            GSON.toJson((Object)dataBuilder.build(), (Appendable)writer);
        };
    }

    private String getProductId(String refererUrl) {
        return this.jsmUrlChecker.isJsmRequest(refererUrl) ? JSM_PLATFORM_ID : this.applicationProperties.getPlatformId();
    }
}

