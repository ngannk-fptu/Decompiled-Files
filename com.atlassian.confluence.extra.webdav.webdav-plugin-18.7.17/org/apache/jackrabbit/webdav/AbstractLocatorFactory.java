/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav;

import org.apache.jackrabbit.webdav.DavLocatorFactory;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.util.EncodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLocatorFactory
implements DavLocatorFactory {
    private static Logger log = LoggerFactory.getLogger(AbstractLocatorFactory.class);
    private final String pathPrefix;

    public AbstractLocatorFactory(String pathPrefix) {
        this.pathPrefix = pathPrefix;
    }

    @Override
    public DavResourceLocator createResourceLocator(String prefix, String href) {
        String workspacePath;
        String resourcePath;
        if (href == null) {
            throw new IllegalArgumentException("Request handle must not be null.");
        }
        StringBuffer b = new StringBuffer("");
        if (prefix != null && prefix.length() > 0) {
            b.append(prefix);
            if (href.startsWith(prefix)) {
                href = href.substring(prefix.length());
            }
        }
        if (this.pathPrefix != null && this.pathPrefix.length() > 0) {
            if (!b.toString().endsWith(this.pathPrefix)) {
                b.append(this.pathPrefix);
            }
            if (href.startsWith(this.pathPrefix)) {
                href = href.substring(this.pathPrefix.length());
            }
        }
        if (href.endsWith("/")) {
            href = href.substring(0, href.length() - 1);
        }
        if ("".equals(href)) {
            resourcePath = null;
            workspacePath = null;
        } else {
            resourcePath = EncodeUtil.unescape(href);
            int pos = href.indexOf(47, 1);
            workspacePath = pos == -1 ? resourcePath : EncodeUtil.unescape(href.substring(0, pos));
        }
        log.trace("createResourceLocator: prefix='" + prefix + "' href='" + href + "' -> prefix='" + b.toString() + "' workspacePath='" + workspacePath + "' resourcePath='" + resourcePath + "'");
        return new DavResourceLocatorImpl(b.toString(), workspacePath, resourcePath, this);
    }

    public DavResourceLocator createResourceLocator(String prefix, String href, boolean forDestination) {
        return this.createResourceLocator(prefix, href);
    }

    @Override
    public DavResourceLocator createResourceLocator(String prefix, String workspacePath, String resourcePath) {
        return this.createResourceLocator(prefix, workspacePath, resourcePath, true);
    }

    @Override
    public DavResourceLocator createResourceLocator(String prefix, String workspacePath, String path, boolean isResourcePath) {
        String resourcePath = isResourcePath ? path : this.getResourcePath(path, workspacePath);
        return new DavResourceLocatorImpl(prefix, workspacePath, resourcePath, this);
    }

    protected abstract String getRepositoryPath(String var1, String var2);

    protected abstract String getResourcePath(String var1, String var2);

    private class DavResourceLocatorImpl
    implements DavResourceLocator {
        private final String prefix;
        private final String workspacePath;
        private final String resourcePath;
        private final AbstractLocatorFactory factory;
        private final String href;

        private DavResourceLocatorImpl(String prefix, String workspacePath, String resourcePath, AbstractLocatorFactory factory) {
            int length;
            this.prefix = prefix;
            this.workspacePath = workspacePath;
            this.resourcePath = resourcePath;
            this.factory = factory;
            StringBuffer buf = new StringBuffer(prefix);
            if (resourcePath != null && resourcePath.length() > 0) {
                if (!resourcePath.startsWith(workspacePath)) {
                    throw new IllegalArgumentException("Resource path '" + resourcePath + "' does not start with workspace path '" + workspacePath + "'.");
                }
                buf.append(EncodeUtil.escapePath(resourcePath));
            }
            if ((length = buf.length()) == 0 || length > 0 && buf.charAt(length - 1) != '/') {
                buf.append("/");
            }
            this.href = buf.toString();
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
            return this.workspacePath;
        }

        @Override
        public String getWorkspaceName() {
            if (this.workspacePath != null && this.workspacePath.length() > 0) {
                return this.workspacePath.substring(1);
            }
            return null;
        }

        @Override
        public boolean isSameWorkspace(DavResourceLocator locator) {
            return locator == null ? false : this.isSameWorkspace(locator.getWorkspaceName());
        }

        @Override
        public boolean isSameWorkspace(String workspaceName) {
            String thisWspName = this.getWorkspaceName();
            return thisWspName == null ? workspaceName == null : thisWspName.equals(workspaceName);
        }

        @Override
        public String getHref(boolean isCollection) {
            return isCollection ? this.href : this.href.substring(0, this.href.length() - 1);
        }

        @Override
        public boolean isRootLocation() {
            return this.getWorkspacePath() == null;
        }

        @Override
        public DavLocatorFactory getFactory() {
            return this.factory;
        }

        @Override
        public String getRepositoryPath() {
            return this.factory.getRepositoryPath(this.getResourcePath(), this.getWorkspacePath());
        }

        public int hashCode() {
            return this.href.hashCode();
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof DavResourceLocatorImpl) {
                DavResourceLocatorImpl other = (DavResourceLocatorImpl)obj;
                return this.hashCode() == other.hashCode();
            }
            return false;
        }
    }
}

