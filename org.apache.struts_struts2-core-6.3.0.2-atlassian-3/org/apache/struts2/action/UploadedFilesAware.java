/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.action;

import java.util.List;
import org.apache.struts2.dispatcher.multipart.UploadedFile;

public interface UploadedFilesAware {
    public void withUploadedFiles(List<UploadedFile> var1);
}

