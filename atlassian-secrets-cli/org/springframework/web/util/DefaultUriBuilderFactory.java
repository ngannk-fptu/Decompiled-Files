/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.util;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriBuilderFactory;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

public class DefaultUriBuilderFactory
implements UriBuilderFactory {
    @Nullable
    private final UriComponentsBuilder baseUri;
    private EncodingMode encodingMode = EncodingMode.URI_COMPONENT;
    private final Map<String, Object> defaultUriVariables = new HashMap<String, Object>();
    private boolean parsePath = true;

    public DefaultUriBuilderFactory() {
        this.baseUri = null;
    }

    public DefaultUriBuilderFactory(String baseUriTemplate) {
        this.baseUri = UriComponentsBuilder.fromUriString(baseUriTemplate);
    }

    public DefaultUriBuilderFactory(UriComponentsBuilder baseUri) {
        this.baseUri = baseUri;
    }

    public void setEncodingMode(EncodingMode encodingMode) {
        this.encodingMode = encodingMode;
    }

    public EncodingMode getEncodingMode() {
        return this.encodingMode;
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

    public void setParsePath(boolean parsePath) {
        this.parsePath = parsePath;
    }

    public boolean shouldParsePath() {
        return this.parsePath;
    }

    @Override
    public URI expand(String uriTemplate, Map<String, ?> uriVars) {
        return this.uriString(uriTemplate).build(uriVars);
    }

    @Override
    public URI expand(String uriTemplate, Object ... uriVars) {
        return this.uriString(uriTemplate).build(uriVars);
    }

    @Override
    public UriBuilder uriString(String uriTemplate) {
        return new DefaultUriBuilder(uriTemplate);
    }

    @Override
    public UriBuilder builder() {
        return new DefaultUriBuilder("");
    }

    private class DefaultUriBuilder
    implements UriBuilder {
        private final UriComponentsBuilder uriComponentsBuilder;

        public DefaultUriBuilder(String uriTemplate) {
            this.uriComponentsBuilder = this.initUriComponentsBuilder(uriTemplate);
        }

        private UriComponentsBuilder initUriComponentsBuilder(String uriTemplate) {
            UriComponentsBuilder builder;
            UriComponents uri;
            UriComponentsBuilder result = !StringUtils.hasLength(uriTemplate) ? (DefaultUriBuilderFactory.this.baseUri != null ? DefaultUriBuilderFactory.this.baseUri.cloneBuilder() : UriComponentsBuilder.newInstance()) : (DefaultUriBuilderFactory.this.baseUri != null ? ((uri = (builder = UriComponentsBuilder.fromUriString(uriTemplate)).build()).getHost() == null ? DefaultUriBuilderFactory.this.baseUri.cloneBuilder().uriComponents(uri) : builder) : UriComponentsBuilder.fromUriString(uriTemplate));
            if (DefaultUriBuilderFactory.this.encodingMode.equals((Object)EncodingMode.TEMPLATE_AND_VALUES)) {
                result.encode();
            }
            this.parsePathIfNecessary(result);
            return result;
        }

        private void parsePathIfNecessary(UriComponentsBuilder result) {
            if (DefaultUriBuilderFactory.this.parsePath && DefaultUriBuilderFactory.this.encodingMode.equals((Object)EncodingMode.URI_COMPONENT)) {
                UriComponents uric = result.build();
                String path = uric.getPath();
                result.replacePath(null);
                for (String segment : uric.getPathSegments()) {
                    result.pathSegment(segment);
                }
                if (path != null && path.endsWith("/")) {
                    result.path("/");
                }
            }
        }

        @Override
        public DefaultUriBuilder scheme(@Nullable String scheme) {
            this.uriComponentsBuilder.scheme(scheme);
            return this;
        }

        @Override
        public DefaultUriBuilder userInfo(@Nullable String userInfo) {
            this.uriComponentsBuilder.userInfo(userInfo);
            return this;
        }

        @Override
        public DefaultUriBuilder host(@Nullable String host) {
            this.uriComponentsBuilder.host(host);
            return this;
        }

        @Override
        public DefaultUriBuilder port(int port) {
            this.uriComponentsBuilder.port(port);
            return this;
        }

        @Override
        public DefaultUriBuilder port(@Nullable String port) {
            this.uriComponentsBuilder.port(port);
            return this;
        }

        @Override
        public DefaultUriBuilder path(String path) {
            this.uriComponentsBuilder.path(path);
            return this;
        }

        @Override
        public DefaultUriBuilder replacePath(@Nullable String path) {
            this.uriComponentsBuilder.replacePath(path);
            return this;
        }

        @Override
        public DefaultUriBuilder pathSegment(String ... pathSegments) {
            this.uriComponentsBuilder.pathSegment(pathSegments);
            return this;
        }

        @Override
        public DefaultUriBuilder query(String query) {
            this.uriComponentsBuilder.query(query);
            return this;
        }

        @Override
        public DefaultUriBuilder replaceQuery(@Nullable String query) {
            this.uriComponentsBuilder.replaceQuery(query);
            return this;
        }

        @Override
        public DefaultUriBuilder queryParam(String name, Object ... values) {
            this.uriComponentsBuilder.queryParam(name, values);
            return this;
        }

        @Override
        public DefaultUriBuilder replaceQueryParam(String name, Object ... values) {
            this.uriComponentsBuilder.replaceQueryParam(name, values);
            return this;
        }

        @Override
        public DefaultUriBuilder queryParams(MultiValueMap<String, String> params) {
            this.uriComponentsBuilder.queryParams((MultiValueMap)params);
            return this;
        }

        @Override
        public DefaultUriBuilder replaceQueryParams(MultiValueMap<String, String> params) {
            this.uriComponentsBuilder.replaceQueryParams((MultiValueMap)params);
            return this;
        }

        @Override
        public DefaultUriBuilder fragment(@Nullable String fragment) {
            this.uriComponentsBuilder.fragment(fragment);
            return this;
        }

        @Override
        public URI build(Map<String, ?> uriVars) {
            if (!DefaultUriBuilderFactory.this.defaultUriVariables.isEmpty()) {
                HashMap map = new HashMap();
                map.putAll(DefaultUriBuilderFactory.this.defaultUriVariables);
                map.putAll(uriVars);
                uriVars = map;
            }
            if (DefaultUriBuilderFactory.this.encodingMode.equals((Object)EncodingMode.VALUES_ONLY)) {
                uriVars = UriUtils.encodeUriVariables(uriVars);
            }
            UriComponents uric = this.uriComponentsBuilder.build().expand(uriVars);
            return this.createUri(uric);
        }

        @Override
        public URI build(Object ... uriVars) {
            if (ObjectUtils.isEmpty(uriVars) && !DefaultUriBuilderFactory.this.defaultUriVariables.isEmpty()) {
                return this.build(Collections.emptyMap());
            }
            if (DefaultUriBuilderFactory.this.encodingMode.equals((Object)EncodingMode.VALUES_ONLY)) {
                uriVars = UriUtils.encodeUriVariables(uriVars);
            }
            UriComponents uric = this.uriComponentsBuilder.build().expand(uriVars);
            return this.createUri(uric);
        }

        private URI createUri(UriComponents uric) {
            if (DefaultUriBuilderFactory.this.encodingMode.equals((Object)EncodingMode.URI_COMPONENT)) {
                uric = uric.encode();
            }
            return URI.create(uric.toString());
        }
    }

    public static enum EncodingMode {
        TEMPLATE_AND_VALUES,
        VALUES_ONLY,
        URI_COMPONENT,
        NONE;

    }
}

