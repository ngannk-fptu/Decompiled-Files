/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.util.io;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.util.io.DataCompressor;
import com.atlassian.confluence.util.io.InputStreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DeflaterInputStream;
import java.util.zip.InflaterInputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class ZipDataCompressor
implements DataCompressor {
    private static final Logger log = LoggerFactory.getLogger(ZipDataCompressor.class);

    @Override
    public InputStreamSource uncompress(InputStreamSource compressed) {
        return () -> new InflaterInputStream(compressed.getInputStream());
    }

    @Override
    public InputStreamSource compress(InputStreamSource uncompressed) {
        return () -> new DeflaterInputStream(uncompressed.getInputStream());
    }

    @Override
    public byte[] uncompress(byte[] compressed) {
        try {
            return IOUtils.toByteArray((InputStream)new InflaterInputStream(new ByteArrayInputStream(compressed)));
        }
        catch (IOException e) {
            log.error("Can't uncompress a byte array");
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] compress(byte[] uncompressed) {
        try {
            return IOUtils.toByteArray((InputStream)new DeflaterInputStream(new ByteArrayInputStream(uncompressed)));
        }
        catch (IOException e) {
            log.error("Can't compress a byte array");
            throw new RuntimeException(e);
        }
    }
}

