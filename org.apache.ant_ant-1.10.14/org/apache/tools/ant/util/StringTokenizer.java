/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.util.StringUtils;
import org.apache.tools.ant.util.Tokenizer;

public class StringTokenizer
extends ProjectComponent
implements Tokenizer {
    private static final int NOT_A_CHAR = -2;
    private String intraString = "";
    private int pushed = -2;
    private char[] delims = null;
    private boolean delimsAreTokens = false;
    private boolean suppressDelims = false;
    private boolean includeDelims = false;

    public void setDelims(String delims) {
        this.delims = StringUtils.resolveBackSlash(delims).toCharArray();
    }

    public void setDelimsAreTokens(boolean delimsAreTokens) {
        this.delimsAreTokens = delimsAreTokens;
    }

    public void setSuppressDelims(boolean suppressDelims) {
        this.suppressDelims = suppressDelims;
    }

    public void setIncludeDelims(boolean includeDelims) {
        this.includeDelims = includeDelims;
    }

    @Override
    public String getToken(Reader in) throws IOException {
        int ch = -1;
        if (this.pushed != -2) {
            ch = this.pushed;
            this.pushed = -2;
        } else {
            ch = in.read();
        }
        if (ch == -1) {
            return null;
        }
        boolean inToken = true;
        this.intraString = "";
        StringBuilder word = new StringBuilder();
        StringBuilder padding = new StringBuilder();
        while (ch != -1) {
            char c = (char)ch;
            boolean isDelim = this.isDelim(c);
            if (inToken) {
                if (isDelim) {
                    if (this.delimsAreTokens) {
                        if (word.length() > 0) {
                            this.pushed = ch;
                            break;
                        }
                        word.append(c);
                        break;
                    }
                    padding.append(c);
                    inToken = false;
                } else {
                    word.append(c);
                }
            } else if (isDelim) {
                padding.append(c);
            } else {
                this.pushed = ch;
                break;
            }
            ch = in.read();
        }
        this.intraString = padding.toString();
        if (this.includeDelims) {
            word.append(this.intraString);
        }
        return word.toString();
    }

    @Override
    public String getPostToken() {
        return this.suppressDelims || this.includeDelims ? "" : this.intraString;
    }

    private boolean isDelim(char ch) {
        if (this.delims == null) {
            return Character.isWhitespace(ch);
        }
        for (char delim : this.delims) {
            if (delim != ch) continue;
            return true;
        }
        return false;
    }
}

