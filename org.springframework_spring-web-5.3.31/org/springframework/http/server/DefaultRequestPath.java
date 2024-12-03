/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 */
package org.springframework.http.server;

import java.util.List;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.RequestPath;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

class DefaultRequestPath
implements RequestPath {
    private final PathContainer fullPath;
    private final PathContainer contextPath;
    private final PathContainer pathWithinApplication;

    DefaultRequestPath(String rawPath, @Nullable String contextPath) {
        this.fullPath = PathContainer.parsePath(rawPath);
        this.contextPath = DefaultRequestPath.initContextPath(this.fullPath, contextPath);
        this.pathWithinApplication = DefaultRequestPath.extractPathWithinApplication(this.fullPath, this.contextPath);
    }

    private DefaultRequestPath(RequestPath requestPath, String contextPath) {
        this.fullPath = requestPath;
        this.contextPath = DefaultRequestPath.initContextPath(this.fullPath, contextPath);
        this.pathWithinApplication = DefaultRequestPath.extractPathWithinApplication(this.fullPath, this.contextPath);
    }

    private static PathContainer initContextPath(PathContainer path, @Nullable String contextPath) {
        if (!StringUtils.hasText((String)contextPath) || StringUtils.matchesCharacter((String)contextPath, (char)'/')) {
            return PathContainer.parsePath("");
        }
        DefaultRequestPath.validateContextPath(path.value(), contextPath);
        int length = contextPath.length();
        int counter = 0;
        for (int i = 0; i < path.elements().size(); ++i) {
            PathContainer.Element element = path.elements().get(i);
            if (length != (counter += element.value().length())) continue;
            return path.subPath(0, i + 1);
        }
        throw new IllegalStateException("Failed to initialize contextPath '" + contextPath + "' for requestPath '" + path.value() + "'");
    }

    private static void validateContextPath(String fullPath, String contextPath) {
        int length = contextPath.length();
        if (contextPath.charAt(0) != '/' || contextPath.charAt(length - 1) == '/') {
            throw new IllegalArgumentException("Invalid contextPath: '" + contextPath + "': must start with '/' and not end with '/'");
        }
        if (!fullPath.startsWith(contextPath)) {
            throw new IllegalArgumentException("Invalid contextPath '" + contextPath + "': must match the start of requestPath: '" + fullPath + "'");
        }
        if (fullPath.length() > length && fullPath.charAt(length) != '/') {
            throw new IllegalArgumentException("Invalid contextPath '" + contextPath + "': must match to full path segments for requestPath: '" + fullPath + "'");
        }
    }

    private static PathContainer extractPathWithinApplication(PathContainer fullPath, PathContainer contextPath) {
        return fullPath.subPath(contextPath.elements().size());
    }

    @Override
    public String value() {
        return this.fullPath.value();
    }

    @Override
    public List<PathContainer.Element> elements() {
        return this.fullPath.elements();
    }

    @Override
    public PathContainer contextPath() {
        return this.contextPath;
    }

    @Override
    public PathContainer pathWithinApplication() {
        return this.pathWithinApplication;
    }

    @Override
    public RequestPath modifyContextPath(String contextPath) {
        return new DefaultRequestPath(this, contextPath);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        DefaultRequestPath otherPath = (DefaultRequestPath)other;
        return this.fullPath.equals(otherPath.fullPath) && this.contextPath.equals(otherPath.contextPath) && this.pathWithinApplication.equals(otherPath.pathWithinApplication);
    }

    public int hashCode() {
        int result = this.fullPath.hashCode();
        result = 31 * result + this.contextPath.hashCode();
        result = 31 * result + this.pathWithinApplication.hashCode();
        return result;
    }

    public String toString() {
        return this.fullPath.toString();
    }
}

