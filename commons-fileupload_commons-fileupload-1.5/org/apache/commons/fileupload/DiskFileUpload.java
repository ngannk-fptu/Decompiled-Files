/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.apache.commons.fileupload;

import java.io.File;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.DefaultFileItemFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;

@Deprecated
public class DiskFileUpload
extends FileUploadBase {
    private DefaultFileItemFactory fileItemFactory;

    @Deprecated
    public DiskFileUpload() {
        this.fileItemFactory = new DefaultFileItemFactory();
    }

    @Deprecated
    public DiskFileUpload(DefaultFileItemFactory fileItemFactory) {
        this.fileItemFactory = fileItemFactory;
    }

    @Override
    @Deprecated
    public FileItemFactory getFileItemFactory() {
        return this.fileItemFactory;
    }

    @Override
    @Deprecated
    public void setFileItemFactory(FileItemFactory factory) {
        this.fileItemFactory = (DefaultFileItemFactory)factory;
    }

    @Deprecated
    public int getSizeThreshold() {
        return this.fileItemFactory.getSizeThreshold();
    }

    @Deprecated
    public void setSizeThreshold(int sizeThreshold) {
        this.fileItemFactory.setSizeThreshold(sizeThreshold);
    }

    @Deprecated
    public String getRepositoryPath() {
        return this.fileItemFactory.getRepository().getPath();
    }

    @Deprecated
    public void setRepositoryPath(String repositoryPath) {
        this.fileItemFactory.setRepository(new File(repositoryPath));
    }

    @Deprecated
    public List<FileItem> parseRequest(HttpServletRequest req, int sizeThreshold, long sizeMax, String path) throws FileUploadException {
        this.setSizeThreshold(sizeThreshold);
        this.setSizeMax(sizeMax);
        this.setRepositoryPath(path);
        return this.parseRequest(req);
    }
}

