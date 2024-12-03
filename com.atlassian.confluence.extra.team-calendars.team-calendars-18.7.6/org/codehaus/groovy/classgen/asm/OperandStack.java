/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm;

import groovyjarjarasm.asm.Label;
import groovyjarjarasm.asm.MethodVisitor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.ConstructorNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.tools.WideningCategories;
import org.codehaus.groovy.classgen.ClassGeneratorException;
import org.codehaus.groovy.classgen.asm.BytecodeHelper;
import org.codehaus.groovy.classgen.asm.BytecodeVariable;
import org.codehaus.groovy.classgen.asm.CompileStack;
import org.codehaus.groovy.classgen.asm.WriterController;

public class OperandStack {
    private WriterController controller;
    private ArrayList<ClassNode> stack = new ArrayList();

    public OperandStack(WriterController wc) {
        this.controller = wc;
    }

    public int getStackLength() {
        return this.stack.size();
    }

    public void popDownTo(int elements) {
        int last = this.stack.size();
        MethodVisitor mv = this.controller.getMethodVisitor();
        while (last > elements) {
            ClassNode element;
            if (OperandStack.isTwoSlotType(element = this.popWithMessage(--last))) {
                mv.visitInsn(88);
                continue;
            }
            mv.visitInsn(87);
        }
    }

    private ClassNode popWithMessage(int last) {
        try {
            return this.stack.remove(last);
        }
        catch (ArrayIndexOutOfBoundsException ai) {
            String method = this.controller.getMethodNode() == null ? this.controller.getConstructorNode().getTypeDescriptor() : this.controller.getMethodNode().getTypeDescriptor();
            throw new GroovyBugError("Error while popping argument from operand stack tracker in class " + this.controller.getClassName() + " method " + method + ".");
        }
    }

    private static boolean isTwoSlotType(ClassNode type) {
        return type == ClassHelper.long_TYPE || type == ClassHelper.double_TYPE;
    }

    public void castToBool(int mark, boolean emptyDefault) {
        int size = this.stack.size();
        MethodVisitor mv = this.controller.getMethodVisitor();
        if (mark == size) {
            if (emptyDefault) {
                mv.visitIntInsn(16, 1);
            } else {
                mv.visitIntInsn(16, 0);
            }
            this.stack.add(null);
        } else if (mark == this.stack.size() - 1) {
            ClassNode last = this.stack.get(size - 1);
            if (last == ClassHelper.boolean_TYPE) {
                return;
            }
            if (!ClassHelper.isPrimitiveType(last)) {
                this.controller.getInvocationWriter().castNonPrimitiveToBool(last);
            } else {
                OperandStack.primitive2b(mv, last);
            }
        } else {
            throw new GroovyBugError("operand stack contains " + this.stack.size() + " elements, but we expected only " + mark);
        }
        this.stack.set(mark, ClassHelper.boolean_TYPE);
    }

    private static void primitive2b(MethodVisitor mv, ClassNode type) {
        Label trueLabel = new Label();
        Label falseLabel = new Label();
        if (type == ClassHelper.double_TYPE) {
            mv.visitInsn(14);
            mv.visitInsn(151);
        } else if (type == ClassHelper.long_TYPE) {
            mv.visitInsn(9);
            mv.visitInsn(148);
        } else if (type == ClassHelper.float_TYPE) {
            mv.visitInsn(11);
            mv.visitInsn(149);
        } else if (type == ClassHelper.int_TYPE) {
            // empty if block
        }
        mv.visitJumpInsn(153, falseLabel);
        mv.visitInsn(4);
        mv.visitJumpInsn(167, trueLabel);
        mv.visitLabel(falseLabel);
        mv.visitInsn(3);
        mv.visitLabel(trueLabel);
    }

    public void pop() {
        this.popDownTo(this.stack.size() - 1);
    }

    public Label jump(int ifIns) {
        Label label = new Label();
        this.jump(ifIns, label);
        return label;
    }

    public void jump(int ifIns, Label label) {
        this.controller.getMethodVisitor().visitJumpInsn(ifIns, label);
        this.remove(1);
    }

