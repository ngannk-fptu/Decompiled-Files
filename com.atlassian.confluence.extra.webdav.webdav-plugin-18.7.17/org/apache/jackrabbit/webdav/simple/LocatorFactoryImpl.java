/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.simple;

import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.webdav.DavLocatorFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocatorFactoryImpl
implements DavLocatorFactory {
    private static final Logger log = LoggerFactory.getLogger(LocatorFactoryImpl.class);
    private final String repositoryPrefix;

    public LocatorFactoryImpl(String repositoryPrefix) {
        this.repositoryPrefix = repositoryPrefix;
    }

    @Override
    public DavResourceLocator createResourceLocator(String prefix, String href) {
        StringBuffer b = new StringBuffer("");
        if (prefix != null && prefix.length() > 0) {
            b.append(prefix);
            if (href.startsWith(prefix)) {
                href = href.substring(prefix.length());
            }
        }
        if (this.repositoryPrefix != null && this.repositoryPrefix.length() > 0 && !prefix.endsWith(this.repositoryPrefix)) {
            b.append(this.repositoryPrefix);
            if (href.startsWith(this.repositoryPrefix)) {
                href = href.substring(this.repositoryPrefix.length());
            }
        }
        if (href == null || "".equals(href)) {
            href = "/";
        }
        return new Locator(b.toString(), Text.unescape(href), this);
    }

    @Override
    public DavResourceLocator createResourceLocator(String prefix, String workspacePath, String resourcePath) {
        return this.createResourceLocator(prefix, workspacePath, resourcePath, true);
    }

    @Override
    public DavResourceLocator createResourceLocator(String prefix, String workspacePath, String path, boolean isResourcePath) {
        return new Locator(prefix, path, this);
    }

    private static class Locator
    implements DavResourceLocator {
        private final String prefix;
        private final String resourcePath;
        private final DavLocatorFactory factory;
        private final String href;

        private Locator(String prefix, String resourcePath, DavLocatorFactory factory) {
            this.prefix = prefix;
            this.factory = factory;
            if (resourcePath.endsWith("/") && !"/".equals(resourcePath)) {
                resourcePath = resourcePath.substring(0, resourcePath.length() - 1);
            }
            this.resourcePath = resourcePath;
            this.href = prefix + Text.escapePath(resourcePath);
        }

        @Override
        public String getPrefix() {
            return this.prefix;
        }

        @Override
        public String getResourcePath() {
            return this.resourcePath;
        }

        @Override
        public String getWorkspacePath() {
            return "";
        }

        @Override
        public String getWorkspaceName() {
            return "";
        }

        @Override
        public boolean isSameWorkspace(DavResourceLocator locator) {
            return this.isSameWorkspace(locator.getWorkspaceName());
        }

        @Override
        public boolean isSameWorkspace(String workspaceName) {
            return this.getWorkspaceName().equals(workspaceName);
        }

        @Override
        public String getHref(boolean isCollection) {
            String suffix = isCollection && !this.isRootLocation() ? "/" : "";
            return this.href + suffix;
        }

        @Override
        public boolean isRootLocation() {
            return "/".equals(this.resourcePath);
        }

        @Override
        public DavLocatorFactory getFactory() {
            return this.factory;
        }

        @Override
        public String getRepositoryPath() {
            return this.getResourcePath();
        }

        public int hashCode() {
            return this.href.hashCode();
        }

        public boolean equals(Object obj) {
            if (obj instanceof DavResourceLocator) {
                DavResourceLocator other = (DavResourceLocator)obj;
                return this.hashCode() == other.hashCode();
            }
            return false;
        }
    }
}

