/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.json.JsonNumber;
import javax.json.JsonValue;

abstract class JsonNumberImpl
implements JsonNumber {
    JsonNumberImpl() {
    }

    static JsonNumber getJsonNumber(int num) {
        return new JsonIntNumber(num);
    }

    static JsonNumber getJsonNumber(long num) {
        return new JsonLongNumber(num);
    }

    static JsonNumber getJsonNumber(BigInteger value) {
        return new JsonBigDecimalNumber(new BigDecimal(value));
    }

    static JsonNumber getJsonNumber(double value) {
        return new JsonBigDecimalNumber(BigDecimal.valueOf(value));
    }

    static JsonNumber getJsonNumber(BigDecimal value) {
        return new JsonBigDecimalNumber(value);
    }

    @Override
    public boolean isIntegral() {
        return this.bigDecimalValue().scale() == 0;
    }

    @Override
    public int intValue() {
        return this.bigDecimalValue().intValue();
    }

    @Override
    public int intValueExact() {
        return this.bigDecimalValue().intValueExact();
    }

    @Override
    public long longValue() {
        return this.bigDecimalValue().longValue();
    }

    @Override
    public long longValueExact() {
        return this.bigDecimalValue().longValueExact();
    }

    @Override
    public double doubleValue() {
        return this.bigDecimalValue().doubleValue();
    }

    @Override
    public BigInteger bigIntegerValue() {
        return this.bigDecimalValue().toBigInteger();
    }

    @Override
    public BigInteger bigIntegerValueExact() {
        return this.bigDecimalValue().toBigIntegerExact();
    }

    @Override
    public JsonValue.ValueType getValueType() {
        return JsonValue.ValueType.NUMBER;
    }

    @Override
    public int hashCode() {
        return this.bigDecimalValue().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof JsonNumber)) {
            return false;
        }
        JsonNumber other = (JsonNumber)obj;
        return this.bigDecimalValue().equals(other.bigDecimalValue());
    }

    @Override
    public String toString() {
        return this.bigDecimalValue().toString();
    }

    private static final class JsonBigDecimalNumber
    extends JsonNumberImpl {
        private final BigDecimal bigDecimal;

        JsonBigDecimalNumber(BigDecimal value) {
            this.bigDecimal = value;
        }

        @Override
        public BigDecimal bigDecimalValue() {
            return this.bigDecimal;
        }

        @Override
        public Number numberValue() {
            return this.bigDecimalValue();
        }
    }

    private static final class JsonLongNumber
    extends JsonNumberImpl {
        private final long num;
        private BigDecimal bigDecimal;

        JsonLongNumber(long num) {
            this.num = num;
        }

        @Override
        public boolean isIntegral() {
            return true;
        }

        @Override
        public int intValue() {
            return (int)this.num;
        }

        @Override
        public int intValueExact() {
            return Math.toIntExact(this.num);
        }

        @Override
        public long longValue() {
            return this.num;
        }

        @Override
        public long longValueExact() {
            return this.num;
        }

        @Override
        public double doubleValue() {
            return this.num;
        }

        @Override
        public BigDecimal bigDecimalValue() {
            BigDecimal bd = this.bigDecimal;
            if (bd == null) {
                this.bigDecimal = bd = new BigDecimal(this.num);
            }
            return bd;
        }

        @Override
        public Number numberValue() {
            return this.num;
        }

        @Override
        public String toString() {
            return Long.toString(this.num);
        }
    }

    private static final class JsonIntNumber
    extends JsonNumberImpl {
        private final int num;
        private BigDecimal bigDecimal;

        JsonIntNumber(int num) {
            this.num = num;
        }

        @Override
        public boolean isIntegral() {
            return true;
        }

        @Override
        public int intValue() {
            return this.num;
        }

        @Override
        public int intValueExact() {
            return this.num;
        }

        @Override
        public long longValue() {
            return this.num;
        }

        @Override
        public long longValueExact() {
            return this.num;
        }

        @Override
        public double doubleValue() {
            return this.num;
        }

        @Override
        public BigDecimal bigDecimalValue() {
            BigDecimal bd = this.bigDecimal;
            if (bd == null) {
                this.bigDecimal = bd = new BigDecimal(this.num);
            }
            return bd;
        }

        @Override
        public Number numberValue() {
            return this.num;
        }

        @Override
        public String toString() {
            return Integer.toString(this.num);
        }
    }
}

