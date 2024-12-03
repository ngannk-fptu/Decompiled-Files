/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.description.type;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Constructor;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.bytebuddy.build.CachedReturnPlugin;
import net.bytebuddy.description.ByteCodeElement;
import net.bytebuddy.description.TypeVariableSource;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeVariableToken;
import net.bytebuddy.implementation.bytecode.StackSize;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.FilterableList;
import net.bytebuddy.utility.JavaConstant;
import net.bytebuddy.utility.nullability.AlwaysNull;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface TypeList
extends FilterableList<TypeDescription, TypeList> {
    @AlwaysNull
    public static final TypeList UNDEFINED = null;
    @AlwaysNull
    @SuppressFBWarnings(value={"MS_MUTABLE_ARRAY", "MS_OOI_PKGPROTECT"}, justification="Null reference cannot be mutated.")
    public static final String[] NO_INTERFACES = null;

    @MaybeNull
    public String[] toInternalNames();

    public int getStackSize();

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface Generic
    extends FilterableList<TypeDescription.Generic, Generic> {
        public TypeList asErasures();

        public Generic asRawTypes();

        public ByteCodeElement.Token.TokenList<TypeVariableToken> asTokenList(ElementMatcher<? super TypeDescription> var1);

        public Generic accept(TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> var1);

        public int getStackSize();

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static class Empty
        extends FilterableList.Empty<TypeDescription.Generic, Generic>
        implements Generic {
            @Override
            public TypeList asErasures() {
                return new net.bytebuddy.description.type.TypeList$Empty();
            }

            @Override
            public Generic asRawTypes() {
                return this;
            }

            @Override
            public Generic accept(TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor) {
                return new Empty();
            }

            @Override
            public ByteCodeElement.Token.TokenList<TypeVariableToken> asTokenList(ElementMatcher<? super TypeDescription> matcher) {
                return new ByteCodeElement.Token.TokenList((ByteCodeElement.Token[])new TypeVariableToken[0]);
            }

            @Override
            public int getStackSize() {
                return 0;
            }
        }

        public static class OfMethodExceptionTypes
        extends AbstractBase {
            private final Method method;

            public OfMethodExceptionTypes(Method method) {
                this.method = method;
            }

            public TypeDescription.Generic get(int index) {
                return new TypeProjection(this.method, index, this.method.getExceptionTypes());
            }

            public int size() {
                return this.method.getExceptionTypes().length;
            }

            public TypeList asErasures() {
                return new net.bytebuddy.description.type.TypeList$ForLoadedTypes(this.method.getExceptionTypes());
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            private static class TypeProjection
            extends TypeDescription.Generic.LazyProjection.WithEagerNavigation.OfAnnotatedElement {
                private final Method method;
                private final int index;
                private final Class<?>[] erasure;
                private transient /* synthetic */ TypeDescription.Generic resolved;

                public TypeProjection(Method method, int index, Class<?>[] erasure) {
                    this.method = method;
                    this.index = index;
                    this.erasure = erasure;
                }

                @Override
                @CachedReturnPlugin.Enhance(value="resolved")
                protected TypeDescription.Generic resolve() {
                    TypeDescription.Generic generic;
                    TypeDescription.Generic generic2;
                    TypeDescription.Generic generic3 = this.resolved;
                    if (generic3 != null) {
                        generic2 = null;
                    } else {
                        generic = this;
                        Type[] type = ((TypeProjection)generic).method.getGenericExceptionTypes();
                        generic2 = generic = ((TypeProjection)generic).erasure.length == type.length ? TypeDefinition.Sort.describe(type[((TypeProjection)generic).index], ((TypeProjection)generic).getAnnotationReader()) : ((TypeDescription.Generic.AbstractBase)generic).asRawType();
                    }
                    if (generic == null) {
                        generic = this.resolved;
                    } else {
                        this.resolved = generic;
                    }
                    return generic;
                }

                @Override
                public TypeDescription asErasure() {
                    return TypeDescription.ForLoadedType.of(this.erasure[this.index]);
                }

                @Override
                protected TypeDescription.Generic.AnnotationReader getAnnotationReader() {
                    return new TypeDescription.Generic.AnnotationReader.Delegator.ForLoadedExecutableExceptionType(this.method, this.index);
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static class OfConstructorExceptionTypes
        extends AbstractBase {
            private final Constructor<?> constructor;

            public OfConstructorExceptionTypes(Constructor<?> constructor) {
                this.constructor = constructor;
            }

            @Override
            public TypeDescription.Generic get(int index) {
                return new TypeProjection(this.constructor, index, this.constructor.getExceptionTypes());
            }

            @Override
            public int size() {
                return this.constructor.getExceptionTypes().length;
            }

            @Override
            public TypeList asErasures() {
                return new net.bytebuddy.description.type.TypeList$ForLoadedTypes(this.constructor.getExceptionTypes());
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            private static class TypeProjection
            extends TypeDescription.Generic.LazyProjection.WithEagerNavigation.OfAnnotatedElement {
                private final Constructor<?> constructor;
                private final int index;
                private final Class<?>[] erasure;
                private transient /* synthetic */ TypeDescription.Generic resolved;

                private TypeProjection(Constructor<?> constructor, int index, Class<?>[] erasure) {
                    this.constructor = constructor;
                    this.index = index;
                    this.erasure = erasure;
                }

                @Override
                @CachedReturnPlugin.Enhance(value="resolved")
                protected TypeDescription.Generic resolve() {
                    TypeDescription.Generic generic;
                    TypeDescription.Generic generic2;
                    TypeDescription.Generic generic3 = this.resolved;
                    if (generic3 != null) {
                        generic2 = null;
                    } else {
                        generic = this;
                        Type[] type = ((TypeProjection)generic).constructor.getGenericExceptionTypes();
                        generic2 = generic = ((TypeProjection)generic).erasure.length == type.length ? TypeDefinition.Sort.describe(type[((TypeProjection)generic).index], ((TypeProjection)generic).getAnnotationReader()) : ((TypeDescription.Generic.AbstractBase)generic).asRawType();
                    }
                    if (generic == null) {
                        generic = this.resolved;
                    } else {
                        this.resolved = generic;
                    }
                    return generic;
                }

                @Override
                public TypeDescription asErasure() {
                    return TypeDescription.ForLoadedType.of(this.erasure[this.index]);
                }

                @Override
                protected TypeDescription.Generic.AnnotationReader getAnnotationReader() {
                    return new TypeDescription.Generic.AnnotationReader.Delegator.ForLoadedExecutableExceptionType(this.constructor, this.index);
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static class OfLoadedInterfaceTypes
        extends AbstractBase {
            private final Class<?> type;

            public OfLoadedInterfaceTypes(Class<?> type) {
                this.type = type;
            }

            @Override
            public TypeDescription.Generic get(int index) {
                return new TypeProjection(this.type, index, this.type.getInterfaces());
            }

            @Override
            public int size() {
                return this.type.getInterfaces().length;
            }

            @Override
            public TypeList asErasures() {
                return new net.bytebuddy.description.type.TypeList$ForLoadedTypes(this.type.getInterfaces());
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            private static class TypeProjection
            extends TypeDescription.Generic.LazyProjection.WithLazyNavigation.OfAnnotatedElement {
                private final Class<?> type;
                private final int index;
                private final Class<?>[] erasure;
                private transient /* synthetic */ TypeDescription.Generic resolved;

                private TypeProjection(Class<?> type, int index, Class<?>[] erasure) {
                    this.type = type;
                    this.index = index;
                    this.erasure = erasure;
                }

                @Override
                @CachedReturnPlugin.Enhance(value="resolved")
                protected TypeDescription.Generic resolve() {
                    TypeDescription.Generic generic;
                    TypeDescription.Generic generic2;
                    TypeDescription.Generic generic3 = this.resolved;
                    if (generic3 != null) {
                        generic2 = null;
                    } else {
                        generic = this;
                        Type[] type = ((TypeProjection)generic).type.getGenericInterfaces();
                        generic2 = generic = ((TypeProjection)generic).erasure.length == type.length ? TypeDefinition.Sort.describe(type[((TypeProjection)generic).index], ((TypeProjection)generic).getAnnotationReader()) : ((TypeDescription.Generic.AbstractBase)generic).asRawType();
                    }
                    if (generic == null) {
                        generic = this.resolved;
                    } else {
                        this.resolved = generic;
                    }
                    return generic;
                }

                @Override
                public TypeDescription asErasure() {
                    return TypeDescription.ForLoadedType.of(this.erasure[this.index]);
                }

                @Override
                protected TypeDescription.Generic.AnnotationReader getAnnotationReader() {
                    return new TypeDescription.Generic.AnnotationReader.Delegator.ForLoadedInterface(this.type, this.index);
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static class ForDetachedTypes
        extends AbstractBase {
            private final List<? extends TypeDescription.Generic> detachedTypes;
            private final TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor;

            public ForDetachedTypes(List<? extends TypeDescription.Generic> detachedTypes, TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor) {
                this.detachedTypes = detachedTypes;
                this.visitor = visitor;
            }

            public static Generic attachVariables(TypeDescription typeDescription, List<? extends TypeVariableToken> detachedTypeVariables) {
                return new OfTypeVariables(typeDescription, detachedTypeVariables, TypeDescription.Generic.Visitor.Substitutor.ForAttachment.of(typeDescription));
            }

            public static Generic attach(FieldDescription fieldDescription, List<? extends TypeDescription.Generic> detachedTypes) {
                return new ForDetachedTypes(detachedTypes, TypeDescription.Generic.Visitor.Substitutor.ForAttachment.of(fieldDescription));
            }

            public static Generic attach(MethodDescription methodDescription, List<? extends TypeDescription.Generic> detachedTypes) {
                return new ForDetachedTypes(detachedTypes, TypeDescription.Generic.Visitor.Substitutor.ForAttachment.of(methodDescription));
            }

            public static Generic attachVariables(MethodDescription methodDescription, List<? extends TypeVariableToken> detachedTypeVariables) {
                return new OfTypeVariables(methodDescription, detachedTypeVariables, TypeDescription.Generic.Visitor.Substitutor.ForAttachment.of(methodDescription));
            }

            public static Generic attach(ParameterDescription parameterDescription, List<? extends TypeDescription.Generic> detachedTypes) {
                return new ForDetachedTypes(detachedTypes, TypeDescription.Generic.Visitor.Substitutor.ForAttachment.of(parameterDescription));
            }

            @Override
            public TypeDescription.Generic get(int index) {
                return this.detachedTypes.get(index).accept(this.visitor);
            }

            @Override
            public int size() {
                return this.detachedTypes.size();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static class OfTypeVariables
            extends AbstractBase {
                private final TypeVariableSource typeVariableSource;
                private final List<? extends TypeVariableToken> detachedTypeVariables;
                private final TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor;

                public OfTypeVariables(TypeVariableSource typeVariableSource, List<? extends TypeVariableToken> detachedTypeVariables, TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor) {
                    this.typeVariableSource = typeVariableSource;
                    this.detachedTypeVariables = detachedTypeVariables;
                    this.visitor = visitor;
                }

                @Override
                public TypeDescription.Generic get(int index) {
                    return new AttachedTypeVariable(this.typeVariableSource, this.detachedTypeVariables.get(index), this.visitor);
                }

                @Override
                public int size() {
                    return this.detachedTypeVariables.size();
                }

                /*
                 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
                 */
                protected static class AttachedTypeVariable
                extends TypeDescription.Generic.OfTypeVariable {
                    private final TypeVariableSource typeVariableSource;
                    private final TypeVariableToken typeVariableToken;
                    private final TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor;

                    protected AttachedTypeVariable(TypeVariableSource typeVariableSource, TypeVariableToken typeVariableToken, TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor) {
                        this.typeVariableSource = typeVariableSource;
                        this.typeVariableToken = typeVariableToken;
                        this.visitor = visitor;
                    }

                    @Override
                    public Generic getUpperBounds() {
                        return this.typeVariableToken.getBounds().accept(this.visitor);
                    }

                    @Override
                    public TypeVariableSource getTypeVariableSource() {
                        return this.typeVariableSource;
                    }

                    @Override
                    public String getSymbol() {
                        return this.typeVariableToken.getSymbol();
                    }

                    @Override
                    public AnnotationList getDeclaredAnnotations() {
                        return this.typeVariableToken.getAnnotations();
                    }
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static class WithResolvedErasure
            extends AbstractBase {
                private final List<? extends TypeDescription.Generic> detachedTypes;
                private final TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor;

                public WithResolvedErasure(List<? extends TypeDescription.Generic> detachedTypes, TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor) {
                    this.detachedTypes = detachedTypes;
                    this.visitor = visitor;
                }

                @Override
                public TypeDescription.Generic get(int index) {
                    return new TypeDescription.Generic.LazyProjection.WithResolvedErasure(this.detachedTypes.get(index), this.visitor);
                }

                @Override
                public int size() {
                    return this.detachedTypes.size();
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static class ForLoadedTypes
        extends AbstractBase {
            private final List<? extends Type> types;

            public ForLoadedTypes(Type ... type) {
                this(Arrays.asList(type));
            }

            public ForLoadedTypes(List<? extends Type> types) {
                this.types = types;
            }

            @Override
            public TypeDescription.Generic get(int index) {
                return TypeDefinition.Sort.describe(this.types.get(index));
            }

            @Override
            public int size() {
                return this.types.size();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static class OfTypeVariables
            extends AbstractBase {
                private final List<TypeVariable<?>> typeVariables;

                protected OfTypeVariables(TypeVariable<?> ... typeVariable) {
                    this(Arrays.asList(typeVariable));
                }

                protected OfTypeVariables(List<TypeVariable<?>> typeVariables) {
                    this.typeVariables = typeVariables;
                }

                public static Generic of(GenericDeclaration genericDeclaration) {
                    return new OfTypeVariables(genericDeclaration.getTypeParameters());
                }

                @Override
                public TypeDescription.Generic get(int index) {
                    TypeVariable<?> typeVariable = this.typeVariables.get(index);
                    return TypeDefinition.Sort.describe(typeVariable, new TypeDescription.Generic.AnnotationReader.Delegator.ForLoadedTypeVariable(typeVariable));
                }

                @Override
                public int size() {
                    return this.typeVariables.size();
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static class Explicit
        extends AbstractBase {
            private final List<? extends TypeDefinition> typeDefinitions;

            public Explicit(TypeDefinition ... typeDefinition) {
                this(Arrays.asList(typeDefinition));
            }

            public Explicit(List<? extends TypeDefinition> typeDefinitions) {
                this.typeDefinitions = typeDefinitions;
            }

            @Override
            public TypeDescription.Generic get(int index) {
                return this.typeDefinitions.get(index).asGenericType();
            }

            @Override
            public int size() {
                return this.typeDefinitions.size();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static abstract class AbstractBase
        extends FilterableList.AbstractBase<TypeDescription.Generic, Generic>
        implements Generic {
            @Override
            protected Generic wrap(List<TypeDescription.Generic> values) {
                return new Explicit(values);
            }

            @Override
            public Generic accept(TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor) {
                ArrayList<TypeDescription.Generic> visited = new ArrayList<TypeDescription.Generic>(this.size());
                for (TypeDescription.Generic typeDescription : this) {
                    visited.add(typeDescription.accept(visitor));
                }
                return new Explicit(visited);
            }

            @Override
            public ByteCodeElement.Token.TokenList<TypeVariableToken> asTokenList(ElementMatcher<? super TypeDescription> matcher) {
                ArrayList<TypeVariableToken> tokens = new ArrayList<TypeVariableToken>(this.size());
                for (TypeDescription.Generic typeVariable : this) {
                    tokens.add(TypeVariableToken.of(typeVariable, matcher));
                }
                return new ByteCodeElement.Token.TokenList<TypeVariableToken>((List<TypeVariableToken>)tokens);
            }

            @Override
            public int getStackSize() {
                int stackSize = 0;
                for (TypeDescription.Generic typeDescription : this) {
                    stackSize += typeDescription.getStackSize().getSize();
                }
                return stackSize;
            }

            @Override
            public TypeList asErasures() {
                ArrayList<TypeDescription> typeDescriptions = new ArrayList<TypeDescription>(this.size());
                for (TypeDescription.Generic typeDescription : this) {
                    typeDescriptions.add(typeDescription.asErasure());
                }
                return new net.bytebuddy.description.type.TypeList$Explicit(typeDescriptions);
            }

            @Override
            public Generic asRawTypes() {
                ArrayList<TypeDescription.Generic> typeDescriptions = new ArrayList<TypeDescription.Generic>(this.size());
                for (TypeDescription.Generic typeDescription : this) {
                    typeDescriptions.add(typeDescription.asRawType());
                }
                return new Explicit(typeDescriptions);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Empty
    extends FilterableList.Empty<TypeDescription, TypeList>
    implements TypeList {
        @Override
        @SuppressFBWarnings(value={"EI_EXPOSE_REP"}, justification="Value is null")
        public String[] toInternalNames() {
            return NO_INTERFACES;
        }

        @Override
        public int getStackSize() {
            return 0;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Explicit
    extends AbstractBase {
        private final List<? extends TypeDescription> typeDescriptions;

        public Explicit(TypeDescription ... typeDescription) {
            this(Arrays.asList(typeDescription));
        }

        public Explicit(List<? extends TypeDescription> typeDescriptions) {
            this.typeDescriptions = typeDescriptions;
        }

        public static TypeList of(List<? extends JavaConstant> constants) {
            ArrayList<TypeDescription> typeDescriptions = new ArrayList<TypeDescription>(constants.size());
            for (JavaConstant javaConstant : constants) {
                typeDescriptions.add(javaConstant.getTypeDescription());
            }
            return new Explicit(typeDescriptions);
        }

        @Override
        public TypeDescription get(int index) {
            return this.typeDescriptions.get(index);
        }

        @Override
        public int size() {
            return this.typeDescriptions.size();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ForLoadedTypes
    extends AbstractBase {
        private final List<? extends Class<?>> types;

        public ForLoadedTypes(Class<?> ... type) {
            this(Arrays.asList(type));
        }

        public ForLoadedTypes(List<? extends Class<?>> types) {
            this.types = types;
        }

        @Override
        public TypeDescription get(int index) {
            return TypeDescription.ForLoadedType.of(this.types.get(index));
        }

        @Override
        public int size() {
            return this.types.size();
        }

        @Override
        @MaybeNull
        public String[] toInternalNames() {
            String[] internalNames = new String[this.types.size()];
            int i = 0;
            for (Class<?> type : this.types) {
                internalNames[i++] = net.bytebuddy.jar.asm.Type.getInternalName(type);
            }
            return internalNames.length == 0 ? NO_INTERFACES : internalNames;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class AbstractBase
    extends FilterableList.AbstractBase<TypeDescription, TypeList>
    implements TypeList {
        @Override
        protected TypeList wrap(List<TypeDescription> values) {
            return new Explicit(values);
        }

        @Override
        public int getStackSize() {
            return StackSize.of(this);
        }

        @Override
        @MaybeNull
        public String[] toInternalNames() {
            String[] internalNames = new String[this.size()];
            int i = 0;
            for (TypeDescription typeDescription : this) {
                internalNames[i++] = typeDescription.getInternalName();
            }
            return internalNames.length == 0 ? NO_INTERFACES : internalNames;
        }
    }
}

