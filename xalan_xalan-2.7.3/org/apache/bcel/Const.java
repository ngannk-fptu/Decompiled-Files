/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel;

import java.util.Arrays;
import java.util.Collections;

public final class Const {
    public static final int JVM_CLASSFILE_MAGIC = -889275714;
    public static final short MAJOR_1_1 = 45;
    public static final short MINOR_1_1 = 3;
    public static final short MAJOR_1_2 = 46;
    public static final short MINOR_1_2 = 0;
    public static final short MAJOR_1_3 = 47;
    public static final short MINOR_1_3 = 0;
    public static final short MAJOR_1_4 = 48;
    public static final short MINOR_1_4 = 0;
    public static final short MAJOR_1_5 = 49;
    public static final short MINOR_1_5 = 0;
    public static final short MAJOR_1_6 = 50;
    public static final short MINOR_1_6 = 0;
    public static final short MAJOR_1_7 = 51;
    public static final short MINOR_1_7 = 0;
    public static final short MAJOR_1_8 = 52;
    public static final short MINOR_1_8 = 0;
    public static final short MAJOR_9 = 53;
    public static final short MINOR_9 = 0;
    @Deprecated
    public static final short MAJOR_1_9 = 53;
    @Deprecated
    public static final short MINOR_1_9 = 0;
    public static final short MAJOR_10 = 54;
    public static final short MINOR_10 = 0;
    public static final short MAJOR_11 = 55;
    public static final short MINOR_11 = 0;
    public static final short MAJOR_12 = 56;
    public static final short MINOR_12 = 0;
    public static final short MAJOR_13 = 57;
    public static final short MINOR_13 = 0;
    public static final short MINOR_14 = 0;
    public static final short MINOR_15 = 0;
    public static final short MINOR_16 = 0;
    public static final short MINOR_17 = 0;
    public static final short MINOR_18 = 0;
    public static final short MINOR_19 = 0;
    public static final short MAJOR_14 = 58;
    public static final short MAJOR_15 = 59;
    public static final short MAJOR_16 = 60;
    public static final short MAJOR_17 = 61;
    public static final short MAJOR_18 = 62;
    public static final short MAJOR_19 = 63;
    public static final short MAJOR = 45;
    public static final short MINOR = 3;
    public static final int MAX_SHORT = 65535;
    public static final int MAX_BYTE = 255;
    public static final short ACC_PUBLIC = 1;
    public static final short ACC_PRIVATE = 2;
    public static final short ACC_PROTECTED = 4;
    public static final short ACC_STATIC = 8;
    public static final short ACC_FINAL = 16;
    public static final short ACC_OPEN = 32;
    public static final short ACC_SUPER = 32;
    public static final short ACC_SYNCHRONIZED = 32;
    public static final short ACC_TRANSITIVE = 32;
    public static final short ACC_BRIDGE = 64;
    public static final short ACC_STATIC_PHASE = 64;
    public static final short ACC_VOLATILE = 64;
    public static final short ACC_TRANSIENT = 128;
    public static final short ACC_VARARGS = 128;
    public static final short ACC_NATIVE = 256;
    public static final short ACC_INTERFACE = 512;
    public static final short ACC_ABSTRACT = 1024;
    public static final short ACC_STRICT = 2048;
    public static final short ACC_SYNTHETIC = 4096;
    public static final short ACC_ANNOTATION = 8192;
    public static final short ACC_ENUM = 16384;
    public static final short ACC_MANDATED = Short.MIN_VALUE;
    public static final short ACC_MODULE = Short.MIN_VALUE;
    @Deprecated
    public static final short MAX_ACC_FLAG = 16384;
    public static final int MAX_ACC_FLAG_I = 32768;
    private static final String[] ACCESS_NAMES = new String[]{"public", "private", "protected", "static", "final", "synchronized", "volatile", "transient", "native", "interface", "abstract", "strictfp", "synthetic", "annotation", "enum", "module"};
    public static final int ACCESS_NAMES_LENGTH = ACCESS_NAMES.length;
    public static final byte CONSTANT_Utf8 = 1;
    public static final byte CONSTANT_Integer = 3;
    public static final byte CONSTANT_Float = 4;
    public static final byte CONSTANT_Long = 5;
    public static final byte CONSTANT_Double = 6;
    public static final byte CONSTANT_Class = 7;
    public static final byte CONSTANT_Fieldref = 9;
    public static final byte CONSTANT_String = 8;
    public static final byte CONSTANT_Methodref = 10;
    public static final byte CONSTANT_InterfaceMethodref = 11;
    public static final byte CONSTANT_NameAndType = 12;
    public static final byte CONSTANT_MethodHandle = 15;
    public static final byte CONSTANT_MethodType = 16;
    public static final byte CONSTANT_Dynamic = 17;
    public static final byte CONSTANT_InvokeDynamic = 18;
    public static final byte CONSTANT_Module = 19;
    public static final byte CONSTANT_Package = 20;
    private static final String[] CONSTANT_NAMES = new String[]{"", "CONSTANT_Utf8", "", "CONSTANT_Integer", "CONSTANT_Float", "CONSTANT_Long", "CONSTANT_Double", "CONSTANT_Class", "CONSTANT_String", "CONSTANT_Fieldref", "CONSTANT_Methodref", "CONSTANT_InterfaceMethodref", "CONSTANT_NameAndType", "", "", "CONSTANT_MethodHandle", "CONSTANT_MethodType", "CONSTANT_Dynamic", "CONSTANT_InvokeDynamic", "CONSTANT_Module", "CONSTANT_Package"};
    public static final String STATIC_INITIALIZER_NAME = "<clinit>";
    public static final String CONSTRUCTOR_NAME = "<init>";
    private static final String[] INTERFACES_IMPLEMENTED_BY_ARRAYS = new String[]{"java.lang.Cloneable", "java.io.Serializable"};
    public static final int MAX_CP_ENTRIES = 65535;
    public static final int MAX_CODE_SIZE = 65536;
    public static final int MAX_ARRAY_DIMENSIONS = 255;
    public static final short NOP = 0;
    public static final short ACONST_NULL = 1;
    public static final short ICONST_M1 = 2;
    public static final short ICONST_0 = 3;
    public static final short ICONST_1 = 4;
    public static final short ICONST_2 = 5;
    public static final short ICONST_3 = 6;
    public static final short ICONST_4 = 7;
    public static final short ICONST_5 = 8;
    public static final short LCONST_0 = 9;
    public static final short LCONST_1 = 10;
    public static final short FCONST_0 = 11;
    public static final short FCONST_1 = 12;
    public static final short FCONST_2 = 13;
    public static final short DCONST_0 = 14;
    public static final short DCONST_1 = 15;
    public static final short BIPUSH = 16;
    public static final short SIPUSH = 17;
    public static final short LDC = 18;
    public static final short LDC_W = 19;
    public static final short LDC2_W = 20;
    public static final short ILOAD = 21;
    public static final short LLOAD = 22;
    public static final short FLOAD = 23;
    public static final short DLOAD = 24;
    public static final short ALOAD = 25;
    public static final short ILOAD_0 = 26;
    public static final short ILOAD_1 = 27;
    public static final short ILOAD_2 = 28;
    public static final short ILOAD_3 = 29;
    public static final short LLOAD_0 = 30;
    public static final short LLOAD_1 = 31;
    public static final short LLOAD_2 = 32;
    public static final short LLOAD_3 = 33;
    public static final short FLOAD_0 = 34;
    public static final short FLOAD_1 = 35;
    public static final short FLOAD_2 = 36;
    public static final short FLOAD_3 = 37;
    public static final short DLOAD_0 = 38;
    public static final short DLOAD_1 = 39;
    public static final short DLOAD_2 = 40;
    public static final short DLOAD_3 = 41;
    public static final short ALOAD_0 = 42;
    public static final short ALOAD_1 = 43;
    public static final short ALOAD_2 = 44;
    public static final short ALOAD_3 = 45;
    public static final short IALOAD = 46;
    public static final short LALOAD = 47;
    public static final short FALOAD = 48;
    public static final short DALOAD = 49;
    public static final short AALOAD = 50;
    public static final short BALOAD = 51;
    public static final short CALOAD = 52;
    public static final short SALOAD = 53;
    public static final short ISTORE = 54;
    public static final short LSTORE = 55;
    public static final short FSTORE = 56;
    public static final short DSTORE = 57;
    public static final short ASTORE = 58;
    public static final short ISTORE_0 = 59;
    public static final short ISTORE_1 = 60;
    public static final short ISTORE_2 = 61;
    public static final short ISTORE_3 = 62;
    public static final short LSTORE_0 = 63;
    public static final short LSTORE_1 = 64;
    public static final short LSTORE_2 = 65;
    public static final short LSTORE_3 = 66;
    public static final short FSTORE_0 = 67;
    public static final short FSTORE_1 = 68;
    public static final short FSTORE_2 = 69;
    public static final short FSTORE_3 = 70;
    public static final short DSTORE_0 = 71;
    public static final short DSTORE_1 = 72;
    public static final short DSTORE_2 = 73;
    public static final short DSTORE_3 = 74;
    public static final short ASTORE_0 = 75;
    public static final short ASTORE_1 = 76;
    public static final short ASTORE_2 = 77;
    public static final short ASTORE_3 = 78;
    public static final short IASTORE = 79;
    public static final short LASTORE = 80;
    public static final short FASTORE = 81;
    public static final short DASTORE = 82;
    public static final short AASTORE = 83;
    public static final short BASTORE = 84;
    public static final short CASTORE = 85;
    public static final short SASTORE = 86;
    public static final short POP = 87;
    public static final short POP2 = 88;
    public static final short DUP = 89;
    public static final short DUP_X1 = 90;
    public static final short DUP_X2 = 91;
    public static final short DUP2 = 92;
    public static final short DUP2_X1 = 93;
    public static final short DUP2_X2 = 94;
    public static final short SWAP = 95;
    public static final short IADD = 96;
    public static final short LADD = 97;
    public static final short FADD = 98;
    public static final short DADD = 99;
    public static final short ISUB = 100;
    public static final short LSUB = 101;
    public static final short FSUB = 102;
    public static final short DSUB = 103;
    public static final short IMUL = 104;
    public static final short LMUL = 105;
    public static final short FMUL = 106;
    public static final short DMUL = 107;
    public static final short IDIV = 108;
    public static final short LDIV = 109;
    public static final short FDIV = 110;
    public static final short DDIV = 111;
    public static final short IREM = 112;
    public static final short LREM = 113;
    public static final short FREM = 114;
    public static final short DREM = 115;
    public static final short INEG = 116;
    public static final short LNEG = 117;
    public static final short FNEG = 118;
    public static final short DNEG = 119;
    public static final short ISHL = 120;
    public static final short LSHL = 121;
    public static final short ISHR = 122;
    public static final short LSHR = 123;
    public static final short IUSHR = 124;
    public static final short LUSHR = 125;
    public static final short IAND = 126;
    public static final short LAND = 127;
    public static final short IOR = 128;
    public static final short LOR = 129;
    public static final short IXOR = 130;
    public static final short LXOR = 131;
    public static final short IINC = 132;
    public static final short I2L = 133;
    public static final short I2F = 134;
    public static final short I2D = 135;
    public static final short L2I = 136;
    public static final short L2F = 137;
    public static final short L2D = 138;
    public static final short F2I = 139;
    public static final short F2L = 140;
    public static final short F2D = 141;
    public static final short D2I = 142;
    public static final short D2L = 143;
    public static final short D2F = 144;
    public static final short I2B = 145;
    public static final short INT2BYTE = 145;
    public static final short I2C = 146;
    public static final short INT2CHAR = 146;
    public static final short I2S = 147;
    public static final short INT2SHORT = 147;
    public static final short LCMP = 148;
    public static final short FCMPL = 149;
    public static final short FCMPG = 150;
    public static final short DCMPL = 151;
    public static final short DCMPG = 152;
    public static final short IFEQ = 153;
    public static final short IFNE = 154;
    public static final short IFLT = 155;
    public static final short IFGE = 156;
    public static final short IFGT = 157;
    public static final short IFLE = 158;
    public static final short IF_ICMPEQ = 159;
    public static final short IF_ICMPNE = 160;
    public static final short IF_ICMPLT = 161;
    public static final short IF_ICMPGE = 162;
    public static final short IF_ICMPGT = 163;
    public static final short IF_ICMPLE = 164;
    public static final short IF_ACMPEQ = 165;
    public static final short IF_ACMPNE = 166;
    public static final short GOTO = 167;
    public static final short JSR = 168;
    public static final short RET = 169;
    public static final short TABLESWITCH = 170;
    public static final short LOOKUPSWITCH = 171;
    public static final short IRETURN = 172;
    public static final short LRETURN = 173;
    public static final short FRETURN = 174;
    public static final short DRETURN = 175;
    public static final short ARETURN = 176;
    public static final short RETURN = 177;
    public static final short GETSTATIC = 178;
    public static final short PUTSTATIC = 179;
    public static final short GETFIELD = 180;
    public static final short PUTFIELD = 181;
    public static final short INVOKEVIRTUAL = 182;
    public static final short INVOKESPECIAL = 183;
    public static final short INVOKENONVIRTUAL = 183;
    public static final short INVOKESTATIC = 184;
    public static final short INVOKEINTERFACE = 185;
    public static final short INVOKEDYNAMIC = 186;
    public static final short NEW = 187;
    public static final short NEWARRAY = 188;
    public static final short ANEWARRAY = 189;
    public static final short ARRAYLENGTH = 190;
    public static final short ATHROW = 191;
    public static final short CHECKCAST = 192;
    public static final short INSTANCEOF = 193;
    public static final short MONITORENTER = 194;
    public static final short MONITOREXIT = 195;
    public static final short WIDE = 196;
    public static final short MULTIANEWARRAY = 197;
    public static final short IFNULL = 198;
    public static final short IFNONNULL = 199;
    public static final short GOTO_W = 200;
    public static final short JSR_W = 201;
    public static final short BREAKPOINT = 202;
    public static final short LDC_QUICK = 203;
    public static final short LDC_W_QUICK = 204;
    public static final short LDC2_W_QUICK = 205;
    public static final short GETFIELD_QUICK = 206;
    public static final short PUTFIELD_QUICK = 207;
    public static final short GETFIELD2_QUICK = 208;
    public static final short PUTFIELD2_QUICK = 209;
    public static final short GETSTATIC_QUICK = 210;
    public static final short PUTSTATIC_QUICK = 211;
    public static final short GETSTATIC2_QUICK = 212;
    public static final short PUTSTATIC2_QUICK = 213;
    public static final short INVOKEVIRTUAL_QUICK = 214;
    public static final short INVOKENONVIRTUAL_QUICK = 215;
    public static final short INVOKESUPER_QUICK = 216;
    public static final short INVOKESTATIC_QUICK = 217;
    public static final short INVOKEINTERFACE_QUICK = 218;
    public static final short INVOKEVIRTUALOBJECT_QUICK = 219;
    public static final short NEW_QUICK = 221;
    public static final short ANEWARRAY_QUICK = 222;
    public static final short MULTIANEWARRAY_QUICK = 223;
    public static final short CHECKCAST_QUICK = 224;
    public static final short INSTANCEOF_QUICK = 225;
    public static final short INVOKEVIRTUAL_QUICK_W = 226;
    public static final short GETFIELD_QUICK_W = 227;
    public static final short PUTFIELD_QUICK_W = 228;
    public static final short IMPDEP1 = 254;
    public static final short IMPDEP2 = 255;
    public static final short PUSH = 4711;
    public static final short SWITCH = 4712;
    public static final short UNDEFINED = -1;
    public static final short UNPREDICTABLE = -2;
    public static final short RESERVED = -3;
    public static final String ILLEGAL_OPCODE = "<illegal opcode>";
    public static final String ILLEGAL_TYPE = "<illegal type>";
    public static final byte T_BOOLEAN = 4;
    public static final byte T_CHAR = 5;
    public static final byte T_FLOAT = 6;
    public static final byte T_DOUBLE = 7;
    public static final byte T_BYTE = 8;
    public static final byte T_SHORT = 9;
    public static final byte T_INT = 10;
    public static final byte T_LONG = 11;
    public static final byte T_VOID = 12;
    public static final byte T_ARRAY = 13;
    public static final byte T_OBJECT = 14;
    public static final byte T_REFERENCE = 14;
    public static final byte T_UNKNOWN = 15;
    public static final byte T_ADDRESS = 16;
    private static final String[] TYPE_NAMES = new String[]{"<illegal type>", "<illegal type>", "<illegal type>", "<illegal type>", "boolean", "char", "float", "double", "byte", "short", "int", "long", "void", "array", "object", "unknown", "address"};
    private static final String[] CLASS_TYPE_NAMES = new String[]{"<illegal type>", "<illegal type>", "<illegal type>", "<illegal type>", "java.lang.Boolean", "java.lang.Character", "java.lang.Float", "java.lang.Double", "java.lang.Byte", "java.lang.Short", "java.lang.Integer", "java.lang.Long", "java.lang.Void", "<illegal type>", "<illegal type>", "<illegal type>", "<illegal type>"};
    private static final String[] SHORT_TYPE_NAMES = new String[]{"<illegal type>", "<illegal type>", "<illegal type>", "<illegal type>", "Z", "C", "F", "D", "B", "S", "I", "J", "V", "<illegal type>", "<illegal type>", "<illegal type>"};
    static final short[] NO_OF_OPERANDS = new short[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 1, 2, 2, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, -2, -2, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 4, 4, 2, 1, 2, 0, 0, 2, 2, 0, 0, -2, 3, 2, 2, 4, 4, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -3, -3};
    static final short[][] TYPE_OF_OPERANDS = new short[][]{new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], {8}, {9}, {8}, {9}, {9}, {8}, {8}, {8}, {8}, {8}, new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], {8}, {8}, {8}, {8}, {8}, new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], {8, 8}, new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], {9}, {9}, {9}, {9}, {9}, {9}, {9}, {9}, {9}, {9}, {9}, {9}, {9}, {9}, {9}, {9}, {8}, new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], {9}, {9}, {9}, {9}, {9}, {9}, {9}, {9, 8, 8}, {9, 8, 8}, {9}, {8}, {9}, new short[0], new short[0], {9}, {9}, new short[0], new short[0], {8}, {9, 8}, {9}, {9}, {10}, {10}, new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0], new short[0]};
    static final String[] OPCODE_NAMES = new String[]{"nop", "aconst_null", "iconst_m1", "iconst_0", "iconst_1", "iconst_2", "iconst_3", "iconst_4", "iconst_5", "lconst_0", "lconst_1", "fconst_0", "fconst_1", "fconst_2", "dconst_0", "dconst_1", "bipush", "sipush", "ldc", "ldc_w", "ldc2_w", "iload", "lload", "fload", "dload", "aload", "iload_0", "iload_1", "iload_2", "iload_3", "lload_0", "lload_1", "lload_2", "lload_3", "fload_0", "fload_1", "fload_2", "fload_3", "dload_0", "dload_1", "dload_2", "dload_3", "aload_0", "aload_1", "aload_2", "aload_3", "iaload", "laload", "faload", "daload", "aaload", "baload", "caload", "saload", "istore", "lstore", "fstore", "dstore", "astore", "istore_0", "istore_1", "istore_2", "istore_3", "lstore_0", "lstore_1", "lstore_2", "lstore_3", "fstore_0", "fstore_1", "fstore_2", "fstore_3", "dstore_0", "dstore_1", "dstore_2", "dstore_3", "astore_0", "astore_1", "astore_2", "astore_3", "iastore", "lastore", "fastore", "dastore", "aastore", "bastore", "castore", "sastore", "pop", "pop2", "dup", "dup_x1", "dup_x2", "dup2", "dup2_x1", "dup2_x2", "swap", "iadd", "ladd", "fadd", "dadd", "isub", "lsub", "fsub", "dsub", "imul", "lmul", "fmul", "dmul", "idiv", "ldiv", "fdiv", "ddiv", "irem", "lrem", "frem", "drem", "ineg", "lneg", "fneg", "dneg", "ishl", "lshl", "ishr", "lshr", "iushr", "lushr", "iand", "land", "ior", "lor", "ixor", "lxor", "iinc", "i2l", "i2f", "i2d", "l2i", "l2f", "l2d", "f2i", "f2l", "f2d", "d2i", "d2l", "d2f", "i2b", "i2c", "i2s", "lcmp", "fcmpl", "fcmpg", "dcmpl", "dcmpg", "ifeq", "ifne", "iflt", "ifge", "ifgt", "ifle", "if_icmpeq", "if_icmpne", "if_icmplt", "if_icmpge", "if_icmpgt", "if_icmple", "if_acmpeq", "if_acmpne", "goto", "jsr", "ret", "tableswitch", "lookupswitch", "ireturn", "lreturn", "freturn", "dreturn", "areturn", "return", "getstatic", "putstatic", "getfield", "putfield", "invokevirtual", "invokespecial", "invokestatic", "invokeinterface", "invokedynamic", "new", "newarray", "anewarray", "arraylength", "athrow", "checkcast", "instanceof", "monitorenter", "monitorexit", "wide", "multianewarray", "ifnull", "ifnonnull", "goto_w", "jsr_w", "breakpoint", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "<illegal opcode>", "impdep1", "impdep2"};
    public static final int OPCODE_NAMES_LENGTH = OPCODE_NAMES.length;
    static final int[] CONSUME_STACK = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 2, 2, 2, 2, 2, 1, 2, 1, 2, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 3, 4, 3, 4, 3, 3, 3, 3, 1, 2, 1, 2, 3, 2, 3, 4, 2, 2, 4, 2, 4, 2, 4, 2, 4, 2, 4, 2, 4, 2, 4, 2, 4, 2, 4, 2, 4, 1, 2, 1, 2, 2, 3, 2, 3, 2, 3, 2, 4, 2, 4, 2, 4, 0, 1, 1, 1, 2, 2, 2, 1, 1, 1, 2, 2, 2, 1, 1, 1, 4, 2, 2, 4, 4, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 1, 1, 1, 2, 1, 2, 1, 0, 0, -2, 1, -2, -2, -2, -2, -2, -2, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, -2, 1, 1, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2, -2};
    static final int[] PRODUCE_STACK = new int[]{0, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 2, 2, 1, 1, 1, 1, 2, 1, 2, 1, 2, 1, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 3, 4, 4, 5, 6, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 0, 2, 1, 2, 1, 1, 2, 1, 2, 2, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, -2, 0, -2, 0, -2, -2, -2, -2, -2, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -2, -2};
    public static final byte ATTR_UNKNOWN = -1;
    public static final byte ATTR_SOURCE_FILE = 0;
    public static final byte ATTR_CONSTANT_VALUE = 1;
    public static final byte ATTR_CODE = 2;
    public static final byte ATTR_EXCEPTIONS = 3;
    public static final byte ATTR_LINE_NUMBER_TABLE = 4;
    public static final byte ATTR_LOCAL_VARIABLE_TABLE = 5;
    public static final byte ATTR_INNER_CLASSES = 6;
    public static final byte ATTR_SYNTHETIC = 7;
    public static final byte ATTR_DEPRECATED = 8;
    public static final byte ATTR_PMG = 9;
    public static final byte ATTR_SIGNATURE = 10;
    public static final byte ATTR_STACK_MAP = 11;
    public static final byte ATTR_RUNTIME_VISIBLE_ANNOTATIONS = 12;
    public static final byte ATTR_RUNTIME_INVISIBLE_ANNOTATIONS = 13;
    public static final byte ATTR_RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS = 14;
    public static final byte ATTR_RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS = 15;
    public static final byte ATTR_ANNOTATION_DEFAULT = 16;
    public static final byte ATTR_LOCAL_VARIABLE_TYPE_TABLE = 17;
    public static final byte ATTR_ENCLOSING_METHOD = 18;
    public static final byte ATTR_STACK_MAP_TABLE = 19;
    public static final byte ATTR_BOOTSTRAP_METHODS = 20;
    public static final byte ATTR_METHOD_PARAMETERS = 21;
    public static final byte ATTR_MODULE = 22;
    public static final byte ATTR_MODULE_PACKAGES = 23;
    public static final byte ATTR_MODULE_MAIN_CLASS = 24;
    public static final byte ATTR_NEST_HOST = 25;
    public static final byte ATTR_NEST_MEMBERS = 26;
    public static final short KNOWN_ATTRIBUTES = 27;
    private static final String[] ATTRIBUTE_NAMES = new String[]{"SourceFile", "ConstantValue", "Code", "Exceptions", "LineNumberTable", "LocalVariableTable", "InnerClasses", "Synthetic", "Deprecated", "PMGClass", "Signature", "StackMap", "RuntimeVisibleAnnotations", "RuntimeInvisibleAnnotations", "RuntimeVisibleParameterAnnotations", "RuntimeInvisibleParameterAnnotations", "AnnotationDefault", "LocalVariableTypeTable", "EnclosingMethod", "StackMapTable", "BootstrapMethods", "MethodParameters", "Module", "ModulePackages", "ModuleMainClass", "NestHost", "NestMembers"};
    public static final byte ITEM_Bogus = 0;
    public static final byte ITEM_Integer = 1;
    public static final byte ITEM_Float = 2;
    public static final byte ITEM_Double = 3;
    public static final byte ITEM_Long = 4;
    public static final byte ITEM_Null = 5;
    public static final byte ITEM_InitObject = 6;
    public static final byte ITEM_Object = 7;
    public static final byte ITEM_NewObject = 8;
    private static final String[] ITEM_NAMES = new String[]{"Bogus", "Integer", "Float", "Double", "Long", "Null", "InitObject", "Object", "NewObject"};
    public static final int SAME_FRAME = 0;
    public static final int SAME_LOCALS_1_STACK_ITEM_FRAME = 64;
    public static final int SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED = 247;
    public static final int CHOP_FRAME = 248;
    public static final int SAME_FRAME_EXTENDED = 251;
    public static final int APPEND_FRAME = 252;
    public static final int FULL_FRAME = 255;
    public static final int SAME_FRAME_MAX = 63;
    public static final int SAME_LOCALS_1_STACK_ITEM_FRAME_MAX = 127;
    public static final int CHOP_FRAME_MAX = 250;
    public static final int APPEND_FRAME_MAX = 254;
    public static final byte REF_getField = 1;
    public static final byte REF_getStatic = 2;
    public static final byte REF_putField = 3;
    public static final byte REF_putStatic = 4;
    public static final byte REF_invokeVirtual = 5;
    public static final byte REF_invokeStatic = 6;
    public static final byte REF_invokeSpecial = 7;
    public static final byte REF_newInvokeSpecial = 8;
    public static final byte REF_invokeInterface = 9;
    private static final String[] METHODHANDLE_NAMES = new String[]{"", "getField", "getStatic", "putField", "putStatic", "invokeVirtual", "invokeStatic", "invokeSpecial", "newInvokeSpecial", "invokeInterface"};

    public static String getAccessName(int index) {
        return ACCESS_NAMES[index];
    }

    public static String getAttributeName(int index) {
        return ATTRIBUTE_NAMES[index];
    }

    public static String getClassTypeName(int index) {
        return CLASS_TYPE_NAMES[index];
    }

    public static String getConstantName(int index) {
        return CONSTANT_NAMES[index];
    }

    public static int getConsumeStack(int index) {
        return CONSUME_STACK[index];
    }

    public static Iterable<String> getInterfacesImplementedByArrays() {
        return Collections.unmodifiableList(Arrays.asList(INTERFACES_IMPLEMENTED_BY_ARRAYS));
    }

    public static String getItemName(int index) {
        return ITEM_NAMES[index];
    }

    public static String getMethodHandleName(int index) {
        return METHODHANDLE_NAMES[index];
    }

    public static short getNoOfOperands(int index) {
        return NO_OF_OPERANDS[index];
    }

    public static String getOpcodeName(int index) {
        return OPCODE_NAMES[index];
    }

    public static short getOperandType(int opcode, int index) {
        return TYPE_OF_OPERANDS[opcode][index];
    }

    public static long getOperandTypeCount(int opcode) {
        return TYPE_OF_OPERANDS[opcode].length;
    }

    public static int getProduceStack(int index) {
        return PRODUCE_STACK[index];
    }

    public static String getShortTypeName(int index) {
        return SHORT_TYPE_NAMES[index];
    }

    public static String getTypeName(int index) {
        return TYPE_NAMES[index];
    }

    private Const() {
    }
}

