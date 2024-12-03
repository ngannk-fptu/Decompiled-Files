/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.protocol.json;

import com.amazonaws.SdkClientException;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.protocol.json.StructuredJsonGenerator;
import com.amazonaws.util.BinaryUtils;
import com.amazonaws.util.DateUtils;
import com.amazonaws.util.TimestampFormat;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Date;

@SdkInternalApi
public class SdkJsonGenerator
implements StructuredJsonGenerator {
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
    private final JsonGenerator generator;
    private final String contentType;

    public SdkJsonGenerator(JsonFactory factory, String contentType) {
        try {
            this.generator = factory.createGenerator(this.baos);
            this.contentType = contentType;
        }
        catch (IOException e) {
            throw new JsonGenerationException(e);
        }
    }

    @Override
    public StructuredJsonGenerator writeStartArray() {
        try {
            this.generator.writeStartArray();
        }
        catch (IOException e) {
            throw new JsonGenerationException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeEndArray() {
        try {
            this.generator.writeEndArray();
        }
        catch (IOException e) {
            throw new JsonGenerationException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeNull() {
        try {
            this.generator.writeNull();
        }
        catch (IOException e) {
            throw new JsonGenerationException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeStartObject() {
        try {
            this.generator.writeStartObject();
        }
        catch (IOException e) {
            throw new JsonGenerationException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeEndObject() {
        try {
            this.generator.writeEndObject();
        }
        catch (IOException e) {
            throw new JsonGenerationException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeFieldName(String fieldName) {
        try {
            this.generator.writeFieldName(fieldName);
        }
        catch (IOException e) {
            throw new JsonGenerationException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeValue(String val) {
        try {
            this.generator.writeString(val);
        }
        catch (IOException e) {
            throw new JsonGenerationException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeValue(boolean bool) {
        try {
            this.generator.writeBoolean(bool);
        }
        catch (IOException e) {
            throw new JsonGenerationException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeValue(long val) {
        try {
            this.generator.writeNumber(val);
        }
        catch (IOException e) {
            throw new JsonGenerationException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeValue(double val) {
        try {
            this.generator.writeNumber(val);
        }
        catch (IOException e) {
            throw new JsonGenerationException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeValue(float val) {
        try {
            this.generator.writeNumber(val);
        }
        catch (IOException e) {
            throw new JsonGenerationException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeValue(short val) {
        try {
            this.generator.writeNumber(val);
        }
        catch (IOException e) {
            throw new JsonGenerationException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeValue(int val) {
        try {
            this.generator.writeNumber(val);
        }
        catch (IOException e) {
            throw new JsonGenerationException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeValue(ByteBuffer bytes) {
        try {
            this.generator.writeBinary(BinaryUtils.copyBytesFrom(bytes));
        }
        catch (IOException e) {
            throw new JsonGenerationException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeValue(Date date, TimestampFormat timestampFormat) {
        try {
            switch (timestampFormat) {
                case UNIX_TIMESTAMP_IN_MILLIS: {
                    this.generator.writeNumber(DateUtils.formatUnixTimestampInMills(date));
                    break;
                }
                case ISO_8601: {
                    this.generator.writeString(DateUtils.formatISO8601Date(date));
                    break;
                }
                case RFC_822: {
                    this.generator.writeString(DateUtils.formatRFC822Date(date));
                    break;
                }
                default: {
                    this.generator.writeNumber(DateUtils.formatServiceSpecificDate(date));
                    break;
                }
            }
        }
        catch (IOException e) {
            throw new JsonGenerationException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeValue(BigDecimal value) {
        try {
            this.generator.writeString(value.toString());
        }
        catch (IOException e) {
            throw new JsonGenerationException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeValue(BigInteger value) {
        try {
            this.generator.writeNumber(value);
        }
        catch (IOException e) {
            throw new JsonGenerationException(e);
        }
        return this;
    }

    private void close() {
        try {
            this.generator.close();
        }
        catch (IOException e) {
            throw new JsonGenerationException(e);
        }
    }

    @Override
    public byte[] getBytes() {
        this.close();
        return this.baos.toByteArray();
    }

    @Override
    public String getContentType() {
        return this.contentType;
    }

    protected JsonGenerator getGenerator() {
        return this.generator;
    }

    public static class JsonGenerationException
    extends SdkClientException {
        public JsonGenerationException(Throwable t) {
            super(t);
        }
    }
}

