/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.confluence.dom;

import com.atlassian.plugins.conversion.confluence.parser.ConfluenceImage;
import java.awt.Dimension;
import java.io.IOException;

public interface ImportContext {
    public void importImage(String var1, String var2, byte[] var3) throws IOException;

    public String createHyperlinkReference(StringBuffer var1);

    public boolean imageExists(ConfluenceImage var1);

    public Dimension getMaxImportedImageSize();

    public void finish(StringBuilder var1);
}

