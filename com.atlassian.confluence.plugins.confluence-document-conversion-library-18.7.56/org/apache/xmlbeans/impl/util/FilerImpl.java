/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.xmlbeans.Filer;
import org.apache.xmlbeans.impl.repackage.Repackager;
import org.apache.xmlbeans.impl.util.Diff;

public class FilerImpl
implements Filer {
    private final File classdir;
    private final File srcdir;
    private final Repackager repackager;
    private final boolean verbose;
    private final List<File> sourceFiles;
    private final boolean incrSrcGen;
    private Set<String> seenTypes;
    private static final Charset CHARSET;

    public FilerImpl(File classdir, File srcdir, Repackager repackager, boolean verbose, boolean incrSrcGen) {
        this.classdir = classdir;
        this.srcdir = srcdir;
        this.repackager = repackager;
        this.verbose = verbose;
        this.sourceFiles = new ArrayList<File>();
        this.incrSrcGen = incrSrcGen;
        if (this.incrSrcGen) {
            this.seenTypes = new HashSet<String>();
        }
    }

    @Override
    public OutputStream createBinaryFile(String typename) throws IOException {
        if (this.verbose) {
            System.err.println("created binary: " + typename);
        }
        File source = new File(this.classdir, typename);
        source.getParentFile().mkdirs();
        return new FileOutputStream(source);
    }

    @Override
    public Writer createSourceFile(String typename) throws IOException {
        if (this.incrSrcGen) {
            this.seenTypes.add(typename);
        }
        if (typename.indexOf(36) > 0) {
            typename = typename.substring(0, typename.lastIndexOf(46)) + "." + typename.substring(typename.indexOf(36) + 1);
        }
        String filename = typename.replace('.', File.separatorChar) + ".java";
        File sourcefile = new File(this.srcdir, filename);
        sourcefile.getParentFile().mkdirs();
        if (this.verbose) {
            System.err.println("created source: " + sourcefile.getAbsolutePath());
        }
        this.sourceFiles.add(sourcefile);
        if (this.incrSrcGen && sourcefile.exists()) {
            return new IncrFileWriter(sourcefile, this.repackager);
        }
        return this.repackager == null ? FilerImpl.writerForFile(sourcefile) : new RepackagingWriter(sourcefile, this.repackager);
    }

    public List<File> getSourceFiles() {
        return new ArrayList<File>(this.sourceFiles);
    }

    public Repackager getRepackager() {
        return this.repackager;
    }

    private static Writer writerForFile(File f) throws IOException {
        if (CHARSET == null) {
            return Files.newBufferedWriter(f.toPath(), StandardCharsets.ISO_8859_1, new OpenOption[0]);
        }
        FileOutputStream fileStream = new FileOutputStream(f);
        CharsetEncoder ce = CHARSET.newEncoder();
        ce.onUnmappableCharacter(CodingErrorAction.REPORT);
        return new OutputStreamWriter((OutputStream)fileStream, ce);
    }

    static {
        Charset temp = null;
        try {
            temp = Charset.forName(System.getProperty("file.encoding"));
        }
        catch (Exception exception) {
            // empty catch block
        }
        CHARSET = temp;
    }

    static class RepackagingWriter
    extends StringWriter {
        private final File _file;
        private final Repackager _repackager;

        public RepackagingWriter(File file, Repackager repackager) {
            this._file = file;
            this._repackager = repackager;
        }

        @Override
        public void close() throws IOException {
            super.close();
            try (Writer fw = FilerImpl.writerForFile(this._file);){
                fw.write(this._repackager.repackage(this.getBuffer()).toString());
            }
        }
    }

    static class IncrFileWriter
    extends StringWriter {
        private final File _file;
        private final Repackager _repackager;

        public IncrFileWriter(File file, Repackager repackager) {
            this._file = file;
            this._repackager = repackager;
        }

        @Override
        public void close() throws IOException {
            super.close();
            StringBuffer sb = this._repackager != null ? this._repackager.repackage(this.getBuffer()) : this.getBuffer();
            String str = sb.toString();
            ArrayList diffs = new ArrayList();
            try (StringReader sReader = new StringReader(str);
                 BufferedReader fReader = Files.newBufferedReader(this._file.toPath(), StandardCharsets.ISO_8859_1);){
                Diff.readersAsText(sReader, "<generated>", fReader, this._file.getName(), diffs);
            }
            if (diffs.size() > 0) {
                var5_5 = null;
                try (Writer fw = FilerImpl.writerForFile(this._file);){
                    fw.write(str);
                }
                catch (Throwable throwable) {
                    var5_5 = throwable;
                    throw throwable;
                }
            }
        }
    }
}

