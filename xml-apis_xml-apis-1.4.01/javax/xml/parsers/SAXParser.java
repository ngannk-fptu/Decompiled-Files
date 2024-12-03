/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.parsers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.FilePathToURI;
import javax.xml.validation.Schema;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public abstract class SAXParser {
    private static final boolean DEBUG = false;

    protected SAXParser() {
    }

    public void reset() {
        throw new UnsupportedOperationException("This SAXParser, \"" + this.getClass().getName() + "\", does not support the reset functionality." + "  Specification \"" + this.getClass().getPackage().getSpecificationTitle() + "\"" + " version \"" + this.getClass().getPackage().getSpecificationVersion() + "\"");
    }

    public void parse(InputStream inputStream, HandlerBase handlerBase) throws SAXException, IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }
        InputSource inputSource = new InputSource(inputStream);
        this.parse(inputSource, handlerBase);
    }

    public void parse(InputStream inputStream, HandlerBase handlerBase, String string) throws SAXException, IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }
        InputSource inputSource = new InputSource(inputStream);
        inputSource.setSystemId(string);
        this.parse(inputSource, handlerBase);
    }

    public void parse(InputStream inputStream, DefaultHandler defaultHandler) throws SAXException, IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }
        InputSource inputSource = new InputSource(inputStream);
        this.parse(inputSource, defaultHandler);
    }

    public void parse(InputStream inputStream, DefaultHandler defaultHandler, String string) throws SAXException, IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }
        InputSource inputSource = new InputSource(inputStream);
        inputSource.setSystemId(string);
        this.parse(inputSource, defaultHandler);
    }

    public void parse(String string, HandlerBase handlerBase) throws SAXException, IOException {
        if (string == null) {
            throw new IllegalArgumentException("uri cannot be null");
        }
        InputSource inputSource = new InputSource(string);
        this.parse(inputSource, handlerBase);
    }

    public void parse(String string, DefaultHandler defaultHandler) throws SAXException, IOException {
        if (string == null) {
            throw new IllegalArgumentException("uri cannot be null");
        }
        InputSource inputSource = new InputSource(string);
        this.parse(inputSource, defaultHandler);
    }

    public void parse(File file, HandlerBase handlerBase) throws SAXException, IOException {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        String string = FilePathToURI.filepath2URI(file.getAbsolutePath());
        InputSource inputSource = new InputSource(string);
        this.parse(inputSource, handlerBase);
    }

    public void parse(File file, DefaultHandler defaultHandler) throws SAXException, IOException {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        String string = FilePathToURI.filepath2URI(file.getAbsolutePath());
        InputSource inputSource = new InputSource(string);
        this.parse(inputSource, defaultHandler);
    }

    public void parse(InputSource inputSource, HandlerBase handlerBase) throws SAXException, IOException {
        if (inputSource == null) {
            throw new IllegalArgumentException("InputSource cannot be null");
        }
        Parser parser = this.getParser();
        if (handlerBase != null) {
            parser.setDocumentHandler(handlerBase);
            parser.setEntityResolver(handlerBase);
            parser.setErrorHandler(handlerBase);
            parser.setDTDHandler(handlerBase);
        }
        parser.parse(inputSource);
    }

    public void parse(InputSource inputSource, DefaultHandler defaultHandler) throws SAXException, IOException {
        if (inputSource == null) {
            throw new IllegalArgumentException("InputSource cannot be null");
        }
        XMLReader xMLReader = this.getXMLReader();
        if (defaultHandler != null) {
            xMLReader.setContentHandler(defaultHandler);
            xMLReader.setEntityResolver(defaultHandler);
            xMLReader.setErrorHandler(defaultHandler);
            xMLReader.setDTDHandler(defaultHandler);
        }
        xMLReader.parse(inputSource);
    }

    public abstract Parser getParser() throws SAXException;

    public abstract XMLReader getXMLReader() throws SAXException;

    public abstract boolean isNamespaceAware();

    public abstract boolean isValidating();

    public abstract void setProperty(String var1, Object var2) throws SAXNotRecognizedException, SAXNotSupportedException;

    public abstract Object getProperty(String var1) throws SAXNotRecognizedException, SAXNotSupportedException;

    public Schema getSchema() {
        throw new UnsupportedOperationException("This parser does not support specification \"" + this.getClass().getPackage().getSpecificationTitle() + "\" version \"" + this.getClass().getPackage().getSpecificationVersion() + "\"");
    }

    public boolean isXIncludeAware() {
        throw new UnsupportedOperationException("This parser does not support specification \"" + this.getClass().getPackage().getSpecificationTitle() + "\" version \"" + this.getClass().getPackage().getSpecificationVersion() + "\"");
    }
}

