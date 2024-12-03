/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.FilenameUtils
 *  org.springframework.core.io.InputStreamSource
 */
package com.atlassian.confluence.importexport.resource;

import com.atlassian.confluence.importexport.resource.DownloadResourceManager;
import com.atlassian.confluence.importexport.resource.DownloadResourceNotFoundException;
import com.atlassian.confluence.importexport.resource.DownloadResourcePrefixEnum;
import com.atlassian.confluence.importexport.resource.DownloadResourceReader;
import com.atlassian.confluence.importexport.resource.GenericDownloadResourceReader;
import com.atlassian.confluence.importexport.resource.ResourceAccessor;
import com.atlassian.confluence.importexport.resource.UnauthorizedDownloadResourceException;
import com.atlassian.confluence.util.io.ConfluenceFileUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.InputStreamSource;

public class PackageResourceManager
implements DownloadResourceManager {
    private ResourceAccessor resourceAccessor;
    private static final String BUNDLE_PLUGIN_PATH_REQUEST_PREFIX = DownloadResourcePrefixEnum.PACKAGE_DOWNLOAD_RESOURCE_PREFIX.getPrefix();
    private static final String BUNDLE_PLUGIN_PATH = "/WEB-INF" + BUNDLE_PLUGIN_PATH_REQUEST_PREFIX;

    public PackageResourceManager(ResourceAccessor resourceAccessor) {
        this.resourceAccessor = resourceAccessor;
    }

    @Override
    public boolean matches(String resourcePath) {
        return resourcePath.startsWith(BUNDLE_PLUGIN_PATH_REQUEST_PREFIX);
    }

    @Override
    public DownloadResourceReader getResourceReader(String userName, String resourcePath, Map parameters) throws UnauthorizedDownloadResourceException, DownloadResourceNotFoundException {
        InputStream resource = null;
        String relativePath = BUNDLE_PLUGIN_PATH + resourcePath.substring(BUNDLE_PLUGIN_PATH_REQUEST_PREFIX.length());
        File bundledPluginsDir = new File(BUNDLE_PLUGIN_PATH);
        File requestedFile = new File(relativePath);
        if (ConfluenceFileUtils.isChildOf(bundledPluginsDir.getAbsoluteFile(), requestedFile.getAbsoluteFile())) {
            resource = this.resourceAccessor.getResource(relativePath);
        }
        if (resource == null) {
            throw new DownloadResourceNotFoundException("Could not find file: " + resourcePath);
        }
        String name = FilenameUtils.getBaseName((String)relativePath);
        return new GenericDownloadResourceReader(name, new BundlePluginInputStreamSource(resource));
    }

    private static class BundlePluginInputStreamSource
    implements InputStreamSource {
        private final InputStream inputStream;

        public BundlePluginInputStreamSource(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public InputStream getInputStream() throws IOException {
            return this.inputStream;
        }
    }
}

