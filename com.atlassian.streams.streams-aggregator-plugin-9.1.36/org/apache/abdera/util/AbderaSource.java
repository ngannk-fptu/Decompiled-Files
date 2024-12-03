/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.apache.abdera.model.Base;

public final class AbderaSource
extends StreamSource
implements Source {
    private final Base base;

    public AbderaSource(Base base) {
        this.base = base;
    }

    public InputStream getInputStream() {
        try {
            PipedOutputStream pipeout = new PipedOutputStream();
            PipedInputStream pipein = new PipedInputStream(pipeout);
            this.base.writeTo(pipeout);
            pipeout.flush();
            pipeout.close();
            return pipein;
        }
        catch (IOException e) {
            return null;
        }
    }

    public Reader getReader() {
        return new InputStreamReader(this.getInputStream());
    }

    public void setInputStream(InputStream in) {
        throw new UnsupportedOperationException();
    }

    public void setReader(Reader reader) {
        throw new UnsupportedOperationException();
    }
}

