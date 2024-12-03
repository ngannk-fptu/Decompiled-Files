/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.InputStreamSource
 */
package com.atlassian.confluence.importexport.resource;

import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.importexport.resource.DownloadResourceManager;
import com.atlassian.confluence.importexport.resource.DownloadResourceNotFoundException;
import com.atlassian.confluence.importexport.resource.DownloadResourceReader;
import com.atlassian.confluence.importexport.resource.ResourceAccessor;
import com.atlassian.confluence.importexport.resource.UnauthorizedDownloadResourceException;
import com.atlassian.confluence.importexport.resource.WebImagesDownloadResourceReader;
import com.atlassian.confluence.util.io.ConfluenceFileUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.springframework.core.io.InputStreamSource;

public class WebImagesDownloadResourceManager
implements DownloadResourceManager {
    private ResourceAccessor resourceAccessor;
    private ContextPathHolder contextPathHolder;
    private static final String WEB_IMAGES_PATH = "/images";

    public WebImagesDownloadResourceManager(ResourceAccessor resourceAccessor, ContextPathHolder contextPathHolder) {
        this.resourceAccessor = resourceAccessor;
        this.contextPathHolder = contextPathHolder;
    }

    @Override
    public boolean matches(String resourcePath) {
        return resourcePath.startsWith(this.contextPathHolder.getContextPath() + WEB_IMAGES_PATH);
    }

    @Override
    public DownloadResourceReader getResourceReader(String userName, String resourcePath, Map parameters) throws UnauthorizedDownloadResourceException, DownloadResourceNotFoundException {
        String relativeResourcePath = resourcePath.substring(resourcePath.indexOf(WEB_IMAGES_PATH));
        File file = new File(relativeResourcePath);
        File images = new File(WEB_IMAGES_PATH);
        if (ConfluenceFileUtils.isChildOf(images.getAbsoluteFile(), file.getAbsoluteFile())) {
            return new WebImagesDownloadResourceReader(resourcePath, new WebImagesInputStreamSource(relativeResourcePath));
        }
        throw new DownloadResourceNotFoundException("File is not child of web images: " + resourcePath);
    }

    private class WebImagesInputStreamSource
    implements InputStreamSource {
        private String resourcePath;

        public WebImagesInputStreamSource(String resourcePath) {
            this.resourcePath = resourcePath;
        }

        public InputStream getInputStream() throws IOException {
            return WebImagesDownloadResourceManager.this.resourceAccessor.getResource(this.resourcePath);
        }
    }
}

