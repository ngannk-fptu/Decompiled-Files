/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.classfmt;

public interface ClassFileConstants {
    public static final int AccDefault = 0;
    public static final int AccPublic = 1;
    public static final int AccPrivate = 2;
    public static final int AccProtected = 4;
    public static final int AccStatic = 8;
    public static final int AccFinal = 16;
    public static final int AccSynchronized = 32;
    public static final int AccVolatile = 64;
    public static final int AccBridge = 64;
    public static final int AccTransient = 128;
    public static final int AccVarargs = 128;
    public static final int AccNative = 256;
    public static final int AccInterface = 512;
    public static final int AccAbstract = 1024;
    public static final int AccStrictfp = 2048;
    public static final int AccSynthetic = 4096;
    public static final int AccAnnotation = 8192;
    public static final int AccEnum = 16384;
    public static final int AccModule = 32768;
    public static final int AccMandated = 32768;
    public static final int ACC_OPEN = 32;
    public static final int ACC_TRANSITIVE = 32;
    public static final int ACC_STATIC_PHASE = 64;
    public static final int ACC_SYNTHETIC = 4096;
    public static final int AccSuper = 32;
    public static final int AccAnnotationDefault = 131072;
    public static final int AccDeprecated = 0x100000;
    public static final int Utf8Tag = 1;
    public static final int IntegerTag = 3;
    public static final int FloatTag = 4;
    public static final int LongTag = 5;
    public static final int DoubleTag = 6;
    public static final int ClassTag = 7;
    public static final int StringTag = 8;
    public static final int FieldRefTag = 9;
    public static final int MethodRefTag = 10;
    public static final int InterfaceMethodRefTag = 11;
    public static final int NameAndTypeTag = 12;
    public static final int MethodHandleTag = 15;
    public static final int MethodTypeTag = 16;
    public static final int DynamicTag = 17;
    public static final int InvokeDynamicTag = 18;
    public static final int ModuleTag = 19;
    public static final int PackageTag = 20;
    public static final int ConstantMethodRefFixedSize = 5;
    public static final int ConstantClassFixedSize = 3;
    public static final int ConstantDoubleFixedSize = 9;
    public static final int ConstantFieldRefFixedSize = 5;
    public static final int ConstantFloatFixedSize = 5;
    public static final int ConstantIntegerFixedSize = 5;
    public static final int ConstantInterfaceMethodRefFixedSize = 5;
    public static final int ConstantLongFixedSize = 9;
    public static final int ConstantStringFixedSize = 3;
    public static final int ConstantUtf8FixedSize = 3;
    public static final int ConstantNameAndTypeFixedSize = 5;
    public static final int ConstantMethodHandleFixedSize = 4;
    public static final int ConstantMethodTypeFixedSize = 3;
    public static final int ConstantDynamicFixedSize = 5;
    public static final int ConstantInvokeDynamicFixedSize = 5;
    public static final int ConstantModuleFixedSize = 3;
    public static final int ConstantPackageFixedSize = 3;
    public static final int MethodHandleRefKindGetField = 1;
    public static final int MethodHandleRefKindGetStatic = 2;
    public static final int MethodHandleRefKindPutField = 3;
    public static final int MethodHandleRefKindPutStatic = 4;
    public static final int MethodHandleRefKindInvokeVirtual = 5;
    public static final int MethodHandleRefKindInvokeStatic = 6;
    public static final int MethodHandleRefKindInvokeSpecial = 7;
    public static final int MethodHandleRefKindNewInvokeSpecial = 8;
    public static final int MethodHandleRefKindInvokeInterface = 9;
    public static final int MAJOR_VERSION_1_1 = 45;
    public static final int MAJOR_VERSION_1_2 = 46;
    public static final int MAJOR_VERSION_1_3 = 47;
    public static final int MAJOR_VERSION_1_4 = 48;
    public static final int MAJOR_VERSION_1_5 = 49;
    public static final int MAJOR_VERSION_1_6 = 50;
    public static final int MAJOR_VERSION_1_7 = 51;
    public static final int MAJOR_VERSION_1_8 = 52;
    public static final int MAJOR_VERSION_9 = 53;
    public static final int MAJOR_VERSION_10 = 54;
    public static final int MAJOR_VERSION_11 = 55;
    public static final int MAJOR_VERSION_12 = 56;
    public static final int MAJOR_VERSION_13 = 57;
    public static final int MAJOR_VERSION_14 = 58;
    public static final int MAJOR_VERSION_15 = 59;
    public static final int MAJOR_VERSION_16 = 60;
    public static final int MAJOR_VERSION_0 = 44;
    public static final int MAJOR_LATEST_VERSION = 60;
    public static final int MINOR_VERSION_0 = 0;
    public static final int MINOR_VERSION_1 = 1;
    public static final int MINOR_VERSION_2 = 2;
    public static final int MINOR_VERSION_3 = 3;
    public static final int MINOR_VERSION_4 = 4;
    public static final int MINOR_VERSION_PREVIEW = 65535;
    public static final long JDK1_1 = 2949123L;
    public static final long JDK1_2 = 0x2E0000L;
    public static final long JDK1_3 = 0x2F0000L;
    public static final long JDK1_4 = 0x300000L;
    public static final long JDK1_5 = 0x310000L;
    public static final long JDK1_6 = 0x320000L;
    public static final long JDK1_7 = 0x330000L;
    public static final long JDK1_8 = 0x340000L;
    public static final long JDK9 = 0x350000L;
    public static final long JDK10 = 0x360000L;
    public static final long JDK11 = 0x370000L;
    public static final long JDK12 = 0x380000L;
    public static final long JDK13 = 0x390000L;
    public static final long JDK14 = 0x3A0000L;
    public static final long JDK15 = 0x3B0000L;
    public static final long JDK16 = 0x3C0000L;
    public static final long CLDC_1_1 = 2949124L;
    public static final long JDK_DEFERRED = Long.MAX_VALUE;
    public static final int INT_ARRAY = 10;
    public static final int BYTE_ARRAY = 8;
    public static final int BOOLEAN_ARRAY = 4;
    public static final int SHORT_ARRAY = 9;
    public static final int CHAR_ARRAY = 5;
    public static final int LONG_ARRAY = 11;
    public static final int FLOAT_ARRAY = 6;
    public static final int DOUBLE_ARRAY = 7;
    public static final int ATTR_SOURCE = 1;
    public static final int ATTR_LINES = 2;
    public static final int ATTR_VARS = 4;
    public static final int ATTR_STACK_MAP_TABLE = 8;
    public static final int ATTR_STACK_MAP = 16;
    public static final int ATTR_TYPE_ANNOTATION = 32;
    public static final int ATTR_METHOD_PARAMETERS = 64;
    public static final int FLAG_SERIALIZABLE = 1;
    public static final int FLAG_MARKERS = 2;
    public static final int FLAG_BRIDGES = 4;

    public static long getLatestJDKLevel() {
        return 0x3C0000L;
    }

    public static long getComplianceLevelForJavaVersion(int major) {
        switch (major) {
            case 45: {
                return 2949123L;
            }
        }
        major = Math.min(major, 60);
        return ((long)major << 16) + 0L;
    }
}

