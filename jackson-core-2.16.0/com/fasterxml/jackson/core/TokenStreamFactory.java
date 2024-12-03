/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.core;

import com.fasterxml.jackson.core.FormatFeature;
import com.fasterxml.jackson.core.FormatSchema;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.core.StreamWriteConstraints;
import com.fasterxml.jackson.core.StreamWriteFeature;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.core.io.DataOutputAsStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.net.URL;

public abstract class TokenStreamFactory
implements Versioned,
Serializable {
    private static final long serialVersionUID = 2L;

    public abstract boolean requiresPropertyOrdering();

    public abstract boolean canHandleBinaryNatively();

    public abstract boolean canParseAsync();

    public abstract Class<? extends FormatFeature> getFormatReadFeatureType();

    public abstract Class<? extends FormatFeature> getFormatWriteFeatureType();

    public abstract boolean canUseSchema(FormatSchema var1);

    public abstract String getFormatName();

    public abstract boolean isEnabled(JsonFactory.Feature var1);

    public abstract boolean isEnabled(StreamReadFeature var1);

    public abstract boolean isEnabled(StreamWriteFeature var1);

    public abstract boolean isEnabled(JsonParser.Feature var1);

    public abstract boolean isEnabled(JsonGenerator.Feature var1);

    public abstract int getFactoryFeatures();

    public abstract int getParserFeatures();

    public abstract int getGeneratorFeatures();

    public abstract int getFormatParserFeatures();

    public abstract int getFormatGeneratorFeatures();

    public abstract StreamReadConstraints streamReadConstraints();

    public abstract StreamWriteConstraints streamWriteConstraints();

    public abstract JsonParser createParser(byte[] var1) throws IOException;

    public abstract JsonParser createParser(byte[] var1, int var2, int var3) throws IOException;

    public abstract JsonParser createParser(char[] var1) throws IOException;

    public abstract JsonParser createParser(char[] var1, int var2, int var3) throws IOException;

    public abstract JsonParser createParser(DataInput var1) throws IOException;

    public abstract JsonParser createParser(File var1) throws IOException;

    public abstract JsonParser createParser(InputStream var1) throws IOException;

    public abstract JsonParser createParser(Reader var1) throws IOException;

    public abstract JsonParser createParser(String var1) throws IOException;

    public abstract JsonParser createParser(URL var1) throws IOException;

    public abstract JsonParser createNonBlockingByteArrayParser() throws IOException;

    public abstract JsonParser createNonBlockingByteBufferParser() throws IOException;

    public abstract JsonGenerator createGenerator(DataOutput var1, JsonEncoding var2) throws IOException;

    public abstract JsonGenerator createGenerator(DataOutput var1) throws IOException;

    public abstract JsonGenerator createGenerator(File var1, JsonEncoding var2) throws IOException;

    public abstract JsonGenerator createGenerator(OutputStream var1) throws IOException;

    public abstract JsonGenerator createGenerator(OutputStream var1, JsonEncoding var2) throws IOException;

    public abstract JsonGenerator createGenerator(Writer var1) throws IOException;

    protected OutputStream _createDataOutputWrapper(DataOutput out) {
        return new DataOutputAsStream(out);
    }

    protected InputStream _optimizedStreamFromURL(URL url) throws IOException {
        String path;
        String host;
        if ("file".equals(url.getProtocol()) && ((host = url.getHost()) == null || host.length() == 0) && (path = url.getPath()).indexOf(37) < 0) {
            return new FileInputStream(url.getPath());
        }
        return url.openStream();
    }

    protected InputStream _fileInputStream(File f) throws IOException {
        return new FileInputStream(f);
    }

    protected OutputStream _fileOutputStream(File f) throws IOException {
        return new FileOutputStream(f);
    }

    protected void _checkRangeBoundsForByteArray(byte[] data, int offset, int len) throws IllegalArgumentException {
        int dataLen;
        int end;
        int anyNegs;
        if (data == null) {
            this._reportRangeError("Invalid `byte[]` argument: `null`");
        }
        if ((anyNegs = offset | len | (end = offset + len) | (dataLen = data.length) - end) < 0) {
            this._reportRangeError(String.format("Invalid 'offset' (%d) and/or 'len' (%d) arguments for `byte[]` of length %d", offset, len, dataLen));
        }
    }

    protected void _checkRangeBoundsForCharArray(char[] data, int offset, int len) throws IOException {
        int dataLen;
        int end;
        int anyNegs;
        if (data == null) {
            this._reportRangeError("Invalid `char[]` argument: `null`");
        }
        if ((anyNegs = offset | len | (end = offset + len) | (dataLen = data.length) - end) < 0) {
            this._reportRangeError(String.format("Invalid 'offset' (%d) and/or 'len' (%d) arguments for `char[]` of length %d", offset, len, dataLen));
        }
    }

    protected <T> T _reportRangeError(String msg) throws IllegalArgumentException {
        throw new IllegalArgumentException(msg);
    }
}

