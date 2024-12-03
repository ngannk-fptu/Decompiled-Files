/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.transform;

import com.amazonaws.transform.Unmarshaller;
import com.amazonaws.util.XpathUtils;
import java.nio.ByteBuffer;
import java.util.Date;
import org.w3c.dom.Node;

public class SimpleTypeUnmarshallers {

    public static class ByteBufferUnmarshaller
    implements Unmarshaller<ByteBuffer, Node> {
        private static ByteBufferUnmarshaller instance;

        @Override
        public ByteBuffer unmarshall(Node in) throws Exception {
            return XpathUtils.asByteBuffer(".", in);
        }

        public static ByteBufferUnmarshaller getInstance() {
            if (instance == null) {
                instance = new ByteBufferUnmarshaller();
            }
            return instance;
        }
    }

    public static class DateUnmarshaller
    implements Unmarshaller<Date, Node> {
        private static DateUnmarshaller instance;

        @Override
        public Date unmarshall(Node in) throws Exception {
            return XpathUtils.asDate(".", in);
        }

        public static DateUnmarshaller getInstance() {
            if (instance == null) {
                instance = new DateUnmarshaller();
            }
            return instance;
        }
    }

    public static class ByteUnmarshaller
    implements Unmarshaller<Byte, Node> {
        private static ByteUnmarshaller instance;

        @Override
        public Byte unmarshall(Node in) throws Exception {
            return XpathUtils.asByte(".", in);
        }

        public static ByteUnmarshaller getInstance() {
            if (instance == null) {
                instance = new ByteUnmarshaller();
            }
            return instance;
        }
    }

    public static class LongUnmarshaller
    implements Unmarshaller<Long, Node> {
        private static LongUnmarshaller instance;

        @Override
        public Long unmarshall(Node in) throws Exception {
            return XpathUtils.asLong(".", in);
        }

        public static LongUnmarshaller getInstance() {
            if (instance == null) {
                instance = new LongUnmarshaller();
            }
            return instance;
        }
    }

    public static class FloatUnmarshaller
    implements Unmarshaller<Float, Node> {
        private static FloatUnmarshaller instance;

        @Override
        public Float unmarshall(Node in) throws Exception {
            return XpathUtils.asFloat(".", in);
        }

        public static FloatUnmarshaller getInstance() {
            if (instance == null) {
                instance = new FloatUnmarshaller();
            }
            return instance;
        }
    }

    public static class BooleanUnmarshaller
    implements Unmarshaller<Boolean, Node> {
        private static BooleanUnmarshaller instance;

        @Override
        public Boolean unmarshall(Node in) throws Exception {
            return XpathUtils.asBoolean(".", in);
        }

        public static BooleanUnmarshaller getInstance() {
            if (instance == null) {
                instance = new BooleanUnmarshaller();
            }
            return instance;
        }
    }

    public static class IntegerUnmarshaller
    implements Unmarshaller<Integer, Node> {
        private static IntegerUnmarshaller instance;

        @Override
        public Integer unmarshall(Node in) throws Exception {
            return XpathUtils.asInteger(".", in);
        }

        public static IntegerUnmarshaller getInstance() {
            if (instance == null) {
                instance = new IntegerUnmarshaller();
            }
            return instance;
        }
    }

    public static class DoubleUnmarshaller
    implements Unmarshaller<Double, Node> {
        private static DoubleUnmarshaller instance;

        @Override
        public Double unmarshall(Node in) throws Exception {
            return XpathUtils.asDouble(".", in);
        }

        public static DoubleUnmarshaller getInstance() {
            if (instance == null) {
                instance = new DoubleUnmarshaller();
            }
            return instance;
        }
    }

    public static class StringUnmarshaller
    implements Unmarshaller<String, Node> {
        private static StringUnmarshaller instance;

        @Override
        public String unmarshall(Node in) throws Exception {
            return XpathUtils.asString(".", in);
        }

        public static StringUnmarshaller getInstance() {
            if (instance == null) {
                instance = new StringUnmarshaller();
            }
            return instance;
        }
    }
}

