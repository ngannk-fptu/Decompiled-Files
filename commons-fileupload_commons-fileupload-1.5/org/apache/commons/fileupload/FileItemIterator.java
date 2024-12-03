/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.fileupload;

import java.io.IOException;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;

public interface FileItemIterator {
    public boolean hasNext() throws FileUploadException, IOException;

    public FileItemStream next() throws FileUploadException, IOException;
}

