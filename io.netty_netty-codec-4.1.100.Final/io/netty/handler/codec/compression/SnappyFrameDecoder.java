/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandlerContext
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.compression.DecompressionException;
import io.netty.handler.codec.compression.Snappy;
import java.util.List;

public class SnappyFrameDecoder
extends ByteToMessageDecoder {
    private static final int SNAPPY_IDENTIFIER_LEN = 6;
    private static final int MAX_UNCOMPRESSED_DATA_SIZE = 65540;
    private static final int MAX_DECOMPRESSED_DATA_SIZE = 65536;
    private static final int MAX_COMPRESSED_CHUNK_SIZE = 0xFFFFFF;
    private final Snappy snappy = new Snappy();
    private final boolean validateChecksums;
    private boolean started;
    private boolean corrupted;
    private int numBytesToSkip;

    public SnappyFrameDecoder() {
        this(false);
    }

    public SnappyFrameDecoder(boolean validateChecksums) {
        this.validateChecksums = validateChecksums;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (this.corrupted) {
            in.skipBytes(in.readableBytes());
            return;
        }
        if (this.numBytesToSkip != 0) {
            skipBytes = Math.min(this.numBytesToSkip, in.readableBytes());
            in.skipBytes(skipBytes);
            this.numBytesToSkip -= skipBytes;
            return;
        }
        try {
            idx = in.readerIndex();
            inSize = in.readableBytes();
            if (inSize < 4) {
                return;
            }
            chunkTypeVal = in.getUnsignedByte(idx);
            chunkType = SnappyFrameDecoder.mapChunkType((byte)chunkTypeVal);
            chunkLength = in.getUnsignedMediumLE(idx + 1);
            switch (1.$SwitchMap$io$netty$handler$codec$compression$SnappyFrameDecoder$ChunkType[chunkType.ordinal()]) {
                case 1: {
                    if (chunkLength != 6) {
                        throw new DecompressionException("Unexpected length of stream identifier: " + chunkLength);
                    }
                    if (inSize < 10) break;
                    in.skipBytes(4);
                    offset = in.readerIndex();
                    in.skipBytes(6);
                    SnappyFrameDecoder.checkByte(in.getByte(offset++), (byte)115);
                    SnappyFrameDecoder.checkByte(in.getByte(offset++), (byte)78);
                    SnappyFrameDecoder.checkByte(in.getByte(offset++), (byte)97);
                    SnappyFrameDecoder.checkByte(in.getByte(offset++), (byte)80);
                    SnappyFrameDecoder.checkByte(in.getByte(offset++), (byte)112);
                    SnappyFrameDecoder.checkByte(in.getByte(offset), (byte)89);
                    this.started = true;
                    break;
                }
                case 2: {
                    if (!this.started) {
                        throw new DecompressionException("Received RESERVED_SKIPPABLE tag before STREAM_IDENTIFIER");
                    }
                    in.skipBytes(4);
                    skipBytes = Math.min(chunkLength, in.readableBytes());
                    in.skipBytes(skipBytes);
                    if (skipBytes == chunkLength) break;
                    this.numBytesToSkip = chunkLength - skipBytes;
                    break;
                }
                case 3: {
                    throw new DecompressionException("Found reserved unskippable chunk type: 0x" + Integer.toHexString(chunkTypeVal));
                }
                case 4: {
                    if (!this.started) {
                        throw new DecompressionException("Received UNCOMPRESSED_DATA tag before STREAM_IDENTIFIER");
                    }
                    if (chunkLength > 65540) {
                        throw new DecompressionException("Received UNCOMPRESSED_DATA larger than 65540 bytes");
                    }
                    if (inSize < 4 + chunkLength) {
                        return;
                    }
                    in.skipBytes(4);
                    if (this.validateChecksums) {
                        checksum = in.readIntLE();
                        Snappy.validateChecksum(checksum, in, in.readerIndex(), chunkLength - 4);
                    } else {
                        in.skipBytes(4);
                    }
                    out.add(in.readRetainedSlice(chunkLength - 4));
                    break;
                }
                case 5: {
                    if (!this.started) {
                        throw new DecompressionException("Received COMPRESSED_DATA tag before STREAM_IDENTIFIER");
                    }
                    if (chunkLength > 0xFFFFFF) {
                        throw new DecompressionException("Received COMPRESSED_DATA that contains chunk that exceeds 16777215 bytes");
                    }
                    if (inSize < 4 + chunkLength) {
                        return;
                    }
                    in.skipBytes(4);
                    checksum = in.readIntLE();
                    uncompressedSize = this.snappy.getPreamble(in);
                    if (uncompressedSize > 65536) {
                        throw new DecompressionException("Received COMPRESSED_DATA that contains uncompressed data that exceeds 65536 bytes");
                    }
                    uncompressed = ctx.alloc().buffer(uncompressedSize, 65536);
                    try {
                        if (!this.validateChecksums) ** GOTO lbl96
                        oldWriterIndex = in.writerIndex();
                        try {
                            in.writerIndex(in.readerIndex() + chunkLength - 4);
                            this.snappy.decode(in, uncompressed);
                        }
                        finally {
                            in.writerIndex(oldWriterIndex);
                        }
                        Snappy.validateChecksum(checksum, uncompressed, 0, uncompressed.writerIndex());
                        ** GOTO lbl97
lbl96:
                        // 1 sources

                        this.snappy.decode(in.readSlice(chunkLength - 4), uncompressed);
lbl97:
                        // 2 sources

                        out.add(uncompressed);
                        uncompressed = null;
                    }
                    finally {
                        if (uncompressed != null) {
                            uncompressed.release();
                        }
                    }
                    this.snappy.reset();
                }
            }
        }
        catch (Exception e) {
            this.corrupted = true;
            throw e;
        }
    }

    private static void checkByte(byte actual, byte expect) {
        if (actual != expect) {
            throw new DecompressionException("Unexpected stream identifier contents. Mismatched snappy protocol version?");
        }
    }

    private static ChunkType mapChunkType(byte type) {
        if (type == 0) {
            return ChunkType.COMPRESSED_DATA;
        }
        if (type == 1) {
            return ChunkType.UNCOMPRESSED_DATA;
        }
        if (type == -1) {
            return ChunkType.STREAM_IDENTIFIER;
        }
        if ((type & 0x80) == 128) {
            return ChunkType.RESERVED_SKIPPABLE;
        }
        return ChunkType.RESERVED_UNSKIPPABLE;
    }

    private static enum ChunkType {
        STREAM_IDENTIFIER,
        COMPRESSED_DATA,
        UNCOMPRESSED_DATA,
        RESERVED_UNSKIPPABLE,
        RESERVED_SKIPPABLE;

    }
}

