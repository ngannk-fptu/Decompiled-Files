/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.FormatPostingsDocsConsumer;
import com.atlassian.lucene36.util.ArrayUtil;
import java.io.IOException;

abstract class FormatPostingsTermsConsumer {
    char[] termBuffer;

    FormatPostingsTermsConsumer() {
    }

    abstract FormatPostingsDocsConsumer addTerm(char[] var1, int var2) throws IOException;

    FormatPostingsDocsConsumer addTerm(String text) throws IOException {
        int len = text.length();
        if (this.termBuffer == null || this.termBuffer.length < 1 + len) {
            this.termBuffer = new char[ArrayUtil.oversize(1 + len, 2)];
        }
        text.getChars(0, len, this.termBuffer, 0);
        this.termBuffer[len] = 65535;
        return this.addTerm(this.termBuffer, 0);
    }

    abstract void finish() throws IOException;
}

