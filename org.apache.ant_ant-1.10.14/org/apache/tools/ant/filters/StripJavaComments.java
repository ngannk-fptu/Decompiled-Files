/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.filters.BaseFilterReader;
import org.apache.tools.ant.filters.ChainableReader;

public final class StripJavaComments
extends BaseFilterReader
implements ChainableReader {
    private int readAheadCh = -1;
    private boolean inString = false;
    private boolean quoted = false;

    public StripJavaComments() {
    }

    public StripJavaComments(Reader in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        int ch = -1;
        if (this.readAheadCh != -1) {
            ch = this.readAheadCh;
            this.readAheadCh = -1;
        } else {
            ch = this.in.read();
            if (ch == 34 && !this.quoted) {
                this.inString = !this.inString;
                this.quoted = false;
            } else if (ch == 92) {
                this.quoted = !this.quoted;
            } else {
                this.quoted = false;
                if (!this.inString && ch == 47) {
                    ch = this.in.read();
                    if (ch == 47) {
                        while (ch != 10 && ch != -1 && ch != 13) {
                            ch = this.in.read();
                        }
                    } else if (ch == 42) {
                        while (ch != -1) {
                            ch = this.in.read();
                            if (ch != 42) continue;
                            ch = this.in.read();
                            while (ch == 42) {
                                ch = this.in.read();
                            }
                            if (ch != 47) continue;
                            ch = this.read();
                            break;
                        }
                    } else {
                        this.readAheadCh = ch;
                        ch = 47;
                    }
                }
            }
        }
        return ch;
    }

    @Override
    public Reader chain(Reader rdr) {
        return new StripJavaComments(rdr);
    }
}

