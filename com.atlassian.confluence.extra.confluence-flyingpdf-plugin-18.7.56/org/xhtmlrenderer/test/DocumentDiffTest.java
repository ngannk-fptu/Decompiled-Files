/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.test;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import org.w3c.dom.Document;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.simple.Graphics2DRenderer;
import org.xhtmlrenderer.util.Uu;
import org.xhtmlrenderer.util.XMLUtil;
import org.xhtmlrenderer.util.XRLog;

public class DocumentDiffTest {
    public static final int width = 500;
    public static final int height = 500;

    public void runTests(File dir, int width, int height) throws Exception {
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; ++i) {
            if (files[i].isDirectory()) {
                this.runTests(files[i], width, height);
                continue;
            }
            if (!files[i].getName().endsWith(".xhtml")) continue;
            String testfile = files[i].getAbsolutePath();
            String difffile = testfile.substring(0, testfile.length() - 6) + ".diff";
            XRLog.log("unittests", Level.WARNING, "test file = " + testfile);
            try {
                boolean is_correct = this.compareTestFile(testfile, difffile, width, height);
                XRLog.log("unittests", Level.WARNING, "is correct = " + is_correct);
                continue;
            }
            catch (Throwable thr) {
                XRLog.log("unittests", Level.WARNING, thr.toString());
                thr.printStackTrace();
            }
        }
    }

    public void generateDiffs(File dir, int width, int height) throws Exception {
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; ++i) {
            if (files[i].isDirectory()) {
                this.generateDiffs(files[i], width, height);
                continue;
            }
            if (!files[i].getName().endsWith(".xhtml")) continue;
            String testfile = files[i].getAbsolutePath();
            String difffile = testfile.substring(0, testfile.length() - 6) + ".diff";
            DocumentDiffTest.generateTestFile(testfile, difffile, width, height);
            Uu.p("generated = " + difffile);
        }
    }

    public static void generateTestFile(String test, String diff, int width, int height) throws Exception {
        Uu.p("test = " + test);
        String out = DocumentDiffTest.xhtmlToDiff(test, width, height);
        Uu.string_to_file(out, new File(diff));
    }

    public static String xhtmlToDiff(String xhtml, int width, int height) throws Exception {
        Document doc = XMLUtil.documentFromFile(xhtml);
        Graphics2DRenderer renderer = new Graphics2DRenderer();
        renderer.setDocument(doc, new File(xhtml).toURL().toString());
        BufferedImage buff = new BufferedImage(width, height, 6);
        Graphics2D g = (Graphics2D)buff.getGraphics();
        Dimension dim = new Dimension(width, height);
        renderer.layout(g, dim);
        renderer.render(g);
        StringBuffer sb = new StringBuffer();
        DocumentDiffTest.getDiff(sb, renderer.getPanel().getRootBox(), "");
        return sb.toString();
    }

    public boolean compareTestFile(String test, String diff, int width, int height) throws Exception {
        String tin = DocumentDiffTest.xhtmlToDiff(test, width, height);
        String din = null;
        try {
            din = Uu.file_to_string(diff);
        }
        catch (FileNotFoundException ex) {
            XRLog.log("unittests", Level.WARNING, "diff file missing");
            return false;
        }
        if (tin.equals(din)) {
            return true;
        }
        XRLog.log("unittests", Level.WARNING, "warning not equals");
        File dfile = new File("correct.diff");
        File tfile = new File("test.diff");
        XRLog.log("unittests", Level.WARNING, "writing to " + dfile + " and " + tfile);
        Uu.string_to_file(tin, tfile);
        Uu.string_to_file(din, dfile);
        return false;
    }

    public static void getDiff(StringBuffer sb, Box box, String tab) {
        for (int i = 0; i < box.getChildCount(); ++i) {
            DocumentDiffTest.getDiff(sb, box.getChild(i), tab + " ");
        }
    }

    public static void main(String[] args) throws Exception {
        XRLog.setLevel("plumbing.general", Level.OFF);
        String file = null;
        file = args.length == 0 ? "tests/diff" : args[0];
        DocumentDiffTest ddt = new DocumentDiffTest();
        if (new File(file).isDirectory()) {
            ddt.runTests(new File(file), 500, 500);
        } else {
            System.out.println(DocumentDiffTest.xhtmlToDiff(file, 1280, 768));
        }
    }
}

