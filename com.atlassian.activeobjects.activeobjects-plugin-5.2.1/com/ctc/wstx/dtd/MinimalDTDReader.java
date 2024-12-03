/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.io.WstxInputSource;
import com.ctc.wstx.sr.StreamScanner;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

public class MinimalDTDReader
extends StreamScanner {
    final boolean mIsExternal;

    private MinimalDTDReader(WstxInputSource input, ReaderConfig cfg) {
        this(input, cfg, false);
    }

    protected MinimalDTDReader(WstxInputSource input, ReaderConfig cfg, boolean isExt) {
        super(input, cfg, cfg.getDtdResolver());
        this.mIsExternal = isExt;
        this.mCfgReplaceEntities = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void skipInternalSubset(WstxInputData srcData, WstxInputSource input, ReaderConfig cfg) throws XMLStreamException {
        MinimalDTDReader r = new MinimalDTDReader(input, cfg);
        r.copyBufferStateFrom(srcData);
        try {
            r.skipInternalSubset();
        }
        finally {
            srcData.copyBufferStateFrom(r);
        }
    }

    public final Location getLocation() {
        return this.getStartLocation();
    }

    protected EntityDecl findEntity(String id, Object arg) {
        this.throwIllegalCall();
        return null;
    }

    protected void handleUndeclaredEntity(String id) throws XMLStreamException {
    }

    protected void handleIncompleteEntityProblem(WstxInputSource closing) throws XMLStreamException {
    }

    protected char handleExpandedSurrogate(char first, char second) {
        return first;
    }

    public EntityDecl findEntity(String entName) {
        return null;
    }

    protected void skipInternalSubset() throws XMLStreamException {
        while (true) {
            int i;
            if ((i = this.getNextAfterWS()) < 0) {
                this.throwUnexpectedEOF(" in internal DTD subset");
            }
            if (i == 37) {
                this.skipPE();
                continue;
            }
            if (i == 60) {
                char c = this.getNextSkippingPEs();
                if (c == '?') {
                    this.skipPI();
                    continue;
                }
                if (c == '!') {
                    c = this.getNextSkippingPEs();
                    if (c == '[') continue;
                    if (c == '-') {
                        this.skipComment();
                        continue;
                    }
                    if (c >= 'A' && c <= 'Z') {
                        this.skipDeclaration(c);
                        continue;
                    }
                    this.skipDeclaration(c);
                    continue;
                }
                --this.mInputPtr;
                continue;
            }
            if (i == 93) {
                if (this.mInput == this.mRootInput) break;
                this.throwParseError("Encountered int. subset end marker ']]>' in an expanded entity; has to be at main level.");
                break;
            }
            this.throwUnexpectedChar(i, " in internal DTD subset; expected a '<' to start a directive, or \"]>\" to end internal subset.");
        }
    }

    protected char dtdNextFromCurr() throws XMLStreamException {
        return this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextCharFromCurrent(this.getErrorMsg());
    }

    protected char dtdNextChar() throws XMLStreamException {
        return this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextChar(this.getErrorMsg());
    }

    protected char getNextSkippingPEs() throws XMLStreamException {
        while (true) {
            char c;
            char c2 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.getNextChar(this.getErrorMsg());
            if (c != '%') {
                return c;
            }
            this.skipPE();
        }
    }

    private void skipPE() throws XMLStreamException {
        char c;
        this.skipDTDName();
        char c2 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr();
        if (c != ';') {
            --this.mInputPtr;
        }
    }

    protected void skipComment() throws XMLStreamException {
        char c;
        this.skipCommentContent();
        char c2 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr();
        if (c != '>') {
            this.throwParseError("String '--' not allowed in comment (missing '>'?)");
        }
    }

    protected void skipCommentContent() throws XMLStreamException {
        while (true) {
            char c;
            char c2 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr();
            if (c == '-') {
                c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr();
                if (c != '-') continue;
                return;
            }
            if (c != '\n' && c != '\r') continue;
            this.skipCRLF(c);
        }
    }

    protected void skipPI() throws XMLStreamException {
        while (true) {
            char c;
            char c2 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr();
            if (c == '?') {
                while ((c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr()) == '?') {
                }
                if (c == '>') break;
            }
            if (c != '\n' && c != '\r') continue;
            this.skipCRLF(c);
        }
    }

    private void skipDeclaration(char c) throws XMLStreamException {
        while (c != '>') {
            char c2 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr();
            if (c == '\n' || c == '\r') {
                this.skipCRLF(c);
                continue;
            }
            if (c != '\'' && c != '\"') continue;
            this.skipLiteral(c);
        }
    }

    private void skipLiteral(char quoteChar) throws XMLStreamException {
        while (true) {
            char c;
            char c2 = c = this.mInputPtr < this.mInputEnd ? this.mInputBuffer[this.mInputPtr++] : this.dtdNextFromCurr();
            if (c == '\n' || c == '\r') {
                this.skipCRLF(c);
                continue;
            }
            if (c == quoteChar) break;
        }
    }

    private void skipDTDName() throws XMLStreamException {
        this.skipFullName(this.getNextChar(this.getErrorMsg()));
    }

    protected String getErrorMsg() {
        return this.mIsExternal ? " in external DTD subset" : " in internal DTD subset";
    }

    protected void throwIllegalCall() throws Error {
        throw new IllegalStateException("Internal error: this method should never be called");
    }
}

