/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.xerial.snappy.Snappy
 */
package com.atlassian.confluence.internal.util.io;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.util.io.DataCompressor;
import com.atlassian.confluence.util.io.InputStreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xerial.snappy.Snappy;

@ParametersAreNonnullByDefault
public class SnappyDataCompressor
implements DataCompressor {
    private static final Logger log = LoggerFactory.getLogger(SnappyDataCompressor.class);

    @Override
    public InputStreamSource uncompress(InputStreamSource compressed) {
        return () -> {
            try (InputStream is = compressed.getInputStream();){
                byte[] buffer = IOUtils.toByteArray((InputStream)is);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.uncompress(buffer));
                return byteArrayInputStream;
            }
        };
    }

    @Override
    public InputStreamSource compress(InputStreamSource uncompressed) {
        return () -> {
            try (InputStream is = uncompressed.getInputStream();){
                byte[] buffer = IOUtils.toByteArray((InputStream)is);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.compress(buffer));
                return byteArrayInputStream;
            }
        };
    }

    @Override
    public byte[] uncompress(byte[] buffer) {
        try {
            return Snappy.uncompress((byte[])buffer);
        }
        catch (IOException e) {
            log.error("Can't uncompress a byte array");
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] compress(byte[] buffer) {
        try {
            return Snappy.compress((byte[])buffer);
        }
        catch (IOException e) {
            log.error("Can't compress a byte array");
            throw new RuntimeException(e);
        }
    }
}

