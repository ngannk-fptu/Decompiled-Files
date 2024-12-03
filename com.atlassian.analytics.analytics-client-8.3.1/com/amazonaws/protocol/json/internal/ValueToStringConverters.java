/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.protocol.json.internal;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.util.Base64;
import com.amazonaws.util.StringUtils;
import java.nio.charset.Charset;
import java.util.Date;

@SdkInternalApi
public class ValueToStringConverters {
    public static final ValueToString<String> FROM_STRING = new ValueToString<String>(){

        @Override
        public String convert(String val) {
            return val;
        }
    };
    public static final ValueToString<Integer> FROM_INTEGER = new ValueToString<Integer>(){

        @Override
        public String convert(Integer val) {
            return StringUtils.fromInteger(val);
        }
    };
    public static final ValueToString<Long> FROM_LONG = new ValueToString<Long>(){

        @Override
        public String convert(Long val) {
            return StringUtils.fromLong(val);
        }
    };
    public static final ValueToString<Short> FROM_SHORT = new ValueToString<Short>(){

        @Override
        public String convert(Short val) {
            return StringUtils.fromShort(val);
        }
    };
    public static final ValueToString<Float> FROM_FLOAT = new ValueToString<Float>(){

        @Override
        public String convert(Float val) {
            return StringUtils.fromFloat(val);
        }
    };
    public static final ValueToString<Double> FROM_DOUBLE = new ValueToString<Double>(){

        @Override
        public String convert(Double val) {
            return StringUtils.fromDouble(val);
        }
    };
    public static final ValueToString<Boolean> FROM_BOOLEAN = new ValueToString<Boolean>(){

        @Override
        public String convert(Boolean val) {
            return StringUtils.fromBoolean(val);
        }
    };
    public static final ValueToString<Date> FROM_DATE = new ValueToString<Date>(){

        @Override
        public String convert(Date val) {
            return StringUtils.fromDate(val);
        }
    };
    public static final ValueToString<String> FROM_JSON_VALUE_HEADER = new ValueToString<String>(){

        @Override
        public String convert(String val) {
            return Base64.encodeAsString(val.getBytes(Charset.forName("utf-8")));
        }
    };

    public static interface ValueToString<T> {
        public String convert(T var1);
    }
}

