/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl;

import java.io.IOException;
import org.apache.xerces.impl.XMLDocumentScannerImpl;
import org.apache.xerces.util.XML11Char;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.util.XMLStringBuffer;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;

public class XML11DocumentScannerImpl
extends XMLDocumentScannerImpl {
    private final XMLString fString = new XMLString();
    private final XMLStringBuffer fStringBuffer = new XMLStringBuffer();
    private final XMLStringBuffer fStringBuffer2 = new XMLStringBuffer();
    private final XMLStringBuffer fStringBuffer3 = new XMLStringBuffer();

    @Override
    protected int scanContent() throws IOException, XNIException {
        XMLString xMLString = this.fString;
        int n = this.fEntityScanner.scanContent(xMLString);
        if (n == 13 || n == 133 || n == 8232) {
            this.fEntityScanner.scanChar();
            this.fStringBuffer.clear();
            this.fStringBuffer.append(this.fString);
            this.fStringBuffer.append((char)n);
            xMLString = this.fStringBuffer;
            n = -1;
        }
        if (this.fDocumentHandler != null && xMLString.length > 0) {
            this.fDocumentHandler.characters(xMLString, null);
        }
        if (n == 93 && this.fString.length == 0) {
            this.fStringBuffer.clear();
            this.fStringBuffer.append((char)this.fEntityScanner.scanChar());
            this.fInScanContent = true;
            if (this.fEntityScanner.skipChar(93)) {
                this.fStringBuffer.append(']');
                while (this.fEntityScanner.skipChar(93)) {
                    this.fStringBuffer.append(']');
                }
                if (this.fEntityScanner.skipChar(62)) {
                    this.reportFatalError("CDEndInContent", null);
                }
            }
            if (this.fDocumentHandler != null && this.fStringBuffer.length != 0) {
                this.fDocumentHandler.characters(this.fStringBuffer, null);
            }
            this.fInScanContent = false;
            n = -1;
        }
        return n;
    }

    /*
     * Enabled aggressive block sorting
     */
    @Override
    protected boolean scanAttributeValue(XMLString xMLString, XMLString xMLString2, String string, boolean bl, String string2) throws IOException, XNIException {
        int n;
        block28: {
            n = this.fEntityScanner.peekChar();
            if (n != 39 && n != 34) {
                this.reportFatalError("OpenQuoteExpected", new Object[]{string2, string});
            }
            this.fEntityScanner.scanChar();
            int n2 = this.fEntityDepth;
            int n3 = this.fEntityScanner.scanLiteral(n, xMLString);
            int n4 = 0;
            if (n3 == n && (n4 = this.isUnchangedByNormalization(xMLString)) == -1) {
                xMLString2.setValues(xMLString);
                int n5 = this.fEntityScanner.scanChar();
                if (n5 != n) {
                    this.reportFatalError("CloseQuoteExpected", new Object[]{string2, string});
                }
                return true;
            }
            this.fStringBuffer2.clear();
            this.fStringBuffer2.append(xMLString);
            this.normalizeWhitespace(xMLString, n4);
            if (n3 == n) break block28;
            this.fScanningAttribute = true;
            this.fStringBuffer.clear();
            do {
                block31: {
                    block40: {
                        block39: {
                            block38: {
                                block37: {
                                    block29: {
                                        String string3;
                                        block36: {
                                            block35: {
                                                block34: {
                                                    block33: {
                                                        block32: {
                                                            block30: {
                                                                int n6;
                                                                this.fStringBuffer.append(xMLString);
                                                                if (n3 != 38) break block29;
                                                                this.fEntityScanner.skipChar(38);
                                                                if (n2 == this.fEntityDepth) {
                                                                    this.fStringBuffer2.append('&');
                                                                }
                                                                if (!this.fEntityScanner.skipChar(35)) break block30;
                                                                if (n2 == this.fEntityDepth) {
                                                                    this.fStringBuffer2.append('#');
                                                                }
                                                                if ((n6 = this.scanCharReferenceValue(this.fStringBuffer, this.fStringBuffer2)) == -1) {
                                                                    // empty if block
                                                                }
                                                                break block31;
                                                            }
                                                            string3 = this.fEntityScanner.scanName();
                                                            if (string3 == null) {
                                                                this.reportFatalError("NameRequiredInReference", null);
                                                            } else if (n2 == this.fEntityDepth) {
                                                                this.fStringBuffer2.append(string3);
                                                            }
                                                            if (!this.fEntityScanner.skipChar(59)) {
                                                                this.reportFatalError("SemicolonRequiredInReference", new Object[]{string3});
                                                            } else if (n2 == this.fEntityDepth) {
                                                                this.fStringBuffer2.append(';');
                                                            }
                                                            if (string3 != fAmpSymbol) break block32;
                                                            this.fStringBuffer.append('&');
                                                            break block31;
                                                        }
                                                        if (string3 != fAposSymbol) break block33;
                                                        this.fStringBuffer.append('\'');
                                                        break block31;
                                                    }
                                                    if (string3 != fLtSymbol) break block34;
                                                    this.fStringBuffer.append('<');
                                                    break block31;
                                                }
                                                if (string3 != fGtSymbol) break block35;
                                                this.fStringBuffer.append('>');
                                                break block31;
                                            }
                                            if (string3 != fQuotSymbol) break block36;
                                            this.fStringBuffer.append('\"');
                                            break block31;
                                        }
                                        if (this.fEntityManager.isExternalEntity(string3)) {
                                            this.reportFatalError("ReferenceToExternalEntity", new Object[]{string3});
                                            break block31;
                                        } else {
                                            if (!this.fEntityManager.isDeclaredEntity(string3)) {
                                                if (bl) {
                                                    if (this.fValidation) {
                                                        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "EntityNotDeclared", new Object[]{string3}, (short)1);
                                                    }
                                                } else {
                                                    this.reportFatalError("EntityNotDeclared", new Object[]{string3});
                                                }
                                            }
                                            this.fEntityManager.startEntity(string3, true);
                                        }
                                        break block31;
                                    }
                                    if (n3 != 60) break block37;
                                    this.reportFatalError("LessthanInAttValue", new Object[]{string2, string});
                                    this.fEntityScanner.scanChar();
                                    if (n2 == this.fEntityDepth) {
                                        this.fStringBuffer2.append((char)n3);
                                    }
                                    break block31;
                                }
                                if (n3 != 37 && n3 != 93) break block38;
                                this.fEntityScanner.scanChar();
                                this.fStringBuffer.append((char)n3);
                                if (n2 == this.fEntityDepth) {
                                    this.fStringBuffer2.append((char)n3);
                                }
                                break block31;
                            }
                            if (n3 != 10 && n3 != 13 && n3 != 133 && n3 != 8232) break block39;
                            this.fEntityScanner.scanChar();
                            this.fStringBuffer.append(' ');
                            if (n2 == this.fEntityDepth) {
                                this.fStringBuffer2.append('\n');
                            }
                            break block31;
                        }
                        if (n3 == -1 || !XMLChar.isHighSurrogate(n3)) break block40;
                        this.fStringBuffer3.clear();
                        if (this.scanSurrogates(this.fStringBuffer3)) {
                            this.fStringBuffer.append(this.fStringBuffer3);
                            if (n2 == this.fEntityDepth) {
                                this.fStringBuffer2.append(this.fStringBuffer3);
                            }
                        }
                        break block31;
                    }
                    if (n3 != -1 && this.isInvalidLiteral(n3)) {
                        this.reportFatalError("InvalidCharInAttValue", new Object[]{string2, string, Integer.toString(n3, 16)});
                        this.fEntityScanner.scanChar();
                        if (n2 == this.fEntityDepth) {
                            this.fStringBuffer2.append((char)n3);
                        }
                    }
                }
                n3 = this.fEntityScanner.scanLiteral(n, xMLString);
                if (n2 == this.fEntityDepth) {
                    this.fStringBuffer2.append(xMLString);
                }
                this.normalizeWhitespace(xMLString);
            } while (n3 != n || n2 != this.fEntityDepth);
            this.fStringBuffer.append(xMLString);
            xMLString.setValues(this.fStringBuffer);
            this.fScanningAttribute = false;
        }
        xMLString2.setValues(this.fStringBuffer2);
        int n7 = this.fEntityScanner.scanChar();
        if (n7 != n) {
            this.reportFatalError("CloseQuoteExpected", new Object[]{string2, string});
        }
        return xMLString2.equals(xMLString.ch, xMLString.offset, xMLString.length);
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
        return XML11Char.isXML11Invalid(n);
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

