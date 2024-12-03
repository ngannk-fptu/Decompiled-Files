/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.zip;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import org.apache.tools.zip.ZipEncoding;
import org.apache.tools.zip.ZipEncodingHelper;

class NioZipEncoding
implements ZipEncoding {
    private final Charset charset;

    public NioZipEncoding(Charset charset) {
        this.charset = charset;
    }

    @Override
    public boolean canEncode(String name) {
        CharsetEncoder enc = this.charset.newEncoder();
        enc.onMalformedInput(CodingErrorAction.REPORT);
        enc.onUnmappableCharacter(CodingErrorAction.REPORT);
        return enc.canEncode(name);
    }

    @Override
    public ByteBuffer encode(String name) {
        CharsetEncoder enc = this.charset.newEncoder();
        enc.onMalformedInput(CodingErrorAction.REPORT);
        enc.onUnmappableCharacter(CodingErrorAction.REPORT);
        CharBuffer cb = CharBuffer.wrap(name);
        ByteBuffer out = ByteBuffer.allocate(name.length() + (name.length() + 1) / 2);
        while (cb.remaining() > 0) {
            CoderResult res = enc.encode(cb, out, true);
            if (res.isUnmappable() || res.isMalformed()) {
                if (res.length() * 6 > out.remaining()) {
                    out = ZipEncodingHelper.growBuffer(out, out.position() + res.length() * 6);
                }
                for (int i = 0; i < res.length(); ++i) {
                    ZipEncodingHelper.appendSurrogate(out, cb.get());
                }
                continue;
            }
            if (res.isOverflow()) {
                out = ZipEncodingHelper.growBuffer(out, 0);
                continue;
            }
            if (!res.isUnderflow()) continue;
            enc.flush(out);
            break;
        }
        ZipEncodingHelper.prepareBufferForRead(out);
        return out;
    }

    @Override
    public String decode(byte[] data) throws IOException {
        return this.charset.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT).decode(ByteBuffer.wrap(data)).toString();
    }
}

