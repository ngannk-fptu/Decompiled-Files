/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.util.Tokenizer;

public class LineTokenizer
extends ProjectComponent
implements Tokenizer {
    private static final int NOT_A_CHAR = -2;
    private String lineEnd = "";
    private int pushed = -2;
    private boolean includeDelims = false;

    public void setIncludeDelims(boolean includeDelims) {
        this.includeDelims = includeDelims;
    }

    @Override
    public String getToken(Reader in) throws IOException {
        int ch;
        if (this.pushed == -2) {
            ch = in.read();
        } else {
            ch = this.pushed;
            this.pushed = -2;
        }
        if (ch == -1) {
            return null;
        }
        this.lineEnd = "";
        StringBuilder line = new StringBuilder();
        boolean state = false;
        while (ch != -1) {
            if (!state) {
                if (ch == 13) {
                    state = true;
                } else {
                    if (ch == 10) {
                        this.lineEnd = "\n";
                        break;
                    }
                    line.append((char)ch);
                }
            } else {
                state = false;
                if (ch == 10) {
                    this.lineEnd = "\r\n";
                    break;
                }
                this.pushed = ch;
                this.lineEnd = "\r";
                break;
            }
            ch = in.read();
        }
        if (ch == -1 && state) {
            this.lineEnd = "\r";
        }
        if (this.includeDelims) {
            line.append(this.lineEnd);
        }
        return line.toString();
    }

    @Override
    public String getPostToken() {
        return this.includeDelims ? "" : this.lineEnd;
    }
}

