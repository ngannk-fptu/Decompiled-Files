/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serialize.ElementState
 *  org.apache.xml.serialize.OutputFormat
 *  org.apache.xml.serialize.XHTMLSerializer
 */
package org.owasp.validator.html.scan;

import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import org.apache.xml.serialize.ElementState;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XHTMLSerializer;
import org.owasp.validator.html.InternalPolicy;
import org.owasp.validator.html.TagMatcher;

@Deprecated
public class ASXHTMLSerializer
extends XHTMLSerializer {
    private boolean encodeAllPossibleEntities;
    private final TagMatcher allowedEmptyTags;
    private final TagMatcher requireClosingTags;

    public ASXHTMLSerializer(Writer w, OutputFormat format, InternalPolicy policy) {
        super(w, format);
        this.allowedEmptyTags = policy.getAllowedEmptyTags();
        this.requireClosingTags = policy.getRequiresClosingTags();
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
        if (state.empty && this.isAllowedEmptyTag(rawName) && !this.requiresClosingTag(rawName)) {
            this._printer.printText(" />");
        } else {
            if (state.empty) {
                this._printer.printText('>');
            }
            if (state.inCData) {
                this._printer.printText("]]>");
            }
            this._printer.printText("</");
            this._printer.printText(state.rawName.toLowerCase(Locale.ENGLISH));
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

    private boolean requiresClosingTag(String tagName) {
        return this.requireClosingTags.matches(tagName);
    }

    private boolean isAllowedEmptyTag(String tagName) {
        return "head".equals(tagName) || this.allowedEmptyTags.matches(tagName);
    }
}

