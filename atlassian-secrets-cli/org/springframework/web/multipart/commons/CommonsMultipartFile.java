/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.fileupload.FileItem
 *  org.apache.commons.fileupload.FileUploadException
 *  org.apache.commons.fileupload.disk.DiskFileItem
 */
package org.springframework.web.multipart.commons;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

public class CommonsMultipartFile
implements MultipartFile,
Serializable {
    protected static final Log logger = LogFactory.getLog(CommonsMultipartFile.class);
    private final FileItem fileItem;
    private final long size;
    private boolean preserveFilename = false;

    public CommonsMultipartFile(FileItem fileItem) {
        this.fileItem = fileItem;
        this.size = this.fileItem.getSize();
    }

    public final FileItem getFileItem() {
        return this.fileItem;
    }

    public void setPreserveFilename(boolean preserveFilename) {
        this.preserveFilename = preserveFilename;
    }

    @Override
    public String getName() {
        return this.fileItem.getFieldName();
    }

    @Override
    public String getOriginalFilename() {
        int pos;
        String filename = this.fileItem.getName();
        if (filename == null) {
            return "";
        }
        if (this.preserveFilename) {
            return filename;
        }
        int unixSep = filename.lastIndexOf(47);
        int winSep = filename.lastIndexOf(92);
        int n = pos = winSep > unixSep ? winSep : unixSep;
        if (pos != -1) {
            return filename.substring(pos + 1);
        }
        return filename;
    }

    @Override
    public String getContentType() {
        return this.fileItem.getContentType();
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0L;
    }

    @Override
    public long getSize() {
        return this.size;
    }

    @Override
    public byte[] getBytes() {
        if (!this.isAvailable()) {
            throw new IllegalStateException("File has been moved - cannot be read again");
        }
        byte[] bytes = this.fileItem.get();
        return bytes != null ? bytes : new byte[]{};
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (!this.isAvailable()) {
            throw new IllegalStateException("File has been moved - cannot be read again");
        }
        InputStream inputStream = this.fileItem.getInputStream();
        return inputStream != null ? inputStream : StreamUtils.emptyInput();
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        if (!this.isAvailable()) {
            throw new IllegalStateException("File has already been moved - cannot be transferred again");
        }
        if (dest.exists() && !dest.delete()) {
            throw new IOException("Destination file [" + dest.getAbsolutePath() + "] already exists and could not be deleted");
        }
        try {
            this.fileItem.write(dest);
            if (logger.isDebugEnabled()) {
                String action = "transferred";
                if (!this.fileItem.isInMemory()) {
                    action = this.isAvailable() ? "copied" : "moved";
                }
                logger.debug("Multipart file '" + this.getName() + "' with original filename [" + this.getOriginalFilename() + "], stored " + this.getStorageDescription() + ": " + action + " to [" + dest.getAbsolutePath() + "]");
            }
        }
        catch (FileUploadException ex) {
            throw new IllegalStateException(ex.getMessage(), ex);
        }
        catch (IOException | IllegalStateException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new IOException("File transfer failed", ex);
        }
    }

    protected boolean isAvailable() {
        if (this.fileItem.isInMemory()) {
            return true;
        }
        if (this.fileItem instanceof DiskFileItem) {
            return ((DiskFileItem)this.fileItem).getStoreLocation().exists();
        }
        return this.fileItem.getSize() == this.size;
    }

    public String getStorageDescription() {
        if (this.fileItem.isInMemory()) {
            return "in memory";
        }
        if (this.fileItem instanceof DiskFileItem) {
            return "at [" + ((DiskFileItem)this.fileItem).getStoreLocation().getAbsolutePath() + "]";
        }
        return "on disk";
    }
}

