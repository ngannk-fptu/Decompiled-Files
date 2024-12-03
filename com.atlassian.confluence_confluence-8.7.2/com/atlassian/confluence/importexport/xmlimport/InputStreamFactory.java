/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.importexport.xmlimport;

import com.atlassian.confluence.importexport.ImportExportException;
import java.io.InputStream;

@Deprecated
public interface InputStreamFactory {
    public InputStream newInputStream() throws ImportExportException;
}

