/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xdgf.util;

import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import org.apache.poi.xdgf.usermodel.XDGFPage;
import org.apache.poi.xdgf.usermodel.XDGFShape;
import org.apache.poi.xdgf.usermodel.XmlVisioDocument;
import org.apache.poi.xdgf.usermodel.shape.ShapeVisitor;
import org.apache.poi.xdgf.util.Util;

public class HierarchyPrinter {
    public static void printHierarchy(XDGFPage page, File outDir) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        File pageFile = new File(outDir, "page" + page.getPageNumber() + "-" + Util.sanitizeFilename(page.getName()) + ".txt");
        try (FileOutputStream os = new FileOutputStream(pageFile);
             PrintStream pos = new PrintStream((OutputStream)os, false, StandardCharsets.UTF_8.name());){
            HierarchyPrinter.printHierarchy(page, pos);
        }
    }

    public static void printHierarchy(XDGFPage page, final PrintStream os) {
        page.getContent().visitShapes(new ShapeVisitor(){

            @Override
            public void visit(XDGFShape shape, AffineTransform globalTransform, int level) {
                for (int i = 0; i < level; ++i) {
                    os.append("  ");
                }
                os.println(shape + " [" + shape.getShapeType() + ", " + shape.getSymbolName() + "] " + shape.getMasterShape() + " " + shape.getTextAsString().trim());
            }
        });
    }

    public static void printHierarchy(XmlVisioDocument document, String outDirname) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        File outDir = new File(outDirname);
        for (XDGFPage page : document.getPages()) {
            HierarchyPrinter.printHierarchy(page, outDir);
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: in.vsdx outdir");
            System.exit(1);
        }
        String inFilename = args[0];
        String outDir = args[1];
        try (FileInputStream is = new FileInputStream(inFilename);){
            XmlVisioDocument doc = new XmlVisioDocument(is);
            HierarchyPrinter.printHierarchy(doc, outDir);
        }
    }
}

