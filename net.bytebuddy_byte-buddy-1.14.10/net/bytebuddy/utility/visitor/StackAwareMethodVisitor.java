/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.utility.visitor;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import net.bytebuddy.build.AccessControllerPlugin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.jar.asm.Handle;
import net.bytebuddy.jar.asm.Label;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.Type;
import net.bytebuddy.utility.CompoundList;
import net.bytebuddy.utility.OpenedClassReader;
import net.bytebuddy.utility.nullability.MaybeNull;
import net.bytebuddy.utility.privilege.GetSystemPropertyAction;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class StackAwareMethodVisitor
extends MethodVisitor {
    public static final String UNADJUSTED_PROPERTY = "net.bytebuddy.unadjusted";
    public static final boolean UNADJUSTED;
    private static final int[] SIZE_CHANGE;
    private List<StackSize> current = new ArrayList<StackSize>();
    private final Map<Label, List<StackSize>> sizes = new HashMap<Label, List<StackSize>>();
    private int freeIndex;
    private static final boolean ACCESS_CONTROLLER;

    protected StackAwareMethodVisitor(MethodVisitor methodVisitor, MethodDescription instrumentedMethod) {
        super(OpenedClassReader.ASM_API, methodVisitor);
        this.freeIndex = instrumentedMethod.getStackSize();
    }

    public static MethodVisitor of(MethodVisitor methodVisitor, MethodDescription instrumentedMethod) {
        return UNADJUSTED ? methodVisitor : new StackAwareMethodVisitor(methodVisitor, instrumentedMethod);
    }

    @AccessControllerPlugin.Enhance
    private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
        PrivilegedAction<T> action;
        if (ACCESS_CONTROLLER) {
            return AccessController.doPrivileged(privilegedAction);
        }
        return action.run();
    }

    private void adjustStack(int delta) {
        this.adjustStack(delta, 0);
    }

    private void adjustStack(int delta, int offset) {
        if (delta > 2) {
            throw new IllegalStateException("Cannot push multiple values onto the operand stack: " + delta);
        }
        if (delta > 0) {
            int position = this.current.size();
            while (offset > 0 && position > 0) {
                offset -= this.current.get(--position).getSize();
            }
            if (offset < 0) {
                throw new IllegalStateException("Unexpected offset underflow: " + offset);
            }
            this.current.add(position, StackSize.of(delta));
        } else {
            if (offset != 0) {
                throw new IllegalStateException("Cannot specify non-zero offset " + offset + " for non-incrementing value: " + delta);
            }
            while (delta < 0) {
                if (this.current.isEmpty()) {
                    return;
                }
                delta += this.current.remove(this.current.size() - 1).getSize();
            }
            if (delta == 1) {
                this.current.add(StackSize.SINGLE);
            } else if (delta != 0) {
                throw new IllegalStateException("Unexpected remainder on the operand stack: " + delta);
            }
        }
    }

    public void drainStack() {
        this.doDrain(this.current);
    }

    public int drainStack(int store, int load, StackSize size) {
        if (this.current.isEmpty()) {
            return 0;
        }
        int difference = this.current.get(this.current.size() - 1).getSize() - size.getSize();
        if (this.current.size() == 1 && difference == 0) {
            return 0;
        }
        super.visitVarInsn(store, this.freeIndex);
        if (difference == 1) {
            super.visitInsn(87);
        } else if (difference != 0) {
            throw new IllegalStateException("Unexpected remainder on the operand stack: " + difference);
        }
        this.doDrain(this.current.subList(0, this.current.size() - 1));
        super.visitVarInsn(load, this.freeIndex);
        return this.freeIndex + size.getSize();
    }

    private void doDrain(List<StackSize> stackSizes) {
        ListIterator<StackSize> iterator = stackSizes.listIterator(stackSizes.size());
        block4: while (iterator.hasPrevious()) {
            StackSize current = iterator.previous();
            switch (current) {
                case SINGLE: {
                    super.visitInsn(87);
                    continue block4;
                }
                case DOUBLE: {
                    super.visitInsn(88);
                    continue block4;
                }
            }
            throw new IllegalStateException("Unexpected stack size: " + (Object)((Object)current));
        }
    }

    public void register(Label label, List<StackSize> stackSizes) {
        this.sizes.put(label, stackSizes);
    }

    @Override
    public void visitInsn(int opcode) {
        switch (opcode) {
            case 172: 
            case 173: 
            case 174: 
            case 175: 
            case 176: 
            case 177: 
            case 191: {
                this.current.clear();
                break;
            }
            case 90: 
            case 93: {
                this.adjustStack(SIZE_CHANGE[opcode], SIZE_CHANGE[opcode] + 1);
                break;
            }
            case 91: 
            case 94: {
                this.adjustStack(SIZE_CHANGE[opcode], SIZE_CHANGE[opcode] + 2);
                break;
            }
            case 136: 
            case 137: 
            case 142: 
            case 144: {
                this.adjustStack(-2);
                this.adjustStack(1);
                break;
            }
            case 133: 
            case 135: 
            case 140: 
            case 141: {
                this.adjustStack(-1);
                this.adjustStack(2);
                break;
            }
            case 47: 
            case 49: {
                this.adjustStack(-2);
                this.adjustStack(2);
                break;
            }
            default: {
                this.adjustStack(SIZE_CHANGE[opcode]);
            }
        }
        super.visitInsn(opcode);
    }

    @Override
    public void visitIntInsn(int opcode, int operand) {
        this.adjustStack(SIZE_CHANGE[opcode]);
        super.visitIntInsn(opcode, operand);
    }

    @Override
    @SuppressFBWarnings(value={"SF_SWITCH_NO_DEFAULT"}, justification="No action required on default option.")
    public void visitVarInsn(int opcode, int variable) {
        switch (opcode) {
            case 54: 
            case 56: 
            case 58: {
                this.freeIndex = Math.max(this.freeIndex, variable + 1);
                break;
            }
            case 55: 
            case 57: {
                this.freeIndex = Math.max(this.freeIndex, variable + 2);
                break;
            }
            case 169: {
                this.current.clear();
            }
        }
        this.adjustStack(SIZE_CHANGE[opcode]);
        super.visitVarInsn(opcode, variable);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        this.adjustStack(SIZE_CHANGE[opcode]);
        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        int baseline = Type.getType(descriptor).getSize();
        switch (opcode) {
            case 180: {
                this.adjustStack(-1);
                this.adjustStack(baseline);
                break;
            }
            case 178: {
                this.adjustStack(baseline);
                break;
            }
            case 181: {
                this.adjustStack(-baseline - 1);
                break;
            }
            case 179: {
                this.adjustStack(-baseline);
                break;
            }
            default: {
                throw new IllegalStateException("Unexpected opcode: " + opcode);
            }
        }
        super.visitFieldInsn(opcode, owner, name, descriptor);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        int baseline = Type.getArgumentsAndReturnSizes(descriptor);
        this.adjustStack(-(baseline >> 2) + (opcode == 184 ? 1 : 0));
        this.adjustStack(baseline & 3);
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrap, Object ... bootstrapArguments) {
        int baseline = Type.getArgumentsAndReturnSizes(descriptor);
        this.adjustStack(-(baseline >> 2) + 1);
        this.adjustStack(baseline & 3);
        super.visitInvokeDynamicInsn(name, descriptor, bootstrap, bootstrapArguments);
    }

    @Override
    public void visitLdcInsn(Object value) {
        this.adjustStack(value instanceof Long || value instanceof Double ? 2 : 1);
        super.visitLdcInsn(value);
    }

    @Override
    public void visitMultiANewArrayInsn(String descriptor, int dimension) {
        this.adjustStack(1 - dimension);
        super.visitMultiANewArrayInsn(descriptor, dimension);
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        this.adjustStack(SIZE_CHANGE[opcode]);
        this.sizes.put(label, new ArrayList<StackSize>(opcode == 168 ? CompoundList.of(this.current, StackSize.SINGLE) : this.current));
        if (opcode == 167) {
            this.current.clear();
        }
        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLabel(Label label) {
        List<StackSize> current = this.sizes.get(label);
        if (current != null) {
            this.current = new ArrayList<StackSize>(current);
        }
        super.visitLabel(label);
    }

    @Override
    public void visitLineNumber(int line, Label start) {
        super.visitLineNumber(line, start);
    }

    @Override
    public void visitTableSwitchInsn(int minimum, int maximum, Label defaultOption, Label ... option) {
        this.adjustStack(-1);
        ArrayList<StackSize> current = new ArrayList<StackSize>(this.current);
        this.sizes.put(defaultOption, current);
        for (Label label : option) {
            this.sizes.put(label, current);
        }
        super.visitTableSwitchInsn(minimum, maximum, defaultOption, option);
    }

    @Override
    public void visitLookupSwitchInsn(Label defaultOption, int[] key, Label[] option) {
        this.adjustStack(-1);
        ArrayList<StackSize> current = new ArrayList<StackSize>(this.current);
        this.sizes.put(defaultOption, current);
        for (Label label : option) {
            this.sizes.put(label, current);
        }
        super.visitLookupSwitchInsn(defaultOption, key, option);
    }

    @Override
    public void visitTryCatchBlock(Label start, Label end, Label handler, @MaybeNull String type) {
        this.sizes.put(handler, Collections.singletonList(StackSize.SINGLE));
        super.visitTryCatchBlock(start, end, handler, type);
    }

    @Override
    @SuppressFBWarnings(value={"RC_REF_COMPARISON_BAD_PRACTICE"}, justification="ASM models frames by reference identity.")
    public void visitFrame(int type, int localVariableLength, @MaybeNull Object[] localVariable, int stackSize, @MaybeNull Object[] stack) {
        switch (type) {
            case 1: 
            case 2: 
            case 3: {
                this.current.clear();
                break;
            }
            case 4: {
                this.current.clear();
                if (stack[0] == Opcodes.LONG || stack[0] == Opcodes.DOUBLE) {
                    this.current.add(StackSize.DOUBLE);
                    break;
                }
                this.current.add(StackSize.SINGLE);
                break;
            }
            case -1: 
            case 0: {
                this.current.clear();
                for (int index = 0; index < stackSize; ++index) {
                    if (stack[index] == Opcodes.LONG || stack[index] == Opcodes.DOUBLE) {
                        this.current.add(StackSize.DOUBLE);
                        continue;
                    }
                    this.current.add(StackSize.SINGLE);
                }
                break;
            }
            default: {
                throw new IllegalStateException("Unknown frame type: " + type);
            }
        }
        super.visitFrame(type, localVariableLength, localVariable, stackSize, stack);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    static {
        boolean disabled;
        try {
            Class.forName("java.security.AccessController", false, null);
            ACCESS_CONTROLLER = Boolean.parseBoolean(System.getProperty("net.bytebuddy.securitymanager", "true"));
        }
        catch (ClassNotFoundException classNotFoundException) {
            ACCESS_CONTROLLER = false;
        }
        catch (SecurityException securityException) {
            ACCESS_CONTROLLER = true;
        }
        try {
            disabled = Boolean.parseBoolean(StackAwareMethodVisitor.doPrivileged(new GetSystemPropertyAction(UNADJUSTED_PROPERTY)));
        }
        catch (Exception ignored) {
            disabled = false;
        }
        UNADJUSTED = disabled;
        SIZE_CHANGE = new int[202];
        String encoded = "EFFFFFFFFGGFFFGGFFFEEFGFGFEEEEEEEEEEEEEEEEEEEEDEDEDDDDDCDCDEEEEEEEEEEEEEEEEEEEEBABABBBBDCFFFGGGEDCDCDCDCDCDCDCDCDCDCEEEEDDDDDDDCDCDCEFEFDDEEFFDEDEEEBDDBBDDDDDDCCCCCCCCEEEDDDCDCDEEEEEEEEEEFEEEEEEDDEEDDEE";
        int index = 0;
        while (index < SIZE_CHANGE.length) {
            StackAwareMethodVisitor.SIZE_CHANGE[index] = encoded.charAt(index) - 69;
            ++index;
        }
        return;
    }
}

