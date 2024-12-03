/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.taskdefs.MatchingTask;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.types.resources.Union;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.StreamUtils;

public class Replace
extends MatchingTask {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private File sourceFile = null;
    private NestedString token = null;
    private NestedString value = new NestedString();
    private Resource propertyResource = null;
    private Resource replaceFilterResource = null;
    private Properties properties = null;
    private List<Replacefilter> replacefilters = new ArrayList<Replacefilter>();
    private File dir = null;
    private int fileCount;
    private int replaceCount;
    private boolean summary = false;
    private String encoding = null;
    private Union resources;
    private boolean preserveLastModified = false;
    private boolean failOnNoReplacements = false;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void execute() throws BuildException {
        Properties savedProperties;
        ArrayList<Replacefilter> savedFilters = new ArrayList<Replacefilter>(this.replacefilters);
        Properties properties = savedProperties = this.properties == null ? null : (Properties)this.properties.clone();
        if (this.token != null) {
            StringBuilder val = new StringBuilder(this.value.getText());
            this.stringReplace(val, "\r\n", "\n");
            this.stringReplace(val, "\n", System.lineSeparator());
            String[] tok2 = new StringBuilder(this.token.getText());
            this.stringReplace((StringBuilder)tok2, "\r\n", "\n");
            this.stringReplace((StringBuilder)tok2, "\n", System.lineSeparator());
            Replacefilter firstFilter = this.createPrimaryfilter();
            firstFilter.setToken(tok2.toString());
            firstFilter.setValue(val.toString());
        }
        try {
            if (this.replaceFilterResource != null) {
                Properties properties2 = this.getProperties(this.replaceFilterResource);
                StreamUtils.iteratorAsStream(this.getOrderedIterator(properties2)).forEach(tok -> {
                    Replacefilter replaceFilter = this.createReplacefilter();
                    replaceFilter.setToken((String)tok);
                    replaceFilter.setValue(properties2.getProperty((String)tok));
                });
            }
            this.validateAttributes();
            if (this.propertyResource != null) {
                this.properties = this.getProperties(this.propertyResource);
            }
            this.validateReplacefilters();
            this.fileCount = 0;
            this.replaceCount = 0;
            if (this.sourceFile != null) {
                this.processFile(this.sourceFile);
            }
            if (this.dir != null) {
                DirectoryScanner ds = super.getDirectoryScanner(this.dir);
                for (String src : ds.getIncludedFiles()) {
                    File file = new File(this.dir, src);
                    this.processFile(file);
                }
            }
            if (this.resources != null) {
                for (Resource r : this.resources) {
                    this.processFile(r.as(FileProvider.class).getFile());
                }
            }
            if (this.summary) {
                this.log("Replaced " + this.replaceCount + " occurrences in " + this.fileCount + " files.", 2);
            }
            if (this.failOnNoReplacements && this.replaceCount == 0) {
                throw new BuildException("didn't replace anything");
            }
        }
        finally {
            this.replacefilters = savedFilters;
            this.properties = savedProperties;
        }
    }

    public void validateAttributes() throws BuildException {
        if (this.sourceFile == null && this.dir == null && this.resources == null) {
            throw new BuildException("Either the file or the dir attribute or nested resources must be specified", this.getLocation());
        }
        if (this.propertyResource != null && !this.propertyResource.isExists()) {
            throw new BuildException("Property file " + this.propertyResource.getName() + " does not exist.", this.getLocation());
        }
        if (this.token == null && this.replacefilters.isEmpty()) {
            throw new BuildException("Either token or a nested replacefilter must be specified", this.getLocation());
        }
        if (this.token != null && this.token.getText().isEmpty()) {
            throw new BuildException("The token attribute must not be an empty string.", this.getLocation());
        }
    }

    public void validateReplacefilters() throws BuildException {
        this.replacefilters.forEach(Replacefilter::validate);
    }

    public Properties getProperties(File propertyFile) throws BuildException {
        return this.getProperties(new FileResource(this.getProject(), propertyFile));
    }

    public Properties getProperties(Resource propertyResource) throws BuildException {
        Properties props = new Properties();
        try (InputStream in = propertyResource.getInputStream();){
            props.load(in);
        }
        catch (IOException e) {
            throw new BuildException("Property resource (%s) cannot be loaded.", propertyResource.getName());
        }
        return props;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void processFile(File src) throws BuildException {
        if (!src.exists()) {
            throw new BuildException("Replace: source file " + src.getPath() + " doesn't exist", this.getLocation());
        }
        int repCountStart = this.replaceCount;
        this.logFilterChain(src.getPath());
        try {
            File temp = FILE_UTILS.createTempFile(this.getProject(), "rep", ".tmp", src.getParentFile(), false, true);
            try {
                boolean changes;
                try (FileInput in = new FileInput(src);
                     FileOutput out = new FileOutput(temp);){
                    out.setInputBuffer(this.buildFilterChain(in.getOutputBuffer()));
                    while (in.readChunk()) {
                        if (!this.processFilterChain()) continue;
                        out.process();
                    }
                    this.flushFilterChain();
                    out.flush();
                }
                boolean bl = changes = this.replaceCount != repCountStart;
                if (changes) {
                    ++this.fileCount;
                    long origLastModified = src.lastModified();
                    FILE_UTILS.rename(temp, src, true);
                    if (this.preserveLastModified) {
                        FILE_UTILS.setFileLastModified(src, origLastModified);
                    }
                }
            }
            finally {
                if (temp.isFile() && !temp.delete()) {
                    temp.deleteOnExit();
                }
            }
        }
        catch (IOException ioe) {
            throw new BuildException("IOException in " + src + " - " + ioe.getClass().getName() + ":" + ioe.getMessage(), ioe, this.getLocation());
        }
    }

    private void flushFilterChain() {
        this.replacefilters.forEach(Replacefilter::flush);
    }

    private boolean processFilterChain() {
        return this.replacefilters.stream().allMatch(Replacefilter::process);
    }

    private StringBuffer buildFilterChain(StringBuffer inputBuffer) {
        StringBuffer buf = inputBuffer;
        for (Replacefilter filter : this.replacefilters) {
            filter.setInputBuffer(buf);
            buf = filter.getOutputBuffer();
        }
        return buf;
    }

    private void logFilterChain(String filename) {
        this.replacefilters.forEach(filter -> this.log("Replacing in " + filename + ": " + filter.getToken() + " --> " + filter.getReplaceValue(), 3));
    }

    public void setFile(File file) {
        this.sourceFile = file;
    }

    public void setSummary(boolean summary) {
        this.summary = summary;
    }

    public void setReplaceFilterFile(File replaceFilterFile) {
        this.setReplaceFilterResource(new FileResource(this.getProject(), replaceFilterFile));
    }

    public void setReplaceFilterResource(Resource replaceFilter) {
        this.replaceFilterResource = replaceFilter;
    }

    public void setDir(File dir) {
        this.dir = dir;
    }

    public void setToken(String token) {
        this.createReplaceToken().addText(token);
    }

    public void setValue(String value) {
        this.createReplaceValue().addText(value);
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public NestedString createReplaceToken() {
        if (this.token == null) {
            this.token = new NestedString();
        }
        return this.token;
    }

    public NestedString createReplaceValue() {
        return this.value;
    }

    public void setPropertyFile(File propertyFile) {
        this.setPropertyResource(new FileResource(propertyFile));
    }

    public void setPropertyResource(Resource propertyResource) {
        this.propertyResource = propertyResource;
    }

    public Replacefilter createReplacefilter() {
        Replacefilter filter = new Replacefilter();
        this.replacefilters.add(filter);
        return filter;
    }

    public void addConfigured(ResourceCollection rc) {
        if (!rc.isFilesystemOnly()) {
            throw new BuildException("only filesystem resources are supported");
        }
        if (this.resources == null) {
            this.resources = new Union();
        }
        this.resources.add(rc);
    }

    public void setPreserveLastModified(boolean b) {
        this.preserveLastModified = b;
    }

    public void setFailOnNoReplacements(boolean b) {
        this.failOnNoReplacements = b;
    }

    private Replacefilter createPrimaryfilter() {
        Replacefilter filter = new Replacefilter();
        this.replacefilters.add(0, filter);
        return filter;
    }

    private void stringReplace(StringBuilder str, String str1, String str2) {
        int found = str.indexOf(str1);
        int str1Length = str1.length();
        int str2Length = str2.length();
        while (found >= 0) {
            str.replace(found, found + str1Length, str2);
            found = str.indexOf(str1, found + str2Length);
        }
    }

    private Iterator<String> getOrderedIterator(Properties props) {
        ArrayList<String> keys = new ArrayList<String>(props.stringPropertyNames());
        keys.sort(Comparator.comparingInt(String::length).reversed());
        return keys.iterator();
    }

    public class NestedString {
        private boolean expandProperties = false;
        private StringBuffer buf = new StringBuffer();

        public void setExpandProperties(boolean b) {
            this.expandProperties = b;
        }

        public void addText(String val) {
            this.buf.append(val);
        }

        public String getText() {
            String s = this.buf.toString();
            return this.expandProperties ? Replace.this.getProject().replaceProperties(s) : s;
        }
    }

    public class Replacefilter {
        private NestedString token;
        private NestedString value;
        private String replaceValue;
        private String property;
        private StringBuffer inputBuffer;
        private StringBuffer outputBuffer = new StringBuffer();

        public void validate() throws BuildException {
            if (this.token == null) {
                throw new BuildException("token is a mandatory for replacefilter.");
            }
            if (this.token.getText().isEmpty()) {
                throw new BuildException("The token must not be an empty string.");
            }
            if (this.value != null && this.property != null) {
                throw new BuildException("Either value or property can be specified, but a replacefilter element cannot have both.");
            }
            if (this.property != null) {
                if (Replace.this.propertyResource == null) {
                    throw new BuildException("The replacefilter's property attribute can only be used with the replacetask's propertyFile/Resource attribute.");
                }
                if (Replace.this.properties == null || Replace.this.properties.getProperty(this.property) == null) {
                    throw new BuildException("property \"%s\" was not found in %s", this.property, Replace.this.propertyResource.getName());
                }
            }
            this.replaceValue = this.getReplaceValue();
        }

        public String getReplaceValue() {
            if (this.property != null) {
                return Replace.this.properties.getProperty(this.property);
            }
            if (this.value != null) {
                return this.value.getText();
            }
            if (Replace.this.value != null) {
                return Replace.this.value.getText();
            }
            return "";
        }

        public void setToken(String t) {
            this.createReplaceToken().addText(t);
        }

        public String getToken() {
            return this.token.getText();
        }

        public void setValue(String value) {
            this.createReplaceValue().addText(value);
        }

        public String getValue() {
            return this.value.getText();
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public String getProperty() {
            return this.property;
        }

        public NestedString createReplaceToken() {
            if (this.token == null) {
                this.token = new NestedString();
            }
            return this.token;
        }

        public NestedString createReplaceValue() {
            if (this.value == null) {
                this.value = new NestedString();
            }
            return this.value;
        }

        StringBuffer getOutputBuffer() {
            return this.outputBuffer;
        }

        void setInputBuffer(StringBuffer input) {
            this.inputBuffer = input;
        }

        boolean process() {
            String t = this.getToken();
            if (this.inputBuffer.length() > t.length()) {
                int pos = this.replace();
                pos = Math.max(this.inputBuffer.length() - t.length(), pos);
                this.outputBuffer.append(this.inputBuffer.substring(0, pos));
                this.inputBuffer.delete(0, pos);
                return true;
            }
            return false;
        }

        void flush() {
            this.replace();
            this.outputBuffer.append(this.inputBuffer);
            this.inputBuffer.delete(0, this.inputBuffer.length());
        }

        private int replace() {
            String t = this.getToken();
            int found = this.inputBuffer.indexOf(t);
            int pos = -1;
            int tokenLength = t.length();
            int replaceValueLength = this.replaceValue.length();
            while (found >= 0) {
                this.inputBuffer.replace(found, found + tokenLength, this.replaceValue);
                pos = found + replaceValueLength;
                found = this.inputBuffer.indexOf(t, pos);
                ++Replace.this.replaceCount;
            }
            return pos;
        }
    }

    private class FileInput
    implements AutoCloseable {
        private static final int BUFF_SIZE = 4096;
        private StringBuffer outputBuffer = new StringBuffer();
        private final InputStream is;
        private Reader reader;
        private char[] buffer = new char[4096];

        FileInput(File source) throws IOException {
            this.is = Files.newInputStream(source.toPath(), new OpenOption[0]);
            try {
                this.reader = new BufferedReader(Replace.this.encoding != null ? new InputStreamReader(this.is, Replace.this.encoding) : new InputStreamReader(this.is));
            }
            finally {
                if (this.reader == null) {
                    this.is.close();
                }
            }
        }

        StringBuffer getOutputBuffer() {
            return this.outputBuffer;
        }

        boolean readChunk() throws IOException {
            int bufferLength = this.reader.read(this.buffer);
            if (bufferLength < 0) {
                return false;
            }
            this.outputBuffer.append(new String(this.buffer, 0, bufferLength));
            return true;
        }

        @Override
        public void close() throws IOException {
            this.is.close();
        }
    }

    private class FileOutput
    implements AutoCloseable {
        private StringBuffer inputBuffer;
        private final OutputStream os;
        private Writer writer;

        FileOutput(File out) throws IOException {
            this.os = Files.newOutputStream(out.toPath(), new OpenOption[0]);
            try {
                this.writer = new BufferedWriter(Replace.this.encoding != null ? new OutputStreamWriter(this.os, Replace.this.encoding) : new OutputStreamWriter(this.os));
            }
            finally {
                if (this.writer == null) {
                    this.os.close();
                }
            }
        }

        void setInputBuffer(StringBuffer input) {
            this.inputBuffer = input;
        }

        boolean process() throws IOException {
            this.writer.write(this.inputBuffer.toString());
            this.inputBuffer.delete(0, this.inputBuffer.length());
            return false;
        }

        void flush() throws IOException {
            this.process();
            this.writer.flush();
        }

        @Override
        public void close() throws IOException {
            this.os.close();
        }
    }
}

