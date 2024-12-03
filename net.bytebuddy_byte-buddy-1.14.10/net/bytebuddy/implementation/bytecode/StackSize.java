/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bytecode;

import java.util.Arrays;
import java.util.Collection;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.implementation.bytecode.StackManipulation;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum StackSize {
    ZERO(0),
    SINGLE(1),
    DOUBLE(2);

    private final int size;

    private StackSize(int size) {
        this.size = size;
    }

    public static StackSize of(Class<?> type) {
        if (type == Void.TYPE) {
            return ZERO;
        }
        if (type == Double.TYPE || type == Long.TYPE) {
            return DOUBLE;
        }
        return SINGLE;
    }

    public static StackSize of(int size) {
        switch (size) {
            case 0: {
                return ZERO;
            }
            case 1: {
                return SINGLE;
            }
            case 2: {
                return DOUBLE;
            }
        }
        throw new IllegalArgumentException("Unexpected stack size value: " + size);
    }

    public static int of(TypeDefinition ... typeDefinition) {
        return StackSize.of(Arrays.asList(typeDefinition));
    }

    public static int of(Collection<? extends TypeDefinition> typeDefinitions) {
        int size = 0;
        for (TypeDefinition typeDefinition : typeDefinitions) {
            size += typeDefinition.getStackSize().getSize();
        }
        return size;
    }

    public int getSize() {
        return this.size;
    }

    public StackManipulation.Size toIncreasingSize() {
        return new StackManipulation.Size(this.getSize(), this.getSize());
    }

    public StackManipulation.Size toDecreasingSize() {
        return new StackManipulation.Size(-1 * this.getSize(), 0);
    }

    public StackSize maximum(StackSize stackSize) {
        switch (this) {
            case ZERO: {
                return stackSize;
            }
            case SINGLE: {
                switch (stackSize) {
                    case DOUBLE: {
                        return stackSize;
                    }
                    case SINGLE: 
                    case ZERO: {
                        return this;
                    }
                }
                throw new AssertionError();
            }
            case DOUBLE: {
                return this;
            }
        }
        throw new AssertionError();
    }
}

