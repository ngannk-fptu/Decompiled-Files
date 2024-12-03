/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bytecode.collection;

import java.util.ArrayList;
import java.util.List;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.Duplication;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum ArrayAccess {
    BYTE(51, 84, StackSize.SINGLE),
    SHORT(53, 86, StackSize.SINGLE),
    CHARACTER(52, 85, StackSize.SINGLE),
    INTEGER(46, 79, StackSize.SINGLE),
    LONG(47, 80, StackSize.DOUBLE),
    FLOAT(48, 81, StackSize.SINGLE),
    DOUBLE(49, 82, StackSize.DOUBLE),
    REFERENCE(50, 83, StackSize.SINGLE);

    private final int loadOpcode;
    private final int storeOpcode;
    private final StackSize stackSize;

    private ArrayAccess(int loadOpcode, int storeOpcode, StackSize stackSize) {
        this.loadOpcode = loadOpcode;
        this.storeOpcode = storeOpcode;
        this.stackSize = stackSize;
    }

    public static ArrayAccess of(TypeDefinition componentType) {
        if (!componentType.isPrimitive()) {
            return REFERENCE;
        }
        if (componentType.represents(Boolean.TYPE) || componentType.represents(Byte.TYPE)) {
            return BYTE;
        }
        if (componentType.represents(Short.TYPE)) {
            return SHORT;
        }
        if (componentType.represents(Character.TYPE)) {
            return CHARACTER;
        }
        if (componentType.represents(Integer.TYPE)) {
            return INTEGER;
        }
        if (componentType.represents(Long.TYPE)) {
            return LONG;
        }
        if (componentType.represents(Float.TYPE)) {
            return FLOAT;
        }
        if (componentType.represents(Double.TYPE)) {
            return DOUBLE;
        }
        throw new IllegalArgumentException("Not a legal array type: " + componentType);
    }

    public StackManipulation load() {
        return new Loader();
    }

    public StackManipulation store() {
        return new Putter();
    }

    public StackManipulation forEach(List<? extends StackManipulation> processInstructions) {
        ArrayList<StackManipulation.Compound> stackManipulations = new ArrayList<StackManipulation.Compound>(processInstructions.size());
        int index = 0;
        for (StackManipulation stackManipulation : processInstructions) {
            stackManipulations.add(new StackManipulation.Compound(Duplication.SINGLE, IntegerConstant.forValue(index++), new Loader(), stackManipulation));
        }
        return new StackManipulation.Compound(stackManipulations);
    }

    @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
    protected class Putter
    extends StackManipulation.AbstractBase {
        protected Putter() {
        }

        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            methodVisitor.visitInsn(ArrayAccess.this.storeOpcode);
            return ArrayAccess.this.stackSize.toDecreasingSize().aggregate(new StackManipulation.Size(-2, 0));
        }

        public boolean equals(@MaybeNull Object object) {
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (this.getClass() != object.getClass()) {
                return false;
            }
            return ArrayAccess.this.equals((Object)((Putter)object).ArrayAccess.this);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + ArrayAccess.this.hashCode();
        }
    }

    @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
    protected class Loader
    extends StackManipulation.AbstractBase {
        protected Loader() {
        }

        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            methodVisitor.visitInsn(ArrayAccess.this.loadOpcode);
            return ArrayAccess.this.stackSize.toIncreasingSize().aggregate(new StackManipulation.Size(-2, 0));
        }

        public boolean equals(@MaybeNull Object object) {
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (this.getClass() != object.getClass()) {
                return false;
            }
            return ArrayAccess.this.equals((Object)((Loader)object).ArrayAccess.this);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + ArrayAccess.this.hashCode();
        }
    }
}

