/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.util.sandbox;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.util.sandbox.SandboxSerializer;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@ExperimentalApi
public final class SandboxSerializers {
    private SandboxSerializers() {
    }

    public static SandboxSerializer<List<byte[]>> compositeByteArraySerializer() {
        return CompositeByteArraySerializer.instance;
    }

    public static SandboxSerializer<String> stringSerializer() {
        return StringSerializer.instance;
    }

    public static SandboxSerializer<Integer> intSerializer() {
        return IntSerializer.instance;
    }

    public static SandboxSerializer<Duration> durationSerializer() {
        return DurationSerializer.instance;
    }

    public static <T> List<T> of(T t1, T t2) {
        ArrayList<T> result = new ArrayList<T>(2);
        result.add(t1);
        result.add(t2);
        return result;
    }

    public static final class CompositeByteArraySerializer
    implements SandboxSerializer<List<byte[]>> {
        static final CompositeByteArraySerializer instance = new CompositeByteArraySerializer();

        private CompositeByteArraySerializer() {
        }

        @Override
        public byte[] serialize(List<byte[]> list) {
            int size = list.stream().mapToInt(x -> 4 + ((byte[])x).length).sum();
            ByteBuffer buffer = ByteBuffer.allocate(size);
            list.forEach(x -> buffer.putInt(((byte[])x).length).put((byte[])x));
            return buffer.array();
        }

        @Override
        public List<byte[]> deserialize(byte[] bytes) {
            ArrayList<byte[]> composite = new ArrayList<byte[]>();
            ByteBuffer buffer = ByteBuffer.wrap(bytes, 0, bytes.length);
            while (buffer.remaining() > 0) {
                int length = buffer.getInt();
                byte[] component = new byte[length];
                buffer.get(component);
                composite.add(component);
            }
            return composite;
        }
    }

    public static final class DurationSerializer
    implements SandboxSerializer<Duration> {
        static final DurationSerializer instance = new DurationSerializer();

        private DurationSerializer() {
        }

        @Override
        public byte[] serialize(Duration duration) {
            return ByteBuffer.allocate(8).putLong(duration.toNanos()).array();
        }

        @Override
        public Duration deserialize(byte[] bytes) {
            return Duration.ofNanos(ByteBuffer.wrap(bytes, 0, bytes.length).getLong());
        }
    }

    public static final class IntSerializer
    implements SandboxSerializer<Integer> {
        static final IntSerializer instance = new IntSerializer();

        private IntSerializer() {
        }

        @Override
        public byte[] serialize(Integer val) {
            return ByteBuffer.allocate(4).putInt(val).array();
        }

        @Override
        public Integer deserialize(byte[] bytes) {
            return ByteBuffer.wrap(bytes, 0, bytes.length).getInt();
        }
    }

    public static final class StringSerializer
    implements SandboxSerializer<String> {
        static final StringSerializer instance = new StringSerializer();

        private StringSerializer() {
        }

        @Override
        public byte[] serialize(String s) {
            return (s != null ? s : "").getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public String deserialize(byte[] bytes) {
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }
}

