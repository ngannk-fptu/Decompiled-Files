/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.springframework.core.io.AbstractResource
 *  org.springframework.core.io.Resource
 *  org.springframework.http.HttpHeaders
 *  org.springframework.lang.Nullable
 *  org.springframework.util.AntPathMatcher
 *  org.springframework.util.Assert
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.servlet.resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.resource.AbstractResourceResolver;
import org.springframework.web.servlet.resource.ContentVersionStrategy;
import org.springframework.web.servlet.resource.FixedVersionStrategy;
import org.springframework.web.servlet.resource.HttpResource;
import org.springframework.web.servlet.resource.ResourceResolverChain;
import org.springframework.web.servlet.resource.VersionStrategy;

public class VersionResourceResolver
extends AbstractResourceResolver {
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final Map<String, VersionStrategy> versionStrategyMap = new LinkedHashMap<String, VersionStrategy>();

    public void setStrategyMap(Map<String, VersionStrategy> map) {
        this.versionStrategyMap.clear();
        this.versionStrategyMap.putAll(map);
    }

    public Map<String, VersionStrategy> getStrategyMap() {
        return this.versionStrategyMap;
    }

    public VersionResourceResolver addContentVersionStrategy(String ... pathPatterns) {
        this.addVersionStrategy(new ContentVersionStrategy(), pathPatterns);
        return this;
    }

    public VersionResourceResolver addFixedVersionStrategy(String version, String ... pathPatterns) {
        List<String> patternsList = Arrays.asList(pathPatterns);
        ArrayList<String> prefixedPatterns = new ArrayList<String>(pathPatterns.length);
        String versionPrefix = "/" + version;
        for (String pattern : patternsList) {
            prefixedPatterns.add(pattern);
            if (pattern.startsWith(versionPrefix) || patternsList.contains(versionPrefix + pattern)) continue;
            prefixedPatterns.add(versionPrefix + pattern);
        }
        return this.addVersionStrategy(new FixedVersionStrategy(version), StringUtils.toStringArray(prefixedPatterns));
    }

    public VersionResourceResolver addVersionStrategy(VersionStrategy strategy, String ... pathPatterns) {
        for (String pattern : pathPatterns) {
            this.getStrategyMap().put(pattern, strategy);
        }
        return this;
    }

    @Override
    protected Resource resolveResourceInternal(@Nullable HttpServletRequest request, String requestPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        Resource resolved = chain.resolveResource(request, requestPath, locations);
        if (resolved != null) {
            return resolved;
        }
        VersionStrategy versionStrategy = this.getStrategyForPath(requestPath);
        if (versionStrategy == null) {
            return null;
        }
        String candidateVersion = versionStrategy.extractVersion(requestPath);
        if (!StringUtils.hasLength((String)candidateVersion)) {
            return null;
        }
        String simplePath = versionStrategy.removeVersion(requestPath, candidateVersion);
        Resource baseResource = chain.resolveResource(request, simplePath, locations);
        if (baseResource == null) {
            return null;
        }
        String actualVersion = versionStrategy.getResourceVersion(baseResource);
        if (candidateVersion.equals(actualVersion)) {
            return new FileNameVersionedResource(baseResource, candidateVersion);
        }
        if (this.logger.isTraceEnabled()) {
            this.logger.trace((Object)("Found resource for \"" + requestPath + "\", but version [" + candidateVersion + "] does not match"));
        }
        return null;
    }

    @Override
    protected String resolveUrlPathInternal(String resourceUrlPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        String baseUrl = chain.resolveUrlPath(resourceUrlPath, locations);
        if (StringUtils.hasText((String)baseUrl)) {
            VersionStrategy versionStrategy = this.getStrategyForPath(resourceUrlPath);
            if (versionStrategy == null) {
                return baseUrl;
            }
            Resource resource = chain.resolveResource(null, baseUrl, locations);
            Assert.state((resource != null ? 1 : 0) != 0, (String)"Unresolvable resource");
            String version = versionStrategy.getResourceVersion(resource);
            return versionStrategy.addVersion(baseUrl, version);
        }
        return baseUrl;
    }

    @Nullable
    protected VersionStrategy getStrategyForPath(String requestPath) {
        String path = "/".concat(requestPath);
        ArrayList<String> matchingPatterns = new ArrayList<String>();
        for (String pattern : this.versionStrategyMap.keySet()) {
            if (!this.pathMatcher.match(pattern, path)) continue;
            matchingPatterns.add(pattern);
        }
        if (!matchingPatterns.isEmpty()) {
            Comparator comparator = this.pathMatcher.getPatternComparator(path);
            matchingPatterns.sort(comparator);
            return this.versionStrategyMap.get(matchingPatterns.get(0));
        }
        return null;
    }

    private static class FileNameVersionedResource
    extends AbstractResource
    implements HttpResource {
        private final Resource original;
        private final String version;

        public FileNameVersionedResource(Resource original, String version) {
            this.original = original;
            this.version = version;
        }

        public boolean exists() {
            return this.original.exists();
        }

        public boolean isReadable() {
            return this.original.isReadable();
        }

        public boolean isOpen() {
            return this.original.isOpen();
        }

        public boolean isFile() {
            return this.original.isFile();
        }

        public URL getURL() throws IOException {
            return this.original.getURL();
        }

        public URI getURI() throws IOException {
            return this.original.getURI();
        }

        public File getFile() throws IOException {
            return this.original.getFile();
        }

        public InputStream getInputStream() throws IOException {
            return this.original.getInputStream();
        }

        public ReadableByteChannel readableChannel() throws IOException {
            return this.original.readableChannel();
        }

        public long contentLength() throws IOException {
            return this.original.contentLength();
        }

        public long lastModified() throws IOException {
            return this.original.lastModified();
        }

        public Resource createRelative(String relativePath) throws IOException {
            return this.original.createRelative(relativePath);
        }

        @Nullable
        public String getFilename() {
            return this.original.getFilename();
        }

        public String getDescription() {
            return this.original.getDescription();
        }

        @Override
        public HttpHeaders getResponseHeaders() {
            HttpHeaders headers = this.original instanceof HttpResource ? ((HttpResource)this.original).getResponseHeaders() : new HttpHeaders();
            headers.setETag("W/\"" + this.version + "\"");
            return headers;
        }
    }
}

