/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.parsers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.FilePathToURI;
import javax.xml.validation.Schema;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public abstract class DocumentBuilder {
    private static final boolean DEBUG = false;

    protected DocumentBuilder() {
    }

    public void reset() {
        throw new UnsupportedOperationException("This DocumentBuilder, \"" + this.getClass().getName() + "\", does not support the reset functionality." + "  Specification \"" + this.getClass().getPackage().getSpecificationTitle() + "\"" + " version \"" + this.getClass().getPackage().getSpecificationVersion() + "\"");
    }

    public Document parse(InputStream inputStream) throws SAXException, IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }
        InputSource inputSource = new InputSource(inputStream);
        return this.parse(inputSource);
    }

    public Document parse(InputStream inputStream, String string) throws SAXException, IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }
        InputSource inputSource = new InputSource(inputStream);
        inputSource.setSystemId(string);
        return this.parse(inputSource);
    }

    public Document parse(String string) throws SAXException, IOException {
        if (string == null) {
            throw new IllegalArgumentException("URI cannot be null");
        }
        InputSource inputSource = new InputSource(string);
        return this.parse(inputSource);
    }

    public Document parse(File file) throws SAXException, IOException {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        String string = FilePathToURI.filepath2URI(file.getAbsolutePath());
        InputSource inputSource = new InputSource(string);
        return this.parse(inputSource);
    }

    public abstract Document parse(InputSource var1) throws SAXException, IOException;

    public abstract boolean isNamespaceAware();

    public abstract boolean isValidating();

    public abstract void setEntityResolver(EntityResolver var1);

    public abstract void setErrorHandler(ErrorHandler var1);

    public abstract Document newDocument();

    public abstract DOMImplementation getDOMImplementation();

    public Schema getSchema() {
        throw new UnsupportedOperationException("This parser does not support specification \"" + this.getClass().getPackage().getSpecificationTitle() + "\" version \"" + this.getClass().getPackage().getSpecificationVersion() + "\"");
    }

    public boolean isXIncludeAware() {
        throw new UnsupportedOperationException("This parser does not support specification \"" + this.getClass().getPackage().getSpecificationTitle() + "\" version \"" + this.getClass().getPackage().getSpecificationVersion() + "\"");
    }
}

