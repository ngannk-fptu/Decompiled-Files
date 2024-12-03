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

public class IconDownloadResourceManager
implements DownloadResourceManager {
    private ResourceAccessor resourceAccessor;
    private static final String ICON_PATH_PREFIX = DownloadResourcePrefixEnum.ICON_DOWNLOAD_RESOURCE_PREFIX.getPrefix();

    public IconDownloadResourceManager(ResourceAccessor resourceAccessor) {
        this.resourceAccessor = resourceAccessor;
    }

    @Override
    public boolean matches(String resourcePath) {
        return resourcePath.contains(ICON_PATH_PREFIX);
    }

    @Override
    public DownloadResourceReader getResourceReader(String userName, String resourcePath, Map parameters) throws UnauthorizedDownloadResourceException, DownloadResourceNotFoundException {
        InputStream resource = null;
        String relativePath = resourcePath.substring(resourcePath.indexOf(ICON_PATH_PREFIX));
        File file = new File(relativePath);
        File icons = new File(ICON_PATH_PREFIX);
        if (ConfluenceFileUtils.isChildOf(icons.getAbsoluteFile(), file.getAbsoluteFile())) {
            resource = this.resourceAccessor.getResource(relativePath);
        }
        String name = FilenameUtils.getBaseName((String)relativePath);
        if (resource == null) {
            throw new DownloadResourceNotFoundException("Could not find file: " + resourcePath);
        }
        return new GenericDownloadResourceReader(name, new IconInputStreamSource(resource));
    }

    private static class IconInputStreamSource
    implements InputStreamSource {
        private final InputStream inputStream;

        public IconInputStreamSource(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        public InputStream getInputStream() throws IOException {
            return this.inputStream;
        }
    }
}

