/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.plugin.servlet.DownloadableResource
 *  com.atlassian.plugin.webresource.QueryParams
 *  com.atlassian.plugin.webresource.transformer.TransformableResource
 *  com.atlassian.plugin.webresource.transformer.TransformerParameters
 *  com.atlassian.plugin.webresource.transformer.UrlReadingWebResourceTransformer
 *  com.atlassian.plugin.webresource.url.UrlBuilder
 *  com.atlassian.webresource.api.prebake.Coordinate
 *  com.atlassian.webresource.api.prebake.DimensionAwareTransformerUrlBuilder
 *  com.atlassian.webresource.api.prebake.DimensionAwareWebResourceTransformerFactory
 *  com.atlassian.webresource.api.prebake.Dimensions
 *  io.atlassian.util.concurrent.Lazy
 */
package com.atlassian.plugin.webresource.transformer.instance;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.plugin.servlet.DownloadableResource;
import com.atlassian.plugin.webresource.QueryParams;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.plugin.webresource.cdn.CDNStrategy;
import com.atlassian.plugin.webresource.cdn.CdnResourceUrlTransformer;
import com.atlassian.plugin.webresource.impl.config.Config;
import com.atlassian.plugin.webresource.transformer.SearchAndReplaceDownloadableResource;
import com.atlassian.plugin.webresource.transformer.SearchAndReplacer;
import com.atlassian.plugin.webresource.transformer.TransformableResource;
import com.atlassian.plugin.webresource.transformer.TransformerParameters;
import com.atlassian.plugin.webresource.transformer.UrlReadingWebResourceTransformer;
import com.atlassian.plugin.webresource.url.UrlBuilder;
import com.atlassian.webresource.api.prebake.Coordinate;
import com.atlassian.webresource.api.prebake.DimensionAwareTransformerUrlBuilder;
import com.atlassian.webresource.api.prebake.DimensionAwareWebResourceTransformerFactory;
import com.atlassian.webresource.api.prebake.Dimensions;
import io.atlassian.util.concurrent.Lazy;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RelativeUrlTransformerFactory
implements DimensionAwareWebResourceTransformerFactory {
    @Deprecated
    public static final String RELATIVE_URL_QUERY_KEY = "relative-url";
    private static final Pattern CSS_URL_PATTERN = Pattern.compile("url\\s*\\(\\s*+([\"'])?+(?!/|https?://|data:)");
    private final WebResourceIntegration webResourceIntegration;
    private final WebResourceUrlProvider webResourceUrlProvider;
    private final CdnResourceUrlTransformer cdnResourceUrlTransformer;
    private final boolean usePluginInstallTimeInsteadOfTheVersionForSnapshotPlugins;

    public RelativeUrlTransformerFactory(WebResourceIntegration webResourceIntegration, WebResourceUrlProvider webResourceUrlProvider, CdnResourceUrlTransformer cdnResourceUrlTransformer) {
        this.webResourceIntegration = webResourceIntegration;
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.cdnResourceUrlTransformer = cdnResourceUrlTransformer;
        this.usePluginInstallTimeInsteadOfTheVersionForSnapshotPlugins = webResourceIntegration.usePluginInstallTimeInsteadOfTheVersionForSnapshotPlugins();
    }

    public Dimensions computeDimensions() {
        return Dimensions.empty().andExactly(RELATIVE_URL_QUERY_KEY, new String[]{"true"}).andAbsent(RELATIVE_URL_QUERY_KEY);
    }

    public DimensionAwareTransformerUrlBuilder makeUrlBuilder(TransformerParameters parameters) {
        return new RelativeUrlTransformerUrlBuilder();
    }

    public UrlReadingWebResourceTransformer makeResourceTransformer(TransformerParameters parameters) {
        return new RelativeUrlTransformer(parameters);
    }

    @VisibleForTesting
    static String removeComponentsBeforePath(String resourceCdnPrefix) {
        int hostnameStart = resourceCdnPrefix.indexOf("//") + 2;
        int pathStart = resourceCdnPrefix.indexOf("/", hostnameStart);
        if (pathStart == -1) {
            return "";
        }
        return resourceCdnPrefix.substring(pathStart);
    }

    class RelativeUrlTransformer
    implements UrlReadingWebResourceTransformer {
        private final TransformerParameters parameters;

        RelativeUrlTransformer(TransformerParameters parameters) {
            this.parameters = parameters;
        }

        public DownloadableResource transform(TransformableResource transformableResource, QueryParams params) {
            boolean requestCdnUrl = Boolean.parseBoolean(params.get(RelativeUrlTransformerFactory.RELATIVE_URL_QUERY_KEY));
            Supplier<String> urlPrefix = this.createUrlPrefixRef(RelativeUrlTransformerFactory.this.cdnResourceUrlTransformer, requestCdnUrl);
            Function<Matcher, CharSequence> replacer = matcher -> new StringBuilder(matcher.group()).append((String)urlPrefix.get());
            SearchAndReplacer grep = SearchAndReplacer.create(CSS_URL_PATTERN, replacer);
            return new SearchAndReplaceDownloadableResource(transformableResource.nextResource(), grep);
        }

        private Supplier<String> createUrlPrefixRef(CdnResourceUrlTransformer cdnResourceUrlTransformer, boolean requestCdnUrl) {
            return Lazy.supplier(() -> {
                String version = Config.getPluginVersionOrInstallTime(RelativeUrlTransformerFactory.this.webResourceIntegration.getPluginAccessor().getPlugin(this.parameters.getPluginKey()), RelativeUrlTransformerFactory.this.usePluginInstallTimeInsteadOfTheVersionForSnapshotPlugins);
                String resourcePrefix = "/download/resources";
                String localRelativeUrl = RelativeUrlTransformerFactory.this.webResourceUrlProvider.getStaticResourcePrefix(version, UrlMode.RELATIVE) + resourcePrefix + "/" + this.parameters.getPluginKey() + ":" + this.parameters.getModuleKey() + "/";
                if (!requestCdnUrl) {
                    return localRelativeUrl;
                }
                CDNStrategy cdnStrategy = RelativeUrlTransformerFactory.this.webResourceIntegration.getCDNStrategy();
                if (cdnStrategy == null || !cdnStrategy.supportsCdn()) {
                    throw new CdnStrategyChangedException("CDN strategy has changed between url generation time and resource fetch time");
                }
                String resourceCdnPrefix = cdnResourceUrlTransformer.getResourceCdnPrefix(localRelativeUrl);
                return RelativeUrlTransformerFactory.removeComponentsBeforePath(resourceCdnPrefix);
            });
        }
    }

    class RelativeUrlTransformerUrlBuilder
    implements DimensionAwareTransformerUrlBuilder {
        RelativeUrlTransformerUrlBuilder() {
        }

        public void addToUrl(UrlBuilder urlBuilder) {
            if (RelativeUrlTransformerFactory.this.webResourceIntegration.getCDNStrategy() != null && RelativeUrlTransformerFactory.this.webResourceIntegration.getCDNStrategy().supportsCdn()) {
                this.addRelativeUrlQueryKey(urlBuilder);
            }
        }

        public void addToUrl(UrlBuilder urlBuilder, Coordinate coord) {
            if (coord.get(RelativeUrlTransformerFactory.RELATIVE_URL_QUERY_KEY) != null) {
                this.addRelativeUrlQueryKey(urlBuilder);
            }
        }

        private void addRelativeUrlQueryKey(UrlBuilder urlBuilder) {
            urlBuilder.addToQueryString(RelativeUrlTransformerFactory.RELATIVE_URL_QUERY_KEY, String.valueOf(true));
        }
    }

    public static class CdnStrategyChangedException
    extends RuntimeException {
        public CdnStrategyChangedException(String message) {
            super(message);
        }
    }
}

