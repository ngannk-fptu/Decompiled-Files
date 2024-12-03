/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serialize.ElementState
 *  org.apache.xml.serialize.HTMLSerializer
 *  org.apache.xml.serialize.HTMLdtd
 *  org.apache.xml.serialize.OutputFormat
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.owasp.validator.html.scan;

import java.io.IOException;
import java.io.Writer;
import org.apache.xml.serialize.ElementState;
import org.apache.xml.serialize.HTMLSerializer;
import org.apache.xml.serialize.HTMLdtd;
import org.apache.xml.serialize.OutputFormat;
import org.owasp.validator.html.InternalPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ASHTMLSerializer
extends HTMLSerializer {
    private static final Logger logger = LoggerFactory.getLogger(ASHTMLSerializer.class);
    private boolean encodeAllPossibleEntities;

    public ASHTMLSerializer(Writer w, OutputFormat format, InternalPolicy policy) {
        super(w, format);
        this.encodeAllPossibleEntities = policy.isEntityEncodeIntlCharacters();
    }

    protected String getEntityRef(int charToPrint) {
        if (this.encodeAllPossibleEntities || "<>\"'&".indexOf(charToPrint) != -1) {
            return super.getEntityRef(charToPrint);
        }
        return null;
    }

    public void endElementIO(String namespaceURI, String localName, String rawName) throws IOException {
        this._printer.unindent();
        ElementState state = this.getElementState();
        if (state.empty) {
            this._printer.printText('>');
        }
        if (rawName == null || !HTMLdtd.isOnlyOpening((String)rawName)) {
            if (this._indenting && !state.preserveSpace && state.afterElement) {
                this._printer.breakLine();
            }
            if (state.inCData) {
                this._printer.printText("]]>");
            }
            this._printer.printText("</");
            this._printer.printText(state.rawName);
            this._printer.printText('>');
        }
        state = this.leaveElementState();
        if (rawName == null || !rawName.equalsIgnoreCase("A") && !rawName.equalsIgnoreCase("TD")) {
            state.afterElement = true;
        }
        state.empty = false;
        if (this.isDocumentState()) {
            this._printer.flush();
        }
    }

    protected String escapeURI(String uri) {
        try {
            this.printEscaped(uri);
        }
        catch (IOException e) {
            logger.error("URI escaping failed for value: " + uri);
        }
        return "";
    }
}

