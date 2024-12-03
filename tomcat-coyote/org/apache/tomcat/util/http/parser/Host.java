/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.buf.ByteChunk
 *  org.apache.tomcat.util.buf.MessageBytes
 */
package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.tomcat.util.http.parser.HttpParser;

public class Host {
    private Host() {
    }

    public static int parse(MessageBytes mb) {
        return Host.parse(new MessageBytesReader(mb));
    }

    public static int parse(String string) {
        return Host.parse(new StringReader(string));
    }

    private static int parse(Reader reader) {
        try {
            reader.mark(1);
            int first = reader.read();
            reader.reset();
            if (HttpParser.isAlpha(first)) {
                return HttpParser.readHostDomainName(reader);
            }
            if (HttpParser.isNumeric(first)) {
                return HttpParser.readHostIPv4(reader, false);
            }
            if (91 == first) {
                return HttpParser.readHostIPv6(reader);
            }
            throw new IllegalArgumentException();
        }
        catch (IOException ioe) {
            throw new IllegalArgumentException(ioe);
        }
    }

    private static class MessageBytesReader
    extends Reader {
        private final byte[] bytes;
        private final int end;
        private int pos;
        private int mark;

        MessageBytesReader(MessageBytes mb) {
            ByteChunk bc = mb.getByteChunk();
            this.bytes = bc.getBytes();
            this.pos = bc.getOffset();
            this.end = bc.getEnd();
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            for (int i = off; i < off + len; ++i) {
                cbuf[i] = (char)(this.bytes[this.pos++] & 0xFF);
            }
            return len;
        }

        @Override
        public void close() throws IOException {
        }

        @Override
        public int read() throws IOException {
            if (this.pos < this.end) {
                return this.bytes[this.pos++] & 0xFF;
            }
            return -1;
        }

        @Override
        public boolean markSupported() {
            return true;
        }

        @Override
        public void mark(int readAheadLimit) throws IOException {
            this.mark = this.pos;
        }

        @Override
        public void reset() throws IOException {
            this.pos = this.mark;
        }
    }
}