    public void dup() {
        ClassNode type = this.getTopOperand();
        this.stack.add(type);
        MethodVisitor mv = this.controller.getMethodVisitor();
        if (type == ClassHelper.double_TYPE || type == ClassHelper.long_TYPE) {
            mv.visitInsn(92);
        } else {
            mv.visitInsn(89);
        }
    }

    public ClassNode box() {
        MethodVisitor mv = this.controller.getMethodVisitor();
        int size = this.stack.size();
        ClassNode type = this.stack.get(size - 1);
        if (ClassHelper.isPrimitiveType(type) && ClassHelper.VOID_TYPE != type) {
            ClassNode wrapper = ClassHelper.getWrapper(type);
            BytecodeHelper.doCastToWrappedType(mv, type, wrapper);
            type = wrapper;
        }
        this.stack.set(size - 1, type);
        return type;
    }

    public void remove(int amount) {
        int size = this.stack.size();
        for (int i = size - 1; i > size - 1 - amount; --i) {
            this.popWithMessage(i);
        }
    }

    public void push(ClassNode type) {
        this.stack.add(type);
    }

    public void swap() {
        MethodVisitor mv = this.controller.getMethodVisitor();
        int size = this.stack.size();
        ClassNode b = this.stack.get(size - 1);
        ClassNode a = this.stack.get(size - 2);
        if (OperandStack.isTwoSlotType(a)) {
            if (OperandStack.isTwoSlotType(b)) {
                mv.visitInsn(94);
                mv.visitInsn(88);
            } else {
                mv.visitInsn(91);
                mv.visitInsn(87);
            }
        } else if (OperandStack.isTwoSlotType(b)) {
            mv.visitInsn(93);
            mv.visitInsn(88);
        } else {
            mv.visitInsn(95);
        }
        this.stack.set(size - 1, a);
        this.stack.set(size - 2, b);
    }

    public void replace(ClassNode type) {
        int size = this.stack.size();
        try {
            if (size == 0) {
                throw new ArrayIndexOutOfBoundsException("size==0");
            }
        }
        catch (ArrayIndexOutOfBoundsException ai) {
            System.err.println("index problem in " + this.controller.getSourceUnit().getName());
            throw ai;
        }
        this.stack.set(size - 1, type);
    }

    public void replace(ClassNode type, int n) {
        this.remove(n);
        this.push(type);
    }

    public void doGroovyCast(ClassNode targetType) {
        this.doConvertAndCast(targetType, false);
    }

    public void doGroovyCast(Variable v) {
        ClassNode targetType = v.getOriginType();
        this.doConvertAndCast(targetType, false);
    }

    public void doAsType(ClassNode targetType) {
        this.doConvertAndCast(targetType, true);
    }

    private void throwExceptionForNoStackElement(int size, ClassNode targetType, boolean coerce) {
        ConstructorNode constructorNode;
        if (size > 0) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Internal compiler error while compiling ").append(this.controller.getSourceUnit().getName()).append("\n");
        MethodNode methodNode = this.controller.getMethodNode();
        if (methodNode != null) {
            sb.append("Method: ");
            sb.append(methodNode);
            sb.append("\n");
        }
        if ((constructorNode = this.controller.getConstructorNode()) != null) {
            sb.append("Constructor: ");
            sb.append(methodNode);
            sb.append("\n");
        }
        sb.append("Line ").append(this.controller.getLineNumber()).append(",");
        sb.append(" expecting ").append(coerce ? "coercion" : "casting").append(" to ").append(targetType.toString(false));
        sb.append(" but operand stack is empty");
        throw new ArrayIndexOutOfBoundsException(sb.toString());
    }

