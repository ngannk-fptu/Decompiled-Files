/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.xslf.util;

import java.awt.Graphics2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.xslf.util.DummyGraphics2d;
import org.apache.poi.xslf.util.MFProxy;
import org.apache.poi.xslf.util.OutputFormat;

public class DummyFormat
implements OutputFormat {
    private final UnsynchronizedByteArrayOutputStream bos;
    private final DummyGraphics2d dummy2d;

    public DummyFormat() {
        try {
            this.bos = new UnsynchronizedByteArrayOutputStream();
            this.dummy2d = new DummyGraphics2d(new PrintStream((OutputStream)this.bos, true, StandardCharsets.UTF_8.name()));
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Graphics2D addSlide(double width, double height) {
        this.bos.reset();
        return this.dummy2d;
    }

    @Override
    public void writeSlide(MFProxy proxy, File outFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outFile);){
            this.bos.writeTo((OutputStream)fos);
            this.bos.reset();
        }
    }

    @Override
    public void writeDocument(MFProxy proxy, File outFile) {
    }

    @Override
    public void close() throws IOException {
        this.bos.reset();
    }
}

