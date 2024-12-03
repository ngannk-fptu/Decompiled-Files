/*
 * Decompiled with CFR 0.152.
 */
package net.minidev.json.writer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import net.minidev.json.writer.JsonReader;
import net.minidev.json.writer.JsonReaderI;

public class ArraysMapper<T>
extends JsonReaderI<T> {
    public static JsonReaderI<int[]> MAPPER_PRIM_INT = new ArraysMapper<int[]>(null){

        @Override
        public int[] convert(Object current) {
            int p = 0;
            int[] r = new int[((List)current).size()];
            for (Object e : (List)current) {
                r[p++] = ((Number)e).intValue();
            }
            return r;
        }
    };
    public static JsonReaderI<Integer[]> MAPPER_INT = new ArraysMapper<Integer[]>(null){

        @Override
        public Integer[] convert(Object current) {
            int p = 0;
            Integer[] r = new Integer[((List)current).size()];
            for (Object e : (List)current) {
                if (e == null) continue;
                r[p] = e instanceof Integer ? (Integer)e : Integer.valueOf(((Number)e).intValue());
                ++p;
            }
            return r;
        }
    };
    public static JsonReaderI<short[]> MAPPER_PRIM_SHORT = new ArraysMapper<short[]>(null){

        @Override
        public short[] convert(Object current) {
            int p = 0;
            short[] r = new short[((List)current).size()];
            for (Object e : (List)current) {
                r[p++] = ((Number)e).shortValue();
            }
            return r;
        }
    };
    public static JsonReaderI<Short[]> MAPPER_SHORT = new ArraysMapper<Short[]>(null){

        @Override
        public Short[] convert(Object current) {
            int p = 0;
            Short[] r = new Short[((List)current).size()];
            for (Object e : (List)current) {
                if (e == null) continue;
                r[p] = e instanceof Short ? (Short)e : Short.valueOf(((Number)e).shortValue());
                ++p;
            }
            return r;
        }
    };
    public static JsonReaderI<byte[]> MAPPER_PRIM_BYTE = new ArraysMapper<byte[]>(null){

        @Override
        public byte[] convert(Object current) {
            int p = 0;
            byte[] r = new byte[((List)current).size()];
            for (Object e : (List)current) {
                r[p++] = ((Number)e).byteValue();
            }
            return r;
        }
    };
    public static JsonReaderI<Byte[]> MAPPER_BYTE = new ArraysMapper<Byte[]>(null){

        @Override
        public Byte[] convert(Object current) {
            int p = 0;
            Byte[] r = new Byte[((List)current).size()];
            for (Object e : (List)current) {
                if (e == null) continue;
                r[p] = e instanceof Byte ? (Byte)e : Byte.valueOf(((Number)e).byteValue());
                ++p;
            }
            return r;
        }
    };
    public static JsonReaderI<char[]> MAPPER_PRIM_CHAR = new ArraysMapper<char[]>(null){

        @Override
        public char[] convert(Object current) {
            int p = 0;
            char[] r = new char[((List)current).size()];
            for (Object e : (List)current) {
                r[p++] = e.toString().charAt(0);
            }
            return r;
        }
    };
    public static JsonReaderI<Character[]> MAPPER_CHAR = new ArraysMapper<Character[]>(null){

        @Override
        public Character[] convert(Object current) {
            int p = 0;
            Character[] r = new Character[((List)current).size()];
            for (Object e : (List)current) {
                if (e == null) continue;
                r[p] = Character.valueOf(e.toString().charAt(0));
                ++p;
            }
            return r;
        }
    };
    public static JsonReaderI<long[]> MAPPER_PRIM_LONG = new ArraysMapper<long[]>(null){

        @Override
        public long[] convert(Object current) {
            int p = 0;
            long[] r = new long[((List)current).size()];
            for (Object e : (List)current) {
                r[p++] = ((Number)e).intValue();
            }
            return r;
        }
    };
    public static JsonReaderI<Long[]> MAPPER_LONG = new ArraysMapper<Long[]>(null){

        @Override
        public Long[] convert(Object current) {
            int p = 0;
            Long[] r = new Long[((List)current).size()];
            for (Object e : (List)current) {
                if (e == null) continue;
                r[p] = e instanceof Float ? (Long)e : Long.valueOf(((Number)e).longValue());
                ++p;
            }
            return r;
        }
    };
    public static JsonReaderI<float[]> MAPPER_PRIM_FLOAT = new ArraysMapper<float[]>(null){

        @Override
        public float[] convert(Object current) {
            int p = 0;
            float[] r = new float[((List)current).size()];
            for (Object e : (List)current) {
                r[p++] = ((Number)e).floatValue();
            }
            return r;
        }
    };
    public static JsonReaderI<Float[]> MAPPER_FLOAT = new ArraysMapper<Float[]>(null){

        @Override
        public Float[] convert(Object current) {
            int p = 0;
            Float[] r = new Float[((List)current).size()];
            for (Object e : (List)current) {
                if (e == null) continue;
                r[p] = e instanceof Float ? (Float)e : Float.valueOf(((Number)e).floatValue());
                ++p;
            }
            return r;
        }
    };
    public static JsonReaderI<double[]> MAPPER_PRIM_DOUBLE = new ArraysMapper<double[]>(null){

        @Override
        public double[] convert(Object current) {
            int p = 0;
            double[] r = new double[((List)current).size()];
            for (Object e : (List)current) {
                r[p++] = ((Number)e).doubleValue();
            }
            return r;
        }
    };
    public static JsonReaderI<Double[]> MAPPER_DOUBLE = new ArraysMapper<Double[]>(null){

        @Override
        public Double[] convert(Object current) {
            int p = 0;
            Double[] r = new Double[((List)current).size()];
            for (Object e : (List)current) {
                if (e == null) continue;
                r[p] = e instanceof Double ? (Double)e : Double.valueOf(((Number)e).doubleValue());
                ++p;
            }
            return r;
        }
    };
    public static JsonReaderI<boolean[]> MAPPER_PRIM_BOOL = new ArraysMapper<boolean[]>(null){

        @Override
        public boolean[] convert(Object current) {
            int p = 0;
            boolean[] r = new boolean[((List)current).size()];
            for (Object e : (List)current) {
                r[p++] = (Boolean)e;
            }
            return r;
        }
    };
    public static JsonReaderI<Boolean[]> MAPPER_BOOL = new ArraysMapper<Boolean[]>(null){

        @Override
        public Boolean[] convert(Object current) {
            int p = 0;
            Boolean[] r = new Boolean[((List)current).size()];
            for (Object e : (List)current) {
                if (e == null) continue;
                if (e instanceof Boolean) {
                    r[p] = (boolean)((Boolean)e);
                } else if (e instanceof Number) {
                    r[p] = ((Number)e).intValue() != 0;
                } else {
                    throw new RuntimeException("can not convert " + e + " toBoolean");
                }
                ++p;
            }
            return r;
        }
    };

    public ArraysMapper(JsonReader base) {
        super(base);
    }

    @Override
    public Object createArray() {
        return new ArrayList();
    }

    @Override
    public void addValue(Object current, Object value) {
        ((List)current).add(value);
    }

    @Override
    public T convert(Object current) {
        return (T)current;
    }

    public static class GenericMapper<T>
    extends ArraysMapper<T> {
        final Class<?> componentType;
        JsonReaderI<?> subMapper;

        public GenericMapper(JsonReader base, Class<T> type) {
            super(base);
            this.componentType = type.getComponentType();
        }

        @Override
        public T convert(Object current) {
            int p = 0;
            Object[] r = (Object[])Array.newInstance(this.componentType, ((List)current).size());
            for (Object e : (List)current) {
                r[p++] = e;
            }
            return (T)r;
        }

        @Override
        public JsonReaderI<?> startArray(String key) {
            if (this.subMapper == null) {
                this.subMapper = this.base.getMapper(this.componentType);
            }
            return this.subMapper;
        }

        @Override
        public JsonReaderI<?> startObject(String key) {
            if (this.subMapper == null) {
                this.subMapper = this.base.getMapper(this.componentType);
            }
            return this.subMapper;
        }
    }
}

