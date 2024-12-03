/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  com.atlassian.dc.filestore.impl.filesystem.FilesystemFileStore
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.resource;

import com.atlassian.confluence.importexport.resource.DownloadResourceNotFoundException;
import com.atlassian.confluence.importexport.resource.DownloadResourceReader;
import com.atlassian.confluence.importexport.resource.DownloadResourceWriter;
import com.atlassian.confluence.importexport.resource.FileDownloadResourceReader;
import com.atlassian.confluence.importexport.resource.FileDownloadResourceWriter;
import com.atlassian.confluence.importexport.resource.UnauthorizedDownloadResourceException;
import com.atlassian.confluence.importexport.resource.WritableDownloadResourceManager;
import com.atlassian.confluence.security.GateKeeper;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import com.atlassian.dc.filestore.impl.filesystem.FilesystemFileStore;
import com.atlassian.user.User;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportDownloadResourceManager
implements WritableDownloadResourceManager {
    private static final Logger log = LoggerFactory.getLogger(ExportDownloadResourceManager.class);
    private final BootstrapManager bootstrapManager;
    private final GateKeeper gateKeeper;
    private final PermissionManager permissionManager;

    public ExportDownloadResourceManager(BootstrapManager bootstrapManager, GateKeeper gateKeeper, PermissionManager permissionManager) {
        this.bootstrapManager = bootstrapManager;
        this.gateKeeper = gateKeeper;
        this.permissionManager = permissionManager;
    }

    @Override
    public boolean matches(String resourcePath) {
        return resourcePath.startsWith(this.getExportRoot());
    }

    @Override
    public DownloadResourceReader getResourceReader(String userName, String resourcePath, Map parameters) throws UnauthorizedDownloadResourceException, DownloadResourceNotFoundException {
        String path = this.getExportRoot();
        if (resourcePath.startsWith(path)) {
            resourcePath = resourcePath.substring(path.length());
        } else {
            log.error("Incorrect resourcePath specified: " + resourcePath);
        }
        File file = this.exportFileStore().path(new String[]{resourcePath}).asJavaFile();
        if (!this.gateKeeper.isAccessPermitted("download/export/" + resourcePath, userName) || file.isDirectory()) {
            throw new UnauthorizedDownloadResourceException();
        }
        if (!file.exists()) {
            throw new DownloadResourceNotFoundException("Could not find file: " + resourcePath);
        }
        boolean deleteFileAfterUse = parameters.get("delete") != null;
        return new FileDownloadResourceReader(file, deleteFileAfterUse);
    }

    @Override
    public DownloadResourceWriter getResourceWriter(String userName, String prefix, String suffix) {
        File file;
        try {
            File dir = this.exportFileStore().asJavaFile();
            if (!dir.isDirectory() && !dir.mkdirs()) {
                log.warn("Failed to create export directory {}", (Object)dir);
            }
            file = File.createTempFile(prefix, suffix, dir);
        }
        catch (IOException e) {
            throw new RuntimeException("Could not create temporary file for macro output: prefix [" + prefix + "], suffix [" + suffix + "]", e);
        }
        String path = this.getExportRoot() + file.getName();
        Predicate<User> permissionPredicate = u -> this.permissionManager.hasPermission((User)u, Permission.VIEW, PermissionManager.TARGET_APPLICATION);
        this.gateKeeper.addKey(path, StringUtils.isBlank((CharSequence)userName) ? null : userName, permissionPredicate);
        return new FileDownloadResourceWriter(path, file);
    }

    private String getExportRoot() {
        return this.bootstrapManager.getWebAppContextPath() + "/download/export/";
    }

    private String getExportDir() {
        return this.bootstrapManager.getFilePathProperty("struts.multipart.saveDir");
    }

    private FilesystemPath exportFileStore() {
        return FilesystemFileStore.forPath((Path)Paths.get(this.getExportDir(), new String[0]));
    }
}

