/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.group.interceptors;

public interface GzipInterceptorMBean {
    public int getOptionFlag();

    public void setOptionFlag(int var1);

    public int getCompressionMinSize();

    public void setCompressionMinSize(int var1);

    public boolean getStatsEnabled();

    public void setStatsEnabled(boolean var1);

    public int getInterval();

    public void setInterval(int var1);

    public int getCount();

    public int getCountCompressedTX();

    public int getCountUncompressedTX();

    public int getCountCompressedRX();

    public int getCountUncompressedRX();

    public long getSizeTX();

    public long getCompressedSizeTX();

    public long getUncompressedSizeTX();

    public long getSizeRX();

    public long getCompressedSizeRX();

    public long getUncompressedSizeRX();

    public void reset();

    public void report();
}

