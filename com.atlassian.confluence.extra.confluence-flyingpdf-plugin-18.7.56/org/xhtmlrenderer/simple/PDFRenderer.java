/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.simple;

import com.lowagie.text.DocumentException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import org.xhtmlrenderer.pdf.ITextRenderer;

public class PDFRenderer {
    private static final Map versionMap = new HashMap();

    public static void renderToPDF(String url, String pdf) throws IOException, DocumentException {
        PDFRenderer.renderToPDF(url, pdf, null);
    }

    public static void renderToPDF(String url, String pdf, Character pdfVersion) throws IOException, DocumentException {
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocument(url);
        if (pdfVersion != null) {
            renderer.setPDFVersion(pdfVersion.charValue());
        }
        PDFRenderer.doRenderToPDF(renderer, pdf);
    }

    public static void renderToPDF(File file, String pdf) throws IOException, DocumentException {
        PDFRenderer.renderToPDF(file, pdf, null);
    }

    public static void renderToPDF(File file, String pdf, Character pdfVersion) throws IOException, DocumentException {
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocument(file);
        if (pdfVersion != null) {
            renderer.setPDFVersion(pdfVersion.charValue());
        }
        PDFRenderer.doRenderToPDF(renderer, pdf);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void doRenderToPDF(ITextRenderer renderer, String pdf) throws IOException, DocumentException {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(pdf);
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

    public static void main(String[] args) throws IOException, DocumentException {
        String url;
        if (args.length < 2) {
            PDFRenderer.usage("Incorrect argument list.");
        }
        Character pdfVersion = null;
        if (args.length == 3) {
            pdfVersion = PDFRenderer.checkVersion(args[2]);
        }
        if ((url = args[0]).indexOf("://") == -1) {
            File f = new File(url);
            if (f.exists()) {
                PDFRenderer.renderToPDF(f, args[1], pdfVersion);
            } else {
                PDFRenderer.usage("File to render is not found: " + url);
            }
        } else {
            PDFRenderer.renderToPDF(url, args[1], pdfVersion);
        }
    }

    private static Character checkVersion(String version) {
        Character val = (Character)versionMap.get(version.trim());
        if (val == null) {
            PDFRenderer.usage("Invalid PDF version number; use 1.2 through 1.7");
        }
        return val;
    }

    private static void usage(String err) {
        if (err != null && err.length() > 0) {
            System.err.println("==>" + err);
        }
        System.err.println("Usage: ... url pdf [version]");
        System.err.println("   where version (optional) is between 1.2 and 1.7");
        System.exit(1);
    }

    static {
        versionMap.put("1.2", new Character('2'));
        versionMap.put("1.3", new Character('3'));
        versionMap.put("1.4", new Character('4'));
        versionMap.put("1.5", new Character('5'));
        versionMap.put("1.6", new Character('6'));
        versionMap.put("1.7", new Character('7'));
    }
}

