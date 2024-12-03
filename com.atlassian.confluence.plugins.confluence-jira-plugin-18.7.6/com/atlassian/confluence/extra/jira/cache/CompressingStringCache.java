/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.jira.cache;

import com.atlassian.confluence.extra.jira.cache.SimpleStringCache;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompressingStringCache
implements SimpleStringCache {
    private static final Logger log = LoggerFactory.getLogger(CompressingStringCache.class);
    private final ConcurrentHashMap wrappedCache;

    public CompressingStringCache(ConcurrentHashMap wrappedCache) {
        this.wrappedCache = wrappedCache;
    }

    @Override
    public void put(Object key, String value) {
        byte[] stringBytes = value.getBytes(StandardCharsets.UTF_8);
        if (log.isDebugEnabled()) {
            log.debug("compressing [ " + stringBytes.length + " ] bytes for storage in the cache");
        }
        long start = System.currentTimeMillis();
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        GZIPOutputStream out = null;
        try {
            out = new GZIPOutputStream(buf);
            out.write(stringBytes, 0, stringBytes.length);
            out.finish();
            out.flush();
            out.close();
        }
        catch (IOException ex) {
            try {
                throw new RuntimeException("Exception while compressing cache content", ex);
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(out);
                throw throwable;
            }
        }
        IOUtils.closeQuietly((OutputStream)out);
        byte[] data = buf.toByteArray();
        if (log.isDebugEnabled()) {
            log.debug(System.currentTimeMillis() - start + ": compressed to [ " + data.length + " ]");
        }
        this.wrappedCache.put(key, data);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String get(Object key) {
        GZIPInputStream in = null;
        try {
            byte[] data = (byte[])this.wrappedCache.get(key);
            if (data == null) {
                String string = null;
                return string;
            }
            if (log.isDebugEnabled()) {
                log.debug("decompressing [ " + data.length + " ] bytes into html");
            }
            long start = System.currentTimeMillis();
            ByteArrayInputStream bin = new ByteArrayInputStream(data);
            in = new GZIPInputStream(bin);
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            IOUtils.copy((InputStream)in, (OutputStream)buf);
            byte[] uncompressedData = buf.toByteArray();
            if (log.isDebugEnabled()) {
                log.debug(System.currentTimeMillis() - start + ": decompressed to [ " + uncompressedData.length + " ]");
            }
            String string = new String(uncompressedData, StandardCharsets.UTF_8);
            IOUtils.closeQuietly((InputStream)in);
            return string;
        }
        catch (IOException e) {
            log.debug("Exception while uncompressing cache data", (Throwable)e);
            String string = null;
            return string;
        }
        finally {
            IOUtils.closeQuietly(in);
        }
    }

    @Override
    public void remove(Object key) {
        this.wrappedCache.remove(key);
    }
}