    private void doConvertAndCast(ClassNode targetType, boolean coerce) {
        int size = this.stack.size();
        this.throwExceptionForNoStackElement(size, targetType, coerce);
        ClassNode top = this.stack.get(size - 1);
        targetType = targetType.redirect();
        if (targetType == top) {
            return;
        }
        if (coerce) {
            this.controller.getInvocationWriter().coerce(top, targetType);
            return;
        }
        boolean primTarget = ClassHelper.isPrimitiveType(targetType);
        boolean primTop = ClassHelper.isPrimitiveType(top);
        if (primTop && primTarget) {
            if (this.convertPrimitive(top, targetType)) {
                this.replace(targetType);
                return;
            }
            this.box();
        } else if (!primTarget) {
            this.controller.getInvocationWriter().castToNonPrimitiveIfNecessary(top, targetType);
        }
        MethodVisitor mv = this.controller.getMethodVisitor();
        if (primTarget && !ClassHelper.boolean_TYPE.equals(targetType) && !primTop && ClassHelper.getWrapper(targetType).equals(top)) {
            BytecodeHelper.doCastToPrimitive(mv, top, targetType);
        } else {
            top = this.stack.get(size - 1);
            if (!WideningCategories.implementsInterfaceOrSubclassOf(top, targetType)) {
                BytecodeHelper.doCast(mv, targetType);
            }
        }
        this.replace(targetType);
    }

    private boolean convertFromInt(ClassNode target) {
        int convertCode;
        if (target == ClassHelper.char_TYPE) {
            convertCode = 146;
        } else if (target == ClassHelper.byte_TYPE) {
            convertCode = 145;
        } else if (target == ClassHelper.short_TYPE) {
            convertCode = 147;
        } else if (target == ClassHelper.long_TYPE) {
            convertCode = 133;
        } else if (target == ClassHelper.float_TYPE) {
            convertCode = 134;
        } else if (target == ClassHelper.double_TYPE) {
            convertCode = 135;
        } else {
            return false;
        }
        this.controller.getMethodVisitor().visitInsn(convertCode);
        return true;
    }

