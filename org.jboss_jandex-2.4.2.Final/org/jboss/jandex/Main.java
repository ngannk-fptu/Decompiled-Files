/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexReader;
import org.jboss.jandex.IndexWriter;
import org.jboss.jandex.Indexer;
import org.jboss.jandex.JarIndexer;
import org.jboss.jandex.Result;

public class Main {
    private boolean modify;
    private boolean verbose;
    private boolean dump;
    private boolean jarFile;
    private File outputFile;
    private File source;
    private Index index;

    private Main() {
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            Main.printUsage();
            return;
        }
        Main main = new Main();
        main.execute(args);
    }

    private void execute(String[] args) {
        block5: {
            boolean printUsage = true;
            try {
                this.parseOptions(args);
                printUsage = false;
                if (this.dump) {
                    this.dumpIndex(this.source);
                    return;
                }
                long start = System.currentTimeMillis();
                this.index = this.getIndex(start);
                this.outputFile = null;
                this.source = null;
            }
            catch (Exception e) {
                if (!this.verbose && (e instanceof IllegalArgumentException || e instanceof FileNotFoundException)) {
                    System.err.println(e.getMessage() == null ? e.getClass().getSimpleName() : "ERROR: " + e.getMessage());
                } else {
                    e.printStackTrace(System.err);
                }
                if (!printUsage) break block5;
                System.out.println();
                Main.printUsage();
            }
        }
    }

    private Index getIndex(long start) throws IOException {
        Indexer indexer = new Indexer();
        Result result = this.source.isDirectory() ? this.indexDirectory(this.source, indexer) : JarIndexer.createJarIndex(this.source, indexer, this.outputFile, this.modify, this.jarFile, this.verbose);
        double time = (double)(System.currentTimeMillis() - start) / 1000.0;
        System.out.printf(Locale.ROOT, "Wrote %s in %.4f seconds (%d classes, %d annotations, %d instances, %d class usages, %d bytes)%n", result.getName(), time, result.getClasses(), result.getAnnotations(), result.getInstances(), result.getUsages(), result.getBytes());
        return result.getIndex();
    }

    private void dumpIndex(File source) throws IOException {
        FileInputStream input = new FileInputStream(source);
        IndexReader reader = new IndexReader(input);
        long start = System.currentTimeMillis();
        Index index = reader.read();
        long end = System.currentTimeMillis() - start;
        System.out.println("Dump index v" + reader.getIndexVersion() + " (current: v" + 10 + ") file: " + source);
        index.printAnnotations();
        index.printSubclasses();
        System.out.printf(Locale.ROOT, "%nRead %s in %.04f seconds%n", source.getName(), (double)end / 1000.0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Result indexDirectory(File source, Indexer indexer) throws FileNotFoundException, IOException {
        File outputFile = this.outputFile;
        this.scanFile(source, indexer);
        if (this.modify) {
            new File(source, "META-INF").mkdirs();
            outputFile = new File(source, "META-INF/jandex.idx");
        }
        if (outputFile == null) {
            outputFile = new File(source.getName().replace('.', '-') + ".idx");
        }
        FileOutputStream out = new FileOutputStream(outputFile);
        IndexWriter writer = new IndexWriter(out);
        try {
            Index index = indexer.complete();
            int bytes = writer.write(index);
            Result result = new Result(index, outputFile.getPath(), bytes, outputFile);
            return result;
        }
        finally {
            out.flush();
            out.close();
        }
    }

    private void printIndexEntryInfo(ClassInfo info) {
        System.out.println("Indexed " + info.name() + " (" + info.annotations().size() + " annotations)");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void scanFile(File source, Indexer indexer) throws FileNotFoundException, IOException {
        if (source.isDirectory()) {
            File[] children = source.listFiles();
            if (children == null) {
                throw new FileNotFoundException("Source directory disappeared: " + source);
            }
            for (File child : children) {
                this.scanFile(child, indexer);
            }
            return;
        }
        if (!source.getName().endsWith(".class")) {
            return;
        }
        FileInputStream input = new FileInputStream(source);
        try {
            ClassInfo info = indexer.index(input);
            if (this.verbose && info != null) {
                this.printIndexEntryInfo(info);
            }
        }
        catch (Exception e) {
            String message = e.getMessage() == null ? e.getClass().getSimpleName() : e.getMessage();
            System.err.println("ERROR: Could not index " + source.getName() + ": " + message);
            if (this.verbose) {
                e.printStackTrace(System.err);
            }
        }
        finally {
            this.safeClose(input);
        }
    }

    private void safeClose(FileInputStream input) {
        if (input != null) {
            try {
                input.close();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    private static void printUsage() {
        System.out.println("Usage: jandex [-v] [-m] [-o file-name] <directory> | <jar>");
        System.out.println("        -or-");
        System.out.println("       jandex [-d] <index-file-name>");
        System.out.println("Options:");
        System.out.println("  -v  verbose output");
        System.out.println("  -m  modify directory or jar instead of creating an external index file");
        System.out.println("  -o  name the external index file file-name");
        System.out.println("  -j  export the index file to a jar file");
        System.out.println("  -d  dump the index file index-file-name");
        System.out.println("\nThe default behavior, with no options specified, is to autogenerate an external index file");
    }

    private void parseOptions(String[] args) {
        int optionCount = 0;
        block7: for (int i = 0; i < args.length; ++i) {
            String arg = args[i];
            if (arg.length() < 2 || arg.charAt(0) != '-') {
                if (this.source != null) {
                    throw new IllegalArgumentException("Only one source location can be specified");
                }
                this.source = new File(arg);
                if (this.source.exists()) continue;
                throw new IllegalArgumentException("Source directory/jar not found: " + this.source.getName());
            }
            switch (arg.charAt(1)) {
                case 'm': {
                    this.modify = true;
                    ++optionCount;
                    continue block7;
                }
                case 'd': {
                    this.dump = true;
                    ++optionCount;
                    continue block7;
                }
                case 'v': {
                    this.verbose = true;
                    ++optionCount;
                    continue block7;
                }
                case 'j': {
                    this.jarFile = true;
                    ++optionCount;
                    continue block7;
                }
                case 'o': {
                    String name;
                    if (i >= args.length) {
                        throw new IllegalArgumentException("-o requires an output file name");
                    }
                    if ((name = args[++i]).length() < 1) {
                        throw new IllegalArgumentException("-o requires an output file name");
                    }
                    this.outputFile = new File(name);
                    ++optionCount;
                    continue block7;
                }
                default: {
                    throw new IllegalArgumentException("Option not understood: " + arg);
                }
            }
        }
        if (this.source == null) {
            throw new IllegalArgumentException("Source location not specified");
        }
        if (this.outputFile != null && this.modify) {
            throw new IllegalArgumentException("-o and -m are mutually exclusive");
        }
        if (this.dump && optionCount != 1) {
            throw new IllegalArgumentException("-d can not be specified with other options");
        }
    }
}

