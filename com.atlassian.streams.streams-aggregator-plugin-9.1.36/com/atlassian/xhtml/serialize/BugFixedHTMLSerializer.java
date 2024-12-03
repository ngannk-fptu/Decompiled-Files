/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.dom.DOMMessageFormatter
 *  org.apache.xerces.util.XMLChar
 *  org.apache.xml.serialize.ElementState
 *  org.apache.xml.serialize.HTMLSerializer
 *  org.apache.xml.serialize.OutputFormat
 */
package com.atlassian.xhtml.serialize;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.util.XMLChar;
import org.apache.xml.serialize.ElementState;
import org.apache.xml.serialize.HTMLSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.w3c.dom.DOMError;
import org.w3c.dom.ls.LSException;
import org.xml.sax.SAXException;

public class BugFixedHTMLSerializer
extends HTMLSerializer {
    static short WELLFORMED = (short)2;
    static short SPLITCDATA = (short)16;

    protected BugFixedHTMLSerializer(boolean xhtml, OutputFormat format) {
        super(xhtml, format);
    }

    public BugFixedHTMLSerializer() {
    }

    public BugFixedHTMLSerializer(OutputFormat format) {
        super(format);
    }

    public BugFixedHTMLSerializer(Writer writer, OutputFormat format) {
        super(writer, format);
    }

    public BugFixedHTMLSerializer(OutputStream output, OutputFormat format) {
        super(output, format);
    }

    protected void printEscaped(int ch) throws IOException {
        String charRef = this.getEntityRef(ch);
        if (charRef != null) {
            this._printer.printText('&');
            this._printer.printText(charRef);
            this._printer.printText(';');
        } else if (ch >= 32 && this._encodingInfo.isPrintable((char)ch) && ch != 127 || ch == 10 || ch == 13 || ch == 9) {
            if (ch < 65536) {
                this._printer.printText((char)ch);
            } else {
                this._printer.printText((char)((ch - 65536 >> 10) + 55296));
                this._printer.printText((char)((ch - 65536 & 0x3FF) + 56320));
            }
        } else {
            this.myPrintHex(ch);
        }
    }

    public void characters(char[] chars, int start, int length) throws SAXException {
        try {
            ElementState state = this.content();
            state.doCData = false;
            if (state.inCData || state.doCData) {
                if (!state.inCData) {
                    this._printer.printText("<![CDATA[");
                    state.inCData = true;
                }
                int saveIndent = this._printer.getNextIndent();
                this._printer.setNextIndent(0);
                int end = start + length;
                for (int index = start; index < end; ++index) {
                    char ch = chars[index];
                    if (ch == ']' && index + 2 < end && chars[index + 1] == ']' && chars[index + 2] == '>') {
                        this._printer.printText("]]]]><![CDATA[>");
                        index += 2;
                        continue;
                    }
                    if (!XMLChar.isValid((int)ch)) {
                        if (++index < end) {
                            this.surrogates(ch, chars[index], true);
                            continue;
                        }
                        this.fatalError("The character '" + ch + "' is an invalid XML character");
                        continue;
                    }
                    if (ch >= ' ' && this._encodingInfo.isPrintable(ch) && ch != '\u007f' || ch == '\n' || ch == '\r' || ch == '\t') {
                        this._printer.printText(ch);
                        continue;
                    }
                    this._printer.printText("]]>&#x");
                    this._printer.printText(Integer.toHexString(ch));
                    this._printer.printText(";<![CDATA[");
                }
                this._printer.setNextIndent(saveIndent);
            } else if (state.preserveSpace) {
                int saveIndent = this._printer.getNextIndent();
                this._printer.setNextIndent(0);
                this.printText(chars, start, length, true, state.unescaped);
                this._printer.setNextIndent(saveIndent);
            } else {
                this.printText(chars, start, length, false, state.unescaped);
            }
        }
        catch (IOException except) {
            throw new SAXException(except);
        }
    }

    protected void printCDATAText(String text) throws IOException {
        int length = text.length();
        for (int index = 0; index < length; ++index) {
            char ch = text.charAt(index);
            if (ch == ']' && index + 2 < length && text.charAt(index + 1) == ']' && text.charAt(index + 2) == '>') {
                if (this.fDOMErrorHandler != null) {
                    String msg;
                    if ((this.features & SPLITCDATA) == 0) {
                        msg = DOMMessageFormatter.formatMessage((String)"http://apache.org/xml/serializer", (String)"EndingCDATA", null);
                        if ((this.features & WELLFORMED) != 0) {
                            this.modifyDOMError(msg, (short)3, "wf-invalid-character", this.fCurrentNode);
                            this.fDOMErrorHandler.handleError((DOMError)this.fDOMError);
                            throw new LSException(82, msg);
                        }
                        this.modifyDOMError(msg, (short)2, "cdata-section-not-splitted", this.fCurrentNode);
                        if (!this.fDOMErrorHandler.handleError((DOMError)this.fDOMError)) {
                            throw new LSException(82, msg);
                        }
                    } else {
                        msg = DOMMessageFormatter.formatMessage((String)"http://apache.org/xml/serializer", (String)"SplittingCDATA", null);
                        this.modifyDOMError(msg, (short)1, null, this.fCurrentNode);
                        this.fDOMErrorHandler.handleError((DOMError)this.fDOMError);
                    }
                }
                this._printer.printText("]]]]><![CDATA[>");
                index += 2;
                continue;
            }
            if (!XMLChar.isValid((int)ch)) {
                if (++index < length) {
                    this.surrogates(ch, text.charAt(index), true);
                    continue;
                }
                this.fatalError("The character '" + ch + "' is an invalid XML character");
                continue;
            }
            if (ch >= ' ' && this._encodingInfo.isPrintable(ch) && ch != '\u007f' || ch == '\n' || ch == '\r' || ch == '\t') {
                this._printer.printText(ch);
                continue;
            }
            this._printer.printText("]]>&#x");
            this._printer.printText(Integer.toHexString(ch));
            this._printer.printText(";<![CDATA[");
        }
    }

    protected void myPrintHex(int ch) throws IOException {
        this._printer.printText("&#x");
        this._printer.printText(Integer.toHexString(ch));
        this._printer.printText(';');
    }
}

