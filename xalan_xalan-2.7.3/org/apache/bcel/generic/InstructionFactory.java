/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ANEWARRAY;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.ArithmeticInstruction;
import org.apache.bcel.generic.ArrayInstruction;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.CHECKCAST;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ClassGenException;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.DLOAD;
import org.apache.bcel.generic.DSTORE;
import org.apache.bcel.generic.FLOAD;
import org.apache.bcel.generic.FSTORE;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.GOTO;
import org.apache.bcel.generic.GOTO_W;
import org.apache.bcel.generic.IFEQ;
import org.apache.bcel.generic.IFGE;
import org.apache.bcel.generic.IFGT;
import org.apache.bcel.generic.IFLE;
import org.apache.bcel.generic.IFLT;
import org.apache.bcel.generic.IFNE;
import org.apache.bcel.generic.IFNONNULL;
import org.apache.bcel.generic.IFNULL;
import org.apache.bcel.generic.IF_ACMPEQ;
import org.apache.bcel.generic.IF_ACMPNE;
import org.apache.bcel.generic.IF_ICMPEQ;
import org.apache.bcel.generic.IF_ICMPGE;
import org.apache.bcel.generic.IF_ICMPGT;
import org.apache.bcel.generic.IF_ICMPLE;
import org.apache.bcel.generic.IF_ICMPLT;
import org.apache.bcel.generic.IF_ICMPNE;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.INSTANCEOF;
import org.apache.bcel.generic.INVOKEDYNAMIC;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.ISTORE;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionConst;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.JSR;
import org.apache.bcel.generic.JSR_W;
import org.apache.bcel.generic.LLOAD;
import org.apache.bcel.generic.LSTORE;
import org.apache.bcel.generic.LocalVariableInstruction;
import org.apache.bcel.generic.MULTIANEWARRAY;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.NEWARRAY;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.PUSH;
import org.apache.bcel.generic.PUTFIELD;
import org.apache.bcel.generic.PUTSTATIC;
import org.apache.bcel.generic.ReferenceType;
import org.apache.bcel.generic.ReturnInstruction;
import org.apache.bcel.generic.StackInstruction;
import org.apache.bcel.generic.Type;

