/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bytecode.member;

import java.util.ArrayList;
import net.bytebuddy.build.CachedReturnPlugin;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.implementation.bytecode.assign.TypeCasting;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum MethodVariableAccess {
    INTEGER(21, 54, StackSize.SINGLE),
    LONG(22, 55, StackSize.DOUBLE),
    FLOAT(23, 56, StackSize.SINGLE),
    DOUBLE(24, 57, StackSize.DOUBLE),
    REFERENCE(25, 58, StackSize.SINGLE);

    private final int loadOpcode;
    private final int storeOpcode;
    private final StackSize size;
    private static /* synthetic */ StackManipulation loadThis;

    private MethodVariableAccess(int loadOpcode, int storeOpcode, StackSize stackSize) {
        this.loadOpcode = loadOpcode;
        this.size = stackSize;
        this.storeOpcode = storeOpcode;
    }

    public static MethodVariableAccess of(TypeDefinition typeDefinition) {
        if (typeDefinition.isPrimitive()) {
            if (typeDefinition.represents(Long.TYPE)) {
                return LONG;
            }
            if (typeDefinition.represents(Double.TYPE)) {
                return DOUBLE;
            }
            if (typeDefinition.represents(Float.TYPE)) {
                return FLOAT;
            }
            if (typeDefinition.represents(Void.TYPE)) {
                throw new IllegalArgumentException("Variable type cannot be void");
            }
            return INTEGER;
        }
        return REFERENCE;
    }

    public static MethodLoading allArgumentsOf(MethodDescription methodDescription) {
        return new MethodLoading(methodDescription, MethodLoading.TypeCastingHandler.NoOp.INSTANCE);
    }

    @CachedReturnPlugin.Enhance(value="loadThis")
    public static StackManipulation loadThis() {
        StackManipulation stackManipulation;
        StackManipulation stackManipulation2 = loadThis;
        StackManipulation stackManipulation3 = stackManipulation = stackManipulation2 != null ? null : REFERENCE.loadFrom(0);
        if (stackManipulation == null) {
            stackManipulation = loadThis;
        } else {
            loadThis = stackManipulation;
        }
        return stackManipulation;
    }

    public StackManipulation loadFrom(int offset) {
        return new OffsetLoading(offset);
    }

    public StackManipulation storeAt(int offset) {
        return new OffsetWriting(offset);
    }

    public StackManipulation increment(int offset, int value) {
        if (this != INTEGER) {
            throw new IllegalStateException("Cannot increment type: " + (Object)((Object)this));
        }
        return new OffsetIncrementing(offset, value);
    }

    public static StackManipulation load(ParameterDescription parameterDescription) {
        return MethodVariableAccess.of(parameterDescription.getType()).loadFrom(parameterDescription.getOffset());
    }

    public static StackManipulation store(ParameterDescription parameterDescription) {
        return MethodVariableAccess.of(parameterDescription.getType()).storeAt(parameterDescription.getOffset());
    }

    public static StackManipulation increment(ParameterDescription parameterDescription, int value) {
        return MethodVariableAccess.of(parameterDescription.getType()).increment(parameterDescription.getOffset(), value);
    }

    @HashCodeAndEqualsPlugin.Enhance
    protected static class OffsetIncrementing
    extends StackManipulation.AbstractBase {
        private final int offset;
        private final int value;

        protected OffsetIncrementing(int offset, int value) {
            this.offset = offset;
            this.value = value;
        }

        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            methodVisitor.visitIincInsn(this.offset, this.value);
            return StackManipulation.Size.ZERO;
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
            if (this.offset != ((OffsetIncrementing)object).offset) {
                return false;
            }
            return this.value == ((OffsetIncrementing)object).value;
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.offset) * 31 + this.value;
        }
    }

    @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
    protected class OffsetWriting
    extends StackManipulation.AbstractBase {
        private final int offset;

        protected OffsetWriting(int offset) {
            this.offset = offset;
        }

        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            methodVisitor.visitVarInsn(MethodVariableAccess.this.storeOpcode, this.offset);
            return MethodVariableAccess.this.size.toDecreasingSize();
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
            if (this.offset != ((OffsetWriting)object).offset) {
                return false;
            }
            return MethodVariableAccess.this.equals((Object)((OffsetWriting)object).MethodVariableAccess.this);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.offset) * 31 + MethodVariableAccess.this.hashCode();
        }
    }

    @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
    protected class OffsetLoading
    extends StackManipulation.AbstractBase {
        private final int offset;

        protected OffsetLoading(int offset) {
            this.offset = offset;
        }

        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            methodVisitor.visitVarInsn(MethodVariableAccess.this.loadOpcode, this.offset);
            return MethodVariableAccess.this.size.toIncreasingSize();
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
            if (this.offset != ((OffsetLoading)object).offset) {
                return false;
            }
            return MethodVariableAccess.this.equals((Object)((OffsetLoading)object).MethodVariableAccess.this);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.offset) * 31 + MethodVariableAccess.this.hashCode();
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    public static class MethodLoading
    extends StackManipulation.AbstractBase {
        private final MethodDescription methodDescription;
        private final TypeCastingHandler typeCastingHandler;

        protected MethodLoading(MethodDescription methodDescription, TypeCastingHandler typeCastingHandler) {
            this.methodDescription = methodDescription;
            this.typeCastingHandler = typeCastingHandler;
        }

        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            ArrayList<StackManipulation> stackManipulations = new ArrayList<StackManipulation>();
            for (ParameterDescription parameterDescription : this.methodDescription.getParameters()) {
                TypeDescription parameterType = parameterDescription.getType().asErasure();
                stackManipulations.add(MethodVariableAccess.of(parameterType).loadFrom(parameterDescription.getOffset()));
                stackManipulations.add(this.typeCastingHandler.ofIndex(parameterType, parameterDescription.getIndex()));
            }
            return new StackManipulation.Compound(stackManipulations).apply(methodVisitor, implementationContext);
        }

        public StackManipulation prependThisReference() {
            return this.methodDescription.isStatic() ? this : new StackManipulation.Compound(MethodVariableAccess.loadThis(), this);
        }

        public MethodLoading asBridgeOf(MethodDescription bridgeTarget) {
            return new MethodLoading(this.methodDescription, new TypeCastingHandler.ForBridgeTarget(bridgeTarget));
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
            if (!this.methodDescription.equals(((MethodLoading)object).methodDescription)) {
                return false;
            }
            return this.typeCastingHandler.equals(((MethodLoading)object).typeCastingHandler);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.methodDescription.hashCode()) * 31 + this.typeCastingHandler.hashCode();
        }

        protected static interface TypeCastingHandler {
            public StackManipulation ofIndex(TypeDescription var1, int var2);

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForBridgeTarget
            implements TypeCastingHandler {
                private final MethodDescription bridgeTarget;

                public ForBridgeTarget(MethodDescription bridgeTarget) {
                    this.bridgeTarget = bridgeTarget;
                }

                public StackManipulation ofIndex(TypeDescription parameterType, int index) {
                    TypeDescription targetType = ((ParameterDescription)this.bridgeTarget.getParameters().get(index)).getType().asErasure();
                    return parameterType.equals(targetType) ? StackManipulation.Trivial.INSTANCE : TypeCasting.to(targetType);
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
                    return this.bridgeTarget.equals(((ForBridgeTarget)object).bridgeTarget);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.bridgeTarget.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum NoOp implements TypeCastingHandler
            {
                INSTANCE;


                @Override
                public StackManipulation ofIndex(TypeDescription parameterType, int index) {
                    return StackManipulation.Trivial.INSTANCE;
                }
            }
        }
    }
}

