/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.usermodel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.sl.usermodel.Shape;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.sl.usermodel.TextRun;

public interface SlideShowProvider<S extends Shape<S, P>, P extends TextParagraph<S, P, ? extends TextRun>> {
    public boolean accepts(FileMagic var1);

    public SlideShow<S, P> create();

    public SlideShow<S, P> create(InputStream var1) throws IOException;

    public SlideShow<S, P> create(InputStream var1, String var2) throws IOException;

    public SlideShow<S, P> create(DirectoryNode var1, String var2) throws IOException;

    public SlideShow<S, P> create(File var1, String var2, boolean var3) throws IOException;
}

