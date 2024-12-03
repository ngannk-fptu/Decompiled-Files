/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.filters.ChainableReader;
import org.apache.tools.ant.filters.FixCrLfFilter;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.util.FileUtils;

public class FixCRLF
extends MatchingTask
implements ChainableReader {
    private static final String FIXCRLF_ERROR = "<fixcrlf> error: ";
    public static final String ERROR_FILE_AND_SRCDIR = "<fixcrlf> error: srcdir and file are mutually exclusive";
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private boolean preserveLastModified = false;
    private File srcDir;
    private File destDir = null;
    private File file;
    private FixCrLfFilter filter = new FixCrLfFilter();
    private Vector<FilterChain> fcv = null;
    private String encoding = null;
    private String outputEncoding = null;

    @Override
    public final Reader chain(Reader rdr) {
        return this.filter.chain(rdr);
    }

    public void setSrcdir(File srcDir) {
        this.srcDir = srcDir;
    }

    public void setDestdir(File destDir) {
        this.destDir = destDir;
    }

    public void setJavafiles(boolean javafiles) {
        this.filter.setJavafiles(javafiles);
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setEol(CrLf attr) {
        this.filter.setEol(FixCrLfFilter.CrLf.newInstance(attr.getValue()));
    }

    @Deprecated
    public void setCr(AddAsisRemove attr) {
        this.log("DEPRECATED: The cr attribute has been deprecated,", 1);
        this.log("Please use the eol attribute instead", 1);
        String option = attr.getValue();
        CrLf c = new CrLf();
        if ("remove".equals(option)) {
            c.setValue("lf");
        } else if ("asis".equals(option)) {
            c.setValue("asis");
        } else {
            c.setValue("crlf");
        }
        this.setEol(c);
    }

    public void setTab(AddAsisRemove attr) {
        this.filter.setTab(FixCrLfFilter.AddAsisRemove.newInstance(attr.getValue()));
    }

    public void setTablength(int tlength) throws BuildException {
        try {
            this.filter.setTablength(tlength);
        }
        catch (IOException e) {
            throw new BuildException(e.getMessage(), e);
        }
    }

    public void setEof(AddAsisRemove attr) {
        this.filter.setEof(FixCrLfFilter.AddAsisRemove.newInstance(attr.getValue()));
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setOutputEncoding(String outputEncoding) {
        this.outputEncoding = outputEncoding;
    }

    public void setFixlast(boolean fixlast) {
        this.filter.setFixlast(fixlast);
    }

    public void setPreserveLastModified(boolean preserve) {
        this.preserveLastModified = preserve;
    }

    @Override
    public void execute() throws BuildException {
        this.validate();
        String enc = this.encoding == null ? "default" : this.encoding;
        this.log("options: eol=" + this.filter.getEol().getValue() + " tab=" + this.filter.getTab().getValue() + " eof=" + this.filter.getEof().getValue() + " tablength=" + this.filter.getTablength() + " encoding=" + enc + " outputencoding=" + (this.outputEncoding == null ? enc : this.outputEncoding), 3);
        DirectoryScanner ds = super.getDirectoryScanner(this.srcDir);
        for (String filename : ds.getIncludedFiles()) {
            this.processFile(filename);
        }
    }

    private void validate() throws BuildException {
        if (this.file != null) {
            if (this.srcDir != null) {
                throw new BuildException(ERROR_FILE_AND_SRCDIR);
            }
            this.fileset.setFile(this.file);
            this.srcDir = this.file.getParentFile();
        }
        if (this.srcDir == null) {
            throw new BuildException("<fixcrlf> error: srcdir attribute must be set!");
        }
        if (!this.srcDir.exists()) {
            throw new BuildException("<fixcrlf> error: srcdir does not exist: '%s'", this.srcDir);
        }
        if (!this.srcDir.isDirectory()) {
            throw new BuildException("<fixcrlf> error: srcdir is not a directory: '%s'", this.srcDir);
        }
        if (this.destDir != null) {
            if (!this.destDir.exists()) {
                throw new BuildException("<fixcrlf> error: destdir does not exist: '%s'", this.destDir);
            }
            if (!this.destDir.isDirectory()) {
                throw new BuildException("<fixcrlf> error: destdir is not a directory: '%s'", this.destDir);
            }
        }
    }

    private void processFile(String file) throws BuildException {
        File destD;
        File srcFile = new File(this.srcDir, file);
        long lastModified = srcFile.lastModified();
        File file2 = destD = this.destDir == null ? this.srcDir : this.destDir;
        if (this.fcv == null) {
            FilterChain fc = new FilterChain();
            fc.add(this.filter);
            this.fcv = new Vector(1);
            this.fcv.add(fc);
        }
        File tmpFile = FILE_UTILS.createTempFile(this.getProject(), "fixcrlf", "", null, true, true);
        try {
            FILE_UTILS.copyFile(srcFile, tmpFile, null, this.fcv, true, false, this.encoding, this.outputEncoding == null ? this.encoding : this.outputEncoding, this.getProject());
            File destFile = new File(destD, file);
            boolean destIsWrong = true;
            if (destFile.exists()) {
                this.log("destFile " + destFile + " exists", 4);
                destIsWrong = !FILE_UTILS.contentEquals(destFile, tmpFile);
                this.log(destFile + (destIsWrong ? " is being written" : " is not written, as the contents are identical"), 4);
            }
            if (destIsWrong) {
                FILE_UTILS.rename(tmpFile, destFile, true);
                if (this.preserveLastModified) {
                    this.log("preserved lastModified for " + destFile, 4);
                    FILE_UTILS.setFileLastModified(destFile, lastModified);
                }
            }
        }
        catch (IOException e) {
            throw new BuildException("error running fixcrlf on file " + srcFile, e);
        }
        finally {
            if (tmpFile != null && tmpFile.exists()) {
                FILE_UTILS.tryHardToDelete(tmpFile);
            }
        }
    }

    public static class CrLf
    extends EnumeratedAttribute {
        @Override
        public String[] getValues() {
            return new String[]{"asis", "cr", "lf", "crlf", "mac", "unix", "dos"};
        }
    }

    public static class AddAsisRemove
    extends EnumeratedAttribute {
        @Override
        public String[] getValues() {
            return new String[]{"add", "asis", "remove"};
        }
    }

    @Deprecated
    protected class OneLiner
    implements Enumeration<Object> {
        private static final int UNDEF = -1;
        private static final int NOTJAVA = 0;
        private static final int LOOKING = 1;
        private static final int INBUFLEN = 8192;
        private static final int LINEBUFLEN = 200;
        private static final char CTRLZ = '\u001a';
        private int state;
        private StringBuffer eolStr;
        private StringBuffer eofStr;
        private BufferedReader reader;
        private StringBuffer line;
        private boolean reachedEof;
        private File srcFile;

        public OneLiner(File srcFile) throws BuildException {
            this.state = FixCRLF.this.filter.getJavafiles() ? 1 : 0;
            this.eolStr = new StringBuffer(200);
            this.eofStr = new StringBuffer();
            this.line = new StringBuffer();
            this.reachedEof = false;
            this.srcFile = srcFile;
            try {
                this.reader = new BufferedReader(FixCRLF.this.encoding == null ? new FileReader(srcFile) : new InputStreamReader(Files.newInputStream(srcFile.toPath(), new OpenOption[0]), FixCRLF.this.encoding), 8192);
                this.nextLine();
            }
            catch (IOException e) {
                throw new BuildException(srcFile + ": " + e.getMessage(), e, FixCRLF.this.getLocation());
            }
        }

        protected void nextLine() throws BuildException {
            int ch = -1;
            int eolcount = 0;
            this.eolStr = new StringBuffer();
            this.line = new StringBuffer();
            try {
                ch = this.reader.read();
                while (ch != -1 && ch != 13 && ch != 10) {
                    this.line.append((char)ch);
                    ch = this.reader.read();
                }
                if (ch == -1 && this.line.length() == 0) {
                    this.reachedEof = true;
                    return;
                }
                block1 : switch ((char)ch) {
                    case '\r': {
                        ++eolcount;
                        this.eolStr.append('\r');
                        this.reader.mark(2);
                        ch = this.reader.read();
                        switch (ch) {
                            case 13: {
                                ch = this.reader.read();
                                if ((char)ch == '\n') {
                                    eolcount += 2;
                                    this.eolStr.append("\r\n");
                                    break block1;
                                }
                                this.reader.reset();
                                break block1;
                            }
                            case 10: {
                                ++eolcount;
                                this.eolStr.append('\n');
                                break block1;
                            }
                            case -1: {
                                break block1;
                            }
                        }
                        this.reader.reset();
                        break;
                    }
                    case '\n': {
                        ++eolcount;
                        this.eolStr.append('\n');
                        break;
                    }
                }
                if (eolcount == 0) {
                    int i = this.line.length();
                    while (--i >= 0 && this.line.charAt(i) == '\u001a') {
                    }
                    if (i < this.line.length() - 1) {
                        this.eofStr.append(this.line.toString().substring(i + 1));
                        if (i < 0) {
                            this.line.setLength(0);
                            this.reachedEof = true;
                        } else {
                            this.line.setLength(i + 1);
                        }
                    }
                }
            }
            catch (IOException e) {
                throw new BuildException(this.srcFile + ": " + e.getMessage(), e, FixCRLF.this.getLocation());
            }
        }

        public String getEofStr() {
            return this.eofStr.substring(0);
        }

        public int getState() {
            return this.state;
        }

        public void setState(int state) {
            this.state = state;
        }

        @Override
        public boolean hasMoreElements() {
            return !this.reachedEof;
        }

        @Override
        public Object nextElement() throws NoSuchElementException {
            if (!this.hasMoreElements()) {
                throw new NoSuchElementException("OneLiner");
            }
            BufferLine tmpLine = new BufferLine(this.line.toString(), this.eolStr.substring(0));
            this.nextLine();
            return tmpLine;
        }

        public void close() throws IOException {
            if (this.reader != null) {
                this.reader.close();
            }
        }

        class BufferLine {
            private int next = 0;
            private int column = 0;
            private int lookahead = -1;
            private String line;
            private String eolStr;

            public BufferLine(String line, String eolStr) throws BuildException {
                this.line = line;
                this.eolStr = eolStr;
            }

            public int getNext() {
                return this.next;
            }

            public void setNext(int next) {
                this.next = next;
            }

            public int getLookahead() {
                return this.lookahead;
            }

            public void setLookahead(int lookahead) {
                this.lookahead = lookahead;
            }

            public char getChar(int i) {
                return this.line.charAt(i);
            }

            public char getNextChar() {
                return this.getChar(this.next);
            }

            public char getNextCharInc() {
                return this.getChar(this.next++);
            }

            public int getColumn() {
                return this.column;
            }

            public void setColumn(int col) {
                this.column = col;
            }

            public int incColumn() {
                return this.column++;
            }

            public int length() {
                return this.line.length();
            }

            public int getEolLength() {
                return this.eolStr.length();
            }

            public String getLineString() {
                return this.line;
            }

            public String getEol() {
                return this.eolStr;
            }

            public String substring(int begin) {
                return this.line.substring(begin);
            }

            public String substring(int begin, int end) {
                return this.line.substring(begin, end);
            }

            public void setState(int state) {
                OneLiner.this.setState(state);
            }

            public int getState() {
                return OneLiner.this.getState();
            }
        }
    }
}

