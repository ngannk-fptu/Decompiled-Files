/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  javax.ws.rs.core.UriBuilder
 */
package com.atlassian.upm.transformers.notification;

import com.atlassian.json.marshal.Jsonable;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import com.google.gson.Gson;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.ws.rs.core.UriBuilder;

public class NotificationsPageDataProvider
implements WebResourceDataProvider {
    private final ApplicationProperties applicationProperties;

    public NotificationsPageDataProvider(ApplicationProperties applicationProperties) {
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
    }

    public Jsonable get() {
        return writer -> {
            Gson gson = new Gson();
            gson.toJson((Object)this.generate(), (Type)((Object)Map.class), new PrintWriter(writer));
        };
    }

    private Map<String, Object> generate() {
        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("productId", this.applicationProperties.getDisplayName().toLowerCase());
        props.put("analyticsUrl", this.newPluginBaseUriBuilder().path("/analytics").build(new Object[0]).toASCIIString());
        props.put("rootNotificationsUrl", this.newPluginBaseUriBuilder().path("/notifications").build(new Object[0]).toASCIIString());
        return props;
    }

    private UriBuilder newPluginBaseUriBuilder() {
        return this.newApplicationBaseUriBuilder().path("/rest/plugins/1.0");
    }

    private UriBuilder newApplicationBaseUriBuilder() {
        URI base = URI.create(this.applicationProperties.getBaseUrl()).normalize();
        return UriBuilder.fromPath((String)base.getPath());
    }
}

