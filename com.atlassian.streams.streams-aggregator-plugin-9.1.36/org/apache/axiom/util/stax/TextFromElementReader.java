/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax;

import java.io.IOException;
import java.io.Reader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.util.stax.XMLStreamIOException;

class TextFromElementReader
extends Reader {
    private final XMLStreamReader stream;
    private final boolean allowNonTextChildren;
    private boolean endOfStream;
    private int skipDepth;
    private int sourceStart = -1;

    TextFromElementReader(XMLStreamReader stream, boolean allowNonTextChildren) {
        this.stream = stream;
        this.allowNonTextChildren = allowNonTextChildren;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (this.endOfStream) {
            return -1;
        }
        int read = 0;
        try {
            while (true) {
                if (this.sourceStart == -1) {
                    block8: while (true) {
                        int type = this.stream.next();
                        switch (type) {
                            case 4: 
                            case 12: {
                                if (this.skipDepth != 0) break;
                                this.sourceStart = 0;
                                break block8;
                            }
                            case 1: {
                                if (!this.allowNonTextChildren) throw new IOException("Unexpected START_ELEMENT event");
                                ++this.skipDepth;
                                break;
                            }
                            case 2: {
                                if (this.skipDepth == 0) {
                                    this.endOfStream = true;
                                    if (read == 0) {
                                        return -1;
                                    }
                                    int n = read;
                                    return n;
                                }
                                --this.skipDepth;
                            }
                        }
                    }
                }
                int c = this.stream.getTextCharacters(this.sourceStart, cbuf, off, len);
                this.sourceStart += c;
                off += c;
                read += c;
                if ((len -= c) <= 0) return read;
                this.sourceStart = -1;
            }
        }
        catch (XMLStreamException ex) {
            throw new XMLStreamIOException(ex);
        }
    }

    public void close() throws IOException {
    }
}

