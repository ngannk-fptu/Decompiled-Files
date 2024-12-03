/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.runtime.output;

import org.apache.xalan.xsltc.runtime.output.OutputBuffer;

class StringOutputBuffer
implements OutputBuffer {
    private StringBuffer _buffer = new StringBuffer();

    @Override
    public String close() {
        return this._buffer.toString();
    }

    @Override
    public OutputBuffer append(String s) {
        this._buffer.append(s);
        return this;
    }

    @Override
    public OutputBuffer append(char[] s, int from, int to) {
        this._buffer.append(s, from, to);
        return this;
    }

    @Override
    public OutputBuffer append(char ch) {
        this._buffer.append(ch);
        return this;
    }
}

