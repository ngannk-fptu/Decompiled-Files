/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package net.bytebuddy.dynamic.scaffold.inline;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.inline.MethodNameTransformer;
import net.bytebuddy.implementation.MethodAccessorFactory;
import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import net.bytebuddy.implementation.auxiliary.TrivialType;
import net.bytebuddy.utility.CompoundList;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface MethodRebaseResolver {
    public Resolution resolve(MethodDescription.InDefinedShape var1);

    public List<DynamicType> getAuxiliaryTypes();

    public Map<MethodDescription.SignatureToken, Resolution> asTokenMap();

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Default
    implements MethodRebaseResolver {
        private final Map<MethodDescription.InDefinedShape, Resolution> resolutions;
        private final List<DynamicType> dynamicTypes;

        protected Default(Map<MethodDescription.InDefinedShape, Resolution> resolutions, List<DynamicType> dynamicTypes) {
            this.resolutions = resolutions;
            this.dynamicTypes = dynamicTypes;
        }

        public static MethodRebaseResolver make(TypeDescription instrumentedType, Set<? extends MethodDescription.SignatureToken> rebaseables, ClassFileVersion classFileVersion, AuxiliaryType.NamingStrategy auxiliaryTypeNamingStrategy, MethodNameTransformer methodNameTransformer) {
            DynamicType placeholderType = null;
            HashMap<MethodDescription.InDefinedShape, Resolution> resolutions = new HashMap<MethodDescription.InDefinedShape, Resolution>();
            for (MethodDescription.InDefinedShape instrumentedMethod : instrumentedType.getDeclaredMethods()) {
                Resolution resolution;
                if (!rebaseables.contains(instrumentedMethod.asSignatureToken())) continue;
                if (instrumentedMethod.isConstructor()) {
                    if (placeholderType == null) {
                        placeholderType = TrivialType.SIGNATURE_RELEVANT.make(auxiliaryTypeNamingStrategy.name(instrumentedType, TrivialType.SIGNATURE_RELEVANT), classFileVersion, MethodAccessorFactory.Illegal.INSTANCE);
                    }
                    resolution = Resolution.ForRebasedConstructor.of(instrumentedMethod, placeholderType.getTypeDescription());
                } else {
                    resolution = Resolution.ForRebasedMethod.of(instrumentedType, instrumentedMethod, methodNameTransformer);
                }
                resolutions.put(instrumentedMethod, resolution);
            }
            return placeholderType == null ? new Default(resolutions, Collections.<DynamicType>emptyList()) : new Default(resolutions, Collections.singletonList(placeholderType));
        }

        @Override
        public Resolution resolve(MethodDescription.InDefinedShape methodDescription) {
            Resolution resolution = this.resolutions.get(methodDescription);
            return resolution == null ? new Resolution.Preserved(methodDescription) : resolution;
        }

        @Override
        public List<DynamicType> getAuxiliaryTypes() {
            return this.dynamicTypes;
        }

        @Override
        public Map<MethodDescription.SignatureToken, Resolution> asTokenMap() {
            HashMap<MethodDescription.SignatureToken, Resolution> tokenMap = new HashMap<MethodDescription.SignatureToken, Resolution>();
            for (Map.Entry<MethodDescription.InDefinedShape, Resolution> entry : this.resolutions.entrySet()) {
                tokenMap.put(entry.getKey().asSignatureToken(), entry.getValue());
            }
            return tokenMap;
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
            if (!((Object)this.resolutions).equals(((Default)object).resolutions)) {
                return false;
            }
            return ((Object)this.dynamicTypes).equals(((Default)object).dynamicTypes);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + ((Object)this.resolutions).hashCode()) * 31 + ((Object)this.dynamicTypes).hashCode();
        }
    }

    public static interface Resolution {
        public boolean isRebased();

        public MethodDescription.InDefinedShape getResolvedMethod();

        public TypeList getAppendedParameters();

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForRebasedConstructor
        implements Resolution {
            private final MethodDescription.InDefinedShape methodDescription;
            private final TypeDescription placeholderType;

            protected ForRebasedConstructor(MethodDescription.InDefinedShape methodDescription, TypeDescription placeholderType) {
                this.methodDescription = methodDescription;
                this.placeholderType = placeholderType;
            }

            public static Resolution of(MethodDescription.InDefinedShape methodDescription, TypeDescription placeholderType) {
                return new ForRebasedConstructor(new RebasedConstructor(methodDescription, placeholderType), placeholderType);
            }

            public boolean isRebased() {
                return true;
            }

            public MethodDescription.InDefinedShape getResolvedMethod() {
                return this.methodDescription;
            }

            public TypeList getAppendedParameters() {
                return new TypeList.Explicit(this.placeholderType);
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
                if (!this.methodDescription.equals(((ForRebasedConstructor)object).methodDescription)) {
                    return false;
                }
                return this.placeholderType.equals(((ForRebasedConstructor)object).placeholderType);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.methodDescription.hashCode()) * 31 + this.placeholderType.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static class RebasedConstructor
            extends MethodDescription.InDefinedShape.AbstractBase {
                private final MethodDescription.InDefinedShape methodDescription;
                private final TypeDescription placeholderType;

                protected RebasedConstructor(MethodDescription.InDefinedShape methodDescription, TypeDescription placeholderType) {
                    this.methodDescription = methodDescription;
                    this.placeholderType = placeholderType;
                }

                @Override
                public TypeDescription.Generic getReturnType() {
                    return TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Void.TYPE);
                }

                @Override
                public ParameterList<ParameterDescription.InDefinedShape> getParameters() {
                    return new ParameterList.Explicit.ForTypes((MethodDescription.InDefinedShape)this, CompoundList.of(this.methodDescription.getParameters().asTypeList().asErasures(), this.placeholderType));
                }

                @Override
                public TypeList.Generic getExceptionTypes() {
                    return this.methodDescription.getExceptionTypes().asRawTypes();
                }

                @Override
                @AlwaysNull
                public AnnotationValue<?, ?> getDefaultValue() {
                    return AnnotationValue.UNDEFINED;
                }

                @Override
                public TypeList.Generic getTypeVariables() {
                    return new TypeList.Generic.Empty();
                }

                @Override
                public AnnotationList getDeclaredAnnotations() {
                    return new AnnotationList.Empty();
                }

                @Override
                @Nonnull
                public TypeDescription getDeclaringType() {
                    return this.methodDescription.getDeclaringType();
                }

                @Override
                public int getModifiers() {
                    return 4098;
                }

                @Override
                public String getInternalName() {
                    return "<init>";
                }
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForRebasedMethod
        implements Resolution {
            private final MethodDescription.InDefinedShape methodDescription;

            protected ForRebasedMethod(MethodDescription.InDefinedShape methodDescription) {
                this.methodDescription = methodDescription;
            }

            public static Resolution of(TypeDescription instrumentedType, MethodDescription.InDefinedShape methodDescription, MethodNameTransformer methodNameTransformer) {
                return new ForRebasedMethod(new RebasedMethod(instrumentedType, methodDescription, methodNameTransformer));
            }

            public boolean isRebased() {
                return true;
            }

            public MethodDescription.InDefinedShape getResolvedMethod() {
                return this.methodDescription;
            }

            public TypeList getAppendedParameters() {
                return new TypeList.Empty();
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
                return this.methodDescription.equals(((ForRebasedMethod)object).methodDescription);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.methodDescription.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static class RebasedMethod
            extends MethodDescription.InDefinedShape.AbstractBase {
                private final TypeDescription instrumentedType;
                private final MethodDescription.InDefinedShape methodDescription;
                private final MethodNameTransformer methodNameTransformer;

                protected RebasedMethod(TypeDescription instrumentedType, MethodDescription.InDefinedShape methodDescription, MethodNameTransformer methodNameTransformer) {
                    this.instrumentedType = instrumentedType;
                    this.methodDescription = methodDescription;
                    this.methodNameTransformer = methodNameTransformer;
                }

                @Override
                public TypeDescription.Generic getReturnType() {
                    return this.methodDescription.getReturnType().asRawType();
                }

                @Override
                public ParameterList<ParameterDescription.InDefinedShape> getParameters() {
                    return new ParameterList.Explicit.ForTypes((MethodDescription.InDefinedShape)this, this.methodDescription.getParameters().asTypeList().asRawTypes());
                }

                @Override
                public TypeList.Generic getExceptionTypes() {
                    return this.methodDescription.getExceptionTypes().asRawTypes();
                }

                @Override
                @AlwaysNull
                public AnnotationValue<?, ?> getDefaultValue() {
                    return AnnotationValue.UNDEFINED;
                }

                @Override
                public TypeList.Generic getTypeVariables() {
                    return new TypeList.Generic.Empty();
                }

                @Override
                public AnnotationList getDeclaredAnnotations() {
                    return new AnnotationList.Empty();
                }

                @Override
                @Nonnull
                public TypeDescription getDeclaringType() {
                    return this.methodDescription.getDeclaringType();
                }

                @Override
                public int getModifiers() {
                    return 0x1000 | (this.methodDescription.isStatic() ? 8 : 0) | (this.methodDescription.isNative() ? 272 : 0) | (this.instrumentedType.isInterface() && !this.methodDescription.isNative() ? 1 : 2);
                }

                @Override
                public String getInternalName() {
                    return this.methodNameTransformer.transform(this.methodDescription);
                }
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class Preserved
        implements Resolution {
            private final MethodDescription.InDefinedShape methodDescription;

            public Preserved(MethodDescription.InDefinedShape methodDescription) {
                this.methodDescription = methodDescription;
            }

            public boolean isRebased() {
                return false;
            }

            public MethodDescription.InDefinedShape getResolvedMethod() {
                return this.methodDescription;
            }

            public TypeList getAppendedParameters() {
                throw new IllegalStateException("Cannot process additional parameters for non-rebased method: " + this.methodDescription);
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
                return this.methodDescription.equals(((Preserved)object).methodDescription);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.methodDescription.hashCode();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Disabled implements MethodRebaseResolver
    {
        INSTANCE;


        @Override
        public Resolution resolve(MethodDescription.InDefinedShape methodDescription) {
            return new Resolution.Preserved(methodDescription);
        }

        @Override
        public List<DynamicType> getAuxiliaryTypes() {
            return Collections.emptyList();
        }

        @Override
        public Map<MethodDescription.SignatureToken, Resolution> asTokenMap() {
            return Collections.emptyMap();
        }
    }
}

