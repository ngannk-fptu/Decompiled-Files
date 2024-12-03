/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.filters.BaseParamFilterReader;
import org.apache.tools.ant.filters.ChainableReader;
import org.apache.tools.ant.util.UnicodeUtil;

public class EscapeUnicode
extends BaseParamFilterReader
implements ChainableReader {
    private StringBuffer unicodeBuf = new StringBuffer();

    public EscapeUnicode() {
    }

    public EscapeUnicode(Reader in) {
        super(in);
    }

    @Override
    public final int read() throws IOException {
        if (!this.getInitialized()) {
            this.initialize();
            this.setInitialized(true);
        }
        int ch = -1;
        if (this.unicodeBuf.length() > 0) {
            ch = this.unicodeBuf.charAt(0);
            this.unicodeBuf.deleteCharAt(0);
        } else {
            char achar;
            ch = this.in.read();
            if (ch != -1 && (achar = (char)ch) >= '\u0080') {
                this.unicodeBuf = UnicodeUtil.EscapeUnicode(achar);
                ch = 92;
            }
        }
        return ch;
    }

    @Override
    public final Reader chain(Reader rdr) {
        EscapeUnicode newFilter = new EscapeUnicode(rdr);
        newFilter.setInitialized(true);
        return newFilter;
    }

    private void initialize() {
    }
}

