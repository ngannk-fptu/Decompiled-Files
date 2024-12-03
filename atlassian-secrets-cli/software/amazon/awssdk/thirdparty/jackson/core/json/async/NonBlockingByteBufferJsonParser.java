/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.thirdparty.jackson.core.json.async;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import software.amazon.awssdk.thirdparty.jackson.core.async.ByteBufferFeeder;
import software.amazon.awssdk.thirdparty.jackson.core.async.NonBlockingInputFeeder;
import software.amazon.awssdk.thirdparty.jackson.core.io.IOContext;
import software.amazon.awssdk.thirdparty.jackson.core.json.async.NonBlockingUtf8JsonParserBase;
import software.amazon.awssdk.thirdparty.jackson.core.sym.ByteQuadsCanonicalizer;

public class NonBlockingByteBufferJsonParser
extends NonBlockingUtf8JsonParserBase
implements ByteBufferFeeder {
    private ByteBuffer _inputBuffer = ByteBuffer.wrap(NO_BYTES);

    public NonBlockingByteBufferJsonParser(IOContext ctxt, int parserFeatures, ByteQuadsCanonicalizer sym) {
        super(ctxt, parserFeatures, sym);
    }

    @Override
    public NonBlockingInputFeeder getNonBlockingInputFeeder() {
        return this;
    }

    @Override
    public void feedInput(ByteBuffer byteBuffer) throws IOException {
        if (this._inputPtr < this._inputEnd) {
            this._reportError("Still have %d undecoded bytes, should not call 'feedInput'", this._inputEnd - this._inputPtr);
        }
        int start = byteBuffer.position();
        int end = byteBuffer.limit();
        if (end < start) {
            this._reportError("Input end (%d) may not be before start (%d)", end, start);
        }
        if (this._endOfInput) {
            this._reportError("Already closed, can not feed more input");
        }
        this._currInputProcessed += (long)this._origBufferLen;
        this._currInputRowStart = start - (this._inputEnd - this._currInputRowStart);
        this._currBufferStart = start;
        this._inputBuffer = byteBuffer;
        this._inputPtr = start;
        this._inputEnd = end;
        this._origBufferLen = end - start;
    }

    @Override
    public int releaseBuffered(OutputStream out) throws IOException {
        int avail = this._inputEnd - this._inputPtr;
        if (avail > 0) {
            WritableByteChannel channel = Channels.newChannel(out);
            channel.write(this._inputBuffer);
        }
        return avail;
    }

    @Override
    protected byte getNextSignedByteFromBuffer() {
        return this._inputBuffer.get(this._inputPtr++);
    }

    @Override
    protected int getNextUnsignedByteFromBuffer() {
        return this._inputBuffer.get(this._inputPtr++) & 0xFF;
    }

    @Override
    protected byte getByteFromBuffer(int ptr) {
        return this._inputBuffer.get(ptr);
    }
}

