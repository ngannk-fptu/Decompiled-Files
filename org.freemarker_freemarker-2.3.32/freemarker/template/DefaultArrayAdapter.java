/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.ObjectWrapper;
import freemarker.template.ObjectWrapperAndUnwrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.WrappingTemplateModel;
import java.io.Serializable;
import java.lang.reflect.Array;

public abstract class DefaultArrayAdapter
extends WrappingTemplateModel
implements TemplateSequenceModel,
AdapterTemplateModel,
WrapperTemplateModel,
Serializable {
    public static DefaultArrayAdapter adapt(Object array, ObjectWrapperAndUnwrapper wrapper) {
        Class<?> componentType = array.getClass().getComponentType();
        if (componentType == null) {
            throw new IllegalArgumentException("Not an array");
        }
        if (componentType.isPrimitive()) {
            if (componentType == Integer.TYPE) {
                return new IntArrayAdapter((int[])array, wrapper);
            }
            if (componentType == Double.TYPE) {
                return new DoubleArrayAdapter((double[])array, wrapper);
            }
            if (componentType == Long.TYPE) {
                return new LongArrayAdapter((long[])array, wrapper);
            }
            if (componentType == Boolean.TYPE) {
                return new BooleanArrayAdapter((boolean[])array, wrapper);
            }
            if (componentType == Float.TYPE) {
                return new FloatArrayAdapter((float[])array, wrapper);
            }
            if (componentType == Character.TYPE) {
                return new CharArrayAdapter((char[])array, wrapper);
            }
            if (componentType == Short.TYPE) {
                return new ShortArrayAdapter((short[])array, wrapper);
            }
            if (componentType == Byte.TYPE) {
                return new ByteArrayAdapter((byte[])array, wrapper);
            }
            return new GenericPrimitiveArrayAdapter(array, wrapper);
        }
        return new ObjectArrayAdapter((Object[])array, wrapper);
    }

    private DefaultArrayAdapter(ObjectWrapper wrapper) {
        super(wrapper);
    }

    public final Object getAdaptedObject(Class hint) {
        return this.getWrappedObject();
    }

    private static class GenericPrimitiveArrayAdapter
    extends DefaultArrayAdapter {
        private final Object array;
        private final int length;

        private GenericPrimitiveArrayAdapter(Object array, ObjectWrapper wrapper) {
            super(wrapper);
            this.array = array;
            this.length = Array.getLength(array);
        }

        @Override
        public TemplateModel get(int index) throws TemplateModelException {
            return index >= 0 && index < this.length ? this.wrap(Array.get(this.array, index)) : null;
        }

        @Override
        public int size() throws TemplateModelException {
            return this.length;
        }

        @Override
        public Object getWrappedObject() {
            return this.array;
        }
    }

    private static class BooleanArrayAdapter
    extends DefaultArrayAdapter {
        private final boolean[] array;

        private BooleanArrayAdapter(boolean[] array, ObjectWrapper wrapper) {
            super(wrapper);
            this.array = array;
        }

        @Override
        public TemplateModel get(int index) throws TemplateModelException {
            return index >= 0 && index < this.array.length ? this.wrap(this.array[index]) : null;
        }

        @Override
        public int size() throws TemplateModelException {
            return this.array.length;
        }

        @Override
        public Object getWrappedObject() {
            return this.array;
        }
    }

    private static class CharArrayAdapter
    extends DefaultArrayAdapter {
        private final char[] array;

        private CharArrayAdapter(char[] array, ObjectWrapper wrapper) {
            super(wrapper);
            this.array = array;
        }

        @Override
        public TemplateModel get(int index) throws TemplateModelException {
            return index >= 0 && index < this.array.length ? this.wrap(Character.valueOf(this.array[index])) : null;
        }

        @Override
        public int size() throws TemplateModelException {
            return this.array.length;
        }

        @Override
        public Object getWrappedObject() {
            return this.array;
        }
    }

    private static class DoubleArrayAdapter
    extends DefaultArrayAdapter {
        private final double[] array;

        private DoubleArrayAdapter(double[] array, ObjectWrapper wrapper) {
            super(wrapper);
            this.array = array;
        }

        @Override
        public TemplateModel get(int index) throws TemplateModelException {
            return index >= 0 && index < this.array.length ? this.wrap(this.array[index]) : null;
        }

        @Override
        public int size() throws TemplateModelException {
            return this.array.length;
        }

        @Override
        public Object getWrappedObject() {
            return this.array;
        }
    }

    private static class FloatArrayAdapter
    extends DefaultArrayAdapter {
        private final float[] array;

        private FloatArrayAdapter(float[] array, ObjectWrapper wrapper) {
            super(wrapper);
            this.array = array;
        }

        @Override
        public TemplateModel get(int index) throws TemplateModelException {
            return index >= 0 && index < this.array.length ? this.wrap(Float.valueOf(this.array[index])) : null;
        }

        @Override
        public int size() throws TemplateModelException {
            return this.array.length;
        }

        @Override
        public Object getWrappedObject() {
            return this.array;
        }
    }

    private static class LongArrayAdapter
    extends DefaultArrayAdapter {
        private final long[] array;

        private LongArrayAdapter(long[] array, ObjectWrapper wrapper) {
            super(wrapper);
            this.array = array;
        }

        @Override
        public TemplateModel get(int index) throws TemplateModelException {
            return index >= 0 && index < this.array.length ? this.wrap(this.array[index]) : null;
        }

        @Override
        public int size() throws TemplateModelException {
            return this.array.length;
        }

        @Override
        public Object getWrappedObject() {
            return this.array;
        }
    }

    private static class IntArrayAdapter
    extends DefaultArrayAdapter {
        private final int[] array;

        private IntArrayAdapter(int[] array, ObjectWrapper wrapper) {
            super(wrapper);
            this.array = array;
        }

        @Override
        public TemplateModel get(int index) throws TemplateModelException {
            return index >= 0 && index < this.array.length ? this.wrap(this.array[index]) : null;
        }

        @Override
        public int size() throws TemplateModelException {
            return this.array.length;
        }

        @Override
        public Object getWrappedObject() {
            return this.array;
        }
    }

    private static class ShortArrayAdapter
    extends DefaultArrayAdapter {
        private final short[] array;

        private ShortArrayAdapter(short[] array, ObjectWrapper wrapper) {
            super(wrapper);
            this.array = array;
        }

        @Override
        public TemplateModel get(int index) throws TemplateModelException {
            return index >= 0 && index < this.array.length ? this.wrap(this.array[index]) : null;
        }

        @Override
        public int size() throws TemplateModelException {
            return this.array.length;
        }

        @Override
        public Object getWrappedObject() {
            return this.array;
        }
    }

    private static class ByteArrayAdapter
    extends DefaultArrayAdapter {
        private final byte[] array;

        private ByteArrayAdapter(byte[] array, ObjectWrapper wrapper) {
            super(wrapper);
            this.array = array;
        }

        @Override
        public TemplateModel get(int index) throws TemplateModelException {
            return index >= 0 && index < this.array.length ? this.wrap(this.array[index]) : null;
        }

        @Override
        public int size() throws TemplateModelException {
            return this.array.length;
        }

        @Override
        public Object getWrappedObject() {
            return this.array;
        }
    }

    private static class ObjectArrayAdapter
    extends DefaultArrayAdapter {
        private final Object[] array;

        private ObjectArrayAdapter(Object[] array, ObjectWrapper wrapper) {
            super(wrapper);
            this.array = array;
        }

        @Override
        public TemplateModel get(int index) throws TemplateModelException {
            return index >= 0 && index < this.array.length ? this.wrap(this.array[index]) : null;
        }

        @Override
        public int size() throws TemplateModelException {
            return this.array.length;
        }

        @Override
        public Object getWrappedObject() {
            return this.array;
        }
    }
}

