/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.implementation.bytecode.assign.reference;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.assign.TypeCasting;
import net.bytebuddy.utility.QueueFactory;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum GenericTypeAwareAssigner implements Assigner
{
    INSTANCE;


    @Override
    public StackManipulation assign(TypeDescription.Generic source, TypeDescription.Generic target, Assigner.Typing typing) {
        if (source.isPrimitive() || target.isPrimitive()) {
            return (StackManipulation)((Object)(source.equals(target) ? StackManipulation.Trivial.INSTANCE : StackManipulation.Illegal.INSTANCE));
        }
        if (source.accept(new IsAssignableToVisitor(target)).booleanValue()) {
            return StackManipulation.Trivial.INSTANCE;
        }
        if (typing.isDynamic()) {
            return source.asErasure().isAssignableTo(target.asErasure()) ? StackManipulation.Trivial.INSTANCE : TypeCasting.to(target);
        }
        return StackManipulation.Illegal.INSTANCE;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    protected static class IsAssignableToVisitor
    implements TypeDescription.Generic.Visitor<Boolean> {
        private final TypeDescription.Generic typeDescription;
        private final boolean polymorphic;

        public IsAssignableToVisitor(TypeDescription.Generic typeDescription) {
            this(typeDescription, true);
        }

        protected IsAssignableToVisitor(TypeDescription.Generic typeDescription, boolean polymorphic) {
            this.typeDescription = typeDescription;
            this.polymorphic = polymorphic;
        }

        @Override
        public Boolean onGenericArray(TypeDescription.Generic genericArray) {
            return this.typeDescription.accept(new OfGenericArray(genericArray, this.polymorphic));
        }

        @Override
        public Boolean onWildcard(TypeDescription.Generic wildcard) {
            return this.typeDescription.accept(new OfWildcard(wildcard));
        }

        @Override
        public Boolean onParameterizedType(TypeDescription.Generic parameterizedType) {
            return this.typeDescription.accept(new OfParameterizedType(parameterizedType, this.polymorphic));
        }

        @Override
        public Boolean onTypeVariable(TypeDescription.Generic typeVariable) {
            if (typeVariable.getTypeVariableSource().isInferrable()) {
                throw new UnsupportedOperationException("Assignability checks for type variables declared by methods are not currently supported");
            }
            if (typeVariable.equals(this.typeDescription)) {
                return true;
            }
            if (this.polymorphic) {
                Queue<TypeDescription.Generic> candidates = QueueFactory.make(typeVariable.getUpperBounds());
                while (!candidates.isEmpty()) {
                    TypeDescription.Generic candidate = candidates.remove();
                    if (candidate.accept(new IsAssignableToVisitor(this.typeDescription)).booleanValue()) {
                        return true;
                    }
                    if (!candidate.getSort().isTypeVariable()) continue;
                    candidates.addAll(candidate.getUpperBounds());
                }
                return false;
            }
            return false;
        }

        @Override
        public Boolean onNonGenericType(TypeDescription.Generic typeDescription) {
            return this.typeDescription.accept(new OfNonGenericType(typeDescription, this.polymorphic));
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
            if (this.polymorphic != ((IsAssignableToVisitor)object).polymorphic) {
                return false;
            }
            return this.typeDescription.equals(((IsAssignableToVisitor)object).typeDescription);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.typeDescription.hashCode()) * 31 + this.polymorphic;
        }

        protected static class OfNonGenericType
        extends OfSimpleType {
            protected OfNonGenericType(TypeDescription.Generic typeDescription, boolean polymorphic) {
                super(typeDescription, polymorphic);
            }

            public Boolean onGenericArray(TypeDescription.Generic genericArray) {
                return this.polymorphic ? this.typeDescription.asErasure().isAssignableTo(genericArray.asErasure()) : this.typeDescription.asErasure().equals(genericArray.asErasure());
            }
        }

        protected static class OfParameterizedType
        extends OfSimpleType {
            protected OfParameterizedType(TypeDescription.Generic typeDescription, boolean polymorphic) {
                super(typeDescription, polymorphic);
            }

            public Boolean onGenericArray(TypeDescription.Generic genericArray) {
                return false;
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class OfWildcard
        implements TypeDescription.Generic.Visitor<Boolean> {
            private final TypeDescription.Generic wildcard;

            protected OfWildcard(TypeDescription.Generic wildcard) {
                this.wildcard = wildcard;
            }

            @Override
            public Boolean onGenericArray(TypeDescription.Generic genericArray) {
                return false;
            }

            @Override
            public Boolean onWildcard(TypeDescription.Generic wildcard) {
                boolean hasUpperBounds = false;
                boolean hasLowerBounds = false;
                for (TypeDescription.Generic target : wildcard.getUpperBounds()) {
                    for (TypeDescription.Generic source : this.wildcard.getUpperBounds()) {
                        if (source.accept(new IsAssignableToVisitor(target)).booleanValue()) continue;
                        return false;
                    }
                    hasUpperBounds = hasUpperBounds || !target.represents((Type)((Object)Object.class));
                }
                for (TypeDescription.Generic target : wildcard.getLowerBounds()) {
                    for (TypeDescription.Generic source : this.wildcard.getLowerBounds()) {
                        if (target.accept(new IsAssignableToVisitor(source)).booleanValue()) continue;
                        return false;
                    }
                    hasLowerBounds = true;
                }
                if (hasUpperBounds) {
                    return this.wildcard.getLowerBounds().isEmpty();
                }
                if (hasLowerBounds) {
                    TypeList.Generic upperBounds = this.wildcard.getUpperBounds();
                    return upperBounds.size() == 0 || upperBounds.size() == 1 && ((TypeDescription.Generic)upperBounds.getOnly()).represents((Type)((Object)Object.class));
                }
                return true;
            }

            @Override
            public Boolean onParameterizedType(TypeDescription.Generic parameterizedType) {
                return false;
            }

            @Override
            public Boolean onTypeVariable(TypeDescription.Generic typeVariable) {
                return false;
            }

            @Override
            public Boolean onNonGenericType(TypeDescription.Generic typeDescription) {
                return false;
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
                return this.wildcard.equals(((OfWildcard)object).wildcard);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.wildcard.hashCode();
            }
        }

        protected static class OfGenericArray
        extends OfManifestType {
            protected OfGenericArray(TypeDescription.Generic typeDescription, boolean polymorphic) {
                super(typeDescription, polymorphic);
            }

            @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
            public Boolean onGenericArray(TypeDescription.Generic genericArray) {
                TypeDescription.Generic source = this.typeDescription.getComponentType();
                TypeDescription.Generic target = genericArray.getComponentType();
                while (source.getSort().isGenericArray() && target.getSort().isGenericArray()) {
                    source = source.getComponentType();
                    target = target.getComponentType();
                }
                return !source.getSort().isGenericArray() && !target.getSort().isGenericArray() && source.accept(new IsAssignableToVisitor(target)) != false;
            }

            public Boolean onParameterizedType(TypeDescription.Generic parameterizedType) {
                return false;
            }

            public Boolean onNonGenericType(TypeDescription.Generic typeDescription) {
                return this.polymorphic ? this.typeDescription.asErasure().isAssignableTo(typeDescription.asErasure()) : this.typeDescription.asErasure().equals(typeDescription.asErasure());
            }
        }

        protected static abstract class OfSimpleType
        extends OfManifestType {
            protected OfSimpleType(TypeDescription.Generic typeDescription, boolean polymorphic) {
                super(typeDescription, polymorphic);
            }

            public Boolean onParameterizedType(TypeDescription.Generic parameterizedType) {
                Queue<TypeDescription.Generic> candidates = QueueFactory.make(Collections.singleton(this.typeDescription));
                HashSet<TypeDescription> previous = new HashSet<TypeDescription>(Collections.singleton(this.typeDescription.asErasure()));
                do {
                    TypeDescription.Generic candidate;
                    if ((candidate = candidates.remove()).asErasure().equals(parameterizedType.asErasure())) {
                        if (candidate.getSort().isNonGeneric()) {
                            return true;
                        }
                        TypeList.Generic source = candidate.getTypeArguments();
                        TypeList.Generic target = parameterizedType.getTypeArguments();
                        int size = target.size();
                        if (source.size() != size) {
                            return false;
                        }
                        for (int index = 0; index < size; ++index) {
                            if (((TypeDescription.Generic)source.get(index)).accept(new IsAssignableToVisitor((TypeDescription.Generic)target.get(index), false)).booleanValue()) continue;
                            return false;
                        }
                        TypeDescription.Generic ownerType = parameterizedType.getOwnerType();
                        return ownerType == null || ownerType.accept(new IsAssignableToVisitor(ownerType)) != false;
                    }
                    if (!this.polymorphic) continue;
                    TypeDescription.Generic superClass = candidate.getSuperClass();
                    if (superClass != null && previous.add(superClass.asErasure())) {
                        candidates.add(superClass);
                    }
                    for (TypeDescription.Generic anInterface : candidate.getInterfaces()) {
                        if (!previous.add(anInterface.asErasure())) continue;
                        candidates.add(anInterface);
                    }
                } while (!candidates.isEmpty());
                return false;
            }

            public Boolean onNonGenericType(TypeDescription.Generic typeDescription) {
                return this.polymorphic ? this.typeDescription.asErasure().isAssignableTo(typeDescription.asErasure()) : this.typeDescription.asErasure().equals(typeDescription.asErasure());
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static abstract class OfManifestType
        implements TypeDescription.Generic.Visitor<Boolean> {
            protected final TypeDescription.Generic typeDescription;
            protected final boolean polymorphic;

            protected OfManifestType(TypeDescription.Generic typeDescription, boolean polymorphic) {
                this.typeDescription = typeDescription;
                this.polymorphic = polymorphic;
            }

            @Override
            public Boolean onWildcard(TypeDescription.Generic wildcard) {
                for (TypeDescription.Generic upperBound : wildcard.getUpperBounds()) {
                    if (this.typeDescription.accept(new IsAssignableToVisitor(upperBound)).booleanValue()) continue;
                    return false;
                }
                for (TypeDescription.Generic lowerBound : wildcard.getLowerBounds()) {
                    if (lowerBound.accept(new IsAssignableToVisitor(this.typeDescription)).booleanValue()) continue;
                    return false;
                }
                return true;
            }

            @Override
            public Boolean onTypeVariable(TypeDescription.Generic typeVariable) {
                if (typeVariable.getTypeVariableSource().isInferrable()) {
                    throw new UnsupportedOperationException("Assignability checks for type variables declared by methods arel not currently supported");
                }
                return false;
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
                if (this.polymorphic != ((OfManifestType)object).polymorphic) {
                    return false;
                }
                return this.typeDescription.equals(((OfManifestType)object).typeDescription);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.typeDescription.hashCode()) * 31 + this.polymorphic;
            }
        }
    }
}

