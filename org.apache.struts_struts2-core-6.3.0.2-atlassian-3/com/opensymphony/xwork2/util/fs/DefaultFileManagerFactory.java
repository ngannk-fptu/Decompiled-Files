/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package com.opensymphony.xwork2.util.fs;

import com.opensymphony.xwork2.FileManager;
import com.opensymphony.xwork2.FileManagerFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DefaultFileManagerFactory
implements FileManagerFactory {
    private static final Logger LOG = LogManager.getLogger(DefaultFileManagerFactory.class);
    private boolean reloadingConfigs;
    private FileManagerHolder fileManagerHolder;
    private FileManager systemFileManager;
    private Container container;

    @Inject(value="system")
    public void setFileManager(FileManager fileManager) {
        this.systemFileManager = fileManager;
    }

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    @Inject(value="struts.configuration.xml.reload", required=false)
    public void setReloadingConfigs(String reloadingConfigs) {
        this.reloadingConfigs = Boolean.parseBoolean(reloadingConfigs);
    }

    @Override
    public FileManager getFileManager() {
        if (this.fileManagerHolder != null) {
            return this.fileManagerHolder.getFileManager();
        }
        FileManager fileManager = this.lookupFileManager();
        if (fileManager != null) {
            LOG.debug("Using FileManager implementation [{}]", (Object)fileManager.getClass().getSimpleName());
            fileManager.setReloadingConfigs(this.reloadingConfigs);
            this.fileManagerHolder = new FileManagerHolder(fileManager);
            return fileManager;
        }
        LOG.debug("Using default implementation of FileManager provided under name [system]: {}", (Object)this.systemFileManager.getClass().getSimpleName());
        this.systemFileManager.setReloadingConfigs(this.reloadingConfigs);
        this.fileManagerHolder = new FileManagerHolder(this.systemFileManager);
        return this.systemFileManager;
    }

    private FileManager lookupFileManager() {
        Set<String> names = this.container.getInstanceNames(FileManager.class);
        LOG.debug("Found following implementations of FileManager interface: {}", names);
        HashSet<FileManager> internals = new HashSet<FileManager>();
        HashSet<FileManager> users = new HashSet<FileManager>();
        for (String fmName : names) {
            FileManager fm = this.container.getInstance(FileManager.class, fmName);
            if (fm.internal()) {
                internals.add(fm);
                continue;
            }
            users.add(fm);
        }
        for (FileManager fm : users) {
            if (!fm.support()) continue;
            LOG.debug("Using FileManager implementation [{}]", (Object)fm.getClass().getSimpleName());
            return fm;
        }
        LOG.debug("No user defined FileManager, looking up for internal implementations!");
        for (FileManager fm : internals) {
            if (!fm.support()) continue;
            return fm;
        }
        return null;
    }

    private static class FileManagerHolder {
        private final FileManager fileManager;

        public FileManagerHolder(FileManager fileManager) {
            this.fileManager = fileManager;
        }

        public FileManager getFileManager() {
            return this.fileManager;
        }
    }
}

