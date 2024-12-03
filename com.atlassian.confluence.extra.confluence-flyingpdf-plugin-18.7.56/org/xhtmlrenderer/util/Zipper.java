/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper {
    private final File sourceDir;
    private final File outputFile;

    public Zipper(File sourceDir, File outputFile) {
        this.sourceDir = sourceDir;
        this.outputFile = outputFile;
        if (!this.outputFile.delete()) {
            throw new IllegalArgumentException("Can't delete outputfile " + outputFile.getAbsolutePath());
        }
    }

    public static void main(String[] args) {
        File sourceDir = Zipper.getSourceDir(args);
        File outputFile = new File(System.getProperty("user.home") + File.separator + sourceDir.getName() + ".zip");
        try {
            new Zipper(sourceDir, outputFile).zipDirectory();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Created zip file " + outputFile.getPath());
    }

    public File zipDirectory() throws IOException {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(this.outputFile));
        Zipper.recurseAndZip(this.sourceDir, zos);
        zos.close();
        return this.outputFile;
    }

    private static void recurseAndZip(File file, ZipOutputStream zos) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; ++i) {
                    File file1 = files[i];
                    Zipper.recurseAndZip(file1, zos);
                }
            }
        } else {
            int len;
            byte[] buf = new byte[1024];
            ZipEntry entry = new ZipEntry(file.getName());
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);
            zos.putNextEntry(entry);
            while ((len = bis.read(buf)) >= 0) {
                zos.write(buf, 0, len);
            }
            bis.close();
            zos.closeEntry();
        }
    }

    private static File getSourceDir(String[] args) {
        String sourceDirPath;
        File sourceDir;
        if (args.length != 1) {
            Zipper.usageAndExit("Need directory name containing input files to render.");
        }
        if (!(sourceDir = new File(sourceDirPath = args[0])).exists()) {
            Zipper.usageAndExit(sourceDirPath);
        }
        return sourceDir;
    }

    private static void usageAndExit(String msg) {
        System.err.println("Source directory not found: " + msg);
        System.exit(-1);
    }
}

