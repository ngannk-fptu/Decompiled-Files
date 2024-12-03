/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.multipart;

import com.atlassian.plugins.rest.common.multipart.FilePart;
import java.util.Collection;

public interface MultipartForm {
    public FilePart getFilePart(String var1);

    public Collection<FilePart> getFileParts(String var1);
}

