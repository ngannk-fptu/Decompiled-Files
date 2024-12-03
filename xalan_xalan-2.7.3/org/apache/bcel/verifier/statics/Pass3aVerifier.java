/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.statics;

import java.util.Arrays;
import org.apache.bcel.Repository;
import org.apache.bcel.classfile.AccessFlags;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.CodeException;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantDouble;
import org.apache.bcel.classfile.ConstantFieldref;
import org.apache.bcel.classfile.ConstantFloat;
import org.apache.bcel.classfile.ConstantInteger;
import org.apache.bcel.classfile.ConstantInterfaceMethodref;
import org.apache.bcel.classfile.ConstantLong;
import org.apache.bcel.classfile.ConstantMethodref;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.FieldOrMethod;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LineNumber;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.LocalVariableTable;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ANEWARRAY;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.ATHROW;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.BREAKPOINT;
import org.apache.bcel.generic.CHECKCAST;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.DLOAD;
import org.apache.bcel.generic.DSTORE;
import org.apache.bcel.generic.EmptyVisitor;
import org.apache.bcel.generic.FLOAD;
import org.apache.bcel.generic.FSTORE;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.GotoInstruction;
import org.apache.bcel.generic.IINC;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.IMPDEP1;
import org.apache.bcel.generic.IMPDEP2;
import org.apache.bcel.generic.INSTANCEOF;
import org.apache.bcel.generic.INVOKEDYNAMIC;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.ISTORE;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.JsrInstruction;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.LDC2_W;
import org.apache.bcel.generic.LLOAD;
import org.apache.bcel.generic.LOOKUPSWITCH;
import org.apache.bcel.generic.LSTORE;
import org.apache.bcel.generic.LoadClass;
import org.apache.bcel.generic.MULTIANEWARRAY;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.NEWARRAY;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.PUTSTATIC;
import org.apache.bcel.generic.RET;
import org.apache.bcel.generic.ReferenceType;
import org.apache.bcel.generic.ReturnInstruction;
import org.apache.bcel.generic.TABLESWITCH;
import org.apache.bcel.generic.Type;
import org.apache.bcel.verifier.PassVerifier;
import org.apache.bcel.verifier.VerificationResult;
import org.apache.bcel.verifier.Verifier;
import org.apache.bcel.verifier.VerifierFactory;
import org.apache.bcel.verifier.exc.AssertionViolatedException;
import org.apache.bcel.verifier.exc.ClassConstraintException;
import org.apache.bcel.verifier.exc.InvalidMethodException;
import org.apache.bcel.verifier.exc.StaticCodeConstraintException;
import org.apache.bcel.verifier.exc.StaticCodeInstructionConstraintException;
import org.apache.bcel.verifier.exc.StaticCodeInstructionOperandConstraintException;
import org.apache.bcel.verifier.statics.IntList;

