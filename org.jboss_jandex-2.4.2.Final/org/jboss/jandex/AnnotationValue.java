/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.util.Arrays;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.DotName;
import org.jboss.jandex.Type;

public abstract class AnnotationValue {
    static final AnnotationValue[] EMPTY_VALUE_ARRAY = new AnnotationValue[0];
    private final String name;

    AnnotationValue(String name) {
        this.name = name;
    }

    public static AnnotationValue createByteValue(String name, byte b) {
        return new ByteValue(name, b);
    }

    public static AnnotationValue createShortValue(String name, short s) {
        return new ShortValue(name, s);
    }

    public static AnnotationValue createIntegerValue(String name, int i) {
        return new IntegerValue(name, i);
    }

    public static AnnotationValue createCharacterValue(String name, char c) {
        return new CharacterValue(name, c);
    }

    public static AnnotationValue createFloatValue(String name, float f) {
        return new FloatValue(name, f);
    }

    @Deprecated
    public static AnnotationValue createDouleValue(String name, double d) {
        return AnnotationValue.createDoubleValue(name, d);
    }

    public static AnnotationValue createDoubleValue(String name, double d) {
        return new DoubleValue(name, d);
    }

    @Deprecated
    public static AnnotationValue createLongalue(String name, long l) {
        return AnnotationValue.createLongValue(name, l);
    }

    public static AnnotationValue createLongValue(String name, long l) {
        return new LongValue(name, l);
    }

    public static AnnotationValue createBooleanValue(String name, boolean bool) {
        return new BooleanValue(name, bool);
    }

    public static AnnotationValue createStringValue(String name, String string) {
        return new StringValue(name, string);
    }

    public static AnnotationValue createClassValue(String name, Type type) {
        return new ClassValue(name, type);
    }

    public static AnnotationValue createEnumValue(String name, DotName typeName, String value) {
        return new EnumValue(name, typeName, value);
    }

    public static AnnotationValue createArrayValue(String name, AnnotationValue[] values) {
        return new ArrayValue(name, values);
    }

    public static AnnotationValue createNestedAnnotationValue(String name, AnnotationInstance instance) {
        return new NestedAnnotation(name, instance);
    }

    public final String name() {
        return this.name;
    }

    public abstract Object value();

    public abstract Kind kind();

    public Kind componentKind() {
        throw new IllegalArgumentException("Not an array");
    }

    public int asInt() {
        throw new IllegalArgumentException("Not a number");
    }

    public long asLong() {
        throw new IllegalArgumentException("Not a number");
    }

    public short asShort() {
        throw new IllegalArgumentException("not a number");
    }

    public byte asByte() {
        throw new IllegalArgumentException("not a number");
    }

    public float asFloat() {
        throw new IllegalArgumentException("not a number");
    }

    public double asDouble() {
        throw new IllegalArgumentException("not a number");
    }

    public char asChar() {
        throw new IllegalArgumentException("not a character");
    }

    public boolean asBoolean() {
        throw new IllegalArgumentException("not a boolean");
    }

    public String asString() {
        return this.value().toString();
    }

    public String asEnum() {
        throw new IllegalArgumentException("not an enum");
    }

    public DotName asEnumType() {
        throw new IllegalArgumentException("not an enum");
    }

    public Type asClass() {
        throw new IllegalArgumentException("not a class");
    }

    public AnnotationInstance asNested() {
        throw new IllegalArgumentException("not a nested annotation");
    }

    AnnotationValue[] asArray() {
        throw new IllegalArgumentException("Not an array");
    }

    public int[] asIntArray() {
        throw new IllegalArgumentException("Not a numerical array");
    }

    public long[] asLongArray() {
        throw new IllegalArgumentException("Not a numerical array");
    }

    public short[] asShortArray() {
        throw new IllegalArgumentException("not a numerical array");
    }

    public byte[] asByteArray() {
        throw new IllegalArgumentException("not a numerical array");
    }

    public float[] asFloatArray() {
        throw new IllegalArgumentException("not a numerical array");
    }

    public double[] asDoubleArray() {
        throw new IllegalArgumentException("not a numerical array");
    }

    public char[] asCharArray() {
        throw new IllegalArgumentException("not a character array");
    }

    public boolean[] asBooleanArray() {
        throw new IllegalArgumentException("not a boolean array");
    }

    public String[] asStringArray() {
        throw new IllegalArgumentException("not a string array");
    }

    public String[] asEnumArray() {
        throw new IllegalArgumentException("not an enum array");
    }

