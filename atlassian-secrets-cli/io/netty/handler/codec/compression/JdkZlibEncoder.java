/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.compression.EncoderUtil;
import io.netty.handler.codec.compression.ZlibEncoder;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.PromiseNotifier;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.zip.CRC32;
import java.util.zip.Deflater;

public class JdkZlibEncoder
extends ZlibEncoder {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(JdkZlibEncoder.class);
    private static final int MAX_INITIAL_OUTPUT_BUFFER_SIZE;
    private static final int MAX_INPUT_BUFFER_SIZE;
    private final ZlibWrapper wrapper;
    private final Deflater deflater;
    private volatile boolean finished;
    private volatile ChannelHandlerContext ctx;
    private final CRC32 crc = new CRC32();
    private static final byte[] gzipHeader;
    private boolean writeHeader = true;

    public JdkZlibEncoder() {
        this(6);
    }

    public JdkZlibEncoder(int compressionLevel) {
        this(ZlibWrapper.ZLIB, compressionLevel);
    }

    public JdkZlibEncoder(ZlibWrapper wrapper) {
        this(wrapper, 6);
    }

    public JdkZlibEncoder(ZlibWrapper wrapper, int compressionLevel) {
        ObjectUtil.checkInRange(compressionLevel, 0, 9, "compressionLevel");
        ObjectUtil.checkNotNull(wrapper, "wrapper");
        if (wrapper == ZlibWrapper.ZLIB_OR_NONE) {
            throw new IllegalArgumentException("wrapper '" + (Object)((Object)ZlibWrapper.ZLIB_OR_NONE) + "' is not allowed for compression.");
        }
        this.wrapper = wrapper;
        this.deflater = new Deflater(compressionLevel, wrapper != ZlibWrapper.ZLIB);
    }

    public JdkZlibEncoder(byte[] dictionary) {
        this(6, dictionary);
    }

    public JdkZlibEncoder(int compressionLevel, byte[] dictionary) {
        ObjectUtil.checkInRange(compressionLevel, 0, 9, "compressionLevel");
        ObjectUtil.checkNotNull(dictionary, "dictionary");
        this.wrapper = ZlibWrapper.ZLIB;
        this.deflater = new Deflater(compressionLevel);
        this.deflater.setDictionary(dictionary);
    }

    @Override
    public ChannelFuture close() {
        return this.close(this.ctx().newPromise());
    }

    @Override
    public ChannelFuture close(final ChannelPromise promise) {
        ChannelHandlerContext ctx = this.ctx();
        EventExecutor executor = ctx.executor();
        if (executor.inEventLoop()) {
            return this.finishEncode(ctx, promise);
        }
        final ChannelPromise p = ctx.newPromise();
        executor.execute(new Runnable(){

            @Override
            public void run() {
                ChannelFuture f = JdkZlibEncoder.this.finishEncode(JdkZlibEncoder.this.ctx(), p);
                PromiseNotifier.cascade(f, promise);
            }
        });
        return p;
    }

    private ChannelHandlerContext ctx() {
        ChannelHandlerContext ctx = this.ctx;
        if (ctx == null) {
            throw new IllegalStateException("not added to a pipeline");
        }
        return ctx;
    }

    @Override
    public boolean isClosed() {
        return this.finished;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf uncompressed, ByteBuf out) throws Exception {
        if (this.finished) {
            out.writeBytes(uncompressed);
            return;
        }
        int len = uncompressed.readableBytes();
        if (len == 0) {
            return;
        }
        if (uncompressed.hasArray()) {
            this.encodeSome(uncompressed, out);
        } else {
            int heapBufferSize = Math.min(len, MAX_INPUT_BUFFER_SIZE);
            ByteBuf heapBuf = ctx.alloc().heapBuffer(heapBufferSize, heapBufferSize);
            try {
                while (uncompressed.isReadable()) {
                    uncompressed.readBytes(heapBuf, Math.min(heapBuf.writableBytes(), uncompressed.readableBytes()));
                    this.encodeSome(heapBuf, out);
                    heapBuf.clear();
                }
            }
            finally {
                heapBuf.release();
            }
        }
        this.deflater.setInput(EmptyArrays.EMPTY_BYTES);
    }

    private void encodeSome(ByteBuf in, ByteBuf out) {
        byte[] inAry = in.array();
        int offset = in.arrayOffset() + in.readerIndex();
        if (this.writeHeader) {
            this.writeHeader = false;
            if (this.wrapper == ZlibWrapper.GZIP) {
                out.writeBytes(gzipHeader);
            }
        }
        int len = in.readableBytes();
        if (this.wrapper == ZlibWrapper.GZIP) {
            this.crc.update(inAry, offset, len);
        }
        this.deflater.setInput(inAry, offset, len);
        while (true) {
            this.deflate(out);
            if (!out.isWritable()) {
                out.ensureWritable(out.writerIndex());
                continue;
            }
            if (this.deflater.needsInput()) break;
        }
        in.skipBytes(len);
    }

    @Override
    protected final ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBuf msg, boolean preferDirect) throws Exception {
        int sizeEstimate = (int)Math.ceil((double)msg.readableBytes() * 1.001) + 12;
        if (this.writeHeader) {
            switch (this.wrapper) {
                case GZIP: {
                    sizeEstimate += gzipHeader.length;
                    break;
                }
                case ZLIB: {
                    sizeEstimate += 2;
                    break;
                }
            }
        }
        if (sizeEstimate < 0 || sizeEstimate > MAX_INITIAL_OUTPUT_BUFFER_SIZE) {
            return ctx.alloc().heapBuffer(MAX_INITIAL_OUTPUT_BUFFER_SIZE);
        }
        return ctx.alloc().heapBuffer(sizeEstimate);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ChannelFuture f = this.finishEncode(ctx, ctx.newPromise());
        EncoderUtil.closeAfterFinishEncode(ctx, f, promise);
    }

    private ChannelFuture finishEncode(ChannelHandlerContext ctx, ChannelPromise promise) {
        if (this.finished) {
            promise.setSuccess();
            return promise;
        }
        this.finished = true;
        ByteBuf footer = ctx.alloc().heapBuffer();
        if (this.writeHeader && this.wrapper == ZlibWrapper.GZIP) {
            this.writeHeader = false;
            footer.writeBytes(gzipHeader);
        }
        this.deflater.finish();
        while (!this.deflater.finished()) {
            this.deflate(footer);
            if (footer.isWritable()) continue;
            ctx.write(footer);
            footer = ctx.alloc().heapBuffer();
        }
        if (this.wrapper == ZlibWrapper.GZIP) {
            int crcValue = (int)this.crc.getValue();
            int uncBytes = this.deflater.getTotalIn();
            footer.writeByte(crcValue);
            footer.writeByte(crcValue >>> 8);
            footer.writeByte(crcValue >>> 16);
            footer.writeByte(crcValue >>> 24);
            footer.writeByte(uncBytes);
            footer.writeByte(uncBytes >>> 8);
            footer.writeByte(uncBytes >>> 16);
            footer.writeByte(uncBytes >>> 24);
        }
        this.deflater.end();
        return ctx.writeAndFlush(footer, promise);
    }

    @SuppressJava6Requirement(reason="Usage guarded by java version check")
    private void deflate(ByteBuf out) {
        int numBytes;
        if (PlatformDependent.javaVersion() < 7) {
            this.deflateJdk6(out);
        }
        do {
            int writerIndex = out.writerIndex();
            numBytes = this.deflater.deflate(out.array(), out.arrayOffset() + writerIndex, out.writableBytes(), 2);
            out.writerIndex(writerIndex + numBytes);
        } while (numBytes > 0);
    }

    private void deflateJdk6(ByteBuf out) {
        int numBytes;
        do {
            int writerIndex = out.writerIndex();
            numBytes = this.deflater.deflate(out.array(), out.arrayOffset() + writerIndex, out.writableBytes());
            out.writerIndex(writerIndex + numBytes);
        } while (numBytes > 0);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    static {
        gzipHeader = new byte[]{31, -117, 8, 0, 0, 0, 0, 0, 0, 0};
        MAX_INITIAL_OUTPUT_BUFFER_SIZE = SystemPropertyUtil.getInt("io.netty.jdkzlib.encoder.maxInitialOutputBufferSize", 65536);
        MAX_INPUT_BUFFER_SIZE = SystemPropertyUtil.getInt("io.netty.jdkzlib.encoder.maxInputBufferSize", 65536);
        if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.jdkzlib.encoder.maxInitialOutputBufferSize={}", (Object)MAX_INITIAL_OUTPUT_BUFFER_SIZE);
            logger.debug("-Dio.netty.jdkzlib.encoder.maxInputBufferSize={}", (Object)MAX_INPUT_BUFFER_SIZE);
        }
    }
}

