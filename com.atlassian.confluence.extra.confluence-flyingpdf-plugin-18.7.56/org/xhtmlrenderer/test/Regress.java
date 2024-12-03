/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.test;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.swing.BoxRenderer;
import org.xhtmlrenderer.swing.Java2DRenderer;
import org.xhtmlrenderer.util.FSImageWriter;

public class Regress {
    public static final List EXTENSIONS = Arrays.asList("htm", "html", "xht", "xhtml", "xml");
    public static final String RENDER_SFX = ".render.txt";
    public static final String LAYOUT_SFX = ".layout.txt";
    public static final String PNG_SFX = ".png";
    private static final String LINE_SEPARATOR = "\n";
    private final File sourceDir;
    private final File outputDir;
    private final int width;
    private int fileCount;
    private int failedCount;

    public static void main(String[] args) throws Exception {
        File sourceDir;
        File outputDir = sourceDir = Regress.getArgSourceDir(args);
        int width = 1024;
        System.out.println("Running regression against files in " + sourceDir);
        Regress regress = new Regress(sourceDir, outputDir, 1024);
        regress.snapshot();
        System.out.println("Ran regressions against " + regress.getFileCount() + " files in source directory; " + regress.getFailedCount() + " failed to generate");
    }

    public Regress(File sourceDir, File outputDir, int width) {
        this.sourceDir = sourceDir;
        this.outputDir = outputDir;
        this.width = width;
    }

    private int getFailedCount() {
        return this.failedCount;
    }

    private int getFileCount() {
        return this.fileCount;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void snapshot() throws IOException {
        this.fileCount = 0;
        this.failedCount = 0;
        boolean wasLogging = this.enableLogging(false);
        try {
            Iterator iter = this.listInputFiles(this.sourceDir);
            while (iter.hasNext()) {
                File file = (File)iter.next();
                this.saveBoxModel(file, this.outputDir, this.width);
                this.saveImage(file, this.outputDir, this.width);
            }
        }
        finally {
            this.enableLogging(wasLogging);
        }
    }

    private void saveImage(File page, File outputDir, int width) throws IOException {
        try {
            Java2DRenderer j2d = new Java2DRenderer(page, width);
            BufferedImage img = j2d.getImage();
            FSImageWriter imageWriter = new FSImageWriter();
            File outputFile = new File(outputDir, page.getName() + PNG_SFX);
            if (outputFile.exists() && !outputFile.delete()) {
                throw new RuntimeException("On rendering image, could not delete new output file (.delete failed) " + outputFile.getAbsolutePath());
            }
            String fileName = outputFile.getPath();
            imageWriter.write(img, fileName);
        }
        catch (Exception e) {
            System.err.println("Could not render input file to image, skipping: " + page + " err: " + e.getMessage());
        }
    }

    private void saveBoxModel(File page, File outputDir, int width) throws IOException {
        Box box;
        BoxRenderer renderer = new BoxRenderer(page, width);
        try {
            box = renderer.render();
        }
        catch (Exception e) {
            System.err.println("Could not render input file, skipping: " + page + " err: " + e.getMessage());
            ++this.failedCount;
            return;
        }
        LayoutContext layoutContext = renderer.getLayoutContext();
        String inputFileName = page.getName();
        this.writeToFile(outputDir, inputFileName + RENDER_SFX, box.dump(layoutContext, "", 2));
        this.writeToFile(outputDir, inputFileName + LAYOUT_SFX, box.dump(layoutContext, "", 1));
        ++this.fileCount;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeToFile(File outputDir, String fileName, String output) throws IOException {
        File outputFile = new File(outputDir, fileName);
        if (outputFile.exists() && !outputFile.delete()) {
            throw new RuntimeException("On rendering, could not delete new output file (.delete failed) " + outputFile.getAbsolutePath());
        }
        FileOutputStream fos = new FileOutputStream(outputFile);
        try {
            OutputStreamWriter fw = new OutputStreamWriter((OutputStream)fos, "UTF-8");
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
            try {
                pw.print(output);
                pw.print(LINE_SEPARATOR);
                pw.flush();
            }
            finally {
                try {
                    pw.close();
                }
                catch (Exception exception) {}
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                fos.close();
            }
            catch (IOException iOException) {}
        }
    }

    private static File getArgOutputZipFile(String[] args) throws IOException {
        String path;
        File file;
        File parentFile;
        if (args.length < 2) {
            Regress.usageAndExit("Need file name which will contain rendered files as a Zip.");
        }
        if (!(parentFile = (file = new File(path = args[1])).getAbsoluteFile().getParentFile()).exists()) {
            Regress.usageAndExit("Output directory not found: " + parentFile.getPath());
        }
        if (file.exists() && !file.delete()) {
            Regress.usageAndExit("Failed to .delete output Zip file " + file.getAbsoluteFile());
        }
        if (!file.createNewFile()) {
            Regress.usageAndExit("Failed to create output Zip file " + file.getAbsoluteFile());
        }
        return file;
    }

    private static File getArgSourceDir(String[] args) {
        String sourceDirPath;
        File sourceDir;
        if (args.length < 1) {
            Regress.usageAndExit("Need directory name containing input files to render.");
        }
        if (!(sourceDir = new File(sourceDirPath = args[0])).exists()) {
            Regress.usageAndExit("Source directory not found: " + sourceDirPath);
        }
        return sourceDir;
    }

    private boolean enableLogging(boolean isEnabled) {
        String prop = "xr.util-logging.loggingEnabled";
        boolean orgVal = Boolean.valueOf(System.getProperty("xr.util-logging.loggingEnabled"));
        System.setProperty("xr.util-logging.loggingEnabled", Boolean.valueOf(isEnabled).toString());
        return orgVal;
    }

    private Iterator listInputFiles(File sourceDir) {
        File[] f = sourceDir.listFiles(new FilenameFilter(){

            @Override
            public boolean accept(File file, String s) {
                return EXTENSIONS.contains(s.substring(s.lastIndexOf(".") + 1));
            }
        });
        return Arrays.asList(f).iterator();
    }

    private static void usageAndExit(String msg) {
        System.err.println(msg);
        System.exit(-1);
    }
}

