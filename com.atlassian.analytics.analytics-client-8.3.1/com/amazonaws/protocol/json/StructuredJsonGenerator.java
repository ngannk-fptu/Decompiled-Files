/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.protocol.json;

import com.amazonaws.annotation.SdkProtectedApi;
import com.amazonaws.util.TimestampFormat;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Date;

@SdkProtectedApi
public interface StructuredJsonGenerator {
    public static final StructuredJsonGenerator NO_OP = new StructuredJsonGenerator(){
        private final byte[] EMPTY_BYTES = new byte[0];

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
        public StructuredJsonGenerator writeValue(Date date, TimestampFormat timestampFormat) {
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
        public byte[] getBytes() {
            return this.EMPTY_BYTES;
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

    public StructuredJsonGenerator writeValue(Date var1, TimestampFormat var2);

    public StructuredJsonGenerator writeValue(BigDecimal var1);

    public StructuredJsonGenerator writeValue(BigInteger var1);

    public byte[] getBytes();

    @Deprecated
    public String getContentType();
}

