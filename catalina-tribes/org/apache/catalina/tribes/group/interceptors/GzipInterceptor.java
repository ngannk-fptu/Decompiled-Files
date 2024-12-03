/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.group.interceptors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.group.ChannelInterceptorBase;
import org.apache.catalina.tribes.group.InterceptorPayload;
import org.apache.catalina.tribes.group.interceptors.GzipInterceptorMBean;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class GzipInterceptor
extends ChannelInterceptorBase
implements GzipInterceptorMBean {
    private static final Log log = LogFactory.getLog(GzipInterceptor.class);
    protected static final StringManager sm = StringManager.getManager(GzipInterceptor.class);
    public static final int DEFAULT_BUFFER_SIZE = 2048;
    public static final int DEFAULT_OPTION_COMPRESSION_ENABLE = 256;
    private int compressionMinSize = 0;
    private volatile boolean statsEnabled = false;
    private int interval = 0;
    private final AtomicInteger count = new AtomicInteger();
    private final AtomicInteger countCompressedTX = new AtomicInteger();
    private final AtomicInteger countUncompressedTX = new AtomicInteger();
    private final AtomicInteger countCompressedRX = new AtomicInteger();
    private final AtomicInteger countUncompressedRX = new AtomicInteger();
    private final AtomicLong sizeTX = new AtomicLong();
    private final AtomicLong compressedSizeTX = new AtomicLong();
    private final AtomicLong uncompressedSizeTX = new AtomicLong();
    private final AtomicLong sizeRX = new AtomicLong();
    private final AtomicLong compressedSizeRX = new AtomicLong();
    private final AtomicLong uncompressedSizeRX = new AtomicLong();

    public GzipInterceptor() {
        this.setOptionFlag(256);
    }

    @Override
    public void sendMessage(Member[] destination, ChannelMessage msg, InterceptorPayload payload) throws ChannelException {
        try {
            byte[] data = msg.getMessage().getBytes();
            if (this.statsEnabled) {
                this.sizeTX.addAndGet(data.length);
            }
            if (data.length > this.compressionMinSize) {
                data = GzipInterceptor.compress(data);
                msg.setOptions(msg.getOptions() | this.getOptionFlag());
                if (this.statsEnabled) {
                    this.countCompressedTX.incrementAndGet();
                    this.compressedSizeTX.addAndGet(data.length);
                }
            } else if (this.statsEnabled) {
                this.countUncompressedTX.incrementAndGet();
                this.uncompressedSizeTX.addAndGet(data.length);
            }
            msg.getMessage().trim(msg.getMessage().getLength());
            msg.getMessage().append(data, 0, data.length);
            super.sendMessage(destination, msg, payload);
            int currentCount = this.count.incrementAndGet();
            if (this.statsEnabled && this.interval > 0 && currentCount % this.interval == 0) {
                this.report();
            }
        }
        catch (IOException x) {
            log.error((Object)sm.getString("gzipInterceptor.compress.failed"));
            throw new ChannelException(x);
        }
    }

    @Override
    public void messageReceived(ChannelMessage msg) {
        try {
            byte[] data = msg.getMessage().getBytes();
            if ((msg.getOptions() & this.getOptionFlag()) > 0) {
                if (this.statsEnabled) {
                    this.countCompressedRX.incrementAndGet();
                    this.compressedSizeRX.addAndGet(data.length);
                }
                data = GzipInterceptor.decompress(data);
            } else if (this.statsEnabled) {
                this.countUncompressedRX.incrementAndGet();
                this.uncompressedSizeRX.addAndGet(data.length);
            }
            if (this.statsEnabled) {
                this.sizeRX.addAndGet(data.length);
            }
            msg.getMessage().trim(msg.getMessage().getLength());
            msg.getMessage().append(data, 0, data.length);
            super.messageReceived(msg);
            int currentCount = this.count.incrementAndGet();
            if (this.statsEnabled && this.interval > 0 && currentCount % this.interval == 0) {
                this.report();
            }
        }
        catch (IOException x) {
            log.error((Object)sm.getString("gzipInterceptor.decompress.failed"), (Throwable)x);
        }
    }

    public static byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        GZIPOutputStream gout = new GZIPOutputStream(bout);
        gout.write(data);
        gout.flush();
        gout.close();
        return bout.toByteArray();
    }

    public static byte[] decompress(byte[] data) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream(2048);
        ByteArrayInputStream bin = new ByteArrayInputStream(data);
        GZIPInputStream gin = new GZIPInputStream(bin);
        byte[] tmp = new byte[2048];
        int length = gin.read(tmp);
        while (length > -1) {
            bout.write(tmp, 0, length);
            length = gin.read(tmp);
        }
        return bout.toByteArray();
    }

    @Override
    public void report() {
        log.info((Object)sm.getString("gzipInterceptor.report", this.getCount(), this.getCountCompressedTX(), this.getCountUncompressedTX(), this.getCountCompressedRX(), this.getCountUncompressedRX(), this.getSizeTX(), this.getCompressedSizeTX(), this.getUncompressedSizeTX(), this.getSizeRX(), this.getCompressedSizeRX(), this.getUncompressedSizeRX()));
    }

    @Override
    public int getCompressionMinSize() {
        return this.compressionMinSize;
    }

    @Override
    public void setCompressionMinSize(int compressionMinSize) {
        this.compressionMinSize = compressionMinSize;
    }

    @Override
    public boolean getStatsEnabled() {
        return this.statsEnabled;
    }

    @Override
    public void setStatsEnabled(boolean statsEnabled) {
        this.statsEnabled = statsEnabled;
    }

    @Override
    public int getInterval() {
        return this.interval;
    }

    @Override
    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public int getCount() {
        return this.count.get();
    }

    @Override
    public int getCountCompressedTX() {
        return this.countCompressedTX.get();
    }

    @Override
    public int getCountUncompressedTX() {
        return this.countUncompressedTX.get();
    }

    @Override
    public int getCountCompressedRX() {
        return this.countCompressedRX.get();
    }

    @Override
    public int getCountUncompressedRX() {
        return this.countUncompressedRX.get();
    }

    @Override
    public long getSizeTX() {
        return this.sizeTX.get();
    }

    @Override
    public long getCompressedSizeTX() {
        return this.compressedSizeTX.get();
    }

    @Override
    public long getUncompressedSizeTX() {
        return this.uncompressedSizeTX.get();
    }

    @Override
    public long getSizeRX() {
        return this.sizeRX.get();
    }

    @Override
    public long getCompressedSizeRX() {
        return this.compressedSizeRX.get();
    }

    @Override
    public long getUncompressedSizeRX() {
        return this.uncompressedSizeRX.get();
    }

    @Override
    public void reset() {
        this.count.set(0);
        this.countCompressedTX.set(0);
        this.countUncompressedTX.set(0);
        this.countCompressedRX.set(0);
        this.countUncompressedRX.set(0);
        this.sizeTX.set(0L);
        this.compressedSizeTX.set(0L);
        this.uncompressedSizeTX.set(0L);
        this.sizeRX.set(0L);
        this.compressedSizeRX.set(0L);
        this.uncompressedSizeRX.set(0L);
    }
}

