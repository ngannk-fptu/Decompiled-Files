/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  org.apache.commons.lang3.SystemUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.util.io;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.internal.util.io.SnappyDataCompressor;
import com.atlassian.confluence.internal.util.io.ZipDataCompressor;
import com.atlassian.confluence.util.io.DataCompressor;
import com.atlassian.confluence.util.io.InputStreamSource;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class DefaultDataCompressor
implements DataCompressor {
    private static final Logger log = LoggerFactory.getLogger(DefaultDataCompressor.class);
    private DataCompressor delegate;

    public DefaultDataCompressor() {
        if (SystemUtils.IS_OS_SOLARIS || SystemUtils.IS_OS_SUN_OS) {
            log.debug("Snappy compression is not available for Sun Solaris/Sun OS, switch to zip");
            this.delegate = new ZipDataCompressor();
        } else {
            this.delegate = new SnappyDataCompressor();
        }
    }

    @Override
    public InputStreamSource uncompress(InputStreamSource compressed) {
        return this.delegate.uncompress(compressed);
    }

    @Override
    public InputStreamSource compress(InputStreamSource uncompressed) {
        return this.delegate.compress(uncompressed);
    }

    @Override
    public byte[] uncompress(byte[] buffer) {
        return this.delegate.uncompress(buffer);
    }

    @Override
    public byte[] compress(byte[] buffer) {
        return this.delegate.compress(buffer);
    }
}