    private boolean convertFromLong(ClassNode target) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        if (target == ClassHelper.int_TYPE) {
            mv.visitInsn(136);
            return true;
        }
        if (target == ClassHelper.char_TYPE || target == ClassHelper.byte_TYPE || target == ClassHelper.short_TYPE) {
            mv.visitInsn(136);
            return this.convertFromInt(target);
        }
        if (target == ClassHelper.double_TYPE) {
            mv.visitInsn(138);
            return true;
        }
        if (target == ClassHelper.float_TYPE) {
            mv.visitInsn(137);
            return true;
        }
        return false;
    }

    private boolean convertFromDouble(ClassNode target) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        if (target == ClassHelper.int_TYPE) {
            mv.visitInsn(142);
            return true;
        }
        if (target == ClassHelper.char_TYPE || target == ClassHelper.byte_TYPE || target == ClassHelper.short_TYPE) {
            mv.visitInsn(142);
            return this.convertFromInt(target);
        }
        if (target == ClassHelper.long_TYPE) {
            mv.visitInsn(143);
            return true;
        }
        if (target == ClassHelper.float_TYPE) {
            mv.visitInsn(144);
            return true;
        }
        return false;
    }

    private boolean convertFromFloat(ClassNode target) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        if (target == ClassHelper.int_TYPE) {
            mv.visitInsn(139);
            return true;
        }
        if (target == ClassHelper.char_TYPE || target == ClassHelper.byte_TYPE || target == ClassHelper.short_TYPE) {
            mv.visitInsn(139);
            return this.convertFromInt(target);
        }
        if (target == ClassHelper.long_TYPE) {
            mv.visitInsn(140);
            return true;
        }
        if (target == ClassHelper.double_TYPE) {
            mv.visitInsn(141);
            return true;
        }
        return false;
    }

    private boolean convertPrimitive(ClassNode top, ClassNode target) {
        if (top == target) {
            return true;
        }
        if (top == ClassHelper.int_TYPE) {
            return this.convertFromInt(target);
        }
        if (top == ClassHelper.char_TYPE || top == ClassHelper.byte_TYPE || top == ClassHelper.short_TYPE) {
            return target == ClassHelper.int_TYPE || this.convertFromInt(target);
        }
        if (top == ClassHelper.float_TYPE) {
            return this.convertFromFloat(target);
        }
        if (top == ClassHelper.double_TYPE) {
            return this.convertFromDouble(target);
        }
        if (top == ClassHelper.long_TYPE) {
            return this.convertFromLong(target);
        }
        return false;
    }

    public void pushConstant(ConstantExpression expression) {
        boolean asPrimitive;
        ClassNode type;
        MethodVisitor mv = this.controller.getMethodVisitor();
        Object value = expression.getValue();
        ClassNode origType = expression.getType().redirect();
        boolean boxing = origType != (type = ClassHelper.getUnwrapper(origType));
        boolean bl = asPrimitive = boxing || ClassHelper.isPrimitiveType(type);
        if (value == null) {
            mv.visitInsn(1);
        } else if (boxing && value instanceof Boolean) {
            Boolean bool = (Boolean)value;
            String text = bool != false ? "TRUE" : "FALSE";
            mv.visitFieldInsn(178, "java/lang/Boolean", text, "Ljava/lang/Boolean;");
            boxing = false;
            type = origType;
        } else if (asPrimitive) {
            OperandStack.pushPrimitiveConstant(mv, value, type);
        } else if (value instanceof BigDecimal) {
            String className = BytecodeHelper.getClassInternalName(value.getClass().getName());
            mv.visitTypeInsn(187, className);
            mv.visitInsn(89);
            mv.visitLdcInsn(value.toString());
            mv.visitMethodInsn(183, className, "<init>", "(Ljava/lang/String;)V", false);
        } else if (value instanceof BigInteger) {
            String className = BytecodeHelper.getClassInternalName(value.getClass().getName());
            mv.visitTypeInsn(187, className);
            mv.visitInsn(89);
            mv.visitLdcInsn(value.toString());
            mv.visitMethodInsn(183, className, "<init>", "(Ljava/lang/String;)V", false);
        } else if (value instanceof String) {
            mv.visitLdcInsn(value);
        } else {
            throw new ClassGeneratorException("Cannot generate bytecode for constant: " + value + " of type: " + type.getName());
        }
        this.push(type);
        if (boxing) {
            this.box();
        }
    }

    private static void pushPrimitiveConstant(MethodVisitor mv, Object value, ClassNode type) {
        boolean isInt = ClassHelper.int_TYPE.equals(type);
        boolean isShort = ClassHelper.short_TYPE.equals(type);
        boolean isByte = ClassHelper.byte_TYPE.equals(type);
        boolean isChar = ClassHelper.char_TYPE.equals(type);
        if (isInt || isShort || isByte || isChar) {
            int val = isInt ? (Integer)value : (isShort ? (int)((Short)value).shortValue() : (isChar ? (int)((Character)value).charValue() : (int)((Byte)value).byteValue()));
            switch (val) {
                case 0: {
                    mv.visitInsn(3);
                    break;
                }
                case 1: {
                    mv.visitInsn(4);
                    break;
                }
                case 2: {
                    mv.visitInsn(5);
                    break;
                }
                case 3: {
                    mv.visitInsn(6);
                    break;
                }
                case 4: {
                    mv.visitInsn(7);
                    break;
                }
                case 5: {
                    mv.visitInsn(8);
                    break;
                }
                default: {
                    if (val >= -128 && val <= 127) {
                        mv.visitIntInsn(16, val);
                        break;
                    }
                    if (val >= Short.MIN_VALUE && val <= Short.MAX_VALUE) {
                        mv.visitIntInsn(17, val);
                        break;
                    }
                    mv.visitLdcInsn(value);
                    break;
                }
            }
        } else if (ClassHelper.long_TYPE.equals(type)) {
            if ((Long)value == 0L) {
                mv.visitInsn(9);
            } else if ((Long)value == 1L) {
                mv.visitInsn(10);
            } else {
                mv.visitLdcInsn(value);
            }
        } else if (ClassHelper.float_TYPE.equals(type)) {
            if (((Float)value).floatValue() == 0.0f) {
                mv.visitInsn(11);
            } else if (((Float)value).floatValue() == 1.0f) {
                mv.visitInsn(12);
            } else if (((Float)value).floatValue() == 2.0f) {
                mv.visitInsn(13);
            } else {
                mv.visitLdcInsn(value);
            }
        } else if (ClassHelper.double_TYPE.equals(type)) {
            if ((Double)value == 0.0) {
                mv.visitInsn(14);
            } else if ((Double)value == 1.0) {
                mv.visitInsn(15);
            } else {
                mv.visitLdcInsn(value);
            }
        } else if (ClassHelper.boolean_TYPE.equals(type)) {
            boolean b = (Boolean)value;
            if (b) {
                mv.visitInsn(4);
            } else {
                mv.visitInsn(3);
            }
        } else {
            mv.visitLdcInsn(value);
        }
    }

    public void pushDynamicName(Expression name) {
        ConstantExpression ce;
        Object value;
        if (name instanceof ConstantExpression && (value = (ce = (ConstantExpression)name).getValue()) instanceof String) {
            this.pushConstant(ce);
            return;
        }
        new CastExpression(ClassHelper.STRING_TYPE, name).visit(this.controller.getAcg());
    }

    public void loadOrStoreVariable(BytecodeVariable variable, boolean useReferenceDirectly) {
        CompileStack compileStack = this.controller.getCompileStack();
        if (compileStack.isLHS()) {
            this.storeVar(variable);
        } else {
            MethodVisitor mv = this.controller.getMethodVisitor();
            int idx = variable.getIndex();
            ClassNode type = variable.getType();
            if (variable.isHolder()) {
                mv.visitVarInsn(25, idx);
                if (!useReferenceDirectly) {
                    mv.visitMethodInsn(182, "groovy/lang/Reference", "get", "()Ljava/lang/Object;", false);
                    BytecodeHelper.doCast(mv, type);
                    this.push(type);
                } else {
                    this.push(ClassHelper.REFERENCE_TYPE);
                }
            } else {
                this.load(type, idx);
            }
        }
    }

    public void storeVar(BytecodeVariable variable) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        int idx = variable.getIndex();
        ClassNode type = variable.getType();
        if (variable.isHolder()) {
            this.doGroovyCast(type);
            this.box();
            mv.visitVarInsn(25, idx);
            mv.visitTypeInsn(192, "groovy/lang/Reference");
            mv.visitInsn(95);
            mv.visitMethodInsn(182, "groovy/lang/Reference", "set", "(Ljava/lang/Object;)V", false);
        } else {
            this.doGroovyCast(type);
            if (type == ClassHelper.double_TYPE) {
                mv.visitVarInsn(57, idx);
            } else if (type == ClassHelper.float_TYPE) {
                mv.visitVarInsn(56, idx);
            } else if (type == ClassHelper.long_TYPE) {
                mv.visitVarInsn(55, idx);
            } else if (type == ClassHelper.boolean_TYPE || type == ClassHelper.char_TYPE || type == ClassHelper.byte_TYPE || type == ClassHelper.int_TYPE || type == ClassHelper.short_TYPE) {
                mv.visitVarInsn(54, idx);
            } else {
                mv.visitVarInsn(58, idx);
            }
        }
        this.remove(1);
    }

    public void load(ClassNode type, int idx) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        BytecodeHelper.load(mv, type, idx);
        this.push(type);
    }

    public void pushBool(boolean inclusive) {
        MethodVisitor mv = this.controller.getMethodVisitor();
        mv.visitLdcInsn(inclusive);
        this.push(ClassHelper.boolean_TYPE);
    }

    public String toString() {
        return "OperandStack(size=" + this.stack.size() + ":" + this.stack.toString() + ")";
    }

    public ClassNode getTopOperand() {
        int size = this.stack.size();
        try {
            if (size == 0) {
                throw new ArrayIndexOutOfBoundsException("size==0");
            }
        }
        catch (ArrayIndexOutOfBoundsException ai) {
            System.err.println("index problem in " + this.controller.getSourceUnit().getName());
            throw ai;
        }
        return this.stack.get(size - 1);
    }
}

