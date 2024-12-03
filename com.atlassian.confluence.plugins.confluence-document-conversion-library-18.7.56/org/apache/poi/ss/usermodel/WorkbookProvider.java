/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.usermodel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.usermodel.Workbook;

public interface WorkbookProvider {
    public boolean accepts(FileMagic var1);

    public Workbook create();

    public Workbook create(InputStream var1) throws IOException;

    public Workbook create(InputStream var1, String var2) throws IOException;

    public Workbook create(DirectoryNode var1, String var2) throws IOException;

    public Workbook create(File var1, String var2, boolean var3) throws IOException;
}

