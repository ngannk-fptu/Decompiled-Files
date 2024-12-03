/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

final class OpaqueUriComponents
extends UriComponents {
    private static final MultiValueMap<String, String> QUERY_PARAMS_NONE = new LinkedMultiValueMap<String, String>();
    @Nullable
    private final String ssp;

    OpaqueUriComponents(@Nullable String scheme, @Nullable String schemeSpecificPart, @Nullable String fragment) {
        super(scheme, fragment);
        this.ssp = schemeSpecificPart;
    }

    @Override
    @Nullable
    public String getSchemeSpecificPart() {
        return this.ssp;
    }

    @Override
    @Nullable
    public String getUserInfo() {
        return null;
    }

    @Override
    @Nullable
    public String getHost() {
        return null;
    }

    @Override
    public int getPort() {
        return -1;
    }

    @Override
    @Nullable
    public String getPath() {
        return null;
    }

    @Override
    public List<String> getPathSegments() {
        return Collections.emptyList();
    }

    @Override
    @Nullable
    public String getQuery() {
        return null;
    }

    @Override
    public MultiValueMap<String, String> getQueryParams() {
        return QUERY_PARAMS_NONE;
    }

    @Override
    public UriComponents encode(Charset charset) {
        return this;
    }

    @Override
    protected UriComponents expandInternal(UriComponents.UriTemplateVariables uriVariables) {
        String expandedScheme = OpaqueUriComponents.expandUriComponent(this.getScheme(), uriVariables);
        String expandedSsp = OpaqueUriComponents.expandUriComponent(this.getSchemeSpecificPart(), uriVariables);
        String expandedFragment = OpaqueUriComponents.expandUriComponent(this.getFragment(), uriVariables);
        return new OpaqueUriComponents(expandedScheme, expandedSsp, expandedFragment);
    }

    @Override
    public UriComponents normalize() {
        return this;
    }

    @Override
    public String toUriString() {
        StringBuilder uriBuilder = new StringBuilder();
        if (this.getScheme() != null) {
            uriBuilder.append(this.getScheme());
            uriBuilder.append(':');
        }
        if (this.ssp != null) {
            uriBuilder.append(this.ssp);
        }
        if (this.getFragment() != null) {
            uriBuilder.append('#');
            uriBuilder.append(this.getFragment());
        }
        return uriBuilder.toString();
    }

    @Override
    public URI toUri() {
        try {
            return new URI(this.getScheme(), this.ssp, this.getFragment());
        }
        catch (URISyntaxException ex) {
            throw new IllegalStateException("Could not create URI object: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected void copyToUriComponentsBuilder(UriComponentsBuilder builder) {
        if (this.getScheme() != null) {
            builder.scheme(this.getScheme());
        }
        if (this.getSchemeSpecificPart() != null) {
            builder.schemeSpecificPart(this.getSchemeSpecificPart());
        }
        if (this.getFragment() != null) {
            builder.fragment(this.getFragment());
        }
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof OpaqueUriComponents)) {
            return false;
        }
        OpaqueUriComponents otherComp = (OpaqueUriComponents)other;
        return ObjectUtils.nullSafeEquals(this.getScheme(), otherComp.getScheme()) && ObjectUtils.nullSafeEquals(this.ssp, otherComp.ssp) && ObjectUtils.nullSafeEquals(this.getFragment(), otherComp.getFragment());
    }

    public int hashCode() {
        int result = ObjectUtils.nullSafeHashCode(this.getScheme());
        result = 31 * result + ObjectUtils.nullSafeHashCode(this.ssp);
        result = 31 * result + ObjectUtils.nullSafeHashCode(this.getFragment());
        return result;
    }
}

