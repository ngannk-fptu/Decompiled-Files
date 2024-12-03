/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.pdf;

import com.lowagie.text.DocumentException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.xhtmlrenderer.pdf.ITextRenderer;

public class ToPDF {
    public static void main(String[] args) throws IOException, DocumentException {
        File f;
        String url;
        if (args.length != 2) {
            System.err.println("Usage: ... [url] [pdf]");
            System.exit(1);
        }
        if ((url = args[0]).indexOf("://") == -1 && (f = new File(url)).exists()) {
            url = f.toURI().toURL().toString();
        }
        ToPDF.createPDF(url, args[1]);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void createPDF(String url, String pdf) throws IOException, DocumentException {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(pdf);
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocument(url);
            renderer.layout();
            renderer.createPDF(os);
            ((OutputStream)os).close();
            os = null;
        }
        finally {
            if (os != null) {
                try {
                    ((OutputStream)os).close();
                }
                catch (IOException iOException) {}
            }
        }
    }
}

