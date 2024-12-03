/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bytecode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.utility.nullability.MaybeNull;

public interface StackManipulation {
    public boolean isValid();

    public Size apply(MethodVisitor var1, Implementation.Context var2);

    @HashCodeAndEqualsPlugin.Enhance
    public static class Simple
    extends AbstractBase {
        private final Dispatcher dispatcher;

        public Simple(Dispatcher dispatcher) {
            this.dispatcher = dispatcher;
        }

        public Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            return this.dispatcher.apply(methodVisitor, implementationContext);
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
            return this.dispatcher.equals(((Simple)object).dispatcher);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.dispatcher.hashCode();
        }

        public static interface Dispatcher {
            public Size apply(MethodVisitor var1, Implementation.Context var2);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Compound
    implements StackManipulation {
        private final List<StackManipulation> stackManipulations = new ArrayList<StackManipulation>();

        public Compound(StackManipulation ... stackManipulation) {
            this(Arrays.asList(stackManipulation));
        }

        public Compound(List<? extends StackManipulation> stackManipulations) {
            for (StackManipulation stackManipulation : stackManipulations) {
                if (stackManipulation instanceof Compound) {
                    this.stackManipulations.addAll(((Compound)stackManipulation).stackManipulations);
                    continue;
                }
                if (stackManipulation instanceof Trivial) continue;
                this.stackManipulations.add(stackManipulation);
            }
        }

        @Override
        public boolean isValid() {
            for (StackManipulation stackManipulation : this.stackManipulations) {
                if (stackManipulation.isValid()) continue;
                return false;
            }
            return true;
        }

        @Override
        public Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            Size size = Size.ZERO;
            for (StackManipulation stackManipulation : this.stackManipulations) {
                size = size.aggregate(stackManipulation.apply(methodVisitor, implementationContext));
            }
            return size;
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
            return ((Object)this.stackManipulations).equals(((Compound)object).stackManipulations);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + ((Object)this.stackManipulations).hashCode();
        }
    }

    public static abstract class AbstractBase
    implements StackManipulation {
        public boolean isValid() {
            return true;
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    public static class Size {
        public static final Size ZERO = new Size(0, 0);
        private final int sizeImpact;
        private final int maximalSize;

        public Size(int sizeImpact, int maximalSize) {
            this.sizeImpact = sizeImpact;
            this.maximalSize = maximalSize;
        }

        public int getSizeImpact() {
            return this.sizeImpact;
        }

        public int getMaximalSize() {
            return this.maximalSize;
        }

        public Size aggregate(Size other) {
            return this.aggregate(other.sizeImpact, other.maximalSize);
        }

        private Size aggregate(int sizeChange, int interimMaximalSize) {
            return new Size(this.sizeImpact + sizeChange, Math.max(this.maximalSize, this.sizeImpact + interimMaximalSize));
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
            if (this.sizeImpact != ((Size)object).sizeImpact) {
                return false;
            }
            return this.maximalSize == ((Size)object).maximalSize;
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.sizeImpact) * 31 + this.maximalSize;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Trivial implements StackManipulation
    {
        INSTANCE;


        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            return StackSize.ZERO.toIncreasingSize();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Illegal implements StackManipulation
    {
        INSTANCE;


        @Override
        public boolean isValid() {
            return false;
        }

        @Override
        public Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            throw new IllegalStateException("An illegal stack manipulation must not be applied");
        }
    }
}

