/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.spi.link;

import com.atlassian.applinks.api.ApplicationLink;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationLinkDetails {
    private final String name;
    private final URI displayUrl;
    private final URI rpcUrl;
    private final boolean isPrimary;

    private ApplicationLinkDetails(String name, URI displayUrl, URI rpcUrl, boolean isPrimary) {
        this.name = name;
        this.displayUrl = displayUrl;
        this.rpcUrl = rpcUrl;
        this.isPrimary = isPrimary;
    }

    public String getName() {
        return this.name;
    }

    public URI getDisplayUrl() {
        return this.displayUrl;
    }

    public URI getRpcUrl() {
        return this.rpcUrl;
    }

    public boolean isPrimary() {
        return this.isPrimary;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ApplicationLinkDetails details) {
        Builder builder = new Builder();
        builder.displayUrl = details.displayUrl;
        builder.rpcUrl = details.rpcUrl;
        builder.name = details.name;
        builder.isPrimary = details.isPrimary;
        return builder;
    }

    public static Builder builder(ApplicationLink applicationLink) {
        Builder builder = new Builder();
        builder.displayUrl = applicationLink.getDisplayUrl();
        builder.rpcUrl = applicationLink.getRpcUrl();
        builder.name = applicationLink.getName();
        builder.isPrimary = applicationLink.isPrimary();
        return builder;
    }

    public static class Builder {
        private String name;
        private URI displayUrl;
        private URI rpcUrl;
        private boolean isPrimary;
        private static final Logger log = LoggerFactory.getLogger(Builder.class);

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder displayUrl(URI url) {
            this.displayUrl = url;
            return this;
        }

        public Builder rpcUrl(URI url) {
            this.rpcUrl = url;
            return this;
        }

        public Builder isPrimary(boolean isPrimary) {
            this.isPrimary = isPrimary;
            return this;
        }

        public ApplicationLinkDetails build() {
            if (this.rpcUrl == null) {
                this.rpcUrl = this.displayUrl;
            } else if (this.displayUrl == null) {
                this.displayUrl = this.rpcUrl;
            }
            if (this.rpcUrl == null) {
                throw new NullPointerException("either displayUrl or rpcUrl must be set before build()");
            }
            this.cleanUriPaths();
            return new ApplicationLinkDetails(Objects.requireNonNull(this.name, "name"), Objects.requireNonNull(this.displayUrl, "displayUrl"), Objects.requireNonNull(this.rpcUrl, "rpcUrl"), this.isPrimary);
        }

        private void cleanUriPaths() {
            String rpcUrlString = this.stripTrailingSlash(this.rpcUrl.getPath());
            String displayString = this.stripTrailingSlash(this.displayUrl.getPath());
            try {
                this.rpcUrl = new URI(this.rpcUrl.getScheme(), this.rpcUrl.getUserInfo(), this.rpcUrl.getHost(), this.rpcUrl.getPort(), rpcUrlString, this.rpcUrl.getQuery(), this.rpcUrl.getFragment());
                this.displayUrl = new URI(this.displayUrl.getScheme(), this.displayUrl.getUserInfo(), this.displayUrl.getHost(), this.displayUrl.getPort(), displayString, this.displayUrl.getQuery(), this.displayUrl.getFragment());
            }
            catch (URISyntaxException e) {
                log.info("Supplied rpc URL " + rpcUrlString + " and display URL" + displayString + "were not sanitized for trailing slashes because one or both were invalid");
            }
        }

        private String stripTrailingSlash(String url) {
            if (url == null) {
                return "";
            }
            return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
        }
    }
}

