/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexWriter;
import org.jboss.jandex.Indexer;
import org.jboss.jandex.Result;

public class JarIndexer {
    private static File getIndexFile(File jarFile, boolean newJar) {
        String name = jarFile.getName();
        int p = name.lastIndexOf(".");
        if (p < 0) {
            throw new IllegalArgumentException("File has no extension / ext: " + jarFile);
        }
        String ext = name.substring(p);
        String pattern = "\\" + ext + "$";
        if (newJar) {
            return new File(jarFile.getAbsolutePath().replaceAll(pattern, "-jandex" + ext));
        }
        return new File(jarFile.getAbsolutePath().replaceAll(pattern, "-" + ext.substring(1)) + ".idx");
    }

    public static Result createJarIndex(File jarFile, Indexer indexer, boolean modify, boolean newJar, boolean verbose) throws IOException {
        return JarIndexer.createJarIndex(jarFile, indexer, modify, newJar, verbose, System.out, System.err);
    }

    public static Result createJarIndex(File jarFile, Indexer indexer, File outputFile, boolean modify, boolean newJar, boolean verbose) throws IOException {
        return JarIndexer.createJarIndex(jarFile, indexer, outputFile, modify, newJar, verbose, System.out, System.err);
    }

    public static Result createJarIndex(File jarFile, Indexer indexer, boolean modify, boolean newJar, boolean verbose, PrintStream infoStream, PrintStream errStream) throws IOException {
        return JarIndexer.createJarIndex(jarFile, indexer, null, modify, newJar, verbose, infoStream, errStream);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Result createJarIndex(File jarFile, Indexer indexer, File outputFile, boolean modify, boolean newJar, boolean verbose, PrintStream infoStream, PrintStream errStream) throws IOException {
        OutputStream out;
        File tmpCopy = null;
        ZipOutputStream zo = null;
        JarFile jar = new JarFile(jarFile);
        if (modify) {
            tmpCopy = File.createTempFile(jarFile.getName().substring(0, jarFile.getName().lastIndexOf(46)) + "00", "jmp");
            zo = new ZipOutputStream(new FileOutputStream(tmpCopy));
            out = zo;
            outputFile = jarFile;
        } else if (newJar) {
            outputFile = JarIndexer.getIndexFile(jarFile, newJar);
            zo = new ZipOutputStream(new FileOutputStream(outputFile));
            out = zo;
        } else {
            if (outputFile == null) {
                outputFile = JarIndexer.getIndexFile(jarFile, newJar);
            }
            out = new FileOutputStream(outputFile);
        }
        try {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (modify && !"META-INF/jandex.idx".equals(entry.getName())) {
                    JarEntry clone = (JarEntry)entry.clone();
                    if (clone.getMethod() != 0) {
                        clone.setCompressedSize(-1L);
                    }
                    zo.putNextEntry(clone);
                    InputStream stream = jar.getInputStream(entry);
                    try {
                        JarIndexer.copy(stream, zo);
                    }
                    finally {
                        JarIndexer.safeClose(stream);
                    }
                }
                if (!entry.getName().endsWith(".class")) continue;
                try {
                    ClassInfo info;
                    InputStream stream = jar.getInputStream(entry);
                    try {
                        info = indexer.index(stream);
                    }
                    finally {
                        JarIndexer.safeClose(stream);
                    }
                    if (!verbose || info == null || infoStream == null) continue;
                    JarIndexer.printIndexEntryInfo(info, infoStream);
                }
                catch (Exception e) {
                    String message = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
                    errStream.println("ERROR: Could not index " + entry.getName() + ": " + message);
                    if (!verbose) continue;
                    e.printStackTrace(errStream);
                }
            }
            if (modify || newJar) {
                zo.putNextEntry(new ZipEntry("META-INF/jandex.idx"));
            }
            IndexWriter writer = new IndexWriter(out);
            Index index = indexer.complete();
            int bytes = writer.write(index);
            out.close();
            if (modify) {
                jarFile.delete();
                if (!tmpCopy.renameTo(jarFile)) {
                    JarIndexer.copy(jarFile, tmpCopy);
                    tmpCopy.delete();
                }
                tmpCopy = null;
            }
            Result result = new Result(index, modify ? "META-INF/jandex.idx" : outputFile.getPath(), bytes, outputFile);
            return result;
        }
        finally {
            JarIndexer.safeClose(out);
            JarIndexer.safeClose(jar);
            if (tmpCopy != null) {
                tmpCopy.delete();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void copy(File dest, File source) throws IOException {
        FileInputStream fis = new FileInputStream(source);
        FileOutputStream fos = new FileOutputStream(new File(dest.getAbsolutePath()));
        try {
            byte[] b = new byte[8196];
            int count = 0;
            while ((count = fis.read(b, 0, 8196)) >= 0) {
                fos.write(b, 0, count);
            }
        }
        finally {
            fis.close();
            fos.close();
        }
    }

    private static void printIndexEntryInfo(ClassInfo info, PrintStream infoStream) {
        infoStream.println("Indexed " + info.name() + " (" + info.annotations().size() + " annotations)");
    }

    private static void copy(InputStream in, OutputStream out) throws IOException {
        int len;
        byte[] buf = new byte[8192];
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        out.flush();
    }

    private static void safeClose(JarFile close) {
        try {
            close.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private static void safeClose(Closeable close) {
        try {
            close.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private JarIndexer() {
    }
}

