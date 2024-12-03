/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.stax2.validation.XMLValidationSchema
 *  org.codehaus.stax2.validation.XMLValidationSchemaFactory
 */
package com.ctc.wstx.msv;

import com.ctc.wstx.api.ValidatorConfig;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.shaded.msv_core.reader.util.IgnoreController;
import com.ctc.wstx.util.URLUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;

public abstract class BaseSchemaFactory
extends XMLValidationSchemaFactory {
    protected static SAXParserFactory sSaxFactory;
    protected final ValidatorConfig mConfig = ValidatorConfig.createDefaults();

    protected BaseSchemaFactory(String schemaType) {
        super(schemaType);
    }

    public boolean isPropertySupported(String propName) {
        return this.mConfig.isPropertySupported(propName);
    }

    public boolean setProperty(String propName, Object value) {
        return this.mConfig.setProperty(propName, value);
    }

    public Object getProperty(String propName) {
        return this.mConfig.getProperty(propName);
    }

    public XMLValidationSchema createSchema(InputStream in, String encoding, String publicId, String systemId) throws XMLStreamException {
        InputSource src = new InputSource(in);
        src.setEncoding(encoding);
        src.setPublicId(publicId);
        src.setSystemId(systemId);
        return this.loadSchema(src, systemId);
    }

    public XMLValidationSchema createSchema(Reader r, String publicId, String systemId) throws XMLStreamException {
        InputSource src = new InputSource(r);
        src.setPublicId(publicId);
        src.setSystemId(systemId);
        return this.loadSchema(src, systemId);
    }

    public XMLValidationSchema createSchema(URL url) throws XMLStreamException {
        try {
            InputStream in = URLUtil.inputStreamFromURL(url);
            InputSource src = new InputSource(in);
            src.setSystemId(url.toExternalForm());
            return this.loadSchema(src, url);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    public XMLValidationSchema createSchema(File f) throws XMLStreamException {
        try {
            return this.createSchema(f.toURL());
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    protected abstract XMLValidationSchema loadSchema(InputSource var1, Object var2) throws XMLStreamException;

    protected static synchronized SAXParserFactory getSaxFactory() {
        if (sSaxFactory == null) {
            sSaxFactory = SAXParserFactory.newInstance();
            sSaxFactory.setNamespaceAware(true);
        }
        return sSaxFactory;
    }

    static final class MyGrammarController
    extends IgnoreController {
        public String mErrorMsg = null;

        @Override
        public void error(Locator[] locs, String msg, Exception nestedException) {
            this.mErrorMsg = this.mErrorMsg == null ? msg : this.mErrorMsg + "; " + msg;
        }
    }
}

