/*
 * Decompiled with CFR 0.152.
 */
package aQute.lib.utf8properties;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

class ThreadLocalCharsetDecoder
extends ThreadLocal<CharsetDecoder> {
    private final Charset charset;

    ThreadLocalCharsetDecoder(Charset charset) {
        this.charset = charset;
    }

    @Override
    protected CharsetDecoder initialValue() {
        return this.charset.newDecoder();
    }
}

