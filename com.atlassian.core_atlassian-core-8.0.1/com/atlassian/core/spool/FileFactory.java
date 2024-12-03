/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.spool;

import java.io.File;
import java.io.IOException;

public interface FileFactory {
    public File createNewFile() throws IOException;
}

