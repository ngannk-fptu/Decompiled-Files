/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.bonnie.Searchable;
import java.io.IOException;
import java.io.InputStream;

public interface SearchableAttachment
extends Searchable {
    public String getContentType();

    public String getFileName();

    public InputStream getContentsAsStream() throws IOException;

    public String getComment();

    public String getNiceType();

    public String getNiceFileSize();

    public String getDownloadPath();
}

