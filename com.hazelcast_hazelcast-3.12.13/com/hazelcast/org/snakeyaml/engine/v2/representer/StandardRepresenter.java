/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.representer;

import com.hazelcast.org.snakeyaml.engine.v2.api.DumpSettings;
import com.hazelcast.org.snakeyaml.engine.v2.api.RepresentToNode;
import com.hazelcast.org.snakeyaml.engine.v2.common.FlowStyle;
import com.hazelcast.org.snakeyaml.engine.v2.common.NonPrintableStyle;
import com.hazelcast.org.snakeyaml.engine.v2.common.ScalarStyle;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.Node;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.Tag;
import com.hazelcast.org.snakeyaml.engine.v2.representer.BaseRepresenter;
import com.hazelcast.org.snakeyaml.engine.v2.scanner.StreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

public class StandardRepresenter
extends BaseRepresenter {
    protected Map<Class<? extends Object>, Tag> classTags;
    protected DumpSettings settings;
    public static final Pattern MULTILINE_PATTERN = Pattern.compile("\n|\u0085|\u2028|\u2029");

    public StandardRepresenter(DumpSettings settings) {
        this.defaultFlowStyle = settings.getDefaultFlowStyle();
        this.defaultScalarStyle = settings.getDefaultScalarStyle();
        this.nullRepresenter = new RepresentNull();
        this.representers.put(String.class, new RepresentString());
        this.representers.put(Boolean.class, new RepresentBoolean());
        this.representers.put(Character.class, new RepresentString());
        this.representers.put(UUID.class, new RepresentUuid());
        this.representers.put(Optional.class, new RepresentOptional());
        this.representers.put(byte[].class, new RepresentByteArray());
        RepresentPrimitiveArray primitiveArray = new RepresentPrimitiveArray();
        this.representers.put(short[].class, primitiveArray);
        this.representers.put(int[].class, primitiveArray);
        this.representers.put(long[].class, primitiveArray);
        this.representers.put(float[].class, primitiveArray);
        this.representers.put(double[].class, primitiveArray);
        this.representers.put(char[].class, primitiveArray);
        this.representers.put(boolean[].class, primitiveArray);
        this.parentClassRepresenters.put(Number.class, new RepresentNumber());
        this.parentClassRepresenters.put(List.class, new RepresentList());
        this.parentClassRepresenters.put(Map.class, new RepresentMap());
        this.parentClassRepresenters.put(Set.class, new RepresentSet());
        this.parentClassRepresenters.put(Iterator.class, new RepresentIterator());
        this.parentClassRepresenters.put(new Object[0].getClass(), new RepresentArray());
        this.parentClassRepresenters.put(Enum.class, new RepresentEnum());
        this.classTags = new HashMap<Class<? extends Object>, Tag>();
        this.settings = settings;
    }

    protected Tag getTag(Class<?> clazz, Tag defaultTag) {
        if (this.classTags.containsKey(clazz)) {
            return this.classTags.get(clazz);
        }
        return defaultTag;
    }

    public Tag addClassTag(Class<? extends Object> clazz, Tag tag) {
        if (tag == null) {
            throw new NullPointerException("Tag must be provided.");
        }
        return this.classTags.put(clazz, tag);
    }

    protected class RepresentOptional
    implements RepresentToNode {
        protected RepresentOptional() {
        }

        @Override
        public Node representData(Object data) {
            Optional opt = (Optional)data;
            if (opt.isPresent()) {
                Node node = StandardRepresenter.this.represent(opt.get());
                node.setTag(new Tag(Optional.class));
                return node;
            }
            return StandardRepresenter.this.representScalar(Tag.NULL, "null");
        }
    }

    protected class RepresentUuid
    implements RepresentToNode {
        protected RepresentUuid() {
        }

        @Override
        public Node representData(Object data) {
            return StandardRepresenter.this.representScalar(StandardRepresenter.this.getTag(data.getClass(), new Tag(UUID.class)), data.toString());
        }
    }

    protected class RepresentByteArray
    implements RepresentToNode {
        protected RepresentByteArray() {
        }

        @Override
        public Node representData(Object data) {
            return StandardRepresenter.this.representScalar(Tag.BINARY, Base64.getEncoder().encodeToString((byte[])data), ScalarStyle.LITERAL);
        }
    }

    protected class RepresentEnum
    implements RepresentToNode {
        protected RepresentEnum() {
        }

        @Override
        public Node representData(Object data) {
            Tag tag = new Tag(data.getClass());
            return StandardRepresenter.this.representScalar(StandardRepresenter.this.getTag(data.getClass(), tag), ((Enum)data).name());
        }
    }

    protected class RepresentSet
    implements RepresentToNode {
        protected RepresentSet() {
        }

        @Override
        public Node representData(Object data) {
            LinkedHashMap value = new LinkedHashMap();
            Set set = (Set)data;
            for (Object key : set) {
                value.put(key, null);
            }
            return StandardRepresenter.this.representMapping(StandardRepresenter.this.getTag(data.getClass(), Tag.SET), value, FlowStyle.AUTO);
        }
    }

    protected class RepresentMap
    implements RepresentToNode {
        protected RepresentMap() {
        }

        @Override
        public Node representData(Object data) {
            return StandardRepresenter.this.representMapping(StandardRepresenter.this.getTag(data.getClass(), Tag.MAP), (Map)data, FlowStyle.AUTO);
        }
    }

    protected class RepresentPrimitiveArray
    implements RepresentToNode {
        protected RepresentPrimitiveArray() {
        }

        @Override
        public Node representData(Object data) {
            Class<?> type = data.getClass().getComponentType();
            if (Byte.TYPE == type) {
                return StandardRepresenter.this.representSequence(Tag.SEQ, this.asByteList(data), FlowStyle.AUTO);
            }
            if (Short.TYPE == type) {
                return StandardRepresenter.this.representSequence(Tag.SEQ, this.asShortList(data), FlowStyle.AUTO);
            }
            if (Integer.TYPE == type) {
                return StandardRepresenter.this.representSequence(Tag.SEQ, this.asIntList(data), FlowStyle.AUTO);
            }
            if (Long.TYPE == type) {
                return StandardRepresenter.this.representSequence(Tag.SEQ, this.asLongList(data), FlowStyle.AUTO);
            }
            if (Float.TYPE == type) {
                return StandardRepresenter.this.representSequence(Tag.SEQ, this.asFloatList(data), FlowStyle.AUTO);
            }
            if (Double.TYPE == type) {
                return StandardRepresenter.this.representSequence(Tag.SEQ, this.asDoubleList(data), FlowStyle.AUTO);
            }
            if (Character.TYPE == type) {
                return StandardRepresenter.this.representSequence(Tag.SEQ, this.asCharList(data), FlowStyle.AUTO);
            }
            if (Boolean.TYPE == type) {
                return StandardRepresenter.this.representSequence(Tag.SEQ, this.asBooleanList(data), FlowStyle.AUTO);
            }
            throw new YamlEngineException("Unexpected primitive '" + type.getCanonicalName() + "'");
        }

        private List<Byte> asByteList(Object in) {
            byte[] array = (byte[])in;
            ArrayList<Byte> list = new ArrayList<Byte>(array.length);
            for (int i = 0; i < array.length; ++i) {
                list.add(array[i]);
            }
            return list;
        }

        private List<Short> asShortList(Object in) {
            short[] array = (short[])in;
            ArrayList<Short> list = new ArrayList<Short>(array.length);
            for (int i = 0; i < array.length; ++i) {
                list.add(array[i]);
            }
            return list;
        }

        private List<Integer> asIntList(Object in) {
            int[] array = (int[])in;
            ArrayList<Integer> list = new ArrayList<Integer>(array.length);
            for (int i = 0; i < array.length; ++i) {
                list.add(array[i]);
            }
            return list;
        }

        private List<Long> asLongList(Object in) {
            long[] array = (long[])in;
            ArrayList<Long> list = new ArrayList<Long>(array.length);
            for (int i = 0; i < array.length; ++i) {
                list.add(array[i]);
            }
            return list;
        }

        private List<Float> asFloatList(Object in) {
            float[] array = (float[])in;
            ArrayList<Float> list = new ArrayList<Float>(array.length);
            for (int i = 0; i < array.length; ++i) {
                list.add(Float.valueOf(array[i]));
            }
            return list;
        }

        private List<Double> asDoubleList(Object in) {
            double[] array = (double[])in;
            ArrayList<Double> list = new ArrayList<Double>(array.length);
            for (int i = 0; i < array.length; ++i) {
                list.add(array[i]);
            }
            return list;
        }

        private List<Character> asCharList(Object in) {
            char[] array = (char[])in;
            ArrayList<Character> list = new ArrayList<Character>(array.length);
            for (int i = 0; i < array.length; ++i) {
                list.add(Character.valueOf(array[i]));
            }
            return list;
        }

        private List<Boolean> asBooleanList(Object in) {
            boolean[] array = (boolean[])in;
            ArrayList<Boolean> list = new ArrayList<Boolean>(array.length);
            for (int i = 0; i < array.length; ++i) {
                list.add(array[i]);
            }
            return list;
        }
    }

    protected class RepresentArray
    implements RepresentToNode {
        protected RepresentArray() {
        }

        @Override
        public Node representData(Object data) {
            Object[] array = (Object[])data;
            List<Object> list = Arrays.asList(array);
            return StandardRepresenter.this.representSequence(Tag.SEQ, list, FlowStyle.AUTO);
        }
    }

    private static class IteratorWrapper
    implements Iterable<Object> {
        private Iterator<Object> iter;

        public IteratorWrapper(Iterator<Object> iter) {
            this.iter = iter;
        }

        @Override
        public Iterator<Object> iterator() {
            return this.iter;
        }
    }

    protected class RepresentIterator
    implements RepresentToNode {
        protected RepresentIterator() {
        }

        @Override
        public Node representData(Object data) {
            Iterator iter = (Iterator)data;
            return StandardRepresenter.this.representSequence(StandardRepresenter.this.getTag(data.getClass(), Tag.SEQ), new IteratorWrapper(iter), FlowStyle.AUTO);
        }
    }

    protected class RepresentList
    implements RepresentToNode {
        protected RepresentList() {
        }

        @Override
        public Node representData(Object data) {
            return StandardRepresenter.this.representSequence(StandardRepresenter.this.getTag(data.getClass(), Tag.SEQ), (List)data, FlowStyle.AUTO);
        }
    }

    protected class RepresentNumber
    implements RepresentToNode {
        protected RepresentNumber() {
        }

        @Override
        public Node representData(Object data) {
            String value;
            Tag tag;
            if (data instanceof Byte || data instanceof Short || data instanceof Integer || data instanceof Long || data instanceof BigInteger) {
                tag = Tag.INT;
                value = data.toString();
            } else {
                Number number = (Number)data;
                tag = Tag.FLOAT;
                value = number.equals(Double.NaN) ? ".NaN" : (number.equals(Double.POSITIVE_INFINITY) ? ".inf" : (number.equals(Double.NEGATIVE_INFINITY) ? "-.inf" : number.toString()));
            }
            return StandardRepresenter.this.representScalar(StandardRepresenter.this.getTag(data.getClass(), tag), value);
        }
    }

    protected class RepresentBoolean
    implements RepresentToNode {
        protected RepresentBoolean() {
        }

        @Override
        public Node representData(Object data) {
            String value = Boolean.TRUE.equals(data) ? "true" : "false";
            return StandardRepresenter.this.representScalar(Tag.BOOL, value);
        }
    }

    protected class RepresentString
    implements RepresentToNode {
        protected RepresentString() {
        }

        @Override
        public Node representData(Object data) {
            Tag tag = Tag.STR;
            ScalarStyle style = ScalarStyle.PLAIN;
            String value = data.toString();
            if (StandardRepresenter.this.settings.getNonPrintableStyle() == NonPrintableStyle.BINARY && !StreamReader.isPrintable(value)) {
                tag = Tag.BINARY;
                byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
                String checkValue = new String(bytes, StandardCharsets.UTF_8);
                if (!checkValue.equals(value)) {
                    throw new YamlEngineException("invalid string value has occurred");
                }
                value = Base64.getEncoder().encodeToString(bytes);
                style = ScalarStyle.LITERAL;
            }
            if (StandardRepresenter.this.defaultScalarStyle == ScalarStyle.PLAIN && MULTILINE_PATTERN.matcher(value).find()) {
                style = ScalarStyle.LITERAL;
            }
            return StandardRepresenter.this.representScalar(tag, value, style);
        }
    }

    protected class RepresentNull
    implements RepresentToNode {
        protected RepresentNull() {
        }

        @Override
        public Node representData(Object data) {
            return StandardRepresenter.this.representScalar(Tag.NULL, "null");
        }
    }
}

