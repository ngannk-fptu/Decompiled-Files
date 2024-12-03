/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.Utility;
import org.aspectj.apache.bcel.generic.ArrayType;
import org.aspectj.apache.bcel.generic.BasicType;
import org.aspectj.apache.bcel.generic.ClassGen;
import org.aspectj.apache.bcel.generic.ClassGenException;
import org.aspectj.apache.bcel.generic.FieldInstruction;
import org.aspectj.apache.bcel.generic.INVOKEINTERFACE;
import org.aspectj.apache.bcel.generic.Instruction;
import org.aspectj.apache.bcel.generic.InstructionBranch;
import org.aspectj.apache.bcel.generic.InstructionByte;
import org.aspectj.apache.bcel.generic.InstructionCP;
import org.aspectj.apache.bcel.generic.InstructionConstants;
import org.aspectj.apache.bcel.generic.InstructionHandle;
import org.aspectj.apache.bcel.generic.InstructionLV;
import org.aspectj.apache.bcel.generic.InstructionList;
import org.aspectj.apache.bcel.generic.InstructionShort;
import org.aspectj.apache.bcel.generic.InvokeInstruction;
import org.aspectj.apache.bcel.generic.MULTIANEWARRAY;
import org.aspectj.apache.bcel.generic.ObjectType;
import org.aspectj.apache.bcel.generic.ReferenceType;
import org.aspectj.apache.bcel.generic.Type;

