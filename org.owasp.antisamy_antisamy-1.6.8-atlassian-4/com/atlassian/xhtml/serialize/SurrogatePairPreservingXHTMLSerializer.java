/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.util.XMLChar
 *  org.apache.xml.serialize.OutputFormat
 */
package com.atlassian.xhtml.serialize;

import java.io.IOException;
import java.io.Writer;
import org.apache.xerces.util.XMLChar;
import org.apache.xml.serialize.OutputFormat;
import org.owasp.validator.html.InternalPolicy;
import org.owasp.validator.html.scan.ASXHTMLSerializer;

public class SurrogatePairPreservingXHTMLSerializer
extends ASXHTMLSerializer {
    public SurrogatePairPreservingXHTMLSerializer(Writer writer, OutputFormat format, InternalPolicy policy) {
        super(writer, format, policy);
    }

    protected void surrogates(int high, int low, boolean inContent) throws IOException {
        if (XMLChar.isHighSurrogate((int)high)) {
            if (!XMLChar.isLowSurrogate((int)low)) {
                this.fatalError("The character '" + (char)low + "' is an invalid XML character");
            } else {
                int supplemental = XMLChar.supplemental((char)((char)high), (char)((char)low));
                if (!XMLChar.isValid((int)supplemental)) {
                    this.fatalError("The character '" + (char)supplemental + "' is an invalid XML character");
                } else {
                    char[] surrogatePair = new char[]{(char)high, (char)low};
                    this._printer.printText(surrogatePair, 0, 2);
                }
            }
        } else {
            this.fatalError("The character '" + (char)high + "' is an invalid XML character");
        }
    }
}

