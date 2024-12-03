/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.fileupload;

import java.io.IOException;
import java.util.List;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileUploadException;

public interface FileItemIterator {
    public long getFileSizeMax();

    public void setFileSizeMax(long var1);

    public long getSizeMax();

    public void setSizeMax(long var1);

    public boolean hasNext() throws FileUploadException, IOException;

    public FileItemStream next() throws FileUploadException, IOException;

    public List<FileItem> getFileItems() throws FileUploadException, IOException;
}