    public DotName[] asEnumTypeArray() {
        throw new IllegalArgumentException("not an enum array");
    }

    public Type[] asClassArray() {
        throw new IllegalArgumentException("not a class array");
    }

    public AnnotationInstance[] asNestedArray() {
        throw new IllegalArgumentException("not a nested annotation array");
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (this.name.length() > 0) {
            builder.append(this.name).append(" = ");
        }
        return builder.append(this.value()).toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AnnotationValue that = (AnnotationValue)o;
        return this.name.equals(that.name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    static final class ArrayValue
    extends AnnotationValue {
        private final AnnotationValue[] value;

        ArrayValue(String name, AnnotationValue[] value) {
            super(name);
            this.value = value.length > 0 ? value : EMPTY_VALUE_ARRAY;
        }

        public AnnotationValue[] value() {
            return this.value;
        }

        @Override
        public Kind kind() {
            return Kind.ARRAY;
        }

        @Override
        public Kind componentKind() {
            return this.value.length > 0 ? this.value[0].kind() : Kind.UNKNOWN;
        }

        @Override
        AnnotationValue[] asArray() {
            return this.value;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            if (((AnnotationValue)this).name.length() > 0) {
                builder.append(((AnnotationValue)this).name).append(" = ");
            }
            builder.append('[');
            for (int i = 0; i < this.value.length; ++i) {
                builder.append(this.value[i]);
                if (i >= this.value.length - 1) continue;
                builder.append(',');
            }
            return builder.append(']').toString();
        }

        @Override
        public int[] asIntArray() {
            int length = this.value.length;
            int[] array = new int[length];
            for (int i = 0; i < length; ++i) {
                array[i] = this.value[i].asInt();
            }
            return array;
        }

        @Override
        public long[] asLongArray() {
            int length = this.value.length;
            long[] array = new long[length];
            for (int i = 0; i < length; ++i) {
                array[i] = this.value[i].asLong();
            }
            return array;
        }

        @Override
        public short[] asShortArray() {
            int length = this.value.length;
            short[] array = new short[length];
            for (int i = 0; i < length; ++i) {
                array[i] = this.value[i].asShort();
            }
            return array;
        }

        @Override
        public byte[] asByteArray() {
            int length = this.value.length;
            byte[] array = new byte[length];
            for (int i = 0; i < length; ++i) {
                array[i] = this.value[i].asByte();
            }
            return array;
        }

        @Override
        public float[] asFloatArray() {
            int length = this.value.length;
            float[] array = new float[length];
            for (int i = 0; i < length; ++i) {
                array[i] = this.value[i].asFloat();
            }
            return array;
        }

        @Override
        public double[] asDoubleArray() {
            int length = this.value.length;
            double[] array = new double[length];
            for (int i = 0; i < length; ++i) {
                array[i] = this.value[i].asDouble();
            }
            return array;
        }

        @Override
        public char[] asCharArray() {
            int length = this.value.length;
            char[] array = new char[length];
            for (int i = 0; i < length; ++i) {
                array[i] = this.value[i].asChar();
            }
            return array;
        }

        @Override
        public boolean[] asBooleanArray() {
            int length = this.value.length;
            boolean[] array = new boolean[length];
            for (int i = 0; i < length; ++i) {
                array[i] = this.value[i].asBoolean();
            }
            return array;
        }

        @Override
        public String[] asStringArray() {
            int length = this.value.length;
            String[] array = new String[length];
            for (int i = 0; i < length; ++i) {
                array[i] = this.value[i].asString();
            }
            return array;
        }

        @Override
        public String[] asEnumArray() {
            int length = this.value.length;
            String[] array = new String[length];
            for (int i = 0; i < length; ++i) {
                array[i] = this.value[i].asEnum();
            }
            return array;
        }

        @Override
        public Type[] asClassArray() {
            int length = this.value.length;
            Type[] array = new Type[length];
            for (int i = 0; i < length; ++i) {
                array[i] = this.value[i].asClass();
            }
            return array;
        }

        @Override
        public AnnotationInstance[] asNestedArray() {
            int length = this.value.length;
            AnnotationInstance[] array = new AnnotationInstance[length];
            for (int i = 0; i < length; ++i) {
                array[i] = this.value[i].asNested();
            }
            return array;
        }

        @Override
        public DotName[] asEnumTypeArray() {
            int length = this.value.length;
            DotName[] array = new DotName[length];
            for (int i = 0; i < length; ++i) {
                array[i] = this.value[i].asEnumType();
            }
            return array;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ArrayValue that = (ArrayValue)o;
            return super.equals(o) && Arrays.equals(this.value, that.value);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + Arrays.hashCode(this.value);
            return result;
        }
    }

    static final class NestedAnnotation
    extends AnnotationValue {
        private final AnnotationInstance value;

        NestedAnnotation(String name, AnnotationInstance value) {
            super(name);
            this.value = value;
        }

        @Override
        public AnnotationInstance value() {
            return this.value;
        }

        @Override
        public Kind kind() {
            return Kind.NESTED;
        }

        @Override
        public AnnotationInstance asNested() {
            return this.value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            NestedAnnotation that = (NestedAnnotation)o;
            return super.equals(o) && this.value.equals(that.value);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + this.value.hashCode();
            return result;
        }
    }

    static final class ClassValue
    extends AnnotationValue {
        private final Type type;

        ClassValue(String name, Type type) {
            super(name);
            this.type = type;
        }

        @Override
        public Type value() {
            return this.type;
        }

        @Override
        public Kind kind() {
            return Kind.CLASS;
        }

        @Override
        public Type asClass() {
            return this.type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ClassValue that = (ClassValue)o;
            return super.equals(o) && this.type.equals(that.type);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + this.type.hashCode();
            return result;
        }
    }

    static final class EnumValue
    extends AnnotationValue {
        private final String value;
        private final DotName typeName;

        EnumValue(String name, DotName typeName, String value) {
            super(name);
            this.typeName = typeName;
            this.value = value;
        }

        @Override
        public String value() {
            return this.value;
        }

        @Override
        public Kind kind() {
            return Kind.ENUM;
        }

        @Override
        public String asEnum() {
            return this.value;
        }

        @Override
        public DotName asEnumType() {
            return this.typeName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            EnumValue enumValue = (EnumValue)o;
            return super.equals(o) && this.typeName.equals(enumValue.typeName) && this.value.equals(enumValue.value);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + this.value.hashCode();
            result = 31 * result + this.typeName.hashCode();
            return result;
        }
    }

    static final class BooleanValue
    extends AnnotationValue {
        private final boolean value;

        BooleanValue(String name, boolean value) {
            super(name);
            this.value = value;
        }

        @Override
        public Boolean value() {
            return this.value;
        }

        @Override
        public Kind kind() {
            return Kind.BOOLEAN;
        }

        @Override
        public boolean asBoolean() {
            return this.value;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (this.value ? 1231 : 1237);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            BooleanValue other = (BooleanValue)obj;
            return this.value == other.value;
        }
    }

    static final class LongValue
    extends AnnotationValue {
        private final long value;

        LongValue(String name, long value) {
            super(name);
            this.value = value;
        }

        @Override
        public Long value() {
            return this.value;
        }

        @Override
        public Kind kind() {
            return Kind.LONG;
        }

        @Override
        public int asInt() {
            return (int)this.value;
        }

        @Override
        public long asLong() {
            return this.value;
        }

        @Override
        public short asShort() {
            return (short)this.value;
        }

        @Override
        public byte asByte() {
            return (byte)this.value;
        }

        @Override
        public float asFloat() {
            return this.value;
        }

        @Override
        public double asDouble() {
            return this.value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            LongValue longValue = (LongValue)o;
            return this.value == longValue.value && super.equals(o);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (int)(this.value ^ this.value >>> 32);
            return result;
        }
    }

    static final class IntegerValue
    extends AnnotationValue {
        private final int value;

        IntegerValue(String name, int value) {
            super(name);
            this.value = value;
        }

        @Override
        public Integer value() {
            return this.value;
        }

        @Override
        public Kind kind() {
            return Kind.INTEGER;
        }

        @Override
        public int asInt() {
            return this.value;
        }

        @Override
        public long asLong() {
            return this.value;
        }

        @Override
        public short asShort() {
            return (short)this.value;
        }

        @Override
        public byte asByte() {
            return (byte)this.value;
        }

        @Override
        public float asFloat() {
            return this.value;
        }

        @Override
        public double asDouble() {
            return this.value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            IntegerValue that = (IntegerValue)o;
            return this.value == that.value && super.equals(o);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + this.value;
            return result;
        }
    }

    static final class ShortValue
    extends AnnotationValue {
        private final short value;

        ShortValue(String name, short value) {
            super(name);
            this.value = value;
        }

        @Override
        public Short value() {
            return this.value;
        }

        @Override
        public Kind kind() {
            return Kind.SHORT;
        }

        @Override
        public int asInt() {
            return this.value;
        }

        @Override
        public long asLong() {
            return this.value;
        }

        @Override
        public short asShort() {
            return this.value;
        }

        @Override
        public byte asByte() {
            return (byte)this.value;
        }

        @Override
        public float asFloat() {
            return this.value;
        }

        @Override
        public double asDouble() {
            return this.value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ShortValue that = (ShortValue)o;
            return this.value == that.value && super.equals(o);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + this.value;
            return result;
        }
    }

    static final class FloatValue
    extends AnnotationValue {
        private final float value;

        FloatValue(String name, float value) {
            super(name);
            this.value = value;
        }

        @Override
        public Float value() {
            return Float.valueOf(this.value);
        }

        @Override
        public Kind kind() {
            return Kind.FLOAT;
        }

        @Override
        public int asInt() {
            return (int)this.value;
        }

        @Override
        public long asLong() {
            return (long)this.value;
        }

        @Override
        public short asShort() {
            return (short)this.value;
        }

        @Override
        public byte asByte() {
            return (byte)this.value;
        }

        @Override
        public float asFloat() {
            return this.value;
        }

        @Override
        public double asDouble() {
            return this.value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            FloatValue that = (FloatValue)o;
            return Float.compare(that.value, this.value) == 0 && super.equals(o);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (this.value != 0.0f ? Float.floatToIntBits(this.value) : 0);
            return result;
        }
    }

    static final class DoubleValue
    extends AnnotationValue {
        private final double value;

        public DoubleValue(String name, double value) {
            super(name);
            this.value = value;
        }

        @Override
        public Double value() {
            return this.value;
        }

        @Override
        public Kind kind() {
            return Kind.DOUBLE;
        }

        @Override
        public int asInt() {
            return (int)this.value;
        }

        @Override
        public long asLong() {
            return (long)this.value;
        }

        @Override
        public short asShort() {
            return (short)this.value;
        }

        @Override
        public byte asByte() {
            return (byte)this.value;
        }

        @Override
        public float asFloat() {
            return (float)this.value;
        }

        @Override
        public double asDouble() {
            return this.value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            DoubleValue that = (DoubleValue)o;
            return Double.compare(that.value, this.value) == 0 && super.equals(o);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            long temp = Double.doubleToLongBits(this.value);
            result = 31 * result + (int)(temp ^ temp >>> 32);
            return result;
        }
    }

    static final class CharacterValue
    extends AnnotationValue {
        private final char value;

        CharacterValue(String name, char value) {
            super(name);
            this.value = value;
        }

        @Override
        public Character value() {
            return Character.valueOf(this.value);
        }

        @Override
        public Kind kind() {
            return Kind.CHARACTER;
        }

        @Override
        public char asChar() {
            return this.value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            CharacterValue that = (CharacterValue)o;
            return this.value == that.value && super.equals(o);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + this.value;
            return result;
        }
    }

    static final class ByteValue
    extends AnnotationValue {
        private final byte value;

        ByteValue(String name, byte value) {
            super(name);
            this.value = value;
        }

        @Override
        public Byte value() {
            return this.value;
        }

        @Override
        public Kind kind() {
            return Kind.BYTE;
        }

        @Override
        public int asInt() {
            return this.value;
        }

        @Override
        public long asLong() {
            return this.value;
        }

        @Override
        public short asShort() {
            return this.value;
        }

        @Override
        public byte asByte() {
            return this.value;
        }

        @Override
        public float asFloat() {
            return this.value;
        }

        @Override
        public double asDouble() {
            return this.value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ByteValue byteValue = (ByteValue)o;
            return this.value == byteValue.value && super.equals(o);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + this.value;
            return result;
        }
    }

    static final class StringValue
    extends AnnotationValue {
        private final String value;

        StringValue(String name, String value) {
            super(name);
            this.value = value;
        }

        @Override
        public String value() {
            return this.value;
        }

        @Override
        public Kind kind() {
            return Kind.STRING;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            if (((AnnotationValue)this).name.length() > 0) {
                builder.append(((AnnotationValue)this).name).append(" = ");
            }
            return builder.append('\"').append(this.value).append('\"').toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            StringValue that = (StringValue)o;
            return super.equals(o) && this.value.equals(that.value);
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + this.value.hashCode();
            return result;
        }
    }

    public static enum Kind {
        BYTE,
        SHORT,
        INTEGER,
        CHARACTER,
        FLOAT,
        DOUBLE,
        LONG,
        BOOLEAN,
        CLASS,
        STRING,
        ENUM,
        ARRAY,
        NESTED,
        UNKNOWN;

    }
}

