/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.spool;

import com.atlassian.core.spool.FileFactory;
import com.atlassian.core.spool.Spool;

public interface FileSpool
extends Spool {
    public FileFactory getFileFactory();

    public void setFileFactory(FileFactory var1);
}

