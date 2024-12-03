/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.protocol.json;

import com.amazonaws.SdkClientException;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.protocol.json.StructuredJsonGenerator;
import com.amazonaws.util.BinaryUtils;
import com.amazonaws.util.TimestampFormat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Date;
import software.amazon.ion.IonType;
import software.amazon.ion.IonWriter;
import software.amazon.ion.Timestamp;
import software.amazon.ion.system.IonWriterBuilder;

@SdkInternalApi
abstract class SdkIonGenerator
implements StructuredJsonGenerator {
    private final String contentType;
    protected final IonWriter writer;

    private SdkIonGenerator(IonWriter writer, String contentType) {
        this.writer = writer;
        this.contentType = contentType;
    }

    @Override
    public StructuredJsonGenerator writeStartArray() {
        try {
            this.writer.stepIn(IonType.LIST);
        }
        catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeNull() {
        try {
            this.writer.writeNull();
        }
        catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeEndArray() {
        try {
            this.writer.stepOut();
        }
        catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeStartObject() {
        try {
            this.writer.stepIn(IonType.STRUCT);
        }
        catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeEndObject() {
        try {
            this.writer.stepOut();
        }
        catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeFieldName(String fieldName) {
        this.writer.setFieldName(fieldName);
        return this;
    }

    @Override
    public StructuredJsonGenerator writeValue(String val) {
        try {
            this.writer.writeString(val);
        }
        catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeValue(boolean bool) {
        try {
            this.writer.writeBool(bool);
        }
        catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeValue(long val) {
        try {
            this.writer.writeInt(val);
        }
        catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeValue(double val) {
        try {
            this.writer.writeFloat(val);
        }
        catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeValue(float val) {
        try {
            this.writer.writeFloat(val);
        }
        catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeValue(short val) {
        try {
            this.writer.writeInt(val);
        }
        catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeValue(int val) {
        try {
            this.writer.writeInt(val);
        }
        catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeValue(ByteBuffer bytes) {
        try {
            this.writer.writeBlob(BinaryUtils.copyAllBytesFrom(bytes));
        }
        catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeValue(Date date, TimestampFormat timestampFormat) {
        try {
            this.writer.writeTimestamp(Timestamp.forDateZ(date));
        }
        catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeValue(BigDecimal value) {
        try {
            this.writer.writeDecimal(value);
        }
        catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeValue(BigInteger value) {
        try {
            this.writer.writeInt(value);
        }
        catch (IOException e) {
            throw new SdkClientException(e);
        }
        return this;
    }

    @Override
    public abstract byte[] getBytes();

    @Override
    public String getContentType() {
        return this.contentType;
    }

    public static SdkIonGenerator create(IonWriterBuilder builder, String contentType) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        IonWriter writer = builder.build(bytes);
        return new ByteArraySdkIonGenerator(bytes, writer, contentType);
    }

    private static class ByteArraySdkIonGenerator
    extends SdkIonGenerator {
        private final ByteArrayOutputStream bytes;

        public ByteArraySdkIonGenerator(ByteArrayOutputStream bytes, IonWriter writer, String contentType) {
            super(writer, contentType);
            this.bytes = bytes;
        }

        @Override
        public byte[] getBytes() {
            try {
                this.writer.finish();
            }
            catch (IOException e) {
                throw new SdkClientException(e);
            }
            return this.bytes.toByteArray();
        }
    }
}

