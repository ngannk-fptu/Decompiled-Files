/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bytecode.collection;

import java.util.List;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.implementation.bytecode.collection.CollectionFactory;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class ArrayFactory
implements CollectionFactory {
    private final TypeDescription.Generic componentType;
    private final ArrayCreator arrayCreator;
    @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.IGNORE)
    private final StackManipulation.Size sizeDecrease;

    protected ArrayFactory(TypeDescription.Generic componentType, ArrayCreator arrayCreator) {
        this.componentType = componentType;
        this.arrayCreator = arrayCreator;
        this.sizeDecrease = StackSize.DOUBLE.toDecreasingSize().aggregate(componentType.getStackSize().toDecreasingSize());
    }

    public static ArrayFactory forType(TypeDescription.Generic componentType) {
        return new ArrayFactory(componentType, ArrayFactory.makeArrayCreatorFor(componentType));
    }

    private static ArrayCreator makeArrayCreatorFor(TypeDefinition componentType) {
        if (!componentType.isPrimitive()) {
            return new ArrayCreator.ForReferenceType(componentType.asErasure());
        }
        if (componentType.represents(Boolean.TYPE)) {
            return ArrayCreator.ForPrimitiveType.BOOLEAN;
        }
        if (componentType.represents(Byte.TYPE)) {
            return ArrayCreator.ForPrimitiveType.BYTE;
        }
        if (componentType.represents(Short.TYPE)) {
            return ArrayCreator.ForPrimitiveType.SHORT;
        }
        if (componentType.represents(Character.TYPE)) {
            return ArrayCreator.ForPrimitiveType.CHARACTER;
        }
        if (componentType.represents(Integer.TYPE)) {
            return ArrayCreator.ForPrimitiveType.INTEGER;
        }
        if (componentType.represents(Long.TYPE)) {
            return ArrayCreator.ForPrimitiveType.LONG;
        }
        if (componentType.represents(Float.TYPE)) {
            return ArrayCreator.ForPrimitiveType.FLOAT;
        }
        if (componentType.represents(Double.TYPE)) {
            return ArrayCreator.ForPrimitiveType.DOUBLE;
        }
        throw new IllegalArgumentException("Cannot create array of type " + componentType);
    }

    @Override
    public StackManipulation withValues(List<? extends StackManipulation> stackManipulations) {
        return new ArrayStackManipulation(stackManipulations);
    }

    @Override
    public TypeDescription.Generic getComponentType() {
        return this.componentType;
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
        if (!this.componentType.equals(((ArrayFactory)object).componentType)) {
            return false;
        }
        return this.arrayCreator.equals(((ArrayFactory)object).arrayCreator);
    }

    public int hashCode() {
        return (this.getClass().hashCode() * 31 + this.componentType.hashCode()) * 31 + this.arrayCreator.hashCode();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
    protected class ArrayStackManipulation
    implements StackManipulation {
        private final List<? extends StackManipulation> stackManipulations;

        protected ArrayStackManipulation(List<? extends StackManipulation> stackManipulations) {
            this.stackManipulations = stackManipulations;
        }

        @Override
        public boolean isValid() {
            for (StackManipulation stackManipulation : this.stackManipulations) {
                if (stackManipulation.isValid()) continue;
                return false;
            }
            return ArrayFactory.this.arrayCreator.isValid();
        }

        @Override
        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            StackManipulation.Size size = IntegerConstant.forValue(this.stackManipulations.size()).apply(methodVisitor, implementationContext);
            size = size.aggregate(ArrayFactory.this.arrayCreator.apply(methodVisitor, implementationContext));
            int index = 0;
            for (StackManipulation stackManipulation : this.stackManipulations) {
                methodVisitor.visitInsn(89);
                size = size.aggregate(StackSize.SINGLE.toIncreasingSize());
                size = size.aggregate(IntegerConstant.forValue(index++).apply(methodVisitor, implementationContext));
                size = size.aggregate(stackManipulation.apply(methodVisitor, implementationContext));
                methodVisitor.visitInsn(ArrayFactory.this.arrayCreator.getStorageOpcode());
                size = size.aggregate(ArrayFactory.this.sizeDecrease);
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
            if (!((Object)this.stackManipulations).equals(((ArrayStackManipulation)object).stackManipulations)) {
                return false;
            }
            return ArrayFactory.this.equals(((ArrayStackManipulation)object).ArrayFactory.this);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + ((Object)this.stackManipulations).hashCode()) * 31 + ArrayFactory.this.hashCode();
        }
    }

    protected static interface ArrayCreator
    extends StackManipulation {
        public static final StackManipulation.Size ARRAY_CREATION_SIZE_CHANGE = StackSize.ZERO.toDecreasingSize();

        public int getStorageOpcode();

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForReferenceType
        extends StackManipulation.AbstractBase
        implements ArrayCreator {
            private final String internalTypeName;

            protected ForReferenceType(TypeDescription referenceType) {
                this.internalTypeName = referenceType.getInternalName();
            }

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitTypeInsn(189, this.internalTypeName);
                return ARRAY_CREATION_SIZE_CHANGE;
            }

            public int getStorageOpcode() {
                return 83;
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
                return this.internalTypeName.equals(((ForReferenceType)object).internalTypeName);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.internalTypeName.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum ForPrimitiveType implements ArrayCreator
        {
            BOOLEAN(4, 84),
            BYTE(8, 84),
            SHORT(9, 86),
            CHARACTER(5, 85),
            INTEGER(10, 79),
            LONG(11, 80),
            FLOAT(6, 81),
            DOUBLE(7, 82);

            private final int creationOpcode;
            private final int storageOpcode;

            private ForPrimitiveType(int creationOpcode, int storageOpcode) {
                this.creationOpcode = creationOpcode;
                this.storageOpcode = storageOpcode;
            }

            @Override
            public boolean isValid() {
                return true;
            }

            @Override
            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                methodVisitor.visitIntInsn(188, this.creationOpcode);
                return ARRAY_CREATION_SIZE_CHANGE;
            }

            @Override
            public int getStorageOpcode() {
                return this.storageOpcode;
            }
        }
    }
}

