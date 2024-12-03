/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.util;

import java.awt.Graphics2D;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import org.apache.poi.util.Internal;
import org.apache.poi.xslf.util.MFProxy;

@Internal
interface OutputFormat
extends Closeable {
    public Graphics2D addSlide(double var1, double var3) throws IOException;

    public void writeSlide(MFProxy var1, File var2) throws IOException;

    default public void writeDocument(MFProxy proxy, File outFile) throws IOException {
    }
}

