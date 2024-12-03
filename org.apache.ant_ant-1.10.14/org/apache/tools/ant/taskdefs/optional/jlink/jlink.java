/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.jlink;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.apache.tools.ant.taskdefs.optional.jlink.ClassNameReader;

public class jlink {
    private static final int BUFFER_SIZE = 8192;
    private static final int VECTOR_INIT_SIZE = 10;
    private String outfile = null;
    private List<String> mergefiles = new Vector<String>(10);
    private List<String> addfiles = new Vector<String>(10);
    private boolean compression = false;
    byte[] buffer = new byte[8192];

    public void setOutfile(String outfile) {
        if (outfile == null) {
            return;
        }
        this.outfile = outfile;
    }

    public void addMergeFile(String fileToMerge) {
        if (fileToMerge == null) {
            return;
        }
        this.mergefiles.add(fileToMerge);
    }

    public void addAddFile(String fileToAdd) {
        if (fileToAdd == null) {
            return;
        }
        this.addfiles.add(fileToAdd);
    }

    public void addMergeFiles(String ... filesToMerge) {
        if (filesToMerge == null) {
            return;
        }
        for (String element : filesToMerge) {
            this.addMergeFile(element);
        }
    }

    public void addAddFiles(String ... filesToAdd) {
        if (filesToAdd == null) {
            return;
        }
        for (String element : filesToAdd) {
            this.addAddFile(element);
        }
    }

    public void setCompression(boolean compress) {
        this.compression = compress;
    }

    public void link() throws Exception {
        try (ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(Paths.get(this.outfile, new String[0]), new OpenOption[0]));){
            File f;
            if (this.compression) {
                output.setMethod(8);
                output.setLevel(-1);
            } else {
                output.setMethod(0);
            }
            for (String path : this.mergefiles) {
                f = new File(path);
                if (f.getName().endsWith(".jar") || f.getName().endsWith(".zip")) {
                    this.mergeZipJarContents(output, f);
                    continue;
                }
                this.addAddFile(path);
            }
            for (String name : this.addfiles) {
                f = new File(name);
                if (f.isDirectory()) {
                    this.addDirContents(output, f, f.getName() + '/', this.compression);
                    continue;
                }
                this.addFile(output, f, "", this.compression);
            }
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("usage: jlink output input1 ... inputN");
            System.exit(1);
        }
        jlink linker = new jlink();
        linker.setOutfile(args[0]);
        for (int i = 1; i < args.length; ++i) {
            linker.addMergeFile(args[i]);
        }
        try {
            linker.link();
        }
        catch (Exception ex) {
            System.err.print(ex.getMessage());
        }
    }

    private void mergeZipJarContents(ZipOutputStream output, File f) throws IOException {
        if (!f.exists()) {
            return;
        }
        try (ZipFile zipf = new ZipFile(f);){
            Enumeration<? extends ZipEntry> entries = zipf.entries();
            while (entries.hasMoreElements()) {
                ZipEntry inputEntry = entries.nextElement();
                String inputEntryName = inputEntry.getName();
                int index = inputEntryName.indexOf("META-INF");
                if (index >= 0) continue;
                try {
                    output.putNextEntry(this.processEntry(zipf, inputEntry));
                }
                catch (ZipException ex) {
                    if (ex.getMessage().contains("duplicate")) continue;
                    throw ex;
                }
                InputStream in = zipf.getInputStream(inputEntry);
                try {
                    int len = this.buffer.length;
                    int count = -1;
                    while ((count = in.read(this.buffer, 0, len)) > 0) {
                        output.write(this.buffer, 0, count);
                    }
                    output.closeEntry();
                }
                finally {
                    if (in == null) continue;
                    in.close();
                }
            }
        }
    }

    private void addDirContents(ZipOutputStream output, File dir, String prefix, boolean compress) throws IOException {
        String[] names = dir.list();
        if (names == null || names.length == 0) {
            return;
        }
        for (String name : names) {
            File file = new File(dir, name);
            if (file.isDirectory()) {
                this.addDirContents(output, file, prefix + name + '/', compress);
                continue;
            }
            this.addFile(output, file, prefix, compress);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private String getEntryName(File file, String prefix) {
        String name = file.getName();
        if (!name.endsWith(".class")) {
            try (InputStream input = Files.newInputStream(file.toPath(), new OpenOption[0]);){
                String className = ClassNameReader.getClassName(input);
                if (className != null) {
                    String string = className.replace('.', '/') + ".class";
                    return string;
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        System.out.printf("From %1$s and prefix %2$s, creating entry %2$s%3$s%n", file.getPath(), prefix, name);
        return prefix + name;
    }

    private void addFile(ZipOutputStream output, File file, String prefix, boolean compress) throws IOException {
        if (!file.exists()) {
            return;
        }
        ZipEntry entry = new ZipEntry(this.getEntryName(file, prefix));
        entry.setTime(file.lastModified());
        entry.setSize(file.length());
        if (!compress) {
            entry.setCrc(this.calcChecksum(file));
        }
        this.addToOutputStream(output, Files.newInputStream(file.toPath(), new OpenOption[0]), entry);
    }

    private void addToOutputStream(ZipOutputStream output, InputStream input, ZipEntry ze) throws IOException {
        int numBytes;
        try {
            output.putNextEntry(ze);
        }
        catch (ZipException zipEx) {
            input.close();
            return;
        }
        while ((numBytes = input.read(this.buffer)) > 0) {
            output.write(this.buffer, 0, numBytes);
        }
        output.closeEntry();
        input.close();
    }

    private ZipEntry processEntry(ZipFile zip, ZipEntry inputEntry) {
        String name = inputEntry.getName();
        if (!inputEntry.isDirectory() && !name.endsWith(".class")) {
            try (InputStream input2 = zip.getInputStream(zip.getEntry(name));){
                String className = ClassNameReader.getClassName(input2);
                if (className != null) {
                    name = className.replace('.', '/') + ".class";
                }
            }
            catch (IOException input2) {
                // empty catch block
            }
        }
        ZipEntry outputEntry = new ZipEntry(name);
        outputEntry.setTime(inputEntry.getTime());
        outputEntry.setExtra(inputEntry.getExtra());
        outputEntry.setComment(inputEntry.getComment());
        outputEntry.setTime(inputEntry.getTime());
        if (this.compression) {
            outputEntry.setMethod(8);
        } else {
            outputEntry.setMethod(0);
            outputEntry.setCrc(inputEntry.getCrc());
            outputEntry.setSize(inputEntry.getSize());
        }
        return outputEntry;
    }

    private long calcChecksum(File f) throws IOException {
        return this.calcChecksum(new BufferedInputStream(Files.newInputStream(f.toPath(), new OpenOption[0])));
    }

    private long calcChecksum(InputStream in) throws IOException {
        int count;
        CRC32 crc = new CRC32();
        int len = this.buffer.length;
        while ((count = in.read(this.buffer, 0, len)) > 0) {
            crc.update(this.buffer, 0, count);
        }
        in.close();
        return crc.getValue();
    }
}