public final class Pass3aVerifier
extends PassVerifier {
    private final Verifier verifier;
    private final int methodNo;
    private InstructionList instructionList;
    private Code code;

    private static boolean contains(int[] ints, int i) {
        for (int k : ints) {
            if (k != i) continue;
            return true;
        }
        return false;
    }

    public Pass3aVerifier(Verifier verifier, int methodNo) {
        this.verifier = verifier;
        this.methodNo = methodNo;
    }

    private void delayedPass2Checks() {
        CodeException[] exceptionTable;
        int[] instructionPositions = this.instructionList.getInstructionPositions();
        int codeLength = this.code.getCode().length;
        LineNumberTable lnt = this.code.getLineNumberTable();
        if (lnt != null) {
            LineNumber[] lineNumbers = lnt.getLineNumberTable();
            IntList offsets = new IntList();
            LineNumber[] lineNumberArray = lineNumbers;
            int n = lineNumberArray.length;
            block0: for (int i = 0; i < n; ++i) {
                LineNumber lineNumber = lineNumberArray[i];
                for (int instructionPosition : instructionPositions) {
                    int offset = lineNumber.getStartPC();
                    if (instructionPosition != offset) continue;
                    if (offsets.contains(offset)) {
                        this.addMessage("LineNumberTable attribute '" + this.code.getLineNumberTable() + "' refers to the same code offset ('" + offset + "') more than once which is violating the semantics [but is sometimes produced by IBM's 'jikes' compiler].");
                        continue block0;
                    }
                    offsets.add(offset);
                    continue block0;
                }
                throw new ClassConstraintException("Code attribute '" + this.tostring(this.code) + "' has a LineNumberTable attribute '" + this.code.getLineNumberTable() + "' referring to a code offset ('" + lineNumber.getStartPC() + "') that does not exist.");
            }
        }
        Attribute[] atts = this.code.getAttributes();
        for (Attribute att : atts) {
            if (!(att instanceof LocalVariableTable)) continue;
            ((LocalVariableTable)att).forEach(localVariable -> {
                int startpc = localVariable.getStartPC();
                int length = localVariable.getLength();
                if (!Pass3aVerifier.contains(instructionPositions, startpc)) {
                    throw new ClassConstraintException("Code attribute '" + this.tostring(this.code) + "' has a LocalVariableTable attribute '" + this.code.getLocalVariableTable() + "' referring to a code offset ('" + startpc + "') that does not exist.");
                }
                if (!Pass3aVerifier.contains(instructionPositions, startpc + length) && startpc + length != codeLength) {
                    throw new ClassConstraintException("Code attribute '" + this.tostring(this.code) + "' has a LocalVariableTable attribute '" + this.code.getLocalVariableTable() + "' referring to a code offset start_pc+length ('" + (startpc + length) + "') that does not exist.");
                }
            });
        }
        for (CodeException element : exceptionTable = this.code.getExceptionTable()) {
            int startpc = element.getStartPC();
            int endpc = element.getEndPC();
            int handlerpc = element.getHandlerPC();
            if (startpc >= endpc) {
                throw new ClassConstraintException("Code attribute '" + this.tostring(this.code) + "' has an exception_table entry '" + element + "' that has its start_pc ('" + startpc + "') not smaller than its end_pc ('" + endpc + "').");
            }
            if (!Pass3aVerifier.contains(instructionPositions, startpc)) {
                throw new ClassConstraintException("Code attribute '" + this.tostring(this.code) + "' has an exception_table entry '" + element + "' that has a non-existant bytecode offset as its start_pc ('" + startpc + "').");
            }
            if (!Pass3aVerifier.contains(instructionPositions, endpc) && endpc != codeLength) {
                throw new ClassConstraintException("Code attribute '" + this.tostring(this.code) + "' has an exception_table entry '" + element + "' that has a non-existant bytecode offset as its end_pc ('" + startpc + "') [that is also not equal to code_length ('" + codeLength + "')].");
            }
            if (Pass3aVerifier.contains(instructionPositions, handlerpc)) continue;
            throw new ClassConstraintException("Code attribute '" + this.tostring(this.code) + "' has an exception_table entry '" + element + "' that has a non-existant bytecode offset as its handler_pc ('" + handlerpc + "').");
        }
    }

    @Override
    public VerificationResult do_verify() {
        try {
            if (this.verifier.doPass2().equals(VerificationResult.VR_OK)) {
                JavaClass jc = Repository.lookupClass(this.verifier.getClassName());
                Method[] methods = jc.getMethods();
                if (this.methodNo >= methods.length) {
                    throw new InvalidMethodException("METHOD DOES NOT EXIST!");
                }
                Method method = methods[this.methodNo];
                this.code = method.getCode();
                if (method.isAbstract() || method.isNative()) {
                    return VerificationResult.VR_OK;
                }
                try {
                    this.instructionList = new InstructionList(method.getCode().getCode());
                }
                catch (RuntimeException re) {
                    return new VerificationResult(2, "Bad bytecode in the code array of the Code attribute of method '" + this.tostring(method) + "'.");
                }
                this.instructionList.setPositions(true);
                VerificationResult vr = VerificationResult.VR_OK;
                try {
                    this.delayedPass2Checks();
                }
                catch (ClassFormatException | ClassConstraintException cce) {
                    vr = new VerificationResult(2, cce.getMessage());
                    return vr;
                }
                try {
                    this.pass3StaticInstructionChecks();
                    this.pass3StaticInstructionOperandsChecks();
                }
                catch (ClassFormatException | StaticCodeConstraintException scce) {
                    vr = new VerificationResult(2, scce.getMessage());
                }
                catch (ClassCastException cce) {
                    vr = new VerificationResult(2, "Class Cast Exception: " + cce.getMessage());
                }
                return vr;
            }
            return VerificationResult.VR_NOTYET;
        }
        catch (ClassNotFoundException e) {
            throw new AssertionViolatedException("Missing class: " + e, e);
        }
    }

    public int getMethodNo() {
        return this.methodNo;
    }

    private void pass3StaticInstructionChecks() {
        if (this.code.getCode().length >= 65536) {
            throw new StaticCodeInstructionConstraintException("Code array in code attribute '" + this.tostring(this.code) + "' too big: must be smaller than " + 65536 + "65536 bytes.");
        }
        for (InstructionHandle ih = this.instructionList.getStart(); ih != null; ih = ih.getNext()) {
            Instruction i = ih.getInstruction();
            if (i instanceof IMPDEP1) {
                throw new StaticCodeInstructionConstraintException("IMPDEP1 must not be in the code, it is an illegal instruction for _internal_ JVM use!");
            }
            if (i instanceof IMPDEP2) {
                throw new StaticCodeInstructionConstraintException("IMPDEP2 must not be in the code, it is an illegal instruction for _internal_ JVM use!");
            }
            if (!(i instanceof BREAKPOINT)) continue;
            throw new StaticCodeInstructionConstraintException("BREAKPOINT must not be in the code, it is an illegal instruction for _internal_ JVM use!");
        }
        Instruction last = this.instructionList.getEnd().getInstruction();
        if (!(last instanceof ReturnInstruction || last instanceof RET || last instanceof GotoInstruction || last instanceof ATHROW)) {
            throw new StaticCodeInstructionConstraintException("Execution must not fall off the bottom of the code array. This constraint is enforced statically as some existing verifiers do - so it may be a false alarm if the last instruction is not reachable.");
        }
    }

    private void pass3StaticInstructionOperandsChecks() {
        try {
            ConstantPoolGen cpg = new ConstantPoolGen(Repository.lookupClass(this.verifier.getClassName()).getConstantPool());
            InstOperandConstraintVisitor v = new InstOperandConstraintVisitor(cpg);
            for (InstructionHandle ih = this.instructionList.getStart(); ih != null; ih = ih.getNext()) {
                Instruction i = ih.getInstruction();
                if (i instanceof JsrInstruction) {
                    InstructionHandle target = ((JsrInstruction)i).getTarget();
                    if (target == this.instructionList.getStart()) {
                        throw new StaticCodeInstructionOperandConstraintException("Due to JustIce's clear definition of subroutines, no JSR or JSR_W may have a top-level instruction (such as the very first instruction, which is targeted by instruction '" + this.tostring(ih) + "' as its target.");
                    }
                    if (!(target.getInstruction() instanceof ASTORE)) {
                        throw new StaticCodeInstructionOperandConstraintException("Due to JustIce's clear definition of subroutines, no JSR or JSR_W may target anything else than an ASTORE instruction. Instruction '" + this.tostring(ih) + "' targets '" + this.tostring(target) + "'.");
                    }
                }
                ih.accept(v);
            }
        }
        catch (ClassNotFoundException e) {
            throw new AssertionViolatedException("Missing class: " + e, e);
        }
    }

    protected String tostring(Object obj) {
        String ret;
        try {
            ret = obj.toString();
        }
        catch (RuntimeException e) {
            String s = obj.getClass().getName();
            s = s.substring(s.lastIndexOf(".") + 1);
            ret = "<<" + s + ">>";
        }
        return ret;
    }

    private class InstOperandConstraintVisitor
    extends EmptyVisitor {
        private final ConstantPoolGen constantPoolGen;

        InstOperandConstraintVisitor(ConstantPoolGen constantPoolGen) {
            this.constantPoolGen = constantPoolGen;
        }

        private void constraintViolated(Instruction i, String message) {
            throw new StaticCodeInstructionOperandConstraintException("Instruction " + Pass3aVerifier.this.tostring(i) + " constraint violated: " + message);
        }

        private Method getMethod(JavaClass jc, InvokeInstruction invoke) {
            Method[] ms;
            for (Method element : ms = jc.getMethods()) {
                if (!element.getName().equals(invoke.getMethodName(this.constantPoolGen)) || !Type.getReturnType(element.getSignature()).equals(invoke.getReturnType(this.constantPoolGen)) || !Arrays.equals(Type.getArgumentTypes(element.getSignature()), invoke.getArgumentTypes(this.constantPoolGen))) continue;
                return element;
            }
            return null;
        }

        private Method getMethodRecursive(JavaClass jc, InvokeInstruction invoke) throws ClassNotFoundException {
            Method m = this.getMethod(jc, invoke);
            if (m != null) {
                return m;
            }
            for (JavaClass superclass : jc.getSuperClasses()) {
                m = this.getMethod(superclass, invoke);
                if (m == null) continue;
                return m;
            }
            for (JavaClass superclass : jc.getInterfaces()) {
                m = this.getMethod(superclass, invoke);
                if (m == null) continue;
                return m;
            }
            return null;
        }

        private ObjectType getObjectType(FieldInstruction o) {
            ReferenceType rt = o.getReferenceType(this.constantPoolGen);
            if (rt instanceof ObjectType) {
                return (ObjectType)rt;
            }
            this.constraintViolated(o, "expecting ObjectType but got " + rt);
            return null;
        }

        private void indexValid(Instruction i, int idx) {
            if (idx < 0 || idx >= this.constantPoolGen.getSize()) {
                this.constraintViolated(i, "Illegal constant pool index '" + idx + "'.");
            }
        }

        private int maxLocals() {
            try {
                return Repository.lookupClass(Pass3aVerifier.this.verifier.getClassName()).getMethods()[Pass3aVerifier.this.methodNo].getCode().getMaxLocals();
            }
            catch (ClassNotFoundException e) {
                throw new AssertionViolatedException("Missing class: " + e, e);
            }
        }

        @Override
        public void visitALOAD(ALOAD o) {
            int idx = o.getIndex();
            if (idx < 0) {
                this.constraintViolated(o, "Index '" + idx + "' must be non-negative.");
            } else {
                int maxminus1 = this.maxLocals() - 1;
                if (idx > maxminus1) {
                    this.constraintViolated(o, "Index '" + idx + "' must not be greater than max_locals-1 '" + maxminus1 + "'.");
                }
            }
        }

        @Override
        public void visitANEWARRAY(ANEWARRAY o) {
            int dimensions;
            Type t;
            this.indexValid(o, o.getIndex());
            Constant c = this.constantPoolGen.getConstant(o.getIndex());
            if (!(c instanceof ConstantClass)) {
                this.constraintViolated(o, "Expecting a CONSTANT_Class operand, but found a '" + Pass3aVerifier.this.tostring(c) + "'.");
            }
            if ((t = o.getType(this.constantPoolGen)) instanceof ArrayType && (dimensions = ((ArrayType)t).getDimensions()) > 255) {
                this.constraintViolated(o, "Not allowed to create an array with more than 255 dimensions; actual: " + dimensions);
            }
        }

        @Override
        public void visitASTORE(ASTORE o) {
            int idx = o.getIndex();
            if (idx < 0) {
                this.constraintViolated(o, "Index '" + idx + "' must be non-negative.");
            } else {
                int maxminus1 = this.maxLocals() - 1;
                if (idx > maxminus1) {
                    this.constraintViolated(o, "Index '" + idx + "' must not be greater than max_locals-1 '" + maxminus1 + "'.");
                }
            }
        }

        @Override
        public void visitCHECKCAST(CHECKCAST o) {
            this.indexValid(o, o.getIndex());
            Constant c = this.constantPoolGen.getConstant(o.getIndex());
            if (!(c instanceof ConstantClass)) {
                this.constraintViolated(o, "Expecting a CONSTANT_Class operand, but found a '" + Pass3aVerifier.this.tostring(c) + "'.");
            }
        }

        @Override
        public void visitDLOAD(DLOAD o) {
            int idx = o.getIndex();
            if (idx < 0) {
                this.constraintViolated(o, "Index '" + idx + "' must be non-negative. [Constraint by JustIce as an analogon to the single-slot xLOAD/xSTORE instructions; may not happen anyway.]");
            } else {
                int maxminus2 = this.maxLocals() - 2;
                if (idx > maxminus2) {
                    this.constraintViolated(o, "Index '" + idx + "' must not be greater than max_locals-2 '" + maxminus2 + "'.");
                }
            }
        }

        @Override
        public void visitDSTORE(DSTORE o) {
            int idx = o.getIndex();
            if (idx < 0) {
                this.constraintViolated(o, "Index '" + idx + "' must be non-negative. [Constraint by JustIce as an analogon to the single-slot xLOAD/xSTORE instructions; may not happen anyway.]");
            } else {
                int maxminus2 = this.maxLocals() - 2;
                if (idx > maxminus2) {
                    this.constraintViolated(o, "Index '" + idx + "' must not be greater than max_locals-2 '" + maxminus2 + "'.");
                }
            }
        }

        @Override
        public void visitFieldInstruction(FieldInstruction o) {
            try {
                this.indexValid(o, o.getIndex());
                Constant c = this.constantPoolGen.getConstant(o.getIndex());
                if (!(c instanceof ConstantFieldref)) {
                    this.constraintViolated(o, "Indexing a constant that's not a CONSTANT_Fieldref but a '" + Pass3aVerifier.this.tostring(c) + "'.");
                }
                String fieldName = o.getFieldName(this.constantPoolGen);
                JavaClass jc = Repository.lookupClass(this.getObjectType(o).getClassName());
                Field[] fields = jc.getFields();
                FieldOrMethod f = null;
                for (Field field : fields) {
                    Type oType;
                    Type fType;
                    if (!field.getName().equals(fieldName) || !(fType = Type.getType(field.getSignature())).equals(oType = o.getType(this.constantPoolGen))) continue;
                    f = field;
                    break;
                }
                if (f == null) {
                    JavaClass[] superclasses;
                    block3: for (JavaClass superclass : superclasses = jc.getSuperClasses()) {
                        for (Field field : fields = superclass.getFields()) {
                            Type oType;
                            Type fType;
                            if (!field.getName().equals(fieldName) || !(fType = Type.getType(field.getSignature())).equals(oType = o.getType(this.constantPoolGen))) continue;
                            f = field;
                            if ((f.getAccessFlags() & 5) != 0) break block3;
                            f = null;
                            break block3;
                        }
                    }
                    if (f == null) {
                        this.constraintViolated(o, "Referenced field '" + fieldName + "' does not exist in class '" + jc.getClassName() + "'.");
                    }
                } else {
                    Type.getType(f.getSignature());
                    o.getType(this.constantPoolGen);
                }
            }
            catch (ClassNotFoundException e) {
                throw new AssertionViolatedException("Missing class: " + e, e);
            }
        }

        @Override
        public void visitFLOAD(FLOAD o) {
            int idx = o.getIndex();
            if (idx < 0) {
                this.constraintViolated(o, "Index '" + idx + "' must be non-negative.");
            } else {
                int maxminus1 = this.maxLocals() - 1;
                if (idx > maxminus1) {
                    this.constraintViolated(o, "Index '" + idx + "' must not be greater than max_locals-1 '" + maxminus1 + "'.");
                }
            }
        }

        @Override
        public void visitFSTORE(FSTORE o) {
            int idx = o.getIndex();
            if (idx < 0) {
                this.constraintViolated(o, "Index '" + idx + "' must be non-negative.");
            } else {
                int maxminus1 = this.maxLocals() - 1;
                if (idx > maxminus1) {
                    this.constraintViolated(o, "Index '" + idx + "' must not be greater than max_locals-1 '" + maxminus1 + "'.");
                }
            }
        }

        @Override
        public void visitGETSTATIC(GETSTATIC o) {
            try {
                String fieldName = o.getFieldName(this.constantPoolGen);
                JavaClass jc = Repository.lookupClass(this.getObjectType(o).getClassName());
                Field[] fields = jc.getFields();
                AccessFlags f = null;
                for (Field field : fields) {
                    if (!field.getName().equals(fieldName)) continue;
                    f = field;
                    break;
                }
                if (f == null) {
                    throw new AssertionViolatedException("Field '" + fieldName + "' not found in " + jc.getClassName());
                }
                if (!f.isStatic()) {
                    this.constraintViolated(o, "Referenced field '" + f + "' is not static which it should be.");
                }
            }
            catch (ClassNotFoundException e) {
                throw new AssertionViolatedException("Missing class: " + e, e);
            }
        }

        @Override
        public void visitIINC(IINC o) {
            int idx = o.getIndex();
            if (idx < 0) {
                this.constraintViolated(o, "Index '" + idx + "' must be non-negative.");
            } else {
                int maxminus1 = this.maxLocals() - 1;
                if (idx > maxminus1) {
                    this.constraintViolated(o, "Index '" + idx + "' must not be greater than max_locals-1 '" + maxminus1 + "'.");
                }
            }
        }

        @Override
        public void visitILOAD(ILOAD o) {
            int idx = o.getIndex();
            if (idx < 0) {
                this.constraintViolated(o, "Index '" + idx + "' must be non-negative.");
            } else {
                int maxminus1 = this.maxLocals() - 1;
                if (idx > maxminus1) {
                    this.constraintViolated(o, "Index '" + idx + "' must not be greater than max_locals-1 '" + maxminus1 + "'.");
                }
            }
        }

        @Override
        public void visitINSTANCEOF(INSTANCEOF o) {
            this.indexValid(o, o.getIndex());
            Constant c = this.constantPoolGen.getConstant(o.getIndex());
            if (!(c instanceof ConstantClass)) {
                this.constraintViolated(o, "Expecting a CONSTANT_Class operand, but found a '" + Pass3aVerifier.this.tostring(c) + "'.");
            }
        }

        @Override
        public void visitINVOKEDYNAMIC(INVOKEDYNAMIC o) {
            throw new UnsupportedOperationException("INVOKEDYNAMIC instruction is not supported at this time");
        }

        @Override
        public void visitInvokeInstruction(InvokeInstruction o) {
            Type[] ts;
            Verifier v;
            VerificationResult vr;
            ConstantNameAndType cnat;
            Constant c;
            this.indexValid(o, o.getIndex());
            if (o instanceof INVOKEVIRTUAL || o instanceof INVOKESPECIAL || o instanceof INVOKESTATIC) {
                c = this.constantPoolGen.getConstant(o.getIndex());
                if (!(c instanceof ConstantMethodref)) {
                    this.constraintViolated(o, "Indexing a constant that's not a CONSTANT_Methodref but a '" + Pass3aVerifier.this.tostring(c) + "'.");
                } else {
                    cnat = (ConstantNameAndType)this.constantPoolGen.getConstant(((ConstantMethodref)c).getNameAndTypeIndex());
                    ConstantUtf8 cutf8 = (ConstantUtf8)this.constantPoolGen.getConstant(cnat.getNameIndex());
                    if (cutf8.getBytes().equals("<init>") && !(o instanceof INVOKESPECIAL)) {
                        this.constraintViolated(o, "Only INVOKESPECIAL is allowed to invoke instance initialization methods.");
                    }
                    if (!cutf8.getBytes().equals("<init>") && cutf8.getBytes().startsWith("<")) {
                        this.constraintViolated(o, "No method with a name beginning with '<' other than the instance initialization methods may be called by the method invocation instructions.");
                    }
                }
            } else {
                String name;
                c = this.constantPoolGen.getConstant(o.getIndex());
                if (!(c instanceof ConstantInterfaceMethodref)) {
                    this.constraintViolated(o, "Indexing a constant that's not a CONSTANT_InterfaceMethodref but a '" + Pass3aVerifier.this.tostring(c) + "'.");
                }
                if ((name = ((ConstantUtf8)this.constantPoolGen.getConstant((cnat = (ConstantNameAndType)this.constantPoolGen.getConstant(((ConstantInterfaceMethodref)c).getNameAndTypeIndex())).getNameIndex())).getBytes()).equals("<init>")) {
                    this.constraintViolated(o, "Method to invoke must not be '<init>'.");
                }
                if (name.equals("<clinit>")) {
                    this.constraintViolated(o, "Method to invoke must not be '<clinit>'.");
                }
            }
            Type t = o.getReturnType(this.constantPoolGen);
            if (t instanceof ArrayType) {
                t = ((ArrayType)t).getBasicType();
            }
            if (t instanceof ObjectType && (vr = (v = VerifierFactory.getVerifier(((ObjectType)t).getClassName())).doPass2()).getStatus() != 1) {
                this.constraintViolated(o, "Return type class/interface could not be verified successfully: '" + vr.getMessage() + "'.");
            }
            for (Type element : ts = o.getArgumentTypes(this.constantPoolGen)) {
                Verifier v2;
                VerificationResult vr2;
                t = element;
                if (t instanceof ArrayType) {
                    t = ((ArrayType)t).getBasicType();
                }
                if (!(t instanceof ObjectType) || (vr2 = (v2 = VerifierFactory.getVerifier(((ObjectType)t).getClassName())).doPass2()).getStatus() == 1) continue;
                this.constraintViolated(o, "Argument type class/interface could not be verified successfully: '" + vr2.getMessage() + "'.");
            }
        }

        @Override
        public void visitINVOKEINTERFACE(INVOKEINTERFACE o) {
            try {
                String className = o.getClassName(this.constantPoolGen);
                JavaClass jc = Repository.lookupClass(className);
                Method m = this.getMethodRecursive(jc, o);
                if (m == null) {
                    this.constraintViolated(o, "Referenced method '" + o.getMethodName(this.constantPoolGen) + "' with expected signature '" + o.getSignature(this.constantPoolGen) + "' not found in class '" + jc.getClassName() + "'.");
                }
                if (jc.isClass()) {
                    this.constraintViolated(o, "Referenced class '" + jc.getClassName() + "' is a class, but not an interface as expected.");
                }
            }
            catch (ClassNotFoundException e) {
                throw new AssertionViolatedException("Missing class: " + e, e);
            }
        }

        @Override
        public void visitINVOKESPECIAL(INVOKESPECIAL o) {
            try {
                JavaClass current;
                String className = o.getClassName(this.constantPoolGen);
                JavaClass jc = Repository.lookupClass(className);
                Method m = this.getMethodRecursive(jc, o);
                if (m == null) {
                    this.constraintViolated(o, "Referenced method '" + o.getMethodName(this.constantPoolGen) + "' with expected signature '" + o.getSignature(this.constantPoolGen) + "' not found in class '" + jc.getClassName() + "'.");
                }
                if ((current = Repository.lookupClass(Pass3aVerifier.this.verifier.getClassName())).isSuper() && Repository.instanceOf(current, jc) && !current.equals(jc) && !o.getMethodName(this.constantPoolGen).equals("<init>")) {
                    int supidx = -1;
                    Method meth = null;
                    while (supidx != 0) {
                        Method[] meths;
                        supidx = current.getSuperclassNameIndex();
                        current = Repository.lookupClass(current.getSuperclassName());
                        for (Method meth2 : meths = current.getMethods()) {
                            if (!meth2.getName().equals(o.getMethodName(this.constantPoolGen)) || !Type.getReturnType(meth2.getSignature()).equals(o.getReturnType(this.constantPoolGen)) || !Arrays.equals(Type.getArgumentTypes(meth2.getSignature()), o.getArgumentTypes(this.constantPoolGen))) continue;
                            meth = meth2;
                            break;
                        }
                        if (meth == null) continue;
                        break;
                    }
                    if (meth == null) {
                        this.constraintViolated(o, "ACC_SUPER special lookup procedure not successful: method '" + o.getMethodName(this.constantPoolGen) + "' with proper signature not declared in superclass hierarchy.");
                    }
                }
            }
            catch (ClassNotFoundException e) {
                throw new AssertionViolatedException("Missing class: " + e, e);
            }
        }

        @Override
        public void visitINVOKESTATIC(INVOKESTATIC o) {
            try {
                String className = o.getClassName(this.constantPoolGen);
                JavaClass jc = Repository.lookupClass(className);
                Method m = this.getMethodRecursive(jc, o);
                if (m == null) {
                    this.constraintViolated(o, "Referenced method '" + o.getMethodName(this.constantPoolGen) + "' with expected signature '" + o.getSignature(this.constantPoolGen) + "' not found in class '" + jc.getClassName() + "'.");
                } else if (!m.isStatic()) {
                    this.constraintViolated(o, "Referenced method '" + o.getMethodName(this.constantPoolGen) + "' has ACC_STATIC unset.");
                }
            }
            catch (ClassNotFoundException e) {
                throw new AssertionViolatedException("Missing class: " + e, e);
            }
        }

        @Override
        public void visitINVOKEVIRTUAL(INVOKEVIRTUAL o) {
            try {
                String className = o.getClassName(this.constantPoolGen);
                JavaClass jc = Repository.lookupClass(className);
                Method m = this.getMethodRecursive(jc, o);
                if (m == null) {
                    this.constraintViolated(o, "Referenced method '" + o.getMethodName(this.constantPoolGen) + "' with expected signature '" + o.getSignature(this.constantPoolGen) + "' not found in class '" + jc.getClassName() + "'.");
                }
                if (!jc.isClass()) {
                    this.constraintViolated(o, "Referenced class '" + jc.getClassName() + "' is an interface, but not a class as expected.");
                }
            }
            catch (ClassNotFoundException e) {
                Pass3aVerifier.this.addMessage("Unable to verify INVOKEVITUAL as cannot load target class: " + e.getCause());
            }
        }

        @Override
        public void visitISTORE(ISTORE o) {
            int idx = o.getIndex();
            if (idx < 0) {
                this.constraintViolated(o, "Index '" + idx + "' must be non-negative.");
            } else {
                int maxminus1 = this.maxLocals() - 1;
                if (idx > maxminus1) {
                    this.constraintViolated(o, "Index '" + idx + "' must not be greater than max_locals-1 '" + maxminus1 + "'.");
                }
            }
        }

        @Override
        public void visitLDC(LDC ldc) {
            this.indexValid(ldc, ldc.getIndex());
            Constant c = this.constantPoolGen.getConstant(ldc.getIndex());
            if (c instanceof ConstantClass) {
                Pass3aVerifier.this.addMessage("Operand of LDC or LDC_W is CONSTANT_Class '" + Pass3aVerifier.this.tostring(c) + "' - this is only supported in JDK 1.5 and higher.");
            } else if (!(c instanceof ConstantInteger || c instanceof ConstantFloat || c instanceof ConstantString)) {
                this.constraintViolated(ldc, "Operand of LDC or LDC_W must be one of CONSTANT_Integer, CONSTANT_Float or CONSTANT_String, but is '" + Pass3aVerifier.this.tostring(c) + "'.");
            }
        }

        @Override
        public void visitLDC2_W(LDC2_W o) {
            this.indexValid(o, o.getIndex());
            Constant c = this.constantPoolGen.getConstant(o.getIndex());
            if (!(c instanceof ConstantLong) && !(c instanceof ConstantDouble)) {
                this.constraintViolated(o, "Operand of LDC2_W must be CONSTANT_Long or CONSTANT_Double, but is '" + Pass3aVerifier.this.tostring(c) + "'.");
            }
            try {
                this.indexValid(o, o.getIndex() + 1);
            }
            catch (StaticCodeInstructionOperandConstraintException e) {
                throw new AssertionViolatedException("Does not BCEL handle that? LDC2_W operand has a problem.", e);
            }
        }

        @Override
        public void visitLLOAD(LLOAD o) {
            int idx = o.getIndex();
            if (idx < 0) {
                this.constraintViolated(o, "Index '" + idx + "' must be non-negative. [Constraint by JustIce as an analogon to the single-slot xLOAD/xSTORE instructions; may not happen anyway.]");
            } else {
                int maxminus2 = this.maxLocals() - 2;
                if (idx > maxminus2) {
                    this.constraintViolated(o, "Index '" + idx + "' must not be greater than max_locals-2 '" + maxminus2 + "'.");
                }
            }
        }

        @Override
        public void visitLoadClass(LoadClass loadClass) {
            Verifier v;
            VerificationResult vr;
            ObjectType t = loadClass.getLoadClassType(this.constantPoolGen);
            if (t != null && (vr = (v = VerifierFactory.getVerifier(t.getClassName())).doPass1()).getStatus() != 1) {
                this.constraintViolated((Instruction)((Object)loadClass), "Class '" + loadClass.getLoadClassType(this.constantPoolGen).getClassName() + "' is referenced, but cannot be loaded: '" + vr + "'.");
            }
        }

        @Override
        public void visitLOOKUPSWITCH(LOOKUPSWITCH o) {
            int[] matchs = o.getMatchs();
            int max = Integer.MIN_VALUE;
            for (int i = 0; i < matchs.length; ++i) {
                if (matchs[i] == max && i != 0) {
                    this.constraintViolated(o, "Match '" + matchs[i] + "' occurs more than once.");
                }
                if (matchs[i] < max) {
                    this.constraintViolated(o, "Lookup table must be sorted but isn't.");
                    continue;
                }
                max = matchs[i];
            }
        }

        @Override
        public void visitLSTORE(LSTORE o) {
            int idx = o.getIndex();
            if (idx < 0) {
                this.constraintViolated(o, "Index '" + idx + "' must be non-negative. [Constraint by JustIce as an analogon to the single-slot xLOAD/xSTORE instructions; may not happen anyway.]");
            } else {
                int maxminus2 = this.maxLocals() - 2;
                if (idx > maxminus2) {
                    this.constraintViolated(o, "Index '" + idx + "' must not be greater than max_locals-2 '" + maxminus2 + "'.");
                }
            }
        }

        @Override
        public void visitMULTIANEWARRAY(MULTIANEWARRAY o) {
            Type t;
            short dimensions2create;
            this.indexValid(o, o.getIndex());
            Constant c = this.constantPoolGen.getConstant(o.getIndex());
            if (!(c instanceof ConstantClass)) {
                this.constraintViolated(o, "Expecting a CONSTANT_Class operand, but found a '" + Pass3aVerifier.this.tostring(c) + "'.");
            }
            if ((dimensions2create = o.getDimensions()) < 1) {
                this.constraintViolated(o, "Number of dimensions to create must be greater than zero.");
            }
            if ((t = o.getType(this.constantPoolGen)) instanceof ArrayType) {
                int dimensions = ((ArrayType)t).getDimensions();
                if (dimensions < dimensions2create) {
                    this.constraintViolated(o, "Not allowed to create array with more dimensions ('" + dimensions2create + "') than the one referenced by the CONSTANT_Class '" + t + "'.");
                }
            } else {
                this.constraintViolated(o, "Expecting a CONSTANT_Class referencing an array type. [Constraint not found in The Java Virtual Machine Specification, Second Edition, 4.8.1]");
            }
        }

        @Override
        public void visitNEW(NEW o) {
            this.indexValid(o, o.getIndex());
            Constant c = this.constantPoolGen.getConstant(o.getIndex());
            if (!(c instanceof ConstantClass)) {
                this.constraintViolated(o, "Expecting a CONSTANT_Class operand, but found a '" + Pass3aVerifier.this.tostring(c) + "'.");
            } else {
                ConstantUtf8 cutf8 = (ConstantUtf8)this.constantPoolGen.getConstant(((ConstantClass)c).getNameIndex());
                Type t = Type.getType("L" + cutf8.getBytes() + ";");
                if (t instanceof ArrayType) {
                    this.constraintViolated(o, "NEW must not be used to create an array.");
                }
            }
        }

        @Override
        public void visitNEWARRAY(NEWARRAY o) {
            byte t = o.getTypecode();
            if (t != 4 && t != 5 && t != 6 && t != 7 && t != 8 && t != 9 && t != 10 && t != 11) {
                this.constraintViolated(o, "Illegal type code '" + Pass3aVerifier.this.tostring(t) + "' for 'atype' operand.");
            }
        }

        @Override
        public void visitPUTSTATIC(PUTSTATIC o) {
            try {
                String fieldName = o.getFieldName(this.constantPoolGen);
                JavaClass jc = Repository.lookupClass(this.getObjectType(o).getClassName());
                Field[] fields = jc.getFields();
                AccessFlags f = null;
                for (Field field : fields) {
                    if (!field.getName().equals(fieldName)) continue;
                    f = field;
                    break;
                }
                if (f == null) {
                    throw new AssertionViolatedException("Field '" + fieldName + "' not found in " + jc.getClassName());
                }
                if (f.isFinal() && !Pass3aVerifier.this.verifier.getClassName().equals(this.getObjectType(o).getClassName())) {
                    this.constraintViolated(o, "Referenced field '" + f + "' is final and must therefore be declared in the current class '" + Pass3aVerifier.this.verifier.getClassName() + "' which is not the case: it is declared in '" + o.getReferenceType(this.constantPoolGen) + "'.");
                }
                if (!f.isStatic()) {
                    this.constraintViolated(o, "Referenced field '" + f + "' is not static which it should be.");
                }
                String methName = Repository.lookupClass(Pass3aVerifier.this.verifier.getClassName()).getMethods()[Pass3aVerifier.this.methodNo].getName();
                if (!jc.isClass() && !methName.equals("<clinit>")) {
                    this.constraintViolated(o, "Interface field '" + f + "' must be set in a '" + "<clinit>" + "' method.");
                }
            }
            catch (ClassNotFoundException e) {
                throw new AssertionViolatedException("Missing class: " + e, e);
            }
        }

        @Override
        public void visitRET(RET o) {
            int idx = o.getIndex();
            if (idx < 0) {
                this.constraintViolated(o, "Index '" + idx + "' must be non-negative.");
            } else {
                int maxminus1 = this.maxLocals() - 1;
                if (idx > maxminus1) {
                    this.constraintViolated(o, "Index '" + idx + "' must not be greater than max_locals-1 '" + maxminus1 + "'.");
                }
            }
        }

        @Override
        public void visitTABLESWITCH(TABLESWITCH o) {
        }
    }
}

