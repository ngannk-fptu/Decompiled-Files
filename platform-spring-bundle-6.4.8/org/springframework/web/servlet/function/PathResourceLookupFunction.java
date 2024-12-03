/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.web.servlet.function;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.server.PathContainer;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

class PathResourceLookupFunction
implements Function<ServerRequest, Optional<Resource>> {
    private final PathPattern pattern;
    private final Resource location;

    public PathResourceLookupFunction(String pattern, Resource location) {
        Assert.hasLength(pattern, "'pattern' must not be empty");
        Assert.notNull((Object)location, "'location' must not be null");
        this.pattern = PathPatternParser.defaultInstance.parse(pattern);
        this.location = location;
    }

    @Override
    public Optional<Resource> apply(ServerRequest request) {
        PathContainer pathContainer = request.requestPath().pathWithinApplication();
        if (!this.pattern.matches(pathContainer)) {
            return Optional.empty();
        }
        String path = this.processPath((pathContainer = this.pattern.extractPathWithinPattern(pathContainer)).value());
        if (path.contains("%")) {
            path = StringUtils.uriDecode(path, StandardCharsets.UTF_8);
        }
        if (!StringUtils.hasLength(path) || this.isInvalidPath(path)) {
            return Optional.empty();
        }
        try {
            Resource resource = this.location.createRelative(path);
            if (resource.isReadable() && this.isResourceUnderLocation(resource)) {
                return Optional.of(resource);
            }
            return Optional.empty();
        }
        catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    private String processPath(String path) {
        boolean slash = false;
        for (int i2 = 0; i2 < path.length(); ++i2) {
            if (path.charAt(i2) == '/') {
                slash = true;
                continue;
            }
            if (path.charAt(i2) <= ' ' || path.charAt(i2) == '\u007f') continue;
            if (i2 == 0 || i2 == 1 && slash) {
                return path;
            }
            path = slash ? "/" + path.substring(i2) : path.substring(i2);
            return path;
        }
        return slash ? "/" : "";
    }

    private boolean isInvalidPath(String path) {
        if (path.contains("WEB-INF") || path.contains("META-INF")) {
            return true;
        }
        if (path.contains(":/")) {
            String relativePath;
            String string = relativePath = path.charAt(0) == '/' ? path.substring(1) : path;
            if (ResourceUtils.isUrl(relativePath) || relativePath.startsWith("url:")) {
                return true;
            }
        }
        return path.contains("..") && StringUtils.cleanPath(path).contains("../");
    }

    private boolean isResourceUnderLocation(Resource resource) throws IOException {
        String locationPath;
        String resourcePath;
        if (resource.getClass() != this.location.getClass()) {
            return false;
        }
        if (resource instanceof UrlResource) {
            resourcePath = resource.getURL().toExternalForm();
            locationPath = StringUtils.cleanPath(this.location.getURL().toString());
        } else if (resource instanceof ClassPathResource) {
            resourcePath = ((ClassPathResource)resource).getPath();
            locationPath = StringUtils.cleanPath(((ClassPathResource)this.location).getPath());
        } else {
            resourcePath = resource.getURL().getPath();
            locationPath = StringUtils.cleanPath(this.location.getURL().getPath());
        }
        if (locationPath.equals(resourcePath)) {
            return true;
        }
        String string = locationPath = locationPath.endsWith("/") || locationPath.isEmpty() ? locationPath : locationPath + "/";
        if (!resourcePath.startsWith(locationPath)) {
            return false;
        }
        return !resourcePath.contains("%") || !StringUtils.uriDecode(resourcePath, StandardCharsets.UTF_8).contains("../");
    }

    public String toString() {
        return this.pattern + " -> " + this.location;
    }
}

