/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.springframework.web.servlet.resource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.resource.VersionPathStrategy;
import org.springframework.web.servlet.resource.VersionStrategy;

public abstract class AbstractVersionStrategy
implements VersionStrategy {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final VersionPathStrategy pathStrategy;

    protected AbstractVersionStrategy(VersionPathStrategy pathStrategy) {
        Assert.notNull((Object)pathStrategy, "VersionPathStrategy is required");
        this.pathStrategy = pathStrategy;
    }

    public VersionPathStrategy getVersionPathStrategy() {
        return this.pathStrategy;
    }

    @Override
    @Nullable
    public String extractVersion(String requestPath) {
        return this.pathStrategy.extractVersion(requestPath);
    }

    @Override
    public String removeVersion(String requestPath, String version) {
        return this.pathStrategy.removeVersion(requestPath, version);
    }

    @Override
    public String addVersion(String requestPath, String version) {
        return this.pathStrategy.addVersion(requestPath, version);
    }

    protected static class FileNameVersionPathStrategy
    implements VersionPathStrategy {
        private static final Pattern pattern = Pattern.compile("-(\\S*)\\.");

        protected FileNameVersionPathStrategy() {
        }

        @Override
        @Nullable
        public String extractVersion(String requestPath) {
            Matcher matcher = pattern.matcher(requestPath);
            if (matcher.find()) {
                String match = matcher.group(1);
                return match.contains("-") ? match.substring(match.lastIndexOf(45) + 1) : match;
            }
            return null;
        }

        @Override
        public String removeVersion(String requestPath, String version) {
            return StringUtils.delete(requestPath, "-" + version);
        }

        @Override
        public String addVersion(String requestPath, String version) {
            String baseFilename = StringUtils.stripFilenameExtension(requestPath);
            String extension = StringUtils.getFilenameExtension(requestPath);
            return baseFilename + '-' + version + '.' + extension;
        }
    }

    protected static class PrefixVersionPathStrategy
    implements VersionPathStrategy {
        private final String prefix;

        public PrefixVersionPathStrategy(String version) {
            Assert.hasText(version, "Version must not be empty");
            this.prefix = version;
        }

        @Override
        @Nullable
        public String extractVersion(String requestPath) {
            return requestPath.startsWith(this.prefix) ? this.prefix : null;
        }

        @Override
        public String removeVersion(String requestPath, String version) {
            return requestPath.substring(this.prefix.length());
        }

        @Override
        public String addVersion(String path, String version) {
            if (path.startsWith(".")) {
                return path;
            }
            return this.prefix.endsWith("/") || path.startsWith("/") ? this.prefix + path : this.prefix + '/' + path;
        }
    }
}

