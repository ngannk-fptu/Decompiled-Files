/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.filters.util.ChainReaderHelper;
import org.apache.tools.ant.taskdefs.FixCRLF;
import org.apache.tools.ant.types.FileList;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.FilterChain;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.Intersect;
import org.apache.tools.ant.types.resources.LogOutputResource;
import org.apache.tools.ant.types.resources.Resources;
import org.apache.tools.ant.types.resources.Restrict;
import org.apache.tools.ant.types.resources.StringResource;
import org.apache.tools.ant.types.resources.selectors.Exists;
import org.apache.tools.ant.types.resources.selectors.Not;
import org.apache.tools.ant.types.resources.selectors.ResourceSelector;
import org.apache.tools.ant.types.selectors.SelectorUtils;
import org.apache.tools.ant.util.ConcatResourceInputStream;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.ReaderInputStream;
import org.apache.tools.ant.util.ResourceUtils;

public class Concat
extends Task
implements ResourceCollection {
    private static final int BUFFER_SIZE = 8192;
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static final ResourceSelector EXISTS = new Exists();
    private static final ResourceSelector NOT_EXISTS = new Not(EXISTS);
    private Resource dest;
    private boolean append;
    private String encoding;
    private String outputEncoding;
    private boolean binary;
    private boolean filterBeforeConcat;
    private StringBuffer textBuffer;
    private Resources rc;
    private Vector<FilterChain> filterChains;
    private boolean forceOverwrite = true;
    private boolean force = false;
    private TextElement footer;
    private TextElement header;
    private boolean fixLastLine = false;
    private String eolString;
    private Writer outputWriter = null;
    private boolean ignoreEmpty = true;
    private String resourceName;
    private ReaderFactory<Resource> resourceReaderFactory = new ReaderFactory<Resource>(){

        @Override
        public Reader getReader(Resource o) throws IOException {
            InputStream is = o.getInputStream();
            return new BufferedReader(Concat.this.encoding == null ? new InputStreamReader(is) : new InputStreamReader(is, Concat.this.encoding));
        }
    };
    private ReaderFactory<Reader> identityReaderFactory = o -> o;

    public Concat() {
        this.reset();
    }

    public void reset() {
        this.append = false;
        this.forceOverwrite = true;
        this.dest = null;
        this.encoding = null;
        this.outputEncoding = null;
        this.fixLastLine = false;
        this.filterChains = null;
        this.footer = null;
        this.header = null;
        this.binary = false;
        this.outputWriter = null;
        this.textBuffer = null;
        this.eolString = System.lineSeparator();
        this.rc = null;
        this.ignoreEmpty = true;
        this.force = false;
    }

    public void setDestfile(File destinationFile) {
        this.setDest(new FileResource(destinationFile));
    }

    public void setDest(Resource dest) {
        this.dest = dest;
    }

    public void setAppend(boolean append) {
        this.append = append;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
        if (this.outputEncoding == null) {
            this.outputEncoding = encoding;
        }
    }

    public void setOutputEncoding(String outputEncoding) {
        this.outputEncoding = outputEncoding;
    }

    @Deprecated
    public void setForce(boolean forceOverwrite) {
        this.forceOverwrite = forceOverwrite;
    }

    public void setOverwrite(boolean forceOverwrite) {
        this.setForce(forceOverwrite);
    }

    public void setForceReadOnly(boolean f) {
        this.force = f;
    }

    public void setIgnoreEmpty(boolean ignoreEmpty) {
        this.ignoreEmpty = ignoreEmpty;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public Path createPath() {
        Path path = new Path(this.getProject());
        this.add(path);
        return path;
    }

    public void addFileset(FileSet set) {
        this.add(set);
    }

    public void addFilelist(FileList list) {
        this.add(list);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void add(ResourceCollection c) {
        Concat concat = this;
        synchronized (concat) {
            if (this.rc == null) {
                this.rc = new Resources();
                this.rc.setProject(this.getProject());
                this.rc.setCache(true);
            }
        }
        this.rc.add(c);
    }

    public void addFilterChain(FilterChain filterChain) {
        if (this.filterChains == null) {
            this.filterChains = new Vector();
        }
        this.filterChains.addElement(filterChain);
    }

    public void addText(String text) {
        if (this.textBuffer == null) {
            this.textBuffer = new StringBuffer(text.length());
        }
        this.textBuffer.append(text);
    }

    public void addHeader(TextElement headerToAdd) {
        this.header = headerToAdd;
    }

    public void addFooter(TextElement footerToAdd) {
        this.footer = footerToAdd;
    }

    public void setFixLastLine(boolean fixLastLine) {
        this.fixLastLine = fixLastLine;
    }

    public void setEol(FixCRLF.CrLf crlf) {
        String s = crlf.getValue();
        if ("cr".equals(s) || "mac".equals(s)) {
            this.eolString = "\r";
        } else if ("lf".equals(s) || "unix".equals(s)) {
            this.eolString = "\n";
        } else if ("crlf".equals(s) || "dos".equals(s)) {
            this.eolString = "\r\n";
        }
    }

    public void setWriter(Writer outputWriter) {
        this.outputWriter = outputWriter;
    }

    public void setBinary(boolean binary) {
        this.binary = binary;
    }

    public void setFilterBeforeConcat(boolean filterBeforeConcat) {
        this.filterBeforeConcat = filterBeforeConcat;
    }

    @Override
    public void execute() {
        this.validate();
        if (this.binary && this.dest == null) {
            throw new BuildException("dest|destfile attribute is required for binary concatenation");
        }
        ResourceCollection c = this.getResources();
        if (this.isUpToDate(c)) {
            this.log(this.dest + " is up-to-date.", 3);
            return;
        }
        if (c.isEmpty() && this.ignoreEmpty) {
            return;
        }
        try {
            ResourceUtils.copyResource(new ConcatResource(c), this.dest == null ? new LogOutputResource(this, 1) : this.dest, null, null, true, false, this.append, null, null, this.getProject(), this.force);
        }
        catch (IOException e) {
            throw new BuildException("error concatenating content to " + this.dest, e);
        }
    }

    @Override
    public Iterator<Resource> iterator() {
        this.validate();
        return Collections.singletonList(new ConcatResource(this.getResources())).iterator();
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isFilesystemOnly() {
        return false;
    }

    private void validate() {
        this.sanitizeText();
        if (this.binary) {
            if (this.textBuffer != null) {
                throw new BuildException("Nested text is incompatible with binary concatenation");
            }
            if (this.encoding != null || this.outputEncoding != null) {
                throw new BuildException("Setting input or output encoding is incompatible with binary concatenation");
            }
            if (this.filterChains != null) {
                throw new BuildException("Setting filters is incompatible with binary concatenation");
            }
            if (this.fixLastLine) {
                throw new BuildException("Setting fixlastline is incompatible with binary concatenation");
            }
            if (this.header != null || this.footer != null) {
                throw new BuildException("Nested header or footer is incompatible with binary concatenation");
            }
        }
        if (this.dest != null && this.outputWriter != null) {
            throw new BuildException("Cannot specify both a destination resource and an output writer");
        }
        if (this.rc == null && this.textBuffer == null) {
            throw new BuildException("At least one resource must be provided, or some text.");
        }
        if (this.rc != null && this.textBuffer != null) {
            throw new BuildException("Cannot include inline text when using resources.");
        }
    }

    private ResourceCollection getResources() {
        if (this.rc == null) {
            return new StringResource(this.getProject(), this.textBuffer.toString());
        }
        if (this.dest != null) {
            Intersect checkDestNotInSources = new Intersect();
            checkDestNotInSources.setProject(this.getProject());
            checkDestNotInSources.add(this.rc);
            checkDestNotInSources.add(this.dest);
            if (checkDestNotInSources.size() > 0) {
                throw new BuildException("Destination resource %s was specified as an input resource.", this.dest);
            }
        }
        Restrict noexistRc = new Restrict();
        noexistRc.add(NOT_EXISTS);
        noexistRc.add(this.rc);
        for (Resource r : noexistRc) {
            this.log(r + " does not exist.", 0);
        }
        Restrict result = new Restrict();
        result.add(EXISTS);
        result.add(this.rc);
        return result;
    }

    private boolean isUpToDate(ResourceCollection c) {
        return this.dest != null && !this.forceOverwrite && c.stream().noneMatch(r -> SelectorUtils.isOutOfDate(r, this.dest, FILE_UTILS.getFileTimestampGranularity()));
    }

    private void sanitizeText() {
        if (this.textBuffer != null && this.textBuffer.toString().trim().isEmpty()) {
            this.textBuffer = null;
        }
    }

    private Reader getFilteredReader(Reader r) {
        if (this.filterChains == null) {
            return r;
        }
        ChainReaderHelper helper = new ChainReaderHelper();
        helper.setBufferSize(8192);
        helper.setPrimaryReader(r);
        helper.setFilterChains(this.filterChains);
        helper.setProject(this.getProject());
        return helper.getAssembledReader();
    }

    private static interface ReaderFactory<S> {
        public Reader getReader(S var1) throws IOException;
    }

    public static class TextElement
    extends ProjectComponent {
        private String value = "";
        private boolean trimLeading = false;
        private boolean trim = false;
        private boolean filtering = true;
        private String encoding = null;

        public void setFiltering(boolean filtering) {
            this.filtering = filtering;
        }

        private boolean getFiltering() {
            return this.filtering;
        }

        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }

        public void setFile(File file) throws BuildException {
            if (!file.exists()) {
                throw new BuildException("File %s does not exist.", file);
            }
            BufferedReader reader = null;
            try {
                reader = this.encoding == null ? new BufferedReader(new FileReader(file)) : new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath(), new OpenOption[0]), this.encoding));
                this.value = FileUtils.safeReadFully(reader);
            }
            catch (IOException ex) {
                try {
                    throw new BuildException(ex);
                }
                catch (Throwable throwable) {
                    FileUtils.close(reader);
                    throw throwable;
                }
            }
            FileUtils.close(reader);
        }

        public void addText(String value) {
            this.value = this.value + this.getProject().replaceProperties(value);
        }

        public void setTrimLeading(boolean strip) {
            this.trimLeading = strip;
        }

        public void setTrim(boolean trim) {
            this.trim = trim;
        }

        public String getValue() {
            if (this.value == null) {
                this.value = "";
            }
            if (this.value.trim().isEmpty()) {
                this.value = "";
            }
            if (this.trimLeading) {
                StringBuilder b = new StringBuilder();
                boolean startOfLine = true;
                for (char ch : this.value.toCharArray()) {
                    if (startOfLine) {
                        if (ch == ' ' || ch == '\t') continue;
                        startOfLine = false;
                    }
                    b.append(ch);
                    if (ch != '\n' && ch != '\r') continue;
                    startOfLine = true;
                }
                this.value = b.toString();
            }
            if (this.trim) {
                this.value = this.value.trim();
            }
            return this.value;
        }
    }

    private final class ConcatResource
    extends Resource {
        private ResourceCollection c;

        private ConcatResource(ResourceCollection c) {
            this.c = c;
        }

        @Override
        public InputStream getInputStream() {
            Reader rdr;
            if (Concat.this.binary) {
                ConcatResourceInputStream result = new ConcatResourceInputStream(this.c);
                result.setManagingComponent(this);
                return result;
            }
            Reader resourceReader = Concat.this.filterBeforeConcat ? new MultiReader(this.c.iterator(), Concat.this.resourceReaderFactory, true) : Concat.this.getFilteredReader(new MultiReader(this.c.iterator(), Concat.this.resourceReaderFactory, false));
            if (Concat.this.header == null && Concat.this.footer == null) {
                rdr = resourceReader;
            } else {
                int readerCount = 1;
                if (Concat.this.header != null) {
                    ++readerCount;
                }
                if (Concat.this.footer != null) {
                    ++readerCount;
                }
                Reader[] readers = new Reader[readerCount];
                int pos = 0;
                if (Concat.this.header != null) {
                    readers[pos] = new StringReader(Concat.this.header.getValue());
                    if (Concat.this.header.getFiltering()) {
                        readers[pos] = Concat.this.getFilteredReader(readers[pos]);
                    }
                }
                int n = ++pos;
                ++pos;
                readers[n] = resourceReader;
                if (Concat.this.footer != null) {
                    readers[pos] = new StringReader(Concat.this.footer.getValue());
                    if (Concat.this.footer.getFiltering()) {
                        readers[pos] = Concat.this.getFilteredReader(readers[pos]);
                    }
                }
                rdr = new MultiReader(Arrays.asList(readers).iterator(), Concat.this.identityReaderFactory, false);
            }
            return Concat.this.outputEncoding == null ? new ReaderInputStream(rdr) : new ReaderInputStream(rdr, Concat.this.outputEncoding);
        }

        @Override
        public String getName() {
            return Concat.this.resourceName == null ? "concat (" + this.c + ")" : Concat.this.resourceName;
        }
    }

    private final class MultiReader<S>
    extends Reader {
        private Reader reader = null;
        private Iterator<S> readerSources;
        private ReaderFactory<S> factory;
        private final boolean filterBeforeConcat;

        private MultiReader(Iterator<S> readerSources, ReaderFactory<S> factory, boolean filterBeforeConcat) {
            this.readerSources = readerSources;
            this.factory = factory;
            this.filterBeforeConcat = filterBeforeConcat;
        }

        private Reader getReader() throws IOException {
            if (this.reader == null && this.readerSources.hasNext()) {
                this.reader = this.factory.getReader(this.readerSources.next());
                if (this.isFixLastLine()) {
                    this.reader = new LastLineFixingReader(this.reader);
                }
                if (this.filterBeforeConcat) {
                    this.reader = Concat.this.getFilteredReader(this.reader);
                }
            }
            return this.reader;
        }

        private void nextReader() throws IOException {
            this.close();
            this.reader = null;
        }

        @Override
        public int read() throws IOException {
            while (this.getReader() != null) {
                int ch = this.getReader().read();
                if (ch == -1) {
                    this.nextReader();
                    continue;
                }
                return ch;
            }
            return -1;
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            int amountRead = 0;
            while (this.getReader() != null) {
                int nRead = this.getReader().read(cbuf, off, len);
                if (nRead == -1 || nRead == 0) {
                    this.nextReader();
                    continue;
                }
                off += nRead;
                amountRead += nRead;
                if ((len -= nRead) != 0) continue;
                return amountRead;
            }
            if (amountRead == 0) {
                return -1;
            }
            return amountRead;
        }

        @Override
        public void close() throws IOException {
            if (this.reader != null) {
                this.reader.close();
            }
        }

        private boolean isFixLastLine() {
            return Concat.this.fixLastLine && Concat.this.textBuffer == null;
        }
    }

    private final class LastLineFixingReader
    extends Reader {
        private final Reader reader;
        private int lastPos = 0;
        private final char[] lastChars = new char[Concat.access$000(Concat.this).length()];
        private boolean needAddSeparator = false;

        private LastLineFixingReader(Reader reader) {
            this.reader = reader;
        }

        @Override
        public int read() throws IOException {
            if (this.needAddSeparator) {
                if (this.lastPos >= Concat.this.eolString.length()) {
                    return -1;
                }
                return Concat.this.eolString.charAt(this.lastPos++);
            }
            int ch = this.reader.read();
            if (ch == -1) {
                if (this.isMissingEndOfLine()) {
                    this.needAddSeparator = true;
                    this.lastPos = 1;
                    return Concat.this.eolString.charAt(0);
                }
            } else {
                this.addLastChar((char)ch);
                return ch;
            }
            return -1;
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            int amountRead;
            block7: {
                amountRead = 0;
                while (true) {
                    if (this.needAddSeparator) {
                        if (this.lastPos < Concat.this.eolString.length()) {
                            cbuf[off] = Concat.this.eolString.charAt(this.lastPos++);
                            ++off;
                            ++amountRead;
                            if (--len != 0) continue;
                            return amountRead;
                        }
                        break block7;
                    }
                    int nRead = this.reader.read(cbuf, off, len);
                    if (nRead == -1 || nRead == 0) {
                        if (this.isMissingEndOfLine()) {
                            this.needAddSeparator = true;
                            this.lastPos = 0;
                            continue;
                        }
                        break block7;
                    }
                    for (int i = nRead; i > nRead - this.lastChars.length && i > 0; --i) {
                        this.addLastChar(cbuf[off + i - 1]);
                    }
                    off += nRead;
                    amountRead += nRead;
                    if ((len -= nRead) == 0) break;
                }
                return amountRead;
            }
            if (amountRead == 0) {
                return -1;
            }
            return amountRead;
        }

        @Override
        public void close() throws IOException {
            this.reader.close();
        }

        private void addLastChar(char ch) {
            System.arraycopy(this.lastChars, 1, this.lastChars, 0, this.lastChars.length - 2 + 1);
            this.lastChars[this.lastChars.length - 1] = ch;
        }

        private boolean isMissingEndOfLine() {
            for (int i = 0; i < this.lastChars.length; ++i) {
                if (this.lastChars[i] == Concat.this.eolString.charAt(i)) continue;
                return true;
            }
            return false;
        }
    }
}

