/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.util;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.generic.AllocationInstruction;
import org.apache.bcel.generic.ArrayInstruction;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.CHECKCAST;
import org.apache.bcel.generic.CPInstruction;
import org.apache.bcel.generic.CodeExceptionGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.EmptyVisitor;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.IINC;
import org.apache.bcel.generic.INSTANCEOF;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionConst;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.LDC2_W;
import org.apache.bcel.generic.LocalVariableInstruction;
import org.apache.bcel.generic.MULTIANEWARRAY;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.NEWARRAY;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.RET;
import org.apache.bcel.generic.ReturnInstruction;
import org.apache.bcel.generic.Select;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.BCELifier;

class BCELFactory
extends EmptyVisitor {
    private static final String CONSTANT_PREFIX = Const.class.getSimpleName() + ".";
    private final MethodGen methodGen;
    private final PrintWriter printWriter;
    private final ConstantPoolGen constantPoolGen;
    private final Map<Instruction, InstructionHandle> branchMap = new HashMap<Instruction, InstructionHandle>();
    private final List<BranchInstruction> branches = new ArrayList<BranchInstruction>();

    BCELFactory(MethodGen mg, PrintWriter out) {
        this.methodGen = mg;
        this.constantPoolGen = mg.getConstantPool();
        this.printWriter = out;
    }

    private void createConstant(Object value) {
        String embed = value.toString();
        if (value instanceof String) {
            embed = '\"' + Utility.convertString(embed) + '\"';
        } else if (value instanceof Character) {
            embed = "(char)0x" + Integer.toHexString(((Character)value).charValue());
        } else if (value instanceof Float) {
            Float f = (Float)value;
            embed = Float.isNaN(f.floatValue()) ? "Float.NaN" : (f.floatValue() == Float.POSITIVE_INFINITY ? "Float.POSITIVE_INFINITY" : (f.floatValue() == Float.NEGATIVE_INFINITY ? "Float.NEGATIVE_INFINITY" : embed + "f"));
        } else if (value instanceof Double) {
            Double d = (Double)value;
            embed = Double.isNaN(d) ? "Double.NaN" : (d == Double.POSITIVE_INFINITY ? "Double.POSITIVE_INFINITY" : (d == Double.NEGATIVE_INFINITY ? "Double.NEGATIVE_INFINITY" : embed + "d"));
        } else if (value instanceof Long) {
            embed = embed + "L";
        } else if (value instanceof ObjectType) {
            ObjectType ot = (ObjectType)value;
            embed = "new ObjectType(\"" + ot.getClassName() + "\")";
        } else if (value instanceof ArrayType) {
            ArrayType at = (ArrayType)value;
            embed = "new ArrayType(" + BCELifier.printType(at.getBasicType()) + ", " + at.getDimensions() + ")";
        }
        this.printWriter.println("il.append(new PUSH(_cp, " + embed + "));");
    }

    public void start() {
        if (!this.methodGen.isAbstract() && !this.methodGen.isNative()) {
            for (InstructionHandle ih = this.methodGen.getInstructionList().getStart(); ih != null; ih = ih.getNext()) {
                Instruction i = ih.getInstruction();
                if (i instanceof BranchInstruction) {
                    this.branchMap.put(i, ih);
                }
                if (ih.hasTargeters()) {
                    if (i instanceof BranchInstruction) {
                        this.printWriter.println("    InstructionHandle ih_" + ih.getPosition() + ";");
                    } else {
                        this.printWriter.print("    InstructionHandle ih_" + ih.getPosition() + " = ");
                    }
                } else {
                    this.printWriter.print("    ");
                }
                if (this.visitInstruction(i)) continue;
                i.accept(this);
            }
            this.updateBranchTargets();
            this.updateExceptionHandlers();
        }
    }

    private void updateBranchTargets() {
        this.branches.forEach(bi -> {
            BranchHandle bh = (BranchHandle)this.branchMap.get(bi);
            int pos = bh.getPosition();
            String name = bi.getName() + "_" + pos;
            int targetPos = bh.getTarget().getPosition();
            this.printWriter.println("    " + name + ".setTarget(ih_" + targetPos + ");");
            if (bi instanceof Select) {
                InstructionHandle[] ihs = ((Select)bi).getTargets();
                for (int j = 0; j < ihs.length; ++j) {
                    targetPos = ihs[j].getPosition();
                    this.printWriter.println("    " + name + ".setTarget(" + j + ", ih_" + targetPos + ");");
                }
            }
        });
    }

    private void updateExceptionHandlers() {
        CodeExceptionGen[] handlers;
        for (CodeExceptionGen h : handlers = this.methodGen.getExceptionHandlers()) {
            String type = h.getCatchType() == null ? "null" : BCELifier.printType(h.getCatchType());
            this.printWriter.println("    method.addExceptionHandler(ih_" + h.getStartPC().getPosition() + ", ih_" + h.getEndPC().getPosition() + ", ih_" + h.getHandlerPC().getPosition() + ", " + type + ");");
        }
    }

    @Override
    public void visitAllocationInstruction(AllocationInstruction i) {
        Type type = i instanceof CPInstruction ? ((CPInstruction)((Object)i)).getType(this.constantPoolGen) : ((NEWARRAY)i).getType();
        short opcode = ((Instruction)((Object)i)).getOpcode();
        short dim = 1;
        switch (opcode) {
            case 187: {
                this.printWriter.println("il.append(_factory.createNew(\"" + ((ObjectType)type).getClassName() + "\"));");
                break;
            }
            case 197: {
                dim = ((MULTIANEWARRAY)i).getDimensions();
            }
            case 188: {
                if (type instanceof ArrayType) {
                    type = ((ArrayType)type).getBasicType();
                }
            }
            case 189: {
                this.printWriter.println("il.append(_factory.createNewArray(" + BCELifier.printType(type) + ", (short) " + dim + "));");
                break;
            }
            default: {
                throw new IllegalArgumentException("Unhandled opcode: " + opcode);
            }
        }
    }

    @Override
    public void visitArrayInstruction(ArrayInstruction i) {
        short opcode = i.getOpcode();
        Type type = i.getType(this.constantPoolGen);
        String kind = opcode < 79 ? "Load" : "Store";
        this.printWriter.println("il.append(_factory.createArray" + kind + "(" + BCELifier.printType(type) + "));");
    }

    @Override
    public void visitBranchInstruction(BranchInstruction bi) {
        BranchHandle bh = (BranchHandle)this.branchMap.get(bi);
        int pos = bh.getPosition();
        String name = bi.getName() + "_" + pos;
        if (bi instanceof Select) {
            int i;
            Select s = (Select)bi;
            this.branches.add(bi);
            StringBuilder args = new StringBuilder("new int[] { ");
            int[] matchs = s.getMatchs();
            for (i = 0; i < matchs.length; ++i) {
                args.append(matchs[i]);
                if (i >= matchs.length - 1) continue;
                args.append(", ");
            }
            args.append(" }");
            this.printWriter.print("Select " + name + " = new " + bi.getName().toUpperCase(Locale.ENGLISH) + "(" + args + ", new InstructionHandle[] { ");
            for (i = 0; i < matchs.length; ++i) {
                this.printWriter.print("null");
                if (i >= matchs.length - 1) continue;
                this.printWriter.print(", ");
            }
            this.printWriter.println(" }, null);");
        } else {
            String target;
            int tPos = bh.getTarget().getPosition();
            if (pos > tPos) {
                target = "ih_" + tPos;
            } else {
                this.branches.add(bi);
                target = "null";
            }
            this.printWriter.println("    BranchInstruction " + name + " = _factory.createBranchInstruction(" + CONSTANT_PREFIX + bi.getName().toUpperCase(Locale.ENGLISH) + ", " + target + ");");
        }
        if (bh.hasTargeters()) {
            this.printWriter.println("    ih_" + pos + " = il.append(" + name + ");");
        } else {
            this.printWriter.println("    il.append(" + name + ");");
        }
    }

    @Override
    public void visitCHECKCAST(CHECKCAST i) {
        Type type = i.getType(this.constantPoolGen);
        this.printWriter.println("il.append(_factory.createCheckCast(" + BCELifier.printType(type) + "));");
    }

    @Override
    public void visitConstantPushInstruction(ConstantPushInstruction i) {
        this.createConstant(i.getValue());
    }

    @Override
    public void visitFieldInstruction(FieldInstruction i) {
        short opcode = i.getOpcode();
        String className = i.getClassName(this.constantPoolGen);
        String fieldName = i.getFieldName(this.constantPoolGen);
        Type type = i.getFieldType(this.constantPoolGen);
        this.printWriter.println("il.append(_factory.createFieldAccess(\"" + className + "\", \"" + fieldName + "\", " + BCELifier.printType(type) + ", " + CONSTANT_PREFIX + Const.getOpcodeName(opcode).toUpperCase(Locale.ENGLISH) + "));");
    }

    @Override
    public void visitINSTANCEOF(INSTANCEOF i) {
        Type type = i.getType(this.constantPoolGen);
        this.printWriter.println("il.append(_factory.createInstanceOf(" + BCELifier.printType(type) + "));");
    }

    private boolean visitInstruction(Instruction i) {
        short opcode = i.getOpcode();
        if (InstructionConst.getInstruction(opcode) != null && !(i instanceof ConstantPushInstruction) && !(i instanceof ReturnInstruction)) {
            this.printWriter.println("il.append(InstructionConst." + i.getName().toUpperCase(Locale.ENGLISH) + ");");
            return true;
        }
        return false;
    }

    @Override
    public void visitInvokeInstruction(InvokeInstruction i) {
        short opcode = i.getOpcode();
        String className = i.getClassName(this.constantPoolGen);
        String methodName = i.getMethodName(this.constantPoolGen);
        Type type = i.getReturnType(this.constantPoolGen);
        Type[] argTypes = i.getArgumentTypes(this.constantPoolGen);
        this.printWriter.println("il.append(_factory.createInvoke(\"" + className + "\", \"" + methodName + "\", " + BCELifier.printType(type) + ", " + BCELifier.printArgumentTypes(argTypes) + ", " + CONSTANT_PREFIX + Const.getOpcodeName(opcode).toUpperCase(Locale.ENGLISH) + "));");
    }

    @Override
    public void visitLDC(LDC i) {
        this.createConstant(i.getValue(this.constantPoolGen));
    }

    @Override
    public void visitLDC2_W(LDC2_W i) {
        this.createConstant(i.getValue(this.constantPoolGen));
    }

    @Override
    public void visitLocalVariableInstruction(LocalVariableInstruction i) {
        short opcode = i.getOpcode();
        Type type = i.getType(this.constantPoolGen);
        if (opcode == 132) {
            this.printWriter.println("il.append(new IINC(" + i.getIndex() + ", " + ((IINC)i).getIncrement() + "));");
        } else {
            String kind = opcode < 54 ? "Load" : "Store";
            this.printWriter.println("il.append(_factory.create" + kind + "(" + BCELifier.printType(type) + ", " + i.getIndex() + "));");
        }
    }

    @Override
    public void visitRET(RET i) {
        this.printWriter.println("il.append(new RET(" + i.getIndex() + "));");
    }

    @Override
    public void visitReturnInstruction(ReturnInstruction i) {
        Type type = i.getType(this.constantPoolGen);
        this.printWriter.println("il.append(_factory.createReturn(" + BCELifier.printType(type) + "));");
    }
}

