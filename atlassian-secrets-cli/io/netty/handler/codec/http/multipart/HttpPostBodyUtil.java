/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;

final class HttpPostBodyUtil {
    public static final int chunkSize = 8096;
    public static final String DEFAULT_BINARY_CONTENT_TYPE = "application/octet-stream";
    public static final String DEFAULT_TEXT_CONTENT_TYPE = "text/plain";

    private HttpPostBodyUtil() {
    }

    static int findNonWhitespace(String sb, int offset) {
        int result;
        for (result = offset; result < sb.length() && Character.isWhitespace(sb.charAt(result)); ++result) {
        }
        return result;
    }

    static int findEndOfString(String sb) {
        int result;
        for (result = sb.length(); result > 0 && Character.isWhitespace(sb.charAt(result - 1)); --result) {
        }
        return result;
    }

    static int findLineBreak(ByteBuf buffer, int index) {
        int toRead = buffer.readableBytes() - (index - buffer.readerIndex());
        int posFirstChar = buffer.bytesBefore(index, toRead, (byte)10);
        if (posFirstChar == -1) {
            return -1;
        }
        if (posFirstChar > 0 && buffer.getByte(index + posFirstChar - 1) == 13) {
            --posFirstChar;
        }
        return posFirstChar;
    }

    static int findLastLineBreak(ByteBuf buffer, int index) {
        int next;
        int candidate = HttpPostBodyUtil.findLineBreak(buffer, index);
        int findCRLF = 0;
        if (candidate >= 0) {
            findCRLF = buffer.getByte(index + candidate) == 13 ? 2 : 1;
            candidate += findCRLF;
        }
        while (candidate > 0 && (next = HttpPostBodyUtil.findLineBreak(buffer, index + candidate)) >= 0) {
            findCRLF = buffer.getByte(index + (candidate += next)) == 13 ? 2 : 1;
            candidate += findCRLF;
        }
        return candidate - findCRLF;
    }

    static int findDelimiter(ByteBuf buffer, int index, byte[] delimiter, boolean precededByLineBreak) {
        int delimiterLength = delimiter.length;
        int readerIndex = buffer.readerIndex();
        int writerIndex = buffer.writerIndex();
        int toRead = writerIndex - index;
        int newOffset = index;
        boolean delimiterNotFound = true;
        while (delimiterNotFound && delimiterLength <= toRead) {
            int posDelimiter = buffer.bytesBefore(newOffset, toRead, delimiter[0]);
            if (posDelimiter < 0) {
                return -1;
            }
            newOffset += posDelimiter;
            if ((toRead -= posDelimiter) >= delimiterLength) {
                delimiterNotFound = false;
                for (int i = 0; i < delimiterLength; ++i) {
                    if (buffer.getByte(newOffset + i) == delimiter[i]) continue;
                    ++newOffset;
                    --toRead;
                    delimiterNotFound = true;
                    break;
                }
            }
            if (delimiterNotFound) continue;
            if (precededByLineBreak && newOffset > readerIndex) {
                if (buffer.getByte(newOffset - 1) == 10) {
                    if (--newOffset > readerIndex && buffer.getByte(newOffset - 1) == 13) {
                        --newOffset;
                    }
                } else {
                    ++newOffset;
                    --toRead;
                    delimiterNotFound = true;
                    continue;
                }
            }
            return newOffset - readerIndex;
        }
        return -1;
    }

    static class SeekAheadOptimize {
        byte[] bytes;
        int readerIndex;
        int pos;
        int origPos;
        int limit;
        ByteBuf buffer;

        SeekAheadOptimize(ByteBuf buffer) {
            if (!buffer.hasArray()) {
                throw new IllegalArgumentException("buffer hasn't backing byte array");
            }
            this.buffer = buffer;
            this.bytes = buffer.array();
            this.readerIndex = buffer.readerIndex();
            this.origPos = this.pos = buffer.arrayOffset() + this.readerIndex;
            this.limit = buffer.arrayOffset() + buffer.writerIndex();
        }

        void setReadPosition(int minus) {
            this.pos -= minus;
            this.readerIndex = this.getReadPosition(this.pos);
            this.buffer.readerIndex(this.readerIndex);
        }

        int getReadPosition(int index) {
            return index - this.origPos + this.readerIndex;
        }
    }

    public static enum TransferEncodingMechanism {
        BIT7("7bit"),
        BIT8("8bit"),
        BINARY("binary");

        private final String value;

        private TransferEncodingMechanism(String value) {
            this.value = value;
        }

        public String value() {
            return this.value;
        }

        public String toString() {
            return this.value;
        }
    }
}

