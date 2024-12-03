/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.protocols.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.time.Instant;
import software.amazon.awssdk.annotations.SdkProtectedApi;

@SdkProtectedApi
public interface StructuredJsonGenerator {
    public static final StructuredJsonGenerator NO_OP = new StructuredJsonGenerator(){

        @Override
        public StructuredJsonGenerator writeStartArray() {
            return this;
        }

        @Override
        public StructuredJsonGenerator writeEndArray() {
            return this;
        }

        @Override
        public StructuredJsonGenerator writeNull() {
            return this;
        }

        @Override
        public StructuredJsonGenerator writeStartObject() {
            return this;
        }

        @Override
        public StructuredJsonGenerator writeEndObject() {
            return this;
        }

        @Override
        public StructuredJsonGenerator writeFieldName(String fieldName) {
            return this;
        }

        @Override
        public StructuredJsonGenerator writeValue(String val) {
            return this;
        }

        @Override
        public StructuredJsonGenerator writeValue(boolean bool) {
            return this;
        }

        @Override
        public StructuredJsonGenerator writeValue(long val) {
            return this;
        }

        @Override
        public StructuredJsonGenerator writeValue(double val) {
            return this;
        }

        @Override
        public StructuredJsonGenerator writeValue(float val) {
            return this;
        }

        @Override
        public StructuredJsonGenerator writeValue(short val) {
            return this;
        }

        @Override
        public StructuredJsonGenerator writeValue(int val) {
            return this;
        }

        @Override
        public StructuredJsonGenerator writeValue(ByteBuffer bytes) {
            return this;
        }

        @Override
        public StructuredJsonGenerator writeValue(Instant instant) {
            return this;
        }

        @Override
        public StructuredJsonGenerator writeValue(BigDecimal value) {
            return this;
        }

        @Override
        public StructuredJsonGenerator writeValue(BigInteger value) {
            return this;
        }

        @Override
        public StructuredJsonGenerator writeNumber(String number) {
            return this;
        }

        @Override
        public byte[] getBytes() {
            return null;
        }

        @Override
        public String getContentType() {
            return null;
        }
    };

    public StructuredJsonGenerator writeStartArray();

    public StructuredJsonGenerator writeEndArray();

    public StructuredJsonGenerator writeNull();

    public StructuredJsonGenerator writeStartObject();

    public StructuredJsonGenerator writeEndObject();

    public StructuredJsonGenerator writeFieldName(String var1);

    public StructuredJsonGenerator writeValue(String var1);

    public StructuredJsonGenerator writeValue(boolean var1);

    public StructuredJsonGenerator writeValue(long var1);

    public StructuredJsonGenerator writeValue(double var1);

    public StructuredJsonGenerator writeValue(float var1);

    public StructuredJsonGenerator writeValue(short var1);

    public StructuredJsonGenerator writeValue(int var1);

    public StructuredJsonGenerator writeValue(ByteBuffer var1);

    public StructuredJsonGenerator writeValue(Instant var1);

    public StructuredJsonGenerator writeNumber(String var1);

    public StructuredJsonGenerator writeValue(BigDecimal var1);

    public StructuredJsonGenerator writeValue(BigInteger var1);

    public byte[] getBytes();

    @Deprecated
    public String getContentType();
}

