/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.transform.stream;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.FilePathToURI;

public class StreamSource
implements Source {
    public static final String FEATURE = "http://javax.xml.transform.stream.StreamSource/feature";
    private String publicId;
    private String systemId;
    private InputStream inputStream;
    private Reader reader;

    public StreamSource() {
    }

    public StreamSource(InputStream inputStream) {
        this.setInputStream(inputStream);
    }

    public StreamSource(InputStream inputStream, String string) {
        this.setInputStream(inputStream);
        this.setSystemId(string);
    }

    public StreamSource(Reader reader) {
        this.setReader(reader);
    }

    public StreamSource(Reader reader, String string) {
        this.setReader(reader);
        this.setSystemId(string);
    }

    public StreamSource(String string) {
        this.systemId = string;
    }

    public StreamSource(File file) {
        this.setSystemId(file);
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
        return this.inputStream;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public Reader getReader() {
        return this.reader;
    }

    public void setPublicId(String string) {
        this.publicId = string;
    }

    public String getPublicId() {
        return this.publicId;
    }

    public void setSystemId(String string) {
        this.systemId = string;
    }

    public String getSystemId() {
        return this.systemId;
    }

    public void setSystemId(File file) {
        this.systemId = FilePathToURI.filepath2URI(file.getAbsolutePath());
    }
}

