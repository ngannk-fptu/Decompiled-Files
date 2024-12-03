/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl;

import java.io.IOException;
import org.apache.xerces.impl.XMLDTDScannerImpl;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XML11Char;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.util.XMLStringBuffer;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;

public class XML11DTDScannerImpl
extends XMLDTDScannerImpl {
    private final XMLStringBuffer fStringBuffer = new XMLStringBuffer();

    public XML11DTDScannerImpl() {
    }

    public XML11DTDScannerImpl(SymbolTable symbolTable, XMLErrorReporter xMLErrorReporter, XMLEntityManager xMLEntityManager) {
        super(symbolTable, xMLErrorReporter, xMLEntityManager);
    }

    @Override
    protected boolean scanPubidLiteral(XMLString xMLString) throws IOException, XNIException {
        int n = this.fEntityScanner.scanChar();
        if (n != 39 && n != 34) {
            this.reportFatalError("QuoteRequiredInPublicID", null);
            return false;
        }
        this.fStringBuffer.clear();
        boolean bl = true;
        boolean bl2 = true;
        while (true) {
            int n2;
            if ((n2 = this.fEntityScanner.scanChar()) == 32 || n2 == 10 || n2 == 13 || n2 == 133 || n2 == 8232) {
                if (bl) continue;
                this.fStringBuffer.append(' ');
                bl = true;
                continue;
            }
            if (n2 == n) {
                if (bl) {
                    --this.fStringBuffer.length;
                }
                break;
            }
            if (XMLChar.isPubid(n2)) {
                this.fStringBuffer.append((char)n2);
                bl = false;
                continue;
            }
            if (n2 == -1) {
                this.reportFatalError("PublicIDUnterminated", null);
                return false;
            }
            bl2 = false;
            this.reportFatalError("InvalidCharInPublicID", new Object[]{Integer.toHexString(n2)});
        }
        xMLString.setValues(this.fStringBuffer);
        return bl2;
    }

    @Override
    protected void normalizeWhitespace(XMLString xMLString) {
        int n = xMLString.offset + xMLString.length;
        for (int i = xMLString.offset; i < n; ++i) {
            char c = xMLString.ch[i];
            if (!XMLChar.isSpace(c)) continue;
            xMLString.ch[i] = 32;
        }
    }

    @Override
    protected void normalizeWhitespace(XMLString xMLString, int n) {
        int n2 = xMLString.offset + xMLString.length;
        for (int i = xMLString.offset + n; i < n2; ++i) {
            char c = xMLString.ch[i];
            if (!XMLChar.isSpace(c)) continue;
            xMLString.ch[i] = 32;
        }
    }

    @Override
    protected int isUnchangedByNormalization(XMLString xMLString) {
        int n = xMLString.offset + xMLString.length;
        for (int i = xMLString.offset; i < n; ++i) {
            char c = xMLString.ch[i];
            if (!XMLChar.isSpace(c)) continue;
            return i - xMLString.offset;
        }
        return -1;
    }

    @Override
    protected boolean isInvalid(int n) {
        return !XML11Char.isXML11Valid(n);
    }

    @Override
    protected boolean isInvalidLiteral(int n) {
        return !XML11Char.isXML11ValidLiteral(n);
    }

    @Override
    protected boolean isValidNameChar(int n) {
        return XML11Char.isXML11Name(n);
    }

    @Override
    protected boolean isValidNameStartChar(int n) {
        return XML11Char.isXML11NameStart(n);
    }

    @Override
    protected boolean isValidNCName(int n) {
        return XML11Char.isXML11NCName(n);
    }

    @Override
    protected boolean isValidNameStartHighSurrogate(int n) {
        return XML11Char.isXML11NameHighSurrogate(n);
    }

    @Override
    protected boolean versionSupported(String string) {
        return string.equals("1.1") || string.equals("1.0");
    }

    @Override
    protected String getVersionNotSupportedKey() {
        return "VersionNotSupported11";
    }
}

