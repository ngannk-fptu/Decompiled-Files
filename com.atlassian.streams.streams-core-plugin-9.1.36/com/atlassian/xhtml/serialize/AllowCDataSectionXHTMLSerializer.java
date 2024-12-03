/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.util.XMLChar
 *  org.apache.xml.serialize.ElementState
 *  org.apache.xml.serialize.OutputFormat
 */
package com.atlassian.xhtml.serialize;

import com.atlassian.xhtml.serialize.SurrogatePairPreservingXHTMLSerializer;
import java.io.IOException;
import java.io.Writer;
import org.apache.xerces.util.XMLChar;
import org.apache.xml.serialize.ElementState;
import org.apache.xml.serialize.OutputFormat;
import org.owasp.validator.html.InternalPolicy;
import org.xml.sax.SAXException;

public class AllowCDataSectionXHTMLSerializer
extends SurrogatePairPreservingXHTMLSerializer {
    public AllowCDataSectionXHTMLSerializer(Writer writer, OutputFormat format, InternalPolicy policy) {
        super(writer, format, policy);
    }

    public void characters(char[] chars, int start, int length) throws SAXException {
        try {
            ElementState state = this.content();
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
                    if (index <= end - 2 && ch == ']' && chars[index + 1] == ']' && index + 2 == end) {
                        this._printer.printText("]]]]><![CDATA[");
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
}

