/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.embedded.EmbeddedChannel
 *  io.netty.handler.codec.MessageToByteEncoder
 *  io.netty.handler.codec.compression.Brotli
 *  io.netty.handler.codec.compression.BrotliEncoder
 *  io.netty.handler.codec.compression.BrotliOptions
 *  io.netty.handler.codec.compression.CompressionOptions
 *  io.netty.handler.codec.compression.DeflateOptions
 *  io.netty.handler.codec.compression.GzipOptions
 *  io.netty.handler.codec.compression.SnappyFrameEncoder
 *  io.netty.handler.codec.compression.SnappyOptions
 *  io.netty.handler.codec.compression.StandardCompressionOptions
 *  io.netty.handler.codec.compression.ZlibCodecFactory
 *  io.netty.handler.codec.compression.ZlibWrapper
 *  io.netty.handler.codec.compression.Zstd
 *  io.netty.handler.codec.compression.ZstdEncoder
 *  io.netty.handler.codec.compression.ZstdOptions
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.compression.Brotli;
import io.netty.handler.codec.compression.BrotliEncoder;
import io.netty.handler.codec.compression.BrotliOptions;
import io.netty.handler.codec.compression.CompressionOptions;
import io.netty.handler.codec.compression.DeflateOptions;
import io.netty.handler.codec.compression.GzipOptions;
import io.netty.handler.codec.compression.SnappyFrameEncoder;
import io.netty.handler.codec.compression.SnappyOptions;
import io.netty.handler.codec.compression.StandardCompressionOptions;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.codec.compression.Zstd;
import io.netty.handler.codec.compression.ZstdEncoder;
import io.netty.handler.codec.compression.ZstdOptions;
import io.netty.handler.codec.http.CompressionEncoderFactory;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpContentEncoder;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.internal.ObjectUtil;
import java.util.HashMap;
import java.util.Map;

