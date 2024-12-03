/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util.type;

public final class PrimitiveWrapperHelper {
    private PrimitiveWrapperHelper() {
    }

    public static <X> PrimitiveWrapperDescriptor<X> getDescriptorByPrimitiveType(Class<X> primitiveClazz) {
        if (!primitiveClazz.isPrimitive()) {
            throw new IllegalArgumentException("Given class is not a primitive type : " + primitiveClazz.getName());
        }
        if (Boolean.TYPE == primitiveClazz) {
            return BooleanDescriptor.INSTANCE;
        }
        if (Character.TYPE == primitiveClazz) {
            return CharacterDescriptor.INSTANCE;
        }
        if (Byte.TYPE == primitiveClazz) {
            return ByteDescriptor.INSTANCE;
        }
        if (Short.TYPE == primitiveClazz) {
            return ShortDescriptor.INSTANCE;
        }
        if (Integer.TYPE == primitiveClazz) {
            return IntegerDescriptor.INSTANCE;
        }
        if (Long.TYPE == primitiveClazz) {
            return LongDescriptor.INSTANCE;
        }
        if (Float.TYPE == primitiveClazz) {
            return FloatDescriptor.INSTANCE;
        }
        if (Double.TYPE == primitiveClazz) {
            return DoubleDescriptor.INSTANCE;
        }
        if (Void.TYPE == primitiveClazz) {
            throw new IllegalArgumentException("void, as primitive type, has no wrapper equivalent");
        }
        throw new IllegalArgumentException("Unrecognized primitive type class : " + primitiveClazz.getName());
    }

    public static <X> PrimitiveWrapperDescriptor<X> getDescriptorByWrapperType(Class<X> wrapperClass) {
        if (wrapperClass.isPrimitive()) {
            throw new IllegalArgumentException("Given class is a primitive type : " + wrapperClass.getName());
        }
        if (Boolean.class.equals(wrapperClass)) {
            return BooleanDescriptor.INSTANCE;
        }
        if (Character.class == wrapperClass) {
            return CharacterDescriptor.INSTANCE;
        }
        if (Byte.class == wrapperClass) {
            return ByteDescriptor.INSTANCE;
        }
        if (Short.class == wrapperClass) {
            return ShortDescriptor.INSTANCE;
        }
        if (Integer.class == wrapperClass) {
            return IntegerDescriptor.INSTANCE;
        }
        if (Long.class == wrapperClass) {
            return LongDescriptor.INSTANCE;
        }
        if (Float.class == wrapperClass) {
            return FloatDescriptor.INSTANCE;
        }
        if (Double.class == wrapperClass) {
            return DoubleDescriptor.INSTANCE;
        }
        throw new IllegalArgumentException("Unrecognized wrapper type class : " + wrapperClass.getName());
    }

    public static boolean isWrapper(Class<?> clazz) {
        try {
            PrimitiveWrapperHelper.getDescriptorByWrapperType(clazz);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    public static boolean arePrimitiveWrapperEquivalents(Class converterDefinedType, Class propertyType) {
        if (converterDefinedType.isPrimitive()) {
            return PrimitiveWrapperHelper.getDescriptorByPrimitiveType(converterDefinedType).getWrapperClass().equals(propertyType);
        }
        if (propertyType.isPrimitive()) {
            return PrimitiveWrapperHelper.getDescriptorByPrimitiveType(propertyType).getWrapperClass().equals(converterDefinedType);
        }
        return false;
    }

    public static class DoubleDescriptor
    implements PrimitiveWrapperDescriptor<Double> {
        public static final DoubleDescriptor INSTANCE = new DoubleDescriptor();

        private DoubleDescriptor() {
        }

        @Override
        public Class<Double> getPrimitiveClass() {
            return Double.TYPE;
        }

        @Override
        public Class<Double> getWrapperClass() {
            return Double.class;
        }
    }

    public static class FloatDescriptor
    implements PrimitiveWrapperDescriptor<Float> {
        public static final FloatDescriptor INSTANCE = new FloatDescriptor();

        private FloatDescriptor() {
        }

        @Override
        public Class<Float> getPrimitiveClass() {
            return Float.TYPE;
        }

        @Override
        public Class<Float> getWrapperClass() {
            return Float.class;
        }
    }

    public static class LongDescriptor
    implements PrimitiveWrapperDescriptor<Long> {
        public static final LongDescriptor INSTANCE = new LongDescriptor();

        private LongDescriptor() {
        }

        @Override
        public Class<Long> getPrimitiveClass() {
            return Long.TYPE;
        }

        @Override
        public Class<Long> getWrapperClass() {
            return Long.class;
        }
    }

    public static class IntegerDescriptor
    implements PrimitiveWrapperDescriptor<Integer> {
        public static final IntegerDescriptor INSTANCE = new IntegerDescriptor();

        private IntegerDescriptor() {
        }

        @Override
        public Class<Integer> getPrimitiveClass() {
            return Integer.TYPE;
        }

        @Override
        public Class<Integer> getWrapperClass() {
            return Integer.class;
        }
    }

    public static class ShortDescriptor
    implements PrimitiveWrapperDescriptor<Short> {
        public static final ShortDescriptor INSTANCE = new ShortDescriptor();

        private ShortDescriptor() {
        }

        @Override
        public Class<Short> getPrimitiveClass() {
            return Short.TYPE;
        }

        @Override
        public Class<Short> getWrapperClass() {
            return Short.class;
        }
    }

    public static class ByteDescriptor
    implements PrimitiveWrapperDescriptor<Byte> {
        public static final ByteDescriptor INSTANCE = new ByteDescriptor();

        private ByteDescriptor() {
        }

        @Override
        public Class<Byte> getPrimitiveClass() {
            return Byte.TYPE;
        }

        @Override
        public Class<Byte> getWrapperClass() {
            return Byte.class;
        }
    }

    public static class CharacterDescriptor
    implements PrimitiveWrapperDescriptor<Character> {
        public static final CharacterDescriptor INSTANCE = new CharacterDescriptor();

        private CharacterDescriptor() {
        }

        @Override
        public Class<Character> getPrimitiveClass() {
            return Character.TYPE;
        }

        @Override
        public Class<Character> getWrapperClass() {
            return Character.class;
        }
    }

    public static class BooleanDescriptor
    implements PrimitiveWrapperDescriptor<Boolean> {
        public static final BooleanDescriptor INSTANCE = new BooleanDescriptor();

        private BooleanDescriptor() {
        }

        @Override
        public Class<Boolean> getPrimitiveClass() {
            return Boolean.TYPE;
        }

        @Override
        public Class<Boolean> getWrapperClass() {
            return Boolean.class;
        }
    }

    public static interface PrimitiveWrapperDescriptor<T> {
        public Class<T> getPrimitiveClass();

        public Class<T> getWrapperClass();
    }
}

