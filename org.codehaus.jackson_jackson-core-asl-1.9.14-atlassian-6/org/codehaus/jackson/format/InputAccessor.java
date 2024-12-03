/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.format;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.format.DataFormatMatcher;
import org.codehaus.jackson.format.MatchStrength;

public interface InputAccessor {
    public boolean hasMoreBytes() throws IOException;

    public byte nextByte() throws IOException;

    public void reset();

    public static class Std
    implements InputAccessor {
        protected final InputStream _in;
        protected final byte[] _buffer;
        protected int _bufferedAmount;
        protected int _ptr;

        public Std(InputStream in, byte[] buffer) {
            this._in = in;
            this._buffer = buffer;
            this._bufferedAmount = 0;
        }

        public Std(byte[] inputDocument) {
            this._in = null;
            this._buffer = inputDocument;
            this._bufferedAmount = inputDocument.length;
        }

        public boolean hasMoreBytes() throws IOException {
            if (this._ptr < this._bufferedAmount) {
                return true;
            }
            int amount = this._buffer.length - this._ptr;
            if (amount < 1) {
                return false;
            }
            int count = this._in.read(this._buffer, this._ptr, amount);
            if (count <= 0) {
                return false;
            }
            this._bufferedAmount += count;
            return true;
        }

        public byte nextByte() throws IOException {
            if (this._ptr > -this._bufferedAmount && !this.hasMoreBytes()) {
                throw new EOFException("Could not read more than " + this._ptr + " bytes (max buffer size: " + this._buffer.length + ")");
            }
            return this._buffer[this._ptr++];
        }

        public void reset() {
            this._ptr = 0;
        }

        public DataFormatMatcher createMatcher(JsonFactory match, MatchStrength matchStrength) {
            return new DataFormatMatcher(this._in, this._buffer, this._bufferedAmount, match, matchStrength);
        }
    }
}

