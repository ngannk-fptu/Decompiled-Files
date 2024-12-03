/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.text.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;

public class CompressionUtil {
    public static CompressionCodec getCodec(String name) {
        CompressionCodec codec = null;
        if (name == null) {
            return null;
        }
        try {
            codec = CompressionCodec.valueOf(name.toUpperCase(Locale.ENGLISH).trim());
        }
        catch (Exception exception) {
            // empty catch block
        }
        return codec;
    }

    public static OutputStream getEncodedOutputStream(OutputStream out, CompressionCodec encoding) throws IOException {
        return CompressionUtil.getEncodedOutputStream(out, new CompressionCodec[]{encoding});
    }

    public static OutputStream getEncodedOutputStream(OutputStream out, CompressionCodec ... encodings) throws IOException {
        block4: for (CompressionCodec encoding : encodings) {
            switch (encoding) {
                case GZIP: {
                    out = new GZIPOutputStream(out);
                    continue block4;
                }
                case DEFLATE: {
                    out = new DeflaterOutputStream(out);
                }
            }
        }
        return out;
    }

    public static InputStream getDecodingInputStream(InputStream in, CompressionCodec encoding) throws IOException {
        switch (encoding) {
            case GZIP: 
            case XGZIP: {
                in = new GZIPInputStream(in);
                break;
            }
            case DEFLATE: {
                in = new InflaterInputStream(in);
            }
        }
        return in;
    }

    public static InputStream getDecodingInputStream(InputStream in, CompressionCodec ... encoding) throws IOException {
        for (CompressionCodec codec : encoding) {
            in = CompressionUtil.getDecodingInputStream(in, codec);
        }
        return in;
    }

    public static InputStream getDecodingInputStream(InputStream in, String ce) throws IOException {
        String[] encodings = CompressionUtil.splitAndTrim(ce, ",", false);
        for (int n = encodings.length - 1; n >= 0; --n) {
            CompressionCodec encoding = CompressionCodec.value(encodings[n]);
            in = CompressionUtil.getDecodingInputStream(in, encoding);
        }
        return in;
    }

    private static String unquote(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        if (s.startsWith("\"")) {
            s = s.substring(1);
        }
        if (s.endsWith("\"")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    public static String[] splitAndTrim(String value, String delim, boolean unquote) {
        String[] headers = unquote ? CompressionUtil.unquote(value).split(delim) : value.split(delim);
        for (int n = 0; n < headers.length; ++n) {
            headers[n] = headers[n].trim();
        }
        return headers;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum CompressionCodec {
        GZIP,
        XGZIP,
        DEFLATE;


        public static CompressionCodec value(String encoding) {
            if (encoding == null) {
                throw new IllegalArgumentException();
            }
            return CompressionCodec.valueOf(encoding.toUpperCase(Locale.ENGLISH).replaceAll("-", ""));
        }
    }
}

