/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serialize;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.util.NamespaceSupport;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XML11Char;
import org.apache.xerces.util.XMLChar;
import org.apache.xml.serialize.ElementState;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.xml.sax.SAXException;

public class XML11Serializer
extends XMLSerializer {
    protected static final boolean DEBUG = false;
    protected NamespaceSupport fNSBinder;
    protected NamespaceSupport fLocalNSBinder;
    protected SymbolTable fSymbolTable;
    protected boolean fDOML1 = false;
    protected int fNamespaceCounter = 1;
    protected static final String PREFIX = "NS";
    protected boolean fNamespaces = false;

    public XML11Serializer() {
        this._format.setVersion("1.1");
    }

    public XML11Serializer(OutputFormat outputFormat) {
        super(outputFormat);
        this._format.setVersion("1.1");
    }

    public XML11Serializer(Writer writer, OutputFormat outputFormat) {
        super(writer, outputFormat);
        this._format.setVersion("1.1");
    }

    public XML11Serializer(OutputStream outputStream, OutputFormat outputFormat) {
        super(outputStream, outputFormat != null ? outputFormat : new OutputFormat("xml", null, false));
        this._format.setVersion("1.1");
    }

    @Override
    public void characters(char[] cArray, int n, int n2) throws SAXException {
        try {
            ElementState elementState = this.content();
            if (elementState.inCData || elementState.doCData) {
                if (!elementState.inCData) {
                    this._printer.printText("<![CDATA[");
                    elementState.inCData = true;
                }
                int n3 = this._printer.getNextIndent();
                this._printer.setNextIndent(0);
                int n4 = n + n2;
                for (int i = n; i < n4; ++i) {
                    char c = cArray[i];
                    if (c == ']' && i + 2 < n4 && cArray[i + 1] == ']' && cArray[i + 2] == '>') {
                        this._printer.printText("]]]]><![CDATA[>");
                        i += 2;
                        continue;
                    }
                    if (!XML11Char.isXML11Valid(c)) {
                        if (++i < n4) {
                            this.surrogates(c, cArray[i], true);
                            continue;
                        }
                        this.fatalError("The character '" + c + "' is an invalid XML character");
                        continue;
                    }
                    if (this._encodingInfo.isPrintable(c) && XML11Char.isXML11ValidLiteral(c)) {
                        this._printer.printText(c);
                        continue;
                    }
                    this._printer.printText("]]>&#x");
                    this._printer.printText(Integer.toHexString(c));
                    this._printer.printText(";<![CDATA[");
                }
                this._printer.setNextIndent(n3);
            } else if (elementState.preserveSpace) {
                int n5 = this._printer.getNextIndent();
                this._printer.setNextIndent(0);
                this.printText(cArray, n, n2, true, elementState.unescaped);
                this._printer.setNextIndent(n5);
            } else {
                this.printText(cArray, n, n2, false, elementState.unescaped);
            }
        }
        catch (IOException iOException) {
            throw new SAXException(iOException);
        }
    }

    @Override
    protected void printEscaped(String string) throws IOException {
        int n = string.length();
        for (int i = 0; i < n; ++i) {
            char c = string.charAt(i);
            if (!XML11Char.isXML11Valid(c)) {
                if (++i < n) {
                    this.surrogates(c, string.charAt(i), false);
                    continue;
                }
                this.fatalError("The character '" + (char)c + "' is an invalid XML character");
                continue;
            }
            if (c == '\n' || c == '\r' || c == '\t' || c == '\u0085' || c == '\u2028') {
                this.printHex(c);
                continue;
            }
            if (c == '<') {
                this._printer.printText("&lt;");
                continue;
            }
            if (c == '&') {
                this._printer.printText("&amp;");
                continue;
            }
            if (c == '\"') {
                this._printer.printText("&quot;");
                continue;
            }
            if (c >= ' ' && this._encodingInfo.isPrintable(c)) {
                this._printer.printText(c);
                continue;
            }
            this.printHex(c);
        }
    }

    @Override
    protected final void printCDATAText(String string) throws IOException {
        int n = string.length();
        for (int i = 0; i < n; ++i) {
            char c = string.charAt(i);
            if (c == ']' && i + 2 < n && string.charAt(i + 1) == ']' && string.charAt(i + 2) == '>') {
                if (this.fDOMErrorHandler != null) {
                    String string2;
                    if ((this.features & 0x10) == 0 && (this.features & 2) == 0) {
                        string2 = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "EndingCDATA", null);
                        this.modifyDOMError(string2, (short)3, null, this.fCurrentNode);
                        boolean bl = this.fDOMErrorHandler.handleError(this.fDOMError);
                        if (!bl) {
                            throw new IOException();
                        }
                    } else {
                        string2 = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "SplittingCDATA", null);
                        this.modifyDOMError(string2, (short)1, null, this.fCurrentNode);
                        this.fDOMErrorHandler.handleError(this.fDOMError);
                    }
                }
                this._printer.printText("]]]]><![CDATA[>");
                i += 2;
                continue;
            }
            if (!XML11Char.isXML11Valid(c)) {
                if (++i < n) {
                    this.surrogates(c, string.charAt(i), true);
                    continue;
                }
                this.fatalError("The character '" + c + "' is an invalid XML character");
                continue;
            }
            if (this._encodingInfo.isPrintable(c) && XML11Char.isXML11ValidLiteral(c)) {
                this._printer.printText(c);
                continue;
            }
            this._printer.printText("]]>&#x");
            this._printer.printText(Integer.toHexString(c));
            this._printer.printText(";<![CDATA[");
        }
    }

    @Override
    protected final void printXMLChar(int n) throws IOException {
        if (n == 13 || n == 133 || n == 8232) {
            this.printHex(n);
        } else if (n == 60) {
            this._printer.printText("&lt;");
        } else if (n == 38) {
            this._printer.printText("&amp;");
        } else if (n == 62) {
            this._printer.printText("&gt;");
        } else if (this._encodingInfo.isPrintable((char)n) && XML11Char.isXML11ValidLiteral(n)) {
            this._printer.printText((char)n);
        } else {
            this.printHex(n);
        }
    }

    @Override
    protected final void surrogates(int n, int n2, boolean bl) throws IOException {
        if (XMLChar.isHighSurrogate(n)) {
            if (!XMLChar.isLowSurrogate(n2)) {
                this.fatalError("The character '" + (char)n2 + "' is an invalid XML character");
            } else {
                int n3 = XMLChar.supplemental((char)n, (char)n2);
                if (!XML11Char.isXML11Valid(n3)) {
                    this.fatalError("The character '" + (char)n3 + "' is an invalid XML character");
                } else if (bl && this.content().inCData) {
                    this._printer.printText("]]>&#x");
                    this._printer.printText(Integer.toHexString(n3));
                    this._printer.printText(";<![CDATA[");
                } else {
                    this.printHex(n3);
                }
            }
        } else {
            this.fatalError("The character '" + (char)n + "' is an invalid XML character");
        }
    }

    @Override
    protected void printText(String string, boolean bl, boolean bl2) throws IOException {
        int n = string.length();
        if (bl) {
            for (int i = 0; i < n; ++i) {
                char c = string.charAt(i);
                if (!XML11Char.isXML11Valid(c)) {
                    if (++i < n) {
                        this.surrogates(c, string.charAt(i), true);
                        continue;
                    }
                    this.fatalError("The character '" + c + "' is an invalid XML character");
                    continue;
                }
                if (bl2 && XML11Char.isXML11ValidLiteral(c)) {
                    this._printer.printText(c);
                    continue;
                }
                this.printXMLChar(c);
            }
        } else {
            for (int i = 0; i < n; ++i) {
                char c = string.charAt(i);
                if (!XML11Char.isXML11Valid(c)) {
                    if (++i < n) {
                        this.surrogates(c, string.charAt(i), true);
                        continue;
                    }
                    this.fatalError("The character '" + c + "' is an invalid XML character");
                    continue;
                }
                if (bl2 && XML11Char.isXML11ValidLiteral(c)) {
                    this._printer.printText(c);
                    continue;
                }
                this.printXMLChar(c);
            }
        }
    }

    @Override
    protected void printText(char[] cArray, int n, int n2, boolean bl, boolean bl2) throws IOException {
        if (bl) {
            while (n2-- > 0) {
                char c;
                if (!XML11Char.isXML11Valid(c = cArray[n++])) {
                    if (n2-- > 0) {
                        this.surrogates(c, cArray[n++], true);
                        continue;
                    }
                    this.fatalError("The character '" + c + "' is an invalid XML character");
                    continue;
                }
                if (bl2 && XML11Char.isXML11ValidLiteral(c)) {
                    this._printer.printText(c);
                    continue;
                }
                this.printXMLChar(c);
            }
        } else {
            while (n2-- > 0) {
                char c;
                if (!XML11Char.isXML11Valid(c = cArray[n++])) {
                    if (n2-- > 0) {
                        this.surrogates(c, cArray[n++], true);
                        continue;
                    }
                    this.fatalError("The character '" + c + "' is an invalid XML character");
                    continue;
                }
                if (bl2 && XML11Char.isXML11ValidLiteral(c)) {
                    this._printer.printText(c);
                    continue;
                }
                this.printXMLChar(c);
            }
        }
    }

    @Override
    public boolean reset() {
        super.reset();
        return true;
    }
}

