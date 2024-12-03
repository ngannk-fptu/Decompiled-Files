/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.web.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplateHandler;

@Deprecated
public abstract class AbstractUriTemplateHandler
implements UriTemplateHandler {
    @Nullable
    private String baseUrl;
    private final Map<String, Object> defaultUriVariables = new HashMap<String, Object>();

    public void setBaseUrl(@Nullable String baseUrl) {
        if (baseUrl != null) {
            UriComponents uriComponents = UriComponentsBuilder.fromUriString(baseUrl).build();
            Assert.hasText((String)uriComponents.getScheme(), (String)"'baseUrl' must have a scheme");
            Assert.hasText((String)uriComponents.getHost(), (String)"'baseUrl' must have a host");
            Assert.isNull((Object)uriComponents.getQuery(), (String)"'baseUrl' cannot have a query");
            Assert.isNull((Object)uriComponents.getFragment(), (String)"'baseUrl' cannot have a fragment");
        }
        this.baseUrl = baseUrl;
    }

    @Nullable
    public String getBaseUrl() {
        return this.baseUrl;
    }

    public void setDefaultUriVariables(@Nullable Map<String, ?> defaultUriVariables) {
        this.defaultUriVariables.clear();
        if (defaultUriVariables != null) {
            this.defaultUriVariables.putAll(defaultUriVariables);
        }
    }

    public Map<String, ?> getDefaultUriVariables() {
        return Collections.unmodifiableMap(this.defaultUriVariables);
    }

    @Override
    public URI expand(String uriTemplate, Map<String, ?> uriVariables) {
        if (!this.getDefaultUriVariables().isEmpty()) {
            HashMap map = new HashMap();
            map.putAll(this.getDefaultUriVariables());
            map.putAll(uriVariables);
            uriVariables = map;
        }
        URI url = this.expandInternal(uriTemplate, uriVariables);
        return this.insertBaseUrl(url);
    }

    @Override
    public URI expand(String uriTemplate, Object ... uriVariables) {
        URI url = this.expandInternal(uriTemplate, uriVariables);
        return this.insertBaseUrl(url);
    }

    protected abstract URI expandInternal(String var1, Map<String, ?> var2);

    protected abstract URI expandInternal(String var1, Object ... var2);

    private URI insertBaseUrl(URI url) {
        try {
            String baseUrl = this.getBaseUrl();
            if (baseUrl != null && url.getHost() == null) {
                url = new URI(baseUrl + url.toString());
            }
            return url;
        }
        catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Invalid URL after inserting base URL: " + url, ex);
        }
    }
}

