/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.thirdparty.jackson.core.JsonFactory
 *  software.amazon.awssdk.thirdparty.jackson.core.JsonGenerator
 *  software.amazon.awssdk.utils.BinaryUtils
 *  software.amazon.awssdk.utils.DateUtils
 */
package software.amazon.awssdk.protocols.json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.time.Instant;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.protocols.json.StructuredJsonGenerator;
import software.amazon.awssdk.thirdparty.jackson.core.JsonFactory;
import software.amazon.awssdk.thirdparty.jackson.core.JsonGenerator;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.DateUtils;

@SdkProtectedApi
public class SdkJsonGenerator
implements StructuredJsonGenerator {
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private final ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
    private final JsonGenerator generator;
    private final String contentType;

    public SdkJsonGenerator(JsonFactory factory, String contentType) {
        try {
            this.generator = factory.createGenerator((OutputStream)this.baos);
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
            this.generator.writeBinary(BinaryUtils.copyBytesFrom((ByteBuffer)bytes));
        }
        catch (IOException e) {
            throw new JsonGenerationException(e);
        }
        return this;
    }

    @Override
    public StructuredJsonGenerator writeValue(Instant instant) {
        try {
            this.generator.writeNumber(DateUtils.formatUnixTimestampInstant((Instant)instant));
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

    @Override
    public StructuredJsonGenerator writeNumber(String number) {
        try {
            this.generator.writeNumber(number);
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
            super(SdkClientException.builder().cause(t));
        }
    }
}

