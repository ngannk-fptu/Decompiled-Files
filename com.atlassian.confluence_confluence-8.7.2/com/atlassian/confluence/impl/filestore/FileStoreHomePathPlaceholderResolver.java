/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.impl.filestore;

import com.atlassian.confluence.impl.filestore.HomePathPlaceholderResolver;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import java.io.File;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public final class FileStoreHomePathPlaceholderResolver
implements HomePathPlaceholderResolver {
    private static final String CONF_HOME_PREFIX = "${confluenceHome}" + File.separator;
    private static final String LOCAL_HOME_PREFIX = "${localHome}" + File.separator;
    private final FilesystemPath localHome;
    private final FilesystemPath confluenceHome;

    public FileStoreHomePathPlaceholderResolver(FilesystemPath localHome, FilesystemPath confluenceHome) {
        this.localHome = localHome;
        this.confluenceHome = confluenceHome;
    }

    @Override
    public Optional<FilesystemPath> resolveFileStorePlaceHolders(String directoryLocation) {
        if (StringUtils.startsWith((CharSequence)directoryLocation, (CharSequence)CONF_HOME_PREFIX)) {
            return Optional.of(this.confluenceHome.path(new String[]{directoryLocation.substring(CONF_HOME_PREFIX.length())}));
        }
        if (StringUtils.startsWith((CharSequence)directoryLocation, (CharSequence)LOCAL_HOME_PREFIX)) {
            return Optional.of(this.localHome.path(new String[]{directoryLocation.substring(LOCAL_HOME_PREFIX.length())}));
        }
        return Optional.empty();
    }
}

