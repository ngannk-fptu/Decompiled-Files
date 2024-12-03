/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.api.ValidatorConfig;
import com.ctc.wstx.dtd.FullDTDReader;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.io.InputBootstrapper;
import com.ctc.wstx.io.InputSourceFactory;
import com.ctc.wstx.io.ReaderBootstrapper;
import com.ctc.wstx.io.ReaderSource;
import com.ctc.wstx.io.StreamBootstrapper;
import com.ctc.wstx.util.DefaultXmlSymbolTable;
import com.ctc.wstx.util.SymbolTable;
import com.ctc.wstx.util.URLUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidationSchemaFactory;

public class DTDSchemaFactory
extends XMLValidationSchemaFactory {
    static final SymbolTable mRootSymbols = DefaultXmlSymbolTable.getInstance();
    protected final ValidatorConfig mSchemaConfig;
    protected final ReaderConfig mReaderConfig = ReaderConfig.createFullDefaults();

    public DTDSchemaFactory() {
        super("http://www.w3.org/XML/1998/namespace");
        this.mSchemaConfig = ValidatorConfig.createDefaults();
    }

    public boolean isPropertySupported(String propName) {
        return this.mSchemaConfig.isPropertySupported(propName);
    }

    public boolean setProperty(String propName, Object value) {
        return this.mSchemaConfig.setProperty(propName, value);
    }

    public Object getProperty(String propName) {
        return this.mSchemaConfig.getProperty(propName);
    }

    public XMLValidationSchema createSchema(InputStream in, String encoding, String publicId, String systemId) throws XMLStreamException {
        ReaderConfig rcfg = this.createPrivateReaderConfig();
        return this.doCreateSchema(rcfg, StreamBootstrapper.getInstance(publicId, systemId, in), publicId, systemId, null);
    }

    public XMLValidationSchema createSchema(Reader r, String publicId, String systemId) throws XMLStreamException {
        ReaderConfig rcfg = this.createPrivateReaderConfig();
        return this.doCreateSchema(rcfg, ReaderBootstrapper.getInstance(publicId, systemId, r, null), publicId, systemId, null);
    }

    public XMLValidationSchema createSchema(URL url) throws XMLStreamException {
        ReaderConfig rcfg = this.createPrivateReaderConfig();
        try {
            InputStream in = URLUtil.inputStreamFromURL(url);
            return this.doCreateSchema(rcfg, StreamBootstrapper.getInstance(null, null, in), null, url.toExternalForm(), url);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    public XMLValidationSchema createSchema(File f) throws XMLStreamException {
        ReaderConfig rcfg = this.createPrivateReaderConfig();
        try {
            URL url = f.toURL();
            return this.doCreateSchema(rcfg, StreamBootstrapper.getInstance(null, null, new FileInputStream(f)), null, url.toExternalForm(), url);
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    protected XMLValidationSchema doCreateSchema(ReaderConfig rcfg, InputBootstrapper bs, String publicId, String systemId, URL ctxt) throws XMLStreamException {
        try {
            Reader r = bs.bootstrapInput(rcfg, false, 0);
            if (bs.declaredXml11()) {
                rcfg.enableXml11(true);
            }
            if (ctxt == null) {
                ctxt = URLUtil.urlFromCurrentDir();
            }
            ReaderSource src = InputSourceFactory.constructEntitySource(rcfg, null, null, bs, publicId, systemId, 0, ctxt, r);
            return FullDTDReader.readExternalSubset(src, rcfg, null, true, bs.getDeclaredVersion());
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    private ReaderConfig createPrivateReaderConfig() {
        return this.mReaderConfig.createNonShared(mRootSymbols.makeChild());
    }

    static {
        mRootSymbols.setInternStrings(true);
    }
}

