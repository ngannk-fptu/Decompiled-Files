/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.buf.ByteChunk
 *  org.apache.tomcat.util.buf.HexUtils
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.coyote.http11.filters;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.coyote.BadRequestException;
import org.apache.coyote.InputBuffer;
import org.apache.coyote.Request;
import org.apache.coyote.http11.InputFilter;
import org.apache.tomcat.util.buf.ByteChunk;
import org.apache.tomcat.util.buf.HexUtils;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.apache.tomcat.util.net.ApplicationBufferHandler;
import org.apache.tomcat.util.res.StringManager;

public class ChunkedInputFilter
implements InputFilter,
ApplicationBufferHandler {
    private static final StringManager sm = StringManager.getManager(ChunkedInputFilter.class);
    protected static final String ENCODING_NAME = "chunked";
    protected static final ByteChunk ENCODING = new ByteChunk();
    protected InputBuffer buffer;
    protected int remaining = 0;
    protected ByteBuffer readChunk;
    protected boolean endChunk = false;
    protected final ByteChunk trailingHeaders = new ByteChunk();
    protected boolean needCRLFParse = false;
    private Request request;
    private final long maxExtensionSize;
    private final int maxTrailerSize;
    private long extensionSize;
    private final int maxSwallowSize;
    private boolean error;
    private final Set<String> allowedTrailerHeaders;

    public ChunkedInputFilter(int maxTrailerSize, Set<String> allowedTrailerHeaders, int maxExtensionSize, int maxSwallowSize) {
        this.trailingHeaders.setLimit(maxTrailerSize);
        this.allowedTrailerHeaders = allowedTrailerHeaders;
        this.maxExtensionSize = maxExtensionSize;
        this.maxTrailerSize = maxTrailerSize;
        this.maxSwallowSize = maxSwallowSize;
    }

    @Override
    public int doRead(ApplicationBufferHandler handler) throws IOException {
        if (this.endChunk) {
            return -1;
        }
        this.checkError();
        if (this.needCRLFParse) {
            this.needCRLFParse = false;
            this.parseCRLF(false);
        }
        if (this.remaining <= 0) {
            if (!this.parseChunkHeader()) {
                this.throwBadRequestException(sm.getString("chunkedInputFilter.invalidHeader"));
            }
            if (this.endChunk) {
                this.parseEndChunk();
                return -1;
            }
        }
        int result = 0;
        if ((this.readChunk == null || this.readChunk.position() >= this.readChunk.limit()) && this.readBytes() < 0) {
            this.throwEOFException(sm.getString("chunkedInputFilter.eos"));
        }
        if (this.remaining > this.readChunk.remaining()) {
            result = this.readChunk.remaining();
            this.remaining -= result;
            if (this.readChunk != handler.getByteBuffer()) {
                handler.setByteBuffer(this.readChunk.duplicate());
            }
            this.readChunk.position(this.readChunk.limit());
        } else {
            result = this.remaining;
            if (this.readChunk != handler.getByteBuffer()) {
                handler.setByteBuffer(this.readChunk.duplicate());
                handler.getByteBuffer().limit(this.readChunk.position() + this.remaining);
            }
            this.readChunk.position(this.readChunk.position() + this.remaining);
            this.remaining = 0;
            if (this.readChunk.position() + 1 >= this.readChunk.limit()) {
                this.needCRLFParse = true;
            } else {
                this.parseCRLF(false);
            }
        }
        return result;
    }

    @Override
    public void setRequest(Request request) {
        this.request = request;
    }

    @Override
    public long end() throws IOException {
        long swallowed = 0L;
        int read = 0;
        while ((read = this.doRead(this)) >= 0) {
            if (this.maxSwallowSize <= -1 || (swallowed += (long)read) <= (long)this.maxSwallowSize) continue;
            this.throwBadRequestException(sm.getString("inputFilter.maxSwallow"));
        }
        return this.readChunk.remaining();
    }

    @Override
    public int available() {
        int available = 0;
        if (this.readChunk != null) {
            available = this.readChunk.remaining();
        }
        if (available == 0) {
            return this.buffer.available();
        }
        return available;
    }

    @Override
    public void setBuffer(InputBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void recycle() {
        this.remaining = 0;
        if (this.readChunk != null) {
            this.readChunk.position(0).limit(0);
        }
        this.endChunk = false;
        this.needCRLFParse = false;
        this.trailingHeaders.recycle();
        this.trailingHeaders.setLimit(this.maxTrailerSize);
        this.extensionSize = 0L;
        this.error = false;
    }

    @Override
    public ByteChunk getEncodingName() {
        return ENCODING;
    }

    @Override
    public boolean isFinished() {
        return this.endChunk;
    }

    protected int readBytes() throws IOException {
        return this.buffer.doRead(this);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected boolean parseChunkHeader() throws IOException {
        int result = 0;
        boolean eol = false;
        int readDigit = 0;
        boolean extension = false;
        while (!eol) {
            if ((this.readChunk == null || this.readChunk.position() >= this.readChunk.limit()) && this.readBytes() <= 0) {
                return false;
            }
            byte chr = this.readChunk.get(this.readChunk.position());
            if (chr == 13 || chr == 10) {
                this.parseCRLF(false);
                eol = true;
            } else if (chr == 59 && !extension) {
                extension = true;
                ++this.extensionSize;
            } else if (!extension) {
                int charValue = HexUtils.getDec((int)chr);
                if (charValue == -1 || readDigit >= 8) return false;
                ++readDigit;
                result = result << 4 | charValue;
            } else {
                ++this.extensionSize;
                if (this.maxExtensionSize > -1L && this.extensionSize > this.maxExtensionSize) {
                    this.throwBadRequestException(sm.getString("chunkedInputFilter.maxExtension"));
                }
            }
            if (eol) continue;
            this.readChunk.position(this.readChunk.position() + 1);
        }
        if (readDigit == 0 || result < 0) {
            return false;
        }
        if (result == 0) {
            this.endChunk = true;
        }
        this.remaining = result;
        return true;
    }

    protected void parseCRLF(boolean tolerant) throws IOException {
        boolean eol = false;
        boolean crfound = false;
        while (!eol) {
            byte chr;
            if ((this.readChunk == null || this.readChunk.position() >= this.readChunk.limit()) && this.readBytes() <= 0) {
                this.throwBadRequestException(sm.getString("chunkedInputFilter.invalidCrlfNoData"));
            }
            if ((chr = this.readChunk.get(this.readChunk.position())) == 13) {
                if (crfound) {
                    this.throwBadRequestException(sm.getString("chunkedInputFilter.invalidCrlfCRCR"));
                }
                crfound = true;
            } else if (chr == 10) {
                if (!tolerant && !crfound) {
                    this.throwBadRequestException(sm.getString("chunkedInputFilter.invalidCrlfNoCR"));
                }
                eol = true;
            } else {
                this.throwBadRequestException(sm.getString("chunkedInputFilter.invalidCrlf"));
            }
            this.readChunk.position(this.readChunk.position() + 1);
        }
    }

    protected void parseEndChunk() throws IOException {
        while (this.parseHeader()) {
        }
    }

    private boolean parseHeader() throws IOException {
        Map<String, String> headers = this.request.getTrailerFields();
        byte chr = 0;
        if ((this.readChunk == null || this.readChunk.position() >= this.readChunk.limit()) && this.readBytes() < 0) {
            this.throwEOFException(sm.getString("chunkedInputFilter.eosTrailer"));
        }
        if ((chr = this.readChunk.get(this.readChunk.position())) == 13 || chr == 10) {
            this.parseCRLF(false);
            return false;
        }
        int startPos = this.trailingHeaders.getEnd();
        boolean colon = false;
        while (!colon) {
            if ((this.readChunk == null || this.readChunk.position() >= this.readChunk.limit()) && this.readBytes() < 0) {
                this.throwEOFException(sm.getString("chunkedInputFilter.eosTrailer"));
            }
            if ((chr = this.readChunk.get(this.readChunk.position())) >= 65 && chr <= 90) {
                chr = (byte)(chr - -32);
            }
            if (chr == 58) {
                colon = true;
            } else if (!HttpParser.isToken(chr)) {
                this.throwBadRequestException(sm.getString("chunkedInputFilter.invalidTrailerHeaderName"));
            } else if (this.trailingHeaders.getEnd() >= this.trailingHeaders.getLimit()) {
                this.throwBadRequestException(sm.getString("chunkedInputFilter.maxTrailer"));
            } else {
                this.trailingHeaders.append(chr);
            }
            this.readChunk.position(this.readChunk.position() + 1);
        }
        int colonPos = this.trailingHeaders.getEnd();
        boolean eol = false;
        boolean validLine = true;
        int lastSignificantChar = 0;
        while (validLine) {
            boolean space = true;
            while (space) {
                if ((this.readChunk == null || this.readChunk.position() >= this.readChunk.limit()) && this.readBytes() < 0) {
                    this.throwEOFException(sm.getString("chunkedInputFilter.eosTrailer"));
                }
                if ((chr = this.readChunk.get(this.readChunk.position())) == 32 || chr == 9) {
                    this.readChunk.position(this.readChunk.position() + 1);
                    int newlimit = this.trailingHeaders.getLimit() - 1;
                    if (this.trailingHeaders.getEnd() > newlimit) {
                        this.throwBadRequestException(sm.getString("chunkedInputFilter.maxTrailer"));
                    }
                    this.trailingHeaders.setLimit(newlimit);
                    continue;
                }
                space = false;
            }
            while (!eol) {
                if ((this.readChunk == null || this.readChunk.position() >= this.readChunk.limit()) && this.readBytes() < 0) {
                    this.throwEOFException(sm.getString("chunkedInputFilter.eosTrailer"));
                }
                if ((chr = this.readChunk.get(this.readChunk.position())) == 13 || chr == 10) {
                    this.parseCRLF(true);
                    eol = true;
                } else {
                    if (HttpParser.isControl(chr) && chr != 9) {
                        throw new IOException(sm.getString("chunkedInputFilter.invalidTrailerHeaderValue"));
                    }
                    if (this.trailingHeaders.getEnd() >= this.trailingHeaders.getLimit()) {
                        this.throwBadRequestException(sm.getString("chunkedInputFilter.maxTrailer"));
                    } else if (chr == 32 || chr == 9) {
                        this.trailingHeaders.append(chr);
                    } else {
                        this.trailingHeaders.append(chr);
                        lastSignificantChar = this.trailingHeaders.getEnd();
                    }
                }
                if (eol) continue;
                this.readChunk.position(this.readChunk.position() + 1);
            }
            if ((this.readChunk == null || this.readChunk.position() >= this.readChunk.limit()) && this.readBytes() < 0) {
                this.throwEOFException(sm.getString("chunkedInputFilter.eosTrailer"));
            }
            if ((chr = this.readChunk.get(this.readChunk.position())) != 32 && chr != 9) {
                validLine = false;
                continue;
            }
            if (this.trailingHeaders.getEnd() >= this.trailingHeaders.getLimit()) {
                this.throwBadRequestException(sm.getString("chunkedInputFilter.maxTrailer"));
                continue;
            }
            eol = false;
            this.trailingHeaders.append(chr);
        }
        String headerName = new String(this.trailingHeaders.getBytes(), startPos, colonPos - startPos, StandardCharsets.ISO_8859_1);
        if (this.allowedTrailerHeaders.contains(headerName = headerName.toLowerCase(Locale.ENGLISH))) {
            String value = new String(this.trailingHeaders.getBytes(), colonPos, lastSignificantChar - colonPos, StandardCharsets.ISO_8859_1);
            headers.put(headerName, value);
        }
        return true;
    }

    private void throwBadRequestException(String msg) throws IOException {
        this.error = true;
        throw new BadRequestException(msg);
    }

    private void throwEOFException(String msg) throws IOException {
        this.error = true;
        throw new EOFException(msg);
    }

    private void checkError() throws IOException {
        if (this.error) {
            throw new IOException(sm.getString("chunkedInputFilter.error"));
        }
    }

    @Override
    public void setByteBuffer(ByteBuffer buffer) {
        this.readChunk = buffer;
    }

    @Override
    public ByteBuffer getByteBuffer() {
        return this.readChunk;
    }

    @Override
    public void expand(int size) {
    }

    static {
        ENCODING.setBytes(ENCODING_NAME.getBytes(StandardCharsets.ISO_8859_1), 0, ENCODING_NAME.length());
    }
}

