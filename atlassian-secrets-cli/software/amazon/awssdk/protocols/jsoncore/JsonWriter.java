/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.protocols.jsoncore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.time.Instant;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.protocols.jsoncore.JsonNodeParser;
import software.amazon.awssdk.thirdparty.jackson.core.JsonFactory;
import software.amazon.awssdk.thirdparty.jackson.core.JsonGenerator;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.DateUtils;
import software.amazon.awssdk.utils.FunctionalUtils;
import software.amazon.awssdk.utils.SdkAutoCloseable;

@SdkProtectedApi
public class JsonWriter
implements SdkAutoCloseable {
    private static final int DEFAULT_BUFFER_SIZE = 1024;
    private final ByteArrayOutputStream baos;
    private final JsonGenerator generator;

    private JsonWriter(Builder builder) {
        JsonGeneratorFactory jsonGeneratorFactory = builder.jsonGeneratorFactory != null ? builder.jsonGeneratorFactory : JsonNodeParser.DEFAULT_JSON_FACTORY::createGenerator;
        try {
            this.baos = new ByteArrayOutputStream(1024);
            this.generator = jsonGeneratorFactory.createGenerator(this.baos);
        }
        catch (IOException e) {
            throw new JsonGenerationException(e);
        }
    }

    public static JsonWriter create() {
        return JsonWriter.builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public JsonWriter writeStartArray() {
        return this.unsafeWrite(this.generator::writeStartArray);
    }

    public JsonWriter writeEndArray() {
        return this.unsafeWrite(this.generator::writeEndArray);
    }

    public JsonWriter writeNull() {
        return this.unsafeWrite(this.generator::writeEndArray);
    }

    public JsonWriter writeStartObject() {
        return this.unsafeWrite(this.generator::writeStartObject);
    }

    public JsonWriter writeEndObject() {
        return this.unsafeWrite(this.generator::writeEndObject);
    }

    public JsonWriter writeFieldName(String fieldName) {
        return this.unsafeWrite(() -> this.generator.writeFieldName(fieldName));
    }

    public JsonWriter writeValue(String val) {
        return this.unsafeWrite(() -> this.generator.writeString(val));
    }

    public JsonWriter writeValue(boolean bool) {
        return this.unsafeWrite(() -> this.generator.writeBoolean(bool));
    }

    public JsonWriter writeValue(long val) {
        return this.unsafeWrite(() -> this.generator.writeNumber(val));
    }

    public JsonWriter writeValue(double val) {
        return this.unsafeWrite(() -> this.generator.writeNumber(val));
    }

    public JsonWriter writeValue(float val) {
        return this.unsafeWrite(() -> this.generator.writeNumber(val));
    }

    public JsonWriter writeValue(short val) {
        return this.unsafeWrite(() -> this.generator.writeNumber(val));
    }

    public JsonWriter writeValue(int val) {
        return this.unsafeWrite(() -> this.generator.writeNumber(val));
    }

    public JsonWriter writeValue(ByteBuffer bytes) {
        return this.unsafeWrite(() -> this.generator.writeBinary(BinaryUtils.copyBytesFrom(bytes)));
    }

    public JsonWriter writeValue(Instant instant) {
        return this.unsafeWrite(() -> this.generator.writeNumber(DateUtils.formatUnixTimestampInstant(instant)));
    }

    public JsonWriter writeValue(BigDecimal value) {
        return this.unsafeWrite(() -> this.generator.writeString(value.toString()));
    }

    public JsonWriter writeValue(BigInteger value) {
        return this.unsafeWrite(() -> this.generator.writeNumber(value));
    }

    public JsonWriter writeNumber(String number) {
        return this.unsafeWrite(() -> this.generator.writeNumber(number));
    }

    @Override
    public void close() {
        try {
            this.generator.close();
        }
        catch (IOException e) {
            throw new JsonGenerationException(e);
        }
    }

    public byte[] getBytes() {
        this.close();
        return this.baos.toByteArray();
    }

    private JsonWriter unsafeWrite(FunctionalUtils.UnsafeRunnable r) {
        try {
            r.run();
        }
        catch (Exception e) {
            throw new JsonGenerationException(e);
        }
        return this;
    }

    public static class JsonGenerationException
    extends RuntimeException {
        public JsonGenerationException(Throwable t) {
            super(t);
        }
    }

    @FunctionalInterface
    public static interface JsonGeneratorFactory {
        public JsonGenerator createGenerator(OutputStream var1) throws IOException;
    }

    public static final class Builder {
        private JsonGeneratorFactory jsonGeneratorFactory;

        private Builder() {
        }

        public Builder jsonFactory(JsonFactory jsonFactory) {
            this.jsonGeneratorFactory(jsonFactory::createGenerator);
            return this;
        }

        public Builder jsonGeneratorFactory(JsonGeneratorFactory jsonGeneratorFactory) {
            this.jsonGeneratorFactory = jsonGeneratorFactory;
            return this;
        }

        public JsonWriter build() {
            return new JsonWriter(this);
        }
    }
}

