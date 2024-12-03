/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletRequest
 *  javax.servlet.http.HttpServletRequest
 */
package org.springframework.web.servlet.resource;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.server.PathContainer;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.servlet.resource.AbstractResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.UrlPathHelper;

public class PathResourceResolver
extends AbstractResourceResolver {
    @Nullable
    private Resource[] allowedLocations;
    private final Map<Resource, Charset> locationCharsets = new HashMap<Resource, Charset>(4);
    @Nullable
    private UrlPathHelper urlPathHelper;

    public void setAllowedLocations(Resource ... locations) {
        this.allowedLocations = locations;
    }

    @Nullable
    public Resource[] getAllowedLocations() {
        return this.allowedLocations;
    }

    public void setLocationCharsets(Map<Resource, Charset> locationCharsets) {
        this.locationCharsets.clear();
        this.locationCharsets.putAll(locationCharsets);
    }

    public Map<Resource, Charset> getLocationCharsets() {
        return Collections.unmodifiableMap(this.locationCharsets);
    }

    public void setUrlPathHelper(@Nullable UrlPathHelper urlPathHelper) {
        this.urlPathHelper = urlPathHelper;
    }

    @Nullable
    public UrlPathHelper getUrlPathHelper() {
        return this.urlPathHelper;
    }

    @Override
    protected Resource resolveResourceInternal(@Nullable HttpServletRequest request, String requestPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        return this.getResource(requestPath, request, locations);
    }

    @Override
    protected String resolveUrlPathInternal(String resourcePath, List<? extends Resource> locations, ResourceResolverChain chain) {
        return StringUtils.hasText(resourcePath) && this.getResource(resourcePath, null, locations) != null ? resourcePath : null;
    }

    @Nullable
    private Resource getResource(String resourcePath, @Nullable HttpServletRequest request, List<? extends Resource> locations) {
        for (Resource resource : locations) {
            try {
                String pathToUse = this.encodeOrDecodeIfNecessary(resourcePath, request, resource);
                Resource resource2 = this.getResource(pathToUse, resource);
                if (resource2 == null) continue;
                return resource2;
            }
            catch (IOException ex) {
                if (!this.logger.isDebugEnabled()) continue;
                String error = "Skip location [" + resource + "] due to error";
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace((Object)error, (Throwable)ex);
                    continue;
                }
                this.logger.debug((Object)(error + ": " + ex.getMessage()));
            }
        }
        return null;
    }

    @Nullable
    protected Resource getResource(String resourcePath, Resource location) throws IOException {
        Resource resource = location.createRelative(resourcePath);
        if (resource.isReadable()) {
            if (this.checkResource(resource, location)) {
                return resource;
            }
            if (this.logger.isWarnEnabled()) {
                Resource[] allowed = this.getAllowedLocations();
                this.logger.warn((Object)LogFormatUtils.formatValue("Resource path \"" + resourcePath + "\" was successfully resolved but resource \"" + resource.getURL() + "\" is neither under the current location \"" + location.getURL() + "\" nor under any of the allowed locations " + (allowed != null ? Arrays.asList(allowed) : "[]"), -1, true));
            }
        }
        return null;
    }

    protected boolean checkResource(Resource resource, Resource location) throws IOException {
        if (this.isResourceUnderLocation(resource, location)) {
            return true;
        }
        Resource[] allowedLocations = this.getAllowedLocations();
        if (allowedLocations != null) {
            for (Resource current : allowedLocations) {
                if (!this.isResourceUnderLocation(resource, current)) continue;
                return true;
            }
        }
        return false;
    }

    private boolean isResourceUnderLocation(Resource resource, Resource location) throws IOException {
        String locationPath;
        String resourcePath;
        if (resource.getClass() != location.getClass()) {
            return false;
        }
        if (resource instanceof UrlResource) {
            resourcePath = resource.getURL().toExternalForm();
            locationPath = StringUtils.cleanPath(location.getURL().toString());
        } else if (resource instanceof ClassPathResource) {
            resourcePath = ((ClassPathResource)resource).getPath();
            locationPath = StringUtils.cleanPath(((ClassPathResource)location).getPath());
        } else if (resource instanceof ServletContextResource) {
            resourcePath = ((ServletContextResource)resource).getPath();
            locationPath = StringUtils.cleanPath(((ServletContextResource)location).getPath());
        } else {
            resourcePath = resource.getURL().getPath();
            locationPath = StringUtils.cleanPath(location.getURL().getPath());
        }
        if (locationPath.equals(resourcePath)) {
            return true;
        }
        locationPath = locationPath.endsWith("/") || locationPath.isEmpty() ? locationPath : locationPath + "/";
        return resourcePath.startsWith(locationPath) && !this.isInvalidEncodedPath(resourcePath);
    }

    private String encodeOrDecodeIfNecessary(String path, @Nullable HttpServletRequest request, Resource location) {
        if (request != null) {
            boolean usesPathPattern;
            boolean bl = usesPathPattern = ServletRequestPathUtils.hasCachedPath((ServletRequest)request) && ServletRequestPathUtils.getCachedPath((ServletRequest)request) instanceof PathContainer;
            if (this.shouldDecodeRelativePath(location, usesPathPattern)) {
                return UriUtils.decode(path, StandardCharsets.UTF_8);
            }
            if (this.shouldEncodeRelativePath(location, usesPathPattern)) {
                Charset charset = this.locationCharsets.getOrDefault(location, StandardCharsets.UTF_8);
                StringBuilder sb = new StringBuilder();
                StringTokenizer tokenizer = new StringTokenizer(path, "/");
                while (tokenizer.hasMoreTokens()) {
                    String value = UriUtils.encode(tokenizer.nextToken(), charset);
                    sb.append(value);
                    sb.append('/');
                }
                if (!path.endsWith("/")) {
                    sb.setLength(sb.length() - 1);
                }
                return sb.toString();
            }
        }
        return path;
    }

    private boolean shouldDecodeRelativePath(Resource location, boolean usesPathPattern) {
        return !(location instanceof UrlResource) && (usesPathPattern || this.urlPathHelper != null && !this.urlPathHelper.isUrlDecode());
    }

    private boolean shouldEncodeRelativePath(Resource location, boolean usesPathPattern) {
        return location instanceof UrlResource && !usesPathPattern && this.urlPathHelper != null && this.urlPathHelper.isUrlDecode();
    }

    private boolean isInvalidEncodedPath(String resourcePath) {
        if (resourcePath.contains("%")) {
            try {
                String decodedPath = URLDecoder.decode(resourcePath, "UTF-8");
                if (decodedPath.contains("../") || decodedPath.contains("..\\")) {
                    this.logger.warn((Object)LogFormatUtils.formatValue("Resolved resource path contains encoded \"../\" or \"..\\\": " + resourcePath, -1, true));
                    return true;
                }
            }
            catch (IllegalArgumentException illegalArgumentException) {
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                // empty catch block
            }
        }
        return false;
    }
}

