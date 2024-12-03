/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.reader;

import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController2;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

public class Controller
implements GrammarReaderController2,
ErrorHandler {
    private final GrammarReaderController core;
    private boolean _hadError = false;

    public GrammarReaderController getCore() {
        return this.core;
    }

    public boolean hadError() {
        return this._hadError;
    }

    public final void setErrorFlag() {
        this._hadError = true;
    }

    public Controller(GrammarReaderController _core) {
        this.core = _core;
    }

    @Deprecated
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        return this.core.resolveEntity(publicId, systemId);
    }

    public void warning(Locator[] locs, String errorMessage) {
        this.core.warning(locs, errorMessage);
    }

    public void error(Locator[] locs, String errorMessage, Exception nestedException) {
        this.setErrorFlag();
        this.core.error(locs, errorMessage, nestedException);
    }

    public void error(String errorMessage, Exception nestedException) {
        this.error(new Locator[0], errorMessage, nestedException);
    }

    public void fatalError(SAXParseException spe) {
        this.error(spe);
    }

    public void error(SAXParseException spe) {
        this.error(this.getLocator(spe), spe.getMessage(), spe.getException());
    }

    public void warning(SAXParseException spe) {
        this.warning(this.getLocator(spe), spe.getMessage());
    }

    public void error(IOException e, Locator source) {
        this.error(new Locator[]{source}, e.getMessage(), e);
    }

    public void error(SAXException e, Locator source) {
        if (e.getException() instanceof RuntimeException) {
            throw (RuntimeException)e.getException();
        }
        if (e instanceof SAXParseException) {
            this.error((SAXParseException)e);
        } else {
            this.error(new Locator[]{source}, e.getMessage(), e);
        }
    }

    public void error(ParserConfigurationException e, Locator source) {
        this.error(new Locator[]{source}, e.getMessage(), e);
    }

    protected Locator[] getLocator(SAXParseException spe) {
        LocatorImpl loc = new LocatorImpl();
        loc.setColumnNumber(spe.getColumnNumber());
        loc.setLineNumber(spe.getLineNumber());
        loc.setSystemId(spe.getSystemId());
        loc.setPublicId(spe.getPublicId());
        return new Locator[]{loc};
    }

    public LSResourceResolver getLSResourceResolver() {
        if (this.core instanceof GrammarReaderController2) {
            return ((GrammarReaderController2)this.core).getLSResourceResolver();
        }
        return null;
    }
}