public class InstructionFactory
implements InstructionConstants {
    protected ClassGen cg;
    protected ConstantPool cp;
    private static final char[] shortNames = new char[]{'C', 'F', 'D', 'B', 'S', 'I', 'L'};

    public InstructionFactory(ClassGen cg, ConstantPool cp) {
        this.cg = cg;
        this.cp = cp;
    }

    public InstructionFactory(ClassGen cg) {
        this(cg, cg.getConstantPool());
    }

    public InstructionFactory(ConstantPool cp) {
        this(null, cp);
    }

    public InvokeInstruction createInvoke(String class_name, String name, Type ret_type, Type[] arg_types, short kind) {
        return this.createInvoke(class_name, name, ret_type, arg_types, kind, false);
    }

    public InvokeInstruction createInvoke(String class_name, String name, Type ret_type, Type[] arg_types, short kind, boolean isInterface) {
        int index;
        String signature = Utility.toMethodSignature(ret_type, arg_types);
        if (kind == 185 || isInterface) {
            index = this.cp.addInterfaceMethodref(class_name, name, signature);
        } else {
            if (kind == 186) {
                throw new IllegalStateException("NYI");
            }
            index = this.cp.addMethodref(class_name, name, signature);
        }
        switch (kind) {
            case 183: {
                return new InvokeInstruction(183, index);
            }
            case 182: {
                return new InvokeInstruction(182, index);
            }
            case 184: {
                return new InvokeInstruction(184, index);
            }
            case 185: {
                int nargs = 0;
                for (int i = 0; i < arg_types.length; ++i) {
                    nargs += arg_types[i].getSize();
                }
                return new INVOKEINTERFACE(index, nargs + 1, 0);
            }
        }
        throw new RuntimeException("Oops: Unknown invoke kind:" + kind);
    }

    public InvokeInstruction createInvoke(String class_name, String name, String signature, short kind) {
        int index;
        if (kind == 185) {
            index = this.cp.addInterfaceMethodref(class_name, name, signature);
        } else {
            if (kind == 186) {
                throw new IllegalStateException("NYI");
            }
            index = this.cp.addMethodref(class_name, name, signature);
        }
        switch (kind) {
            case 183: {
                return new InvokeInstruction(183, index);
            }
            case 182: {
                return new InvokeInstruction(182, index);
            }
            case 184: {
                return new InvokeInstruction(184, index);
            }
            case 185: {
                Type[] argumentTypes = Type.getArgumentTypes(signature);
                int nargs = 0;
                for (int i = 0; i < argumentTypes.length; ++i) {
                    nargs += argumentTypes[i].getSize();
                }
                return new INVOKEINTERFACE(index, nargs + 1, 0);
            }
        }
        throw new RuntimeException("Oops: Unknown invoke kind:" + kind);
    }

    public static Instruction createALOAD(int n) {
        if (n < 4) {
            return new InstructionLV((short)(42 + n));
        }
        return new InstructionLV(25, n);
    }

    public static Instruction createASTORE(int n) {
        if (n < 4) {
            return new InstructionLV((short)(75 + n));
        }
        return new InstructionLV(58, n);
    }

    public Instruction createConstant(Object value) {
        Instruction instruction;
        if (value instanceof Number) {
            instruction = InstructionFactory.PUSH(this.cp, (Number)value);
        } else if (value instanceof String) {
            instruction = InstructionFactory.PUSH(this.cp, (String)value);
        } else if (value instanceof Boolean) {
            instruction = InstructionFactory.PUSH(this.cp, (Boolean)value);
        } else if (value instanceof Character) {
            instruction = InstructionFactory.PUSH(this.cp, (Character)value);
        } else if (value instanceof ObjectType) {
            instruction = InstructionFactory.PUSH(this.cp, (ObjectType)value);
        } else {
            throw new ClassGenException("Illegal type: " + value.getClass());
        }
        return instruction;
    }

    public FieldInstruction createFieldAccess(String class_name, String name, Type type, short kind) {
        String signature = type.getSignature();
        int index = this.cp.addFieldref(class_name, name, signature);
        switch (kind) {
            case 180: {
                return new FieldInstruction(180, index);
            }
            case 181: {
                return new FieldInstruction(181, index);
            }
            case 178: {
                return new FieldInstruction(178, index);
            }
            case 179: {
                return new FieldInstruction(179, index);
            }
        }
        throw new RuntimeException("Oops: Unknown getfield kind:" + kind);
    }

    public static Instruction createThis() {
        return new InstructionLV(25, 0);
    }

    public static Instruction createReturn(Type type) {
        switch (type.getType()) {
            case 13: 
            case 14: {
                return ARETURN;
            }
            case 4: 
            case 5: 
            case 8: 
            case 9: 
            case 10: {
                return IRETURN;
            }
            case 6: {
                return FRETURN;
            }
            case 7: {
                return DRETURN;
            }
            case 11: {
                return LRETURN;
            }
            case 12: {
                return RETURN;
            }
        }
        throw new RuntimeException("Invalid type: " + type);
    }

    public static Instruction createPop(int size) {
        return size == 2 ? POP2 : POP;
    }

    public static Instruction createDup(int size) {
        return size == 2 ? DUP2 : DUP;
    }

    public static Instruction createDup_2(int size) {
        return size == 2 ? DUP2_X2 : DUP_X2;
    }

    public static Instruction createDup_1(int size) {
        return size == 2 ? DUP2_X1 : DUP_X1;
    }

    public static InstructionLV createStore(Type type, int index) {
        switch (type.getType()) {
            case 4: 
            case 5: 
            case 8: 
            case 9: 
            case 10: {
                return new InstructionLV(54, index);
            }
            case 6: {
                return new InstructionLV(56, index);
            }
            case 7: {
                return new InstructionLV(57, index);
            }
            case 11: {
                return new InstructionLV(55, index);
            }
            case 13: 
            case 14: {
                return new InstructionLV(58, index);
            }
        }
        throw new RuntimeException("Invalid type " + type);
    }

    public static InstructionLV createLoad(Type type, int index) {
        switch (type.getType()) {
            case 4: 
            case 5: 
            case 8: 
            case 9: 
            case 10: {
                return new InstructionLV(21, index);
            }
            case 6: {
                return new InstructionLV(23, index);
            }
            case 7: {
                return new InstructionLV(24, index);
            }
            case 11: {
                return new InstructionLV(22, index);
            }
            case 13: 
            case 14: {
                return new InstructionLV(25, index);
            }
        }
        throw new RuntimeException("Invalid type " + type);
    }

    public static Instruction createArrayLoad(Type type) {
        switch (type.getType()) {
            case 4: 
            case 8: {
                return BALOAD;
            }
            case 5: {
                return CALOAD;
            }
            case 9: {
                return SALOAD;
            }
            case 10: {
                return IALOAD;
            }
            case 6: {
                return FALOAD;
            }
            case 7: {
                return DALOAD;
            }
            case 11: {
                return LALOAD;
            }
            case 13: 
            case 14: {
                return AALOAD;
            }
        }
        throw new RuntimeException("Invalid type " + type);
    }

    public static Instruction createArrayStore(Type type) {
        switch (type.getType()) {
            case 4: 
            case 8: {
                return BASTORE;
            }
            case 5: {
                return CASTORE;
            }
            case 9: {
                return SASTORE;
            }
            case 10: {
                return IASTORE;
            }
            case 6: {
                return FASTORE;
            }
            case 7: {
                return DASTORE;
            }
            case 11: {
                return LASTORE;
            }
            case 13: 
            case 14: {
                return AASTORE;
            }
        }
        throw new RuntimeException("Invalid type " + type);
    }

    public Instruction createCast(Type src_type, Type dest_type) {
        if (src_type instanceof BasicType && dest_type instanceof BasicType) {
            byte dest = dest_type.getType();
            int src = src_type.getType();
            if (dest == 11 && (src == 5 || src == 8 || src == 9)) {
                src = 10;
            }
            if (src == 7) {
                switch (dest) {
                    case 6: {
                        return InstructionConstants.D2F;
                    }
                    case 10: {
                        return InstructionConstants.D2I;
                    }
                    case 11: {
                        return InstructionConstants.D2L;
                    }
                }
            } else if (src == 6) {
                switch (dest) {
                    case 7: {
                        return InstructionConstants.F2D;
                    }
                    case 10: {
                        return InstructionConstants.F2I;
                    }
                    case 11: {
                        return InstructionConstants.F2L;
                    }
                }
            } else if (src == 10) {
                switch (dest) {
                    case 8: {
                        return InstructionConstants.I2B;
                    }
                    case 5: {
                        return InstructionConstants.I2C;
                    }
                    case 7: {
                        return InstructionConstants.I2D;
                    }
                    case 6: {
                        return InstructionConstants.I2F;
                    }
                    case 11: {
                        return InstructionConstants.I2L;
                    }
                    case 9: {
                        return InstructionConstants.I2S;
                    }
                }
            } else if (src == 11) {
                switch (dest) {
                    case 7: {
                        return InstructionConstants.L2D;
                    }
                    case 6: {
                        return InstructionConstants.L2F;
                    }
                    case 10: {
                        return InstructionConstants.L2I;
                    }
                }
            }
            return null;
        }
        if (src_type instanceof ReferenceType && dest_type instanceof ReferenceType) {
            if (dest_type instanceof ArrayType) {
                return new InstructionCP(192, this.cp.addArrayClass((ArrayType)dest_type));
            }
            return new InstructionCP(192, this.cp.addClass(((ObjectType)dest_type).getClassName()));
        }
        throw new RuntimeException("Can not cast " + src_type + " to " + dest_type);
    }

    public FieldInstruction createGetField(String class_name, String name, Type t) {
        return new FieldInstruction(180, this.cp.addFieldref(class_name, name, t.getSignature()));
    }

    public FieldInstruction createGetStatic(String class_name, String name, Type t) {
        return new FieldInstruction(178, this.cp.addFieldref(class_name, name, t.getSignature()));
    }

    public FieldInstruction createPutField(String class_name, String name, Type t) {
        return new FieldInstruction(181, this.cp.addFieldref(class_name, name, t.getSignature()));
    }

    public FieldInstruction createPutStatic(String class_name, String name, Type t) {
        return new FieldInstruction(179, this.cp.addFieldref(class_name, name, t.getSignature()));
    }

    public Instruction createCheckCast(ReferenceType t) {
        if (t instanceof ArrayType) {
            return new InstructionCP(192, this.cp.addArrayClass((ArrayType)t));
        }
        return new InstructionCP(192, this.cp.addClass((ObjectType)t));
    }

    public Instruction createInstanceOf(ReferenceType t) {
        if (t instanceof ArrayType) {
            return new InstructionCP(193, this.cp.addArrayClass((ArrayType)t));
        }
        return new InstructionCP(193, this.cp.addClass((ObjectType)t));
    }

    public Instruction createNew(ObjectType t) {
        return new InstructionCP(187, this.cp.addClass(t));
    }

    public Instruction createNew(String s) {
        return this.createNew(new ObjectType(s));
    }

    public Instruction createNewArray(Type t, short dim) {
        if (dim == 1) {
            if (t instanceof ObjectType) {
                return new InstructionCP(189, this.cp.addClass((ObjectType)t));
            }
            if (t instanceof ArrayType) {
                return new InstructionCP(189, this.cp.addArrayClass((ArrayType)t));
            }
            return new InstructionByte(188, ((BasicType)t).getType());
        }
        ArrayType at = t instanceof ArrayType ? (ArrayType)t : new ArrayType(t, (int)dim);
        return new MULTIANEWARRAY(this.cp.addArrayClass(at), dim);
    }

    public static Instruction createNull(Type type) {
        switch (type.getType()) {
            case 13: 
            case 14: {
                return ACONST_NULL;
            }
            case 4: 
            case 5: 
            case 8: 
            case 9: 
            case 10: {
                return ICONST_0;
            }
            case 6: {
                return FCONST_0;
            }
            case 7: {
                return DCONST_0;
            }
            case 11: {
                return LCONST_0;
            }
            case 12: {
                return NOP;
            }
        }
        throw new RuntimeException("Invalid type: " + type);
    }

    public static InstructionBranch createBranchInstruction(short opcode, InstructionHandle target) {
        switch (opcode) {
            case 153: {
                return new InstructionBranch(153, target);
            }
            case 154: {
                return new InstructionBranch(154, target);
            }
            case 155: {
                return new InstructionBranch(155, target);
            }
            case 156: {
                return new InstructionBranch(156, target);
            }
            case 157: {
                return new InstructionBranch(157, target);
            }
            case 158: {
                return new InstructionBranch(158, target);
            }
            case 159: {
                return new InstructionBranch(159, target);
            }
            case 160: {
                return new InstructionBranch(160, target);
            }
            case 161: {
                return new InstructionBranch(161, target);
            }
            case 162: {
                return new InstructionBranch(162, target);
            }
            case 163: {
                return new InstructionBranch(163, target);
            }
            case 164: {
                return new InstructionBranch(164, target);
            }
            case 165: {
                return new InstructionBranch(165, target);
            }
            case 166: {
                return new InstructionBranch(166, target);
            }
            case 167: {
                return new InstructionBranch(167, target);
            }
            case 168: {
                return new InstructionBranch(168, target);
            }
            case 198: {
                return new InstructionBranch(198, target);
            }
            case 199: {
                return new InstructionBranch(199, target);
            }
            case 200: {
                return new InstructionBranch(200, target);
            }
            case 201: {
                return new InstructionBranch(201, target);
            }
        }
        throw new RuntimeException("Invalid opcode: " + opcode);
    }

    public void setClassGen(ClassGen c) {
        this.cg = c;
    }

    public ClassGen getClassGen() {
        return this.cg;
    }

    public void setConstantPool(ConstantPool c) {
        this.cp = c;
    }

    public ConstantPool getConstantPool() {
        return this.cp;
    }

    public static Instruction PUSH(ConstantPool cp, int value) {
        int pos;
        Instruction instruction = null;
        if (value >= -1 && value <= 5) {
            return INSTRUCTIONS[3 + value];
        }
        instruction = value >= -128 && value <= 127 ? new InstructionByte(16, (byte)value) : (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE ? new InstructionShort(17, (short)value) : ((pos = cp.addInteger(value)) <= 255 ? new InstructionCP(18, pos) : new InstructionCP(19, pos)));
        return instruction;
    }

    public static Instruction PUSH(ConstantPool cp, ObjectType t) {
        return new InstructionCP(19, cp.addClass(t));
    }

    public static Instruction PUSH(ConstantPool cp, boolean value) {
        return INSTRUCTIONS[3 + (value ? 1 : 0)];
    }

    public static Instruction PUSH(ConstantPool cp, float value) {
        int i;
        Instruction instruction = null;
        instruction = (double)value == 0.0 ? FCONST_0 : ((double)value == 1.0 ? FCONST_1 : ((double)value == 2.0 ? FCONST_2 : new InstructionCP((i = cp.addFloat(value)) <= 255 ? (short)18 : 19, i)));
        return instruction;
    }

    public static Instruction PUSH(ConstantPool cp, long value) {
        Instruction instruction = null;
        instruction = value == 0L ? LCONST_0 : (value == 1L ? LCONST_1 : new InstructionCP(20, cp.addLong(value)));
        return instruction;
    }

    public static Instruction PUSH(ConstantPool cp, double value) {
        Instruction instruction = null;
        instruction = value == 0.0 ? DCONST_0 : (value == 1.0 ? DCONST_1 : new InstructionCP(20, cp.addDouble(value)));
        return instruction;
    }

    public static Instruction PUSH(ConstantPool cp, String value) {
        int i;
        Instruction instruction = null;
        instruction = value == null ? ACONST_NULL : new InstructionCP((i = cp.addString(value)) <= 255 ? (short)18 : 19, i);
        return instruction;
    }

    public static Instruction PUSH(ConstantPool cp, Number value) {
        Instruction instruction = null;
        if (value instanceof Integer || value instanceof Short || value instanceof Byte) {
            instruction = InstructionFactory.PUSH(cp, value.intValue());
        } else if (value instanceof Double) {
            instruction = InstructionFactory.PUSH(cp, value.doubleValue());
        } else if (value instanceof Float) {
            instruction = InstructionFactory.PUSH(cp, value.floatValue());
        } else if (value instanceof Long) {
            instruction = InstructionFactory.PUSH(cp, value.longValue());
        } else {
            throw new ClassGenException("What's this: " + value);
        }
        return instruction;
    }

    public static Instruction PUSH(ConstantPool cp, Character value) {
        return InstructionFactory.PUSH(cp, (int)value.charValue());
    }

    public static Instruction PUSH(ConstantPool cp, Boolean value) {
        return InstructionFactory.PUSH(cp, (boolean)value);
    }

    public InstructionList PUSHCLASS(ConstantPool cp, String className) {
        InstructionList iList = new InstructionList();
        int classIndex = cp.addClass(className);
        if (this.cg != null && this.cg.getMajor() >= 49) {
            if (classIndex <= 255) {
                iList.append(new InstructionCP(18, classIndex));
            } else {
                iList.append(new InstructionCP(19, classIndex));
            }
        } else {
            className = className.replace('/', '.');
            iList.append(InstructionFactory.PUSH(cp, className));
            iList.append(this.createInvoke("java.lang.Class", "forName", ObjectType.CLASS, Type.STRINGARRAY1, (short)184));
        }
        return iList;
    }
}

