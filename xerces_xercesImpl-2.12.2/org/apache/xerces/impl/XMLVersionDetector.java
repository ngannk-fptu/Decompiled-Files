/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl;

import java.io.CharConversionException;
import java.io.EOFException;
import java.io.IOException;
import org.apache.xerces.impl.XMLEntityHandler;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLEntityScanner;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.io.MalformedByteSequenceException;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLInputSource;

public class XMLVersionDetector {
    private static final char[] XML11_VERSION = new char[]{'1', '.', '1'};
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
    protected static final String fVersionSymbol = "version".intern();
    protected static final String fXMLSymbol = "[xml]".intern();
    protected SymbolTable fSymbolTable;
    protected XMLErrorReporter fErrorReporter;
    protected XMLEntityManager fEntityManager;
    protected String fEncoding = null;
    private final char[] fExpectedVersionString = new char[]{'<', '?', 'x', 'm', 'l', ' ', 'v', 'e', 'r', 's', 'i', 'o', 'n', '=', ' ', ' ', ' ', ' ', ' '};

    public void reset(XMLComponentManager xMLComponentManager) throws XMLConfigurationException {
        this.fSymbolTable = (SymbolTable)xMLComponentManager.getProperty(SYMBOL_TABLE);
        this.fErrorReporter = (XMLErrorReporter)xMLComponentManager.getProperty(ERROR_REPORTER);
        this.fEntityManager = (XMLEntityManager)xMLComponentManager.getProperty(ENTITY_MANAGER);
        for (int i = 14; i < this.fExpectedVersionString.length; ++i) {
            this.fExpectedVersionString[i] = 32;
        }
    }

    public void startDocumentParsing(XMLEntityHandler xMLEntityHandler, short s) {
        if (s == 1) {
            this.fEntityManager.setScannerVersion((short)1);
        } else {
            this.fEntityManager.setScannerVersion((short)2);
        }
        this.fErrorReporter.setDocumentLocator(this.fEntityManager.getEntityScanner());
        this.fEntityManager.setEntityHandler(xMLEntityHandler);
        xMLEntityHandler.startEntity(fXMLSymbol, this.fEntityManager.getCurrentResourceIdentifier(), this.fEncoding, null);
    }

    public short determineDocVersion(XMLInputSource xMLInputSource) throws IOException {
        this.fEncoding = this.fEntityManager.setupCurrentEntity(fXMLSymbol, xMLInputSource, false, true);
        this.fEntityManager.setScannerVersion((short)1);
        XMLEntityScanner xMLEntityScanner = this.fEntityManager.getEntityScanner();
        try {
            int n;
            if (!xMLEntityScanner.skipString("<?xml")) {
                return 1;
            }
            if (!xMLEntityScanner.skipDeclSpaces()) {
                this.fixupCurrentEntity(this.fEntityManager, this.fExpectedVersionString, 5);
                return 1;
            }
            if (!xMLEntityScanner.skipString("version")) {
                this.fixupCurrentEntity(this.fEntityManager, this.fExpectedVersionString, 6);
                return 1;
            }
            xMLEntityScanner.skipDeclSpaces();
            if (xMLEntityScanner.peekChar() != 61) {
                this.fixupCurrentEntity(this.fEntityManager, this.fExpectedVersionString, 13);
                return 1;
            }
            xMLEntityScanner.scanChar();
            xMLEntityScanner.skipDeclSpaces();
            int n2 = xMLEntityScanner.scanChar();
            this.fExpectedVersionString[14] = (char)n2;
            for (n = 0; n < XML11_VERSION.length; ++n) {
                this.fExpectedVersionString[15 + n] = (char)xMLEntityScanner.scanChar();
            }
            this.fExpectedVersionString[18] = (char)xMLEntityScanner.scanChar();
            this.fixupCurrentEntity(this.fEntityManager, this.fExpectedVersionString, 19);
            for (n = 0; n < XML11_VERSION.length && this.fExpectedVersionString[15 + n] == XML11_VERSION[n]; ++n) {
            }
            return n == XML11_VERSION.length ? (short)2 : 1;
        }
        catch (MalformedByteSequenceException malformedByteSequenceException) {
            this.fErrorReporter.reportError(malformedByteSequenceException.getDomain(), malformedByteSequenceException.getKey(), malformedByteSequenceException.getArguments(), (short)2, malformedByteSequenceException);
            return -1;
        }
        catch (CharConversionException charConversionException) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "CharConversionFailure", null, (short)2, charConversionException);
            return -1;
        }
        catch (EOFException eOFException) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "PrematureEOF", null, (short)2);
            return -1;
        }
    }

    private void fixupCurrentEntity(XMLEntityManager xMLEntityManager, char[] cArray, int n) {
        XMLEntityManager.ScannedEntity scannedEntity = xMLEntityManager.getCurrentEntity();
        if (scannedEntity.count - scannedEntity.position + n > scannedEntity.ch.length) {
            char[] cArray2 = scannedEntity.ch;
            scannedEntity.ch = new char[n + scannedEntity.count - scannedEntity.position + 1];
            System.arraycopy(cArray2, 0, scannedEntity.ch, 0, cArray2.length);
        }
        if (scannedEntity.position < n) {
            System.arraycopy(scannedEntity.ch, scannedEntity.position, scannedEntity.ch, n, scannedEntity.count - scannedEntity.position);
            scannedEntity.count += n - scannedEntity.position;
        } else {
            for (int i = n; i < scannedEntity.position; ++i) {
                scannedEntity.ch[i] = 32;
            }
        }
        System.arraycopy(cArray, 0, scannedEntity.ch, 0, n);
        scannedEntity.position = 0;
        scannedEntity.baseCharOffset = 0;
        scannedEntity.startPosition = 0;
        scannedEntity.lineNumber = 1;
        scannedEntity.columnNumber = 1;
    }
}

