/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.commons.vfs2.FileObject
 *  org.apache.commons.vfs2.FileSystemException
 *  org.apache.commons.vfs2.FileSystemManager
 *  org.apache.commons.vfs2.VFS
 */
package org.apache.commons.configuration2.reloading;

import org.apache.commons.configuration2.ex.ConfigurationRuntimeException;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.commons.configuration2.io.FileSystem;
import org.apache.commons.configuration2.reloading.FileHandlerReloadingDetector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;

public class VFSFileHandlerReloadingDetector
extends FileHandlerReloadingDetector {
    private final Log log = LogFactory.getLog(this.getClass());

    public VFSFileHandlerReloadingDetector() {
    }

    public VFSFileHandlerReloadingDetector(FileHandler handler, long refreshDelay) {
        super(handler, refreshDelay);
    }

    public VFSFileHandlerReloadingDetector(FileHandler handler) {
        super(handler);
    }

    @Override
    protected long getLastModificationDate() {
        FileObject file = this.getFileObject();
        try {
            if (file == null || !file.exists()) {
                return 0L;
            }
            return file.getContent().getLastModifiedTime();
        }
        catch (FileSystemException ex) {
            this.log.error((Object)("Unable to get last modified time for" + file.getName().getURI()), (Throwable)ex);
            return 0L;
        }
    }

    protected FileObject getFileObject() {
        if (!this.getFileHandler().isLocationDefined()) {
            return null;
        }
        try {
            FileSystemManager fsManager = VFS.getManager();
            String uri = this.resolveFileURI();
            if (uri == null) {
                throw new ConfigurationRuntimeException("Unable to determine file to monitor");
            }
            return fsManager.resolveFile(uri);
        }
        catch (FileSystemException fse) {
            String msg = "Unable to monitor " + this.getFileHandler().getURL().toString();
            this.log.error((Object)msg);
            throw new ConfigurationRuntimeException(msg, fse);
        }
    }

    protected String resolveFileURI() {
        FileSystem fs = this.getFileHandler().getFileSystem();
        return fs.getPath(null, this.getFileHandler().getURL(), this.getFileHandler().getBasePath(), this.getFileHandler().getFileName());
    }
}

