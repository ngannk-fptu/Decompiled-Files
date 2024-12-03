/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Writer;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class AbderaResult
extends StreamResult
implements Result {
    private final Abdera abdera;
    private PipedOutputStream pipeout;
    private PipedInputStream pipein;
    private Document<?> doc;

    public AbderaResult() {
        this(new Abdera());
    }

    public AbderaResult(Abdera abdera) {
        this.abdera = abdera;
    }

    public <T extends Element> Document<T> getDocument() {
        if (this.doc == null) {
            if (this.pipein == null) {
                return null;
            }
            this.doc = this.abdera.getParser().parse(this.pipein);
        }
        return this.doc;
    }

    @Override
    public OutputStream getOutputStream() {
        if (this.pipein == null && this.pipeout == null) {
            try {
                this.pipeout = new PipedOutputStream();
                this.pipein = new PipedInputStream(this.pipeout);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return this.pipeout;
    }

    @Override
    public Writer getWriter() {
        return new OutputStreamWriter(this.getOutputStream());
    }

    @Override
    public void setOutputStream(OutputStream out) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setWriter(Writer out) {
        throw new UnsupportedOperationException();
    }
}

