/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.nio.codecs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.ConnectionClosedException;
import org.apache.http.Header;
import org.apache.http.MalformedChunkCodingException;
import org.apache.http.MessageConstraintException;
import org.apache.http.ParseException;
import org.apache.http.TruncatedChunkException;
import org.apache.http.config.MessageConstraints;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.impl.nio.codecs.AbstractContentDecoder;
import org.apache.http.message.BufferedHeader;
import org.apache.http.nio.reactor.SessionInputBuffer;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

public class ChunkDecoder
extends AbstractContentDecoder {
    private static final int READ_CONTENT = 0;
    private static final int READ_FOOTERS = 1;
    private static final int COMPLETED = 2;
    private int state = 0;
    private boolean endOfChunk = false;
    private boolean endOfStream = false;
    private CharArrayBuffer lineBuf;
    private long chunkSize = -1L;
    private long pos = 0L;
    private final MessageConstraints constraints;
    private final List<CharArrayBuffer> trailerBufs;
    private Header[] footers;

    public ChunkDecoder(ReadableByteChannel channel, SessionInputBuffer buffer, MessageConstraints constraints, HttpTransportMetricsImpl metrics) {
        super(channel, buffer, metrics);
        this.constraints = constraints != null ? constraints : MessageConstraints.DEFAULT;
        this.trailerBufs = new ArrayList<CharArrayBuffer>();
    }

    public ChunkDecoder(ReadableByteChannel channel, SessionInputBuffer buffer, HttpTransportMetricsImpl metrics) {
        this(channel, buffer, null, metrics);
    }

    private void readChunkHead() throws IOException {
        if (this.lineBuf == null) {
            this.lineBuf = new CharArrayBuffer(32);
        } else {
            this.lineBuf.clear();
        }
        if (this.endOfChunk) {
            if (this.buffer.readLine(this.lineBuf, this.endOfStream)) {
                if (!this.lineBuf.isEmpty()) {
                    throw new MalformedChunkCodingException("CRLF expected at end of chunk");
                }
            } else {
                if (this.buffer.length() > 2 || this.endOfStream) {
                    throw new MalformedChunkCodingException("CRLF expected at end of chunk");
                }
                return;
            }
            this.endOfChunk = false;
        }
        boolean lineComplete = this.buffer.readLine(this.lineBuf, this.endOfStream);
        int maxLineLen = this.constraints.getMaxLineLength();
        if (maxLineLen > 0 && (this.lineBuf.length() > maxLineLen || !lineComplete && this.buffer.length() > maxLineLen)) {
            throw new MessageConstraintException("Maximum line length limit exceeded");
        }
        if (lineComplete) {
            int separator = this.lineBuf.indexOf(59);
            if (separator < 0) {
                separator = this.lineBuf.length();
            }
            String s = this.lineBuf.substringTrimmed(0, separator);
            try {
                this.chunkSize = Long.parseLong(s, 16);
            }
            catch (NumberFormatException e) {
                throw new MalformedChunkCodingException("Bad chunk header: " + s);
            }
            this.pos = 0L;
        } else if (this.endOfStream) {
            throw new ConnectionClosedException("Premature end of chunk coded message body: closing chunk expected");
        }
    }

    private void parseHeader() throws IOException {
        CharArrayBuffer current = this.lineBuf;
        int count = this.trailerBufs.size();
        if ((this.lineBuf.charAt(0) == ' ' || this.lineBuf.charAt(0) == '\t') && count > 0) {
            char ch;
            int i;
            CharArrayBuffer previous = this.trailerBufs.get(count - 1);
            for (i = 0; i < current.length() && ((ch = current.charAt(i)) == ' ' || ch == '\t'); ++i) {
            }
            int maxLineLen = this.constraints.getMaxLineLength();
            if (maxLineLen > 0 && previous.length() + 1 + current.length() - i > maxLineLen) {
                throw new MessageConstraintException("Maximum line length limit exceeded");
            }
            previous.append(' ');
            previous.append(current, i, current.length() - i);
        } else {
            this.trailerBufs.add(current);
            this.lineBuf = null;
        }
    }

    private void processFooters() throws IOException {
        int count = this.trailerBufs.size();
        if (count > 0) {
            this.footers = new Header[this.trailerBufs.size()];
            for (int i = 0; i < this.trailerBufs.size(); ++i) {
                try {
                    this.footers[i] = new BufferedHeader(this.trailerBufs.get(i));
                    continue;
                }
                catch (ParseException ex) {
                    throw new IOException(ex);
                }
            }
        }
        this.trailerBufs.clear();
    }

    @Override
    public int read(ByteBuffer dst) throws IOException {
        Args.notNull(dst, "Byte buffer");
        if (this.state == 2) {
            return -1;
        }
        int totalRead = 0;
        while (this.state != 2) {
            int bytesRead;
            if (!(this.buffer.hasData() && this.chunkSize != -1L || (bytesRead = this.fillBufferFromChannel()) != -1)) {
                this.endOfStream = true;
            }
            switch (this.state) {
                case 0: {
                    long maxLen;
                    int len;
                    if (this.chunkSize == -1L) {
                        this.readChunkHead();
                        if (this.chunkSize == -1L) {
                            return totalRead;
                        }
                        if (this.chunkSize == 0L) {
                            this.chunkSize = -1L;
                            this.state = 1;
                            break;
                        }
                    }
                    if ((len = this.buffer.read(dst, (int)Math.min(maxLen = this.chunkSize - this.pos, Integer.MAX_VALUE))) > 0) {
                        this.pos += (long)len;
                        totalRead += len;
                    } else if (!this.buffer.hasData() && this.endOfStream) {
                        this.state = 2;
                        this.setCompleted();
                        throw new TruncatedChunkException("Truncated chunk (expected size: %,d; actual size: %,d)", this.chunkSize, this.pos);
                    }
                    if (this.pos == this.chunkSize) {
                        this.chunkSize = -1L;
                        this.pos = 0L;
                        this.endOfChunk = true;
                        break;
                    }
                    return totalRead;
                }
                case 1: {
                    if (this.lineBuf == null) {
                        this.lineBuf = new CharArrayBuffer(32);
                    } else {
                        this.lineBuf.clear();
                    }
                    if (!this.buffer.readLine(this.lineBuf, this.endOfStream)) {
                        if (this.endOfStream) {
                            this.state = 2;
                            this.setCompleted();
                        }
                        return totalRead;
                    }
                    if (this.lineBuf.length() > 0) {
                        int maxHeaderCount = this.constraints.getMaxHeaderCount();
                        if (maxHeaderCount > 0 && this.trailerBufs.size() >= maxHeaderCount) {
                            throw new MessageConstraintException("Maximum header count exceeded");
                        }
                        this.parseHeader();
                        break;
                    }
                    this.state = 2;
                    this.setCompleted();
                    this.processFooters();
                }
            }
        }
        return totalRead;
    }

    public Header[] getFooters() {
        return this.footers != null ? (Header[])this.footers.clone() : new Header[]{};
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[chunk-coded; completed: ");
        sb.append(this.completed);
        sb.append("]");
        return sb.toString();
    }
}