public class InstructionFactory
implements InstructionConstants {
    private static final String APPEND = "append";
    private static final String FQCN_STRING_BUFFER = "java.lang.StringBuffer";
    private static final String[] shortNames = new String[]{"C", "F", "D", "B", "S", "I", "L"};
    private static final MethodObject[] appendMethodObjects = new MethodObject[]{new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[]{Type.STRING}), new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[]{Type.OBJECT}), null, null, new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[]{Type.BOOLEAN}), new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[]{Type.CHAR}), new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[]{Type.FLOAT}), new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[]{Type.DOUBLE}), new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[]{Type.INT}), new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[]{Type.INT}), new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[]{Type.INT}), new MethodObject("java.lang.StringBuffer", "append", Type.STRINGBUFFER, new Type[]{Type.LONG})};
    @Deprecated
    protected ClassGen cg;
    @Deprecated
    protected ConstantPoolGen cp;

    public static ArrayInstruction createArrayLoad(Type type) {
        switch (type.getType()) {
            case 4: 
            case 8: {
                return InstructionConst.BALOAD;
            }
            case 5: {
                return InstructionConst.CALOAD;
            }
            case 9: {
                return InstructionConst.SALOAD;
            }
            case 10: {
                return InstructionConst.IALOAD;
            }
            case 6: {
                return InstructionConst.FALOAD;
            }
            case 7: {
                return InstructionConst.DALOAD;
            }
            case 11: {
                return InstructionConst.LALOAD;
            }
            case 13: 
            case 14: {
                return InstructionConst.AALOAD;
            }
        }
        throw new IllegalArgumentException("Invalid type " + type);
    }

    public static ArrayInstruction createArrayStore(Type type) {
        switch (type.getType()) {
            case 4: 
            case 8: {
                return InstructionConst.BASTORE;
            }
            case 5: {
                return InstructionConst.CASTORE;
            }
            case 9: {
                return InstructionConst.SASTORE;
            }
            case 10: {
                return InstructionConst.IASTORE;
            }
            case 6: {
                return InstructionConst.FASTORE;
            }
            case 7: {
                return InstructionConst.DASTORE;
            }
            case 11: {
                return InstructionConst.LASTORE;
            }
            case 13: 
            case 14: {
                return InstructionConst.AASTORE;
            }
        }
        throw new IllegalArgumentException("Invalid type " + type);
    }

    private static ArithmeticInstruction createBinaryDoubleOp(char op) {
        switch (op) {
            case '-': {
                return InstructionConst.DSUB;
            }
            case '+': {
                return InstructionConst.DADD;
            }
            case '*': {
                return InstructionConst.DMUL;
            }
            case '/': {
                return InstructionConst.DDIV;
            }
            case '%': {
                return InstructionConst.DREM;
            }
        }
        throw new IllegalArgumentException("Invalid operand " + op);
    }

    private static ArithmeticInstruction createBinaryFloatOp(char op) {
        switch (op) {
            case '-': {
                return InstructionConst.FSUB;
            }
            case '+': {
                return InstructionConst.FADD;
            }
            case '*': {
                return InstructionConst.FMUL;
            }
            case '/': {
                return InstructionConst.FDIV;
            }
            case '%': {
                return InstructionConst.FREM;
            }
        }
        throw new IllegalArgumentException("Invalid operand " + op);
    }

    private static ArithmeticInstruction createBinaryIntOp(char first, String op) {
        switch (first) {
            case '-': {
                return InstructionConst.ISUB;
            }
            case '+': {
                return InstructionConst.IADD;
            }
            case '%': {
                return InstructionConst.IREM;
            }
            case '*': {
                return InstructionConst.IMUL;
            }
            case '/': {
                return InstructionConst.IDIV;
            }
            case '&': {
                return InstructionConst.IAND;
            }
            case '|': {
                return InstructionConst.IOR;
            }
            case '^': {
                return InstructionConst.IXOR;
            }
            case '<': {
                return InstructionConst.ISHL;
            }
            case '>': {
                return op.equals(">>>") ? InstructionConst.IUSHR : InstructionConst.ISHR;
            }
        }
        throw new IllegalArgumentException("Invalid operand " + op);
    }

    private static ArithmeticInstruction createBinaryLongOp(char first, String op) {
        switch (first) {
            case '-': {
                return InstructionConst.LSUB;
            }
            case '+': {
                return InstructionConst.LADD;
            }
            case '%': {
                return InstructionConst.LREM;
            }
            case '*': {
                return InstructionConst.LMUL;
            }
            case '/': {
                return InstructionConst.LDIV;
            }
            case '&': {
                return InstructionConst.LAND;
            }
            case '|': {
                return InstructionConst.LOR;
            }
            case '^': {
                return InstructionConst.LXOR;
            }
            case '<': {
                return InstructionConst.LSHL;
            }
            case '>': {
                return op.equals(">>>") ? InstructionConst.LUSHR : InstructionConst.LSHR;
            }
        }
        throw new IllegalArgumentException("Invalid operand " + op);
    }

    public static ArithmeticInstruction createBinaryOperation(String op, Type type) {
        char first = op.charAt(0);
        switch (type.getType()) {
            case 5: 
            case 8: 
            case 9: 
            case 10: {
                return InstructionFactory.createBinaryIntOp(first, op);
            }
            case 11: {
                return InstructionFactory.createBinaryLongOp(first, op);
            }
            case 6: {
                return InstructionFactory.createBinaryFloatOp(first);
            }
            case 7: {
                return InstructionFactory.createBinaryDoubleOp(first);
            }
        }
        throw new IllegalArgumentException("Invalid type " + type);
    }

    public static BranchInstruction createBranchInstruction(short opcode, InstructionHandle target) {
        switch (opcode) {
            case 153: {
                return new IFEQ(target);
            }
            case 154: {
                return new IFNE(target);
            }
            case 155: {
                return new IFLT(target);
            }
            case 156: {
                return new IFGE(target);
            }
            case 157: {
                return new IFGT(target);
            }
            case 158: {
                return new IFLE(target);
            }
            case 159: {
                return new IF_ICMPEQ(target);
            }
            case 160: {
                return new IF_ICMPNE(target);
            }
            case 161: {
                return new IF_ICMPLT(target);
            }
            case 162: {
                return new IF_ICMPGE(target);
            }
            case 163: {
                return new IF_ICMPGT(target);
            }
            case 164: {
                return new IF_ICMPLE(target);
            }
            case 165: {
                return new IF_ACMPEQ(target);
            }
            case 166: {
                return new IF_ACMPNE(target);
            }
            case 167: {
                return new GOTO(target);
            }
            case 168: {
                return new JSR(target);
            }
            case 198: {
                return new IFNULL(target);
            }
            case 199: {
                return new IFNONNULL(target);
            }
            case 200: {
                return new GOTO_W(target);
            }
            case 201: {
                return new JSR_W(target);
            }
        }
        throw new IllegalArgumentException("Invalid opcode: " + opcode);
    }

    public static StackInstruction createDup(int size) {
        return size == 2 ? InstructionConst.DUP2 : InstructionConst.DUP;
    }

    public static StackInstruction createDup_1(int size) {
        return size == 2 ? InstructionConst.DUP2_X1 : InstructionConst.DUP_X1;
    }

    public static StackInstruction createDup_2(int size) {
        return size == 2 ? InstructionConst.DUP2_X2 : InstructionConst.DUP_X2;
    }

    public static LocalVariableInstruction createLoad(Type type, int index) {
        switch (type.getType()) {
            case 4: 
            case 5: 
            case 8: 
            case 9: 
            case 10: {
                return new ILOAD(index);
            }
            case 6: {
                return new FLOAD(index);
            }
            case 7: {
                return new DLOAD(index);
            }
            case 11: {
                return new LLOAD(index);
            }
            case 13: 
            case 14: {
                return new ALOAD(index);
            }
        }
        throw new IllegalArgumentException("Invalid type " + type);
    }

    public static Instruction createNull(Type type) {
        switch (type.getType()) {
            case 13: 
            case 14: {
                return InstructionConst.ACONST_NULL;
            }
            case 4: 
            case 5: 
            case 8: 
            case 9: 
            case 10: {
                return InstructionConst.ICONST_0;
            }
            case 6: {
                return InstructionConst.FCONST_0;
            }
            case 7: {
                return InstructionConst.DCONST_0;
            }
            case 11: {
                return InstructionConst.LCONST_0;
            }
            case 12: {
                return InstructionConst.NOP;
            }
        }
        throw new IllegalArgumentException("Invalid type: " + type);
    }

    public static StackInstruction createPop(int size) {
        return size == 2 ? InstructionConst.POP2 : InstructionConst.POP;
    }

    public static ReturnInstruction createReturn(Type type) {
        switch (type.getType()) {
            case 13: 
            case 14: {
                return InstructionConst.ARETURN;
            }
            case 4: 
            case 5: 
            case 8: 
            case 9: 
            case 10: {
                return InstructionConst.IRETURN;
            }
            case 6: {
                return InstructionConst.FRETURN;
            }
            case 7: {
                return InstructionConst.DRETURN;
            }
            case 11: {
                return InstructionConst.LRETURN;
            }
            case 12: {
                return InstructionConst.RETURN;
            }
        }
        throw new IllegalArgumentException("Invalid type: " + type);
    }

    public static LocalVariableInstruction createStore(Type type, int index) {
        switch (type.getType()) {
            case 4: 
            case 5: 
            case 8: 
            case 9: 
            case 10: {
                return new ISTORE(index);
            }
            case 6: {
                return new FSTORE(index);
            }
            case 7: {
                return new DSTORE(index);
            }
            case 11: {
                return new LSTORE(index);
            }
            case 13: 
            case 14: {
                return new ASTORE(index);
            }
        }
        throw new IllegalArgumentException("Invalid type " + type);
    }

    public static Instruction createThis() {
        return new ALOAD(0);
    }

    private static boolean isString(Type type) {
        return type instanceof ObjectType && ((ObjectType)type).getClassName().equals("java.lang.String");
    }

    public InstructionFactory(ClassGen cg) {
        this(cg, cg.getConstantPool());
    }

    public InstructionFactory(ClassGen cg, ConstantPoolGen cp) {
        this.cg = cg;
        this.cp = cp;
    }

    public InstructionFactory(ConstantPoolGen cp) {
        this(null, cp);
    }

    public Instruction createAppend(Type type) {
        byte t = type.getType();
        if (InstructionFactory.isString(type)) {
            return this.createInvoke(appendMethodObjects[0], (short)182);
        }
        switch (t) {
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 9: 
            case 10: 
            case 11: {
                return this.createInvoke(appendMethodObjects[t], (short)182);
            }
            case 13: 
            case 14: {
                return this.createInvoke(appendMethodObjects[1], (short)182);
            }
        }
        throw new IllegalArgumentException("No append for this type? " + type);
    }

    public Instruction createCast(Type srcType, Type destType) {
        if (srcType instanceof BasicType && destType instanceof BasicType) {
            byte dest = destType.getType();
            int src = srcType.getType();
            if (dest == 11 && (src == 5 || src == 8 || src == 9)) {
                src = 10;
            }
            String name = "org.apache.bcel.generic." + shortNames[src - 5] + "2" + shortNames[dest - 5];
            Instruction i = null;
            try {
                i = (Instruction)Class.forName(name).newInstance();
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Could not find instruction: " + name, e);
            }
            return i;
        }
        if (!(srcType instanceof ReferenceType) || !(destType instanceof ReferenceType)) {
            throw new IllegalArgumentException("Cannot cast " + srcType + " to " + destType);
        }
        if (destType instanceof ArrayType) {
            return new CHECKCAST(this.cp.addArrayClass((ArrayType)destType));
        }
        return new CHECKCAST(this.cp.addClass(((ObjectType)destType).getClassName()));
    }

    public CHECKCAST createCheckCast(ReferenceType t) {
        if (t instanceof ArrayType) {
            return new CHECKCAST(this.cp.addArrayClass((ArrayType)t));
        }
        return new CHECKCAST(this.cp.addClass((ObjectType)t));
    }

    public Instruction createConstant(Object value) {
        PUSH push;
        if (value instanceof Number) {
            push = new PUSH(this.cp, (Number)value);
        } else if (value instanceof String) {
            push = new PUSH(this.cp, (String)value);
        } else if (value instanceof Boolean) {
            push = new PUSH(this.cp, (Boolean)value);
        } else if (value instanceof Character) {
            push = new PUSH(this.cp, (Character)value);
        } else {
            throw new ClassGenException("Illegal type: " + value.getClass());
        }
        return push.getInstruction();
    }

    public FieldInstruction createFieldAccess(String className, String name, Type type, short kind) {
        String signature = type.getSignature();
        int index = this.cp.addFieldref(className, name, signature);
        switch (kind) {
            case 180: {
                return new GETFIELD(index);
            }
            case 181: {
                return new PUTFIELD(index);
            }
            case 178: {
                return new GETSTATIC(index);
            }
            case 179: {
                return new PUTSTATIC(index);
            }
        }
        throw new IllegalArgumentException("Unknown getfield kind:" + kind);
    }

    public GETFIELD createGetField(String className, String name, Type t) {
        return new GETFIELD(this.cp.addFieldref(className, name, t.getSignature()));
    }

    public GETSTATIC createGetStatic(String className, String name, Type t) {
        return new GETSTATIC(this.cp.addFieldref(className, name, t.getSignature()));
    }

    public INSTANCEOF createInstanceOf(ReferenceType t) {
        if (t instanceof ArrayType) {
            return new INSTANCEOF(this.cp.addArrayClass((ArrayType)t));
        }
        return new INSTANCEOF(this.cp.addClass((ObjectType)t));
    }

    private InvokeInstruction createInvoke(MethodObject m, short kind) {
        return this.createInvoke(m.className, m.name, m.resultType, m.argTypes, kind);
    }

    public InvokeInstruction createInvoke(String className, String name, Type retType, Type[] argTypes, short kind) {
        return this.createInvoke(className, name, retType, argTypes, kind, kind == 185);
    }

    public InvokeInstruction createInvoke(String className, String name, Type retType, Type[] argTypes, short kind, boolean useInterface) {
        if (kind != 183 && kind != 182 && kind != 184 && kind != 185 && kind != 186) {
            throw new IllegalArgumentException("Unknown invoke kind: " + kind);
        }
        int nargs = 0;
        String signature = Type.getMethodSignature(retType, argTypes);
        for (Type argType : argTypes) {
            nargs += argType.getSize();
        }
        int index = useInterface ? this.cp.addInterfaceMethodref(className, name, signature) : this.cp.addMethodref(className, name, signature);
        switch (kind) {
            case 183: {
                return new INVOKESPECIAL(index);
            }
            case 182: {
                return new INVOKEVIRTUAL(index);
            }
            case 184: {
                return new INVOKESTATIC(index);
            }
            case 185: {
                return new INVOKEINTERFACE(index, nargs + 1);
            }
            case 186: {
                return new INVOKEDYNAMIC(index);
            }
        }
        throw new IllegalStateException("Unknown invoke kind: " + kind);
    }

    public NEW createNew(ObjectType t) {
        return new NEW(this.cp.addClass(t));
    }

    public NEW createNew(String s) {
        return this.createNew(ObjectType.getInstance(s));
    }

    public Instruction createNewArray(Type t, short dim) {
        if (dim == 1) {
            if (t instanceof ObjectType) {
                return new ANEWARRAY(this.cp.addClass((ObjectType)t));
            }
            if (t instanceof ArrayType) {
                return new ANEWARRAY(this.cp.addArrayClass((ArrayType)t));
            }
            return new NEWARRAY(t.getType());
        }
        ArrayType at = t instanceof ArrayType ? (ArrayType)t : new ArrayType(t, (int)dim);
        return new MULTIANEWARRAY(this.cp.addArrayClass(at), dim);
    }

    public InstructionList createPrintln(String s) {
        InstructionList il = new InstructionList();
        il.append(this.createGetStatic("java.lang.System", "out", Type.getType("Ljava/io/PrintStream;")));
        il.append(new PUSH(this.cp, s));
        MethodObject methodObject = new MethodObject("java.io.PrintStream", "println", Type.VOID, new Type[]{Type.getType("Ljava/lang/String;")});
        il.append(this.createInvoke(methodObject, (short)182));
        return il;
    }

    public PUTFIELD createPutField(String className, String name, Type t) {
        return new PUTFIELD(this.cp.addFieldref(className, name, t.getSignature()));
    }

    public PUTSTATIC createPutStatic(String className, String name, Type t) {
        return new PUTSTATIC(this.cp.addFieldref(className, name, t.getSignature()));
    }

    public ClassGen getClassGen() {
        return this.cg;
    }

    public ConstantPoolGen getConstantPool() {
        return this.cp;
    }

    public void setClassGen(ClassGen c) {
        this.cg = c;
    }

    public void setConstantPool(ConstantPoolGen c) {
        this.cp = c;
    }

    private static class MethodObject {
        final Type[] argTypes;
        final Type resultType;
        final String className;
        final String name;

        MethodObject(String c, String n, Type r, Type[] a) {
            this.className = c;
            this.name = n;
            this.resultType = r;
            this.argTypes = a;
        }
    }
}