public class HttpContentCompressor
extends HttpContentEncoder {
    private final boolean supportsCompressionOptions;
    private final BrotliOptions brotliOptions;
    private final GzipOptions gzipOptions;
    private final DeflateOptions deflateOptions;
    private final ZstdOptions zstdOptions;
    private final SnappyOptions snappyOptions;
    private final int compressionLevel;
    private final int windowBits;
    private final int memLevel;
    private final int contentSizeThreshold;
    private ChannelHandlerContext ctx;
    private final Map<String, CompressionEncoderFactory> factories;

    public HttpContentCompressor() {
        this(6);
    }

    @Deprecated
    public HttpContentCompressor(int compressionLevel) {
        this(compressionLevel, 15, 8, 0);
    }

    @Deprecated
    public HttpContentCompressor(int compressionLevel, int windowBits, int memLevel) {
        this(compressionLevel, windowBits, memLevel, 0);
    }

    @Deprecated
    public HttpContentCompressor(int compressionLevel, int windowBits, int memLevel, int contentSizeThreshold) {
        this.compressionLevel = ObjectUtil.checkInRange((int)compressionLevel, (int)0, (int)9, (String)"compressionLevel");
        this.windowBits = ObjectUtil.checkInRange((int)windowBits, (int)9, (int)15, (String)"windowBits");
        this.memLevel = ObjectUtil.checkInRange((int)memLevel, (int)1, (int)9, (String)"memLevel");
        this.contentSizeThreshold = ObjectUtil.checkPositiveOrZero((int)contentSizeThreshold, (String)"contentSizeThreshold");
        this.brotliOptions = null;
        this.gzipOptions = null;
        this.deflateOptions = null;
        this.zstdOptions = null;
        this.snappyOptions = null;
        this.factories = null;
        this.supportsCompressionOptions = false;
    }

    public HttpContentCompressor(CompressionOptions ... compressionOptions) {
        this(0, compressionOptions);
    }

    public HttpContentCompressor(int contentSizeThreshold, CompressionOptions ... compressionOptions) {
        this.contentSizeThreshold = ObjectUtil.checkPositiveOrZero((int)contentSizeThreshold, (String)"contentSizeThreshold");
        BrotliOptions brotliOptions = null;
        GzipOptions gzipOptions = null;
        DeflateOptions deflateOptions = null;
        ZstdOptions zstdOptions = null;
        SnappyOptions snappyOptions = null;
        if (compressionOptions == null || compressionOptions.length == 0) {
            brotliOptions = Brotli.isAvailable() ? StandardCompressionOptions.brotli() : null;
            gzipOptions = StandardCompressionOptions.gzip();
            deflateOptions = StandardCompressionOptions.deflate();
            zstdOptions = Zstd.isAvailable() ? StandardCompressionOptions.zstd() : null;
            snappyOptions = StandardCompressionOptions.snappy();
        } else {
            ObjectUtil.deepCheckNotNull((String)"compressionOptions", (Object[])compressionOptions);
            for (CompressionOptions compressionOption : compressionOptions) {
                if (Brotli.isAvailable() && compressionOption instanceof BrotliOptions) {
                    brotliOptions = (BrotliOptions)compressionOption;
                    continue;
                }
                if (compressionOption instanceof GzipOptions) {
                    gzipOptions = (GzipOptions)compressionOption;
                    continue;
                }
                if (compressionOption instanceof DeflateOptions) {
                    deflateOptions = (DeflateOptions)compressionOption;
                    continue;
                }
                if (compressionOption instanceof ZstdOptions) {
                    zstdOptions = (ZstdOptions)compressionOption;
                    continue;
                }
                if (compressionOption instanceof SnappyOptions) {
                    snappyOptions = (SnappyOptions)compressionOption;
                    continue;
                }
                throw new IllegalArgumentException("Unsupported " + CompressionOptions.class.getSimpleName() + ": " + compressionOption);
            }
        }
        this.gzipOptions = gzipOptions;
        this.deflateOptions = deflateOptions;
        this.brotliOptions = brotliOptions;
        this.zstdOptions = zstdOptions;
        this.snappyOptions = snappyOptions;
        this.factories = new HashMap<String, CompressionEncoderFactory>();
        if (this.gzipOptions != null) {
            this.factories.put("gzip", new GzipEncoderFactory());
        }
        if (this.deflateOptions != null) {
            this.factories.put("deflate", new DeflateEncoderFactory());
        }
        if (Brotli.isAvailable() && this.brotliOptions != null) {
            this.factories.put("br", new BrEncoderFactory());
        }
        if (this.zstdOptions != null) {
            this.factories.put("zstd", new ZstdEncoderFactory());
        }
        if (this.snappyOptions != null) {
            this.factories.put("snappy", new SnappyEncoderFactory());
        }
        this.compressionLevel = -1;
        this.windowBits = -1;
        this.memLevel = -1;
        this.supportsCompressionOptions = true;
    }

    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
    }

    @Override
    protected HttpContentEncoder.Result beginEncode(HttpResponse httpResponse, String acceptEncoding) throws Exception {
        String targetContentEncoding;
        if (this.contentSizeThreshold > 0 && httpResponse instanceof HttpContent && ((HttpContent)((Object)httpResponse)).content().readableBytes() < this.contentSizeThreshold) {
            return null;
        }
        String contentEncoding = httpResponse.headers().get((CharSequence)HttpHeaderNames.CONTENT_ENCODING);
        if (contentEncoding != null) {
            return null;
        }
        if (this.supportsCompressionOptions) {
            String targetContentEncoding2 = this.determineEncoding(acceptEncoding);
            if (targetContentEncoding2 == null) {
                return null;
            }
            CompressionEncoderFactory encoderFactory = this.factories.get(targetContentEncoding2);
            if (encoderFactory == null) {
                throw new Error();
            }
            return new HttpContentEncoder.Result(targetContentEncoding2, new EmbeddedChannel(this.ctx.channel().id(), this.ctx.channel().metadata().hasDisconnect(), this.ctx.channel().config(), new ChannelHandler[]{encoderFactory.createEncoder()}));
        }
        ZlibWrapper wrapper = this.determineWrapper(acceptEncoding);
        if (wrapper == null) {
            return null;
        }
        switch (wrapper) {
            case GZIP: {
                targetContentEncoding = "gzip";
                break;
            }
            case ZLIB: {
                targetContentEncoding = "deflate";
                break;
            }
            default: {
                throw new Error();
            }
        }
        return new HttpContentEncoder.Result(targetContentEncoding, new EmbeddedChannel(this.ctx.channel().id(), this.ctx.channel().metadata().hasDisconnect(), this.ctx.channel().config(), new ChannelHandler[]{ZlibCodecFactory.newZlibEncoder((ZlibWrapper)wrapper, (int)this.compressionLevel, (int)this.windowBits, (int)this.memLevel)}));
    }

    protected String determineEncoding(String acceptEncoding) {
        float starQ = -1.0f;
        float brQ = -1.0f;
        float zstdQ = -1.0f;
        float snappyQ = -1.0f;
        float gzipQ = -1.0f;
        float deflateQ = -1.0f;
        for (String encoding : acceptEncoding.split(",")) {
            float q = 1.0f;
            int equalsPos = encoding.indexOf(61);
            if (equalsPos != -1) {
                try {
                    q = Float.parseFloat(encoding.substring(equalsPos + 1));
                }
                catch (NumberFormatException e) {
                    q = 0.0f;
                }
            }
            if (encoding.contains("*")) {
                starQ = q;
                continue;
            }
            if (encoding.contains("br") && q > brQ) {
                brQ = q;
                continue;
            }
            if (encoding.contains("zstd") && q > zstdQ) {
                zstdQ = q;
                continue;
            }
            if (encoding.contains("snappy") && q > snappyQ) {
                snappyQ = q;
                continue;
            }
            if (encoding.contains("gzip") && q > gzipQ) {
                gzipQ = q;
                continue;
            }
            if (!encoding.contains("deflate") || !(q > deflateQ)) continue;
            deflateQ = q;
        }
        if (brQ > 0.0f || zstdQ > 0.0f || snappyQ > 0.0f || gzipQ > 0.0f || deflateQ > 0.0f) {
            if (brQ != -1.0f && brQ >= zstdQ && this.brotliOptions != null) {
                return "br";
            }
            if (zstdQ != -1.0f && zstdQ >= snappyQ && this.zstdOptions != null) {
                return "zstd";
            }
            if (snappyQ != -1.0f && snappyQ >= gzipQ && this.snappyOptions != null) {
                return "snappy";
            }
            if (gzipQ != -1.0f && gzipQ >= deflateQ && this.gzipOptions != null) {
                return "gzip";
            }
            if (deflateQ != -1.0f && this.deflateOptions != null) {
                return "deflate";
            }
        }
        if (starQ > 0.0f) {
            if (brQ == -1.0f && this.brotliOptions != null) {
                return "br";
            }
            if (zstdQ == -1.0f && this.zstdOptions != null) {
                return "zstd";
            }
            if (snappyQ == -1.0f && this.snappyOptions != null) {
                return "snappy";
            }
            if (gzipQ == -1.0f && this.gzipOptions != null) {
                return "gzip";
            }
            if (deflateQ == -1.0f && this.deflateOptions != null) {
                return "deflate";
            }
        }
        return null;
    }

    @Deprecated
    protected ZlibWrapper determineWrapper(String acceptEncoding) {
        float starQ = -1.0f;
        float gzipQ = -1.0f;
        float deflateQ = -1.0f;
        for (String encoding : acceptEncoding.split(",")) {
            float q = 1.0f;
            int equalsPos = encoding.indexOf(61);
            if (equalsPos != -1) {
                try {
                    q = Float.parseFloat(encoding.substring(equalsPos + 1));
                }
                catch (NumberFormatException e) {
                    q = 0.0f;
                }
            }
            if (encoding.contains("*")) {
                starQ = q;
                continue;
            }
            if (encoding.contains("gzip") && q > gzipQ) {
                gzipQ = q;
                continue;
            }
            if (!encoding.contains("deflate") || !(q > deflateQ)) continue;
            deflateQ = q;
        }
        if (gzipQ > 0.0f || deflateQ > 0.0f) {
            if (gzipQ >= deflateQ) {
                return ZlibWrapper.GZIP;
            }
            return ZlibWrapper.ZLIB;
        }
        if (starQ > 0.0f) {
            if (gzipQ == -1.0f) {
                return ZlibWrapper.GZIP;
            }
            if (deflateQ == -1.0f) {
                return ZlibWrapper.ZLIB;
            }
        }
        return null;
    }

    private static final class SnappyEncoderFactory
    implements CompressionEncoderFactory {
        private SnappyEncoderFactory() {
        }

        @Override
        public MessageToByteEncoder<ByteBuf> createEncoder() {
            return new SnappyFrameEncoder();
        }
    }

    private final class ZstdEncoderFactory
    implements CompressionEncoderFactory {
        private ZstdEncoderFactory() {
        }

        @Override
        public MessageToByteEncoder<ByteBuf> createEncoder() {
            return new ZstdEncoder(HttpContentCompressor.this.zstdOptions.compressionLevel(), HttpContentCompressor.this.zstdOptions.blockSize(), HttpContentCompressor.this.zstdOptions.maxEncodeSize());
        }
    }

    private final class BrEncoderFactory
    implements CompressionEncoderFactory {
        private BrEncoderFactory() {
        }

        @Override
        public MessageToByteEncoder<ByteBuf> createEncoder() {
            return new BrotliEncoder(HttpContentCompressor.this.brotliOptions.parameters());
        }
    }

    private final class DeflateEncoderFactory
    implements CompressionEncoderFactory {
        private DeflateEncoderFactory() {
        }

        @Override
        public MessageToByteEncoder<ByteBuf> createEncoder() {
            return ZlibCodecFactory.newZlibEncoder((ZlibWrapper)ZlibWrapper.ZLIB, (int)HttpContentCompressor.this.deflateOptions.compressionLevel(), (int)HttpContentCompressor.this.deflateOptions.windowBits(), (int)HttpContentCompressor.this.deflateOptions.memLevel());
        }
    }

    private final class GzipEncoderFactory
    implements CompressionEncoderFactory {
        private GzipEncoderFactory() {
        }

        @Override
        public MessageToByteEncoder<ByteBuf> createEncoder() {
            return ZlibCodecFactory.newZlibEncoder((ZlibWrapper)ZlibWrapper.GZIP, (int)HttpContentCompressor.this.gzipOptions.compressionLevel(), (int)HttpContentCompressor.this.gzipOptions.windowBits(), (int)HttpContentCompressor.this.gzipOptions.memLevel());
        }
    }
}

