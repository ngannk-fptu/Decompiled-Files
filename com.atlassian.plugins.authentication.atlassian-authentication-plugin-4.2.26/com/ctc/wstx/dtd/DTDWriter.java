/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.dtd;

import com.ctc.wstx.exc.WstxIOException;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;

final class DTDWriter {
    final Writer mWriter;
    final boolean mIncludeComments;
    final boolean mIncludeConditionals;
    final boolean mIncludePEs;
    int mIsFlattening = 0;
    int mFlattenStart = 0;

    public DTDWriter(Writer out, boolean inclComments, boolean inclCond, boolean inclPEs) {
        this.mWriter = out;
        this.mIncludeComments = inclComments;
        this.mIncludeConditionals = inclCond;
        this.mIncludePEs = inclPEs;
        this.mIsFlattening = 1;
    }

    public boolean includeComments() {
        return this.mIncludeComments;
    }

    public boolean includeConditionals() {
        return this.mIncludeConditionals;
    }

    public boolean includeParamEntities() {
        return this.mIncludePEs;
    }

    public void disableOutput() {
        --this.mIsFlattening;
    }

    public void enableOutput(int newStart) {
        ++this.mIsFlattening;
        this.mFlattenStart = newStart;
    }

    public void setFlattenStart(int ptr) {
        this.mFlattenStart = ptr;
    }

    public int getFlattenStart() {
        return this.mFlattenStart;
    }

    public void flush(char[] buf, int upUntil) throws XMLStreamException {
        if (this.mFlattenStart < upUntil) {
            if (this.mIsFlattening > 0) {
                try {
                    this.mWriter.write(buf, this.mFlattenStart, upUntil - this.mFlattenStart);
                }
                catch (IOException ioe) {
                    throw new WstxIOException(ioe);
                }
            }
            this.mFlattenStart = upUntil;
        }
    }

    public void output(String output) throws XMLStreamException {
        if (this.mIsFlattening > 0) {
            try {
                this.mWriter.write(output);
            }
            catch (IOException ioe) {
                throw new WstxIOException(ioe);
            }
        }
    }

    public void output(char c) throws XMLStreamException {
        if (this.mIsFlattening > 0) {
            try {
                this.mWriter.write(c);
            }
            catch (IOException ioe) {
                throw new WstxIOException(ioe);
            }
        }
    }
}

