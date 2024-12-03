/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.description.method;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.bytebuddy.build.AccessControllerPlugin;
import net.bytebuddy.description.ByteCodeElement;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.FilterableList;
import net.bytebuddy.utility.dispatcher.JavaDispatcher;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ParameterList<T extends ParameterDescription>
extends FilterableList<T, ParameterList<T>> {
    public TypeList.Generic asTypeList();

    public ByteCodeElement.Token.TokenList<ParameterDescription.Token> asTokenList(ElementMatcher<? super TypeDescription> var1);

    public ParameterList<ParameterDescription.InDefinedShape> asDefined();

    public boolean hasExplicitMetaData();

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Empty<S extends ParameterDescription>
    extends FilterableList.Empty<S, ParameterList<S>>
    implements ParameterList<S> {
        @Override
        public boolean hasExplicitMetaData() {
            return true;
        }

        @Override
        public TypeList.Generic asTypeList() {
            return new TypeList.Generic.Empty();
        }

        @Override
        public ByteCodeElement.Token.TokenList<ParameterDescription.Token> asTokenList(ElementMatcher<? super TypeDescription> matcher) {
            return new ByteCodeElement.Token.TokenList((ByteCodeElement.Token[])new ParameterDescription.Token[0]);
        }

        @Override
        public ParameterList<ParameterDescription.InDefinedShape> asDefined() {
            return this;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class TypeSubstituting
    extends AbstractBase<ParameterDescription.InGenericShape> {
        private final MethodDescription.InGenericShape declaringMethod;
        private final List<? extends ParameterDescription> parameterDescriptions;
        private final TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor;

        public TypeSubstituting(MethodDescription.InGenericShape declaringMethod, List<? extends ParameterDescription> parameterDescriptions, TypeDescription.Generic.Visitor<? extends TypeDescription.Generic> visitor) {
            this.declaringMethod = declaringMethod;
            this.parameterDescriptions = parameterDescriptions;
            this.visitor = visitor;
        }

        @Override
        public ParameterDescription.InGenericShape get(int index) {
            return new ParameterDescription.TypeSubstituting(this.declaringMethod, this.parameterDescriptions.get(index), this.visitor);
        }

        @Override
        public int size() {
            return this.parameterDescriptions.size();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ForTokens
    extends AbstractBase<ParameterDescription.InDefinedShape> {
        private final MethodDescription.InDefinedShape declaringMethod;
        private final List<? extends ParameterDescription.Token> tokens;

        public ForTokens(MethodDescription.InDefinedShape declaringMethod, List<? extends ParameterDescription.Token> tokens) {
            this.declaringMethod = declaringMethod;
            this.tokens = tokens;
        }

        @Override
        public ParameterDescription.InDefinedShape get(int index) {
            int offset = this.declaringMethod.isStatic() ? 0 : 1;
            for (ParameterDescription.Token token : this.tokens.subList(0, index)) {
                offset += token.getType().getStackSize().getSize();
            }
            return new ParameterDescription.Latent(this.declaringMethod, this.tokens.get(index), index, offset);
        }

        @Override
        public int size() {
            return this.tokens.size();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class Explicit<S extends ParameterDescription>
    extends AbstractBase<S> {
        private final List<? extends S> parameterDescriptions;

        public Explicit(S ... parameterDescription) {
            this(Arrays.asList(parameterDescription));
        }

        public Explicit(List<? extends S> parameterDescriptions) {
            this.parameterDescriptions = parameterDescriptions;
        }

        @Override
        public S get(int index) {
            return (S)((ParameterDescription)this.parameterDescriptions.get(index));
        }

        @Override
        public int size() {
            return this.parameterDescriptions.size();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static class ForTypes
        extends AbstractBase<ParameterDescription.InDefinedShape> {
            private final MethodDescription.InDefinedShape methodDescription;
            private final List<? extends TypeDefinition> typeDefinitions;

            public ForTypes(MethodDescription.InDefinedShape methodDescription, TypeDefinition ... typeDefinition) {
                this(methodDescription, Arrays.asList(typeDefinition));
            }

            public ForTypes(MethodDescription.InDefinedShape methodDescription, List<? extends TypeDefinition> typeDefinitions) {
                this.methodDescription = methodDescription;
                this.typeDefinitions = typeDefinitions;
            }

            @Override
            public ParameterDescription.InDefinedShape get(int index) {
                int offset = this.methodDescription.isStatic() ? 0 : 1;
                for (int previous = 0; previous < index; ++previous) {
                    offset += this.typeDefinitions.get(previous).getStackSize().getSize();
                }
                return new ParameterDescription.Latent(this.methodDescription, this.typeDefinitions.get(index).asGenericType(), index, offset);
            }

            @Override
            public int size() {
                return this.typeDefinitions.size();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class ForLoadedExecutable<T>
    extends AbstractBase<ParameterDescription.InDefinedShape> {
        protected static final Executable EXECUTABLE;
        protected final T executable;
        protected final ParameterDescription.ForLoadedParameter.ParameterAnnotationSource parameterAnnotationSource;
        private static final boolean ACCESS_CONTROLLER;

        protected ForLoadedExecutable(T executable, ParameterDescription.ForLoadedParameter.ParameterAnnotationSource parameterAnnotationSource) {
            this.executable = executable;
            this.parameterAnnotationSource = parameterAnnotationSource;
        }

        @AccessControllerPlugin.Enhance
        private static <T> T doPrivileged(PrivilegedAction<T> privilegedAction) {
            PrivilegedAction<T> action;
            if (ACCESS_CONTROLLER) {
                return AccessController.doPrivileged(privilegedAction);
            }
            return action.run();
        }

        public static ParameterList<ParameterDescription.InDefinedShape> of(Constructor<?> constructor) {
            return ForLoadedExecutable.of(constructor, (ParameterDescription.ForLoadedParameter.ParameterAnnotationSource)new ParameterDescription.ForLoadedParameter.ParameterAnnotationSource.ForLoadedConstructor(constructor));
        }

        public static ParameterList<ParameterDescription.InDefinedShape> of(Constructor<?> constructor, ParameterDescription.ForLoadedParameter.ParameterAnnotationSource parameterAnnotationSource) {
            return EXECUTABLE.isInstance(constructor) ? new OfConstructor(constructor, parameterAnnotationSource) : new OfLegacyVmConstructor(constructor, parameterAnnotationSource);
        }

        public static ParameterList<ParameterDescription.InDefinedShape> of(Method method) {
            return ForLoadedExecutable.of(method, (ParameterDescription.ForLoadedParameter.ParameterAnnotationSource)new ParameterDescription.ForLoadedParameter.ParameterAnnotationSource.ForLoadedMethod(method));
        }

        public static ParameterList<ParameterDescription.InDefinedShape> of(Method method, ParameterDescription.ForLoadedParameter.ParameterAnnotationSource parameterAnnotationSource) {
            return EXECUTABLE.isInstance(method) ? new OfMethod(method, parameterAnnotationSource) : new OfLegacyVmMethod(method, parameterAnnotationSource);
        }

        @Override
        public int size() {
            return EXECUTABLE.getParameterCount(this.executable);
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        static {
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
            EXECUTABLE = ForLoadedExecutable.doPrivileged(JavaDispatcher.of(Executable.class));
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static class OfLegacyVmMethod
        extends AbstractBase<ParameterDescription.InDefinedShape> {
            private final Method method;
            private final Class<?>[] parameterType;
            private final ParameterDescription.ForLoadedParameter.ParameterAnnotationSource parameterAnnotationSource;

            protected OfLegacyVmMethod(Method method, ParameterDescription.ForLoadedParameter.ParameterAnnotationSource parameterAnnotationSource) {
                this.method = method;
                this.parameterType = method.getParameterTypes();
                this.parameterAnnotationSource = parameterAnnotationSource;
            }

            @Override
            public ParameterDescription.InDefinedShape get(int index) {
                return new ParameterDescription.ForLoadedParameter.OfLegacyVmMethod(this.method, index, this.parameterType, this.parameterAnnotationSource);
            }

            @Override
            public int size() {
                return this.parameterType.length;
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static class OfLegacyVmConstructor
        extends AbstractBase<ParameterDescription.InDefinedShape> {
            private final Constructor<?> constructor;
            private final Class<?>[] parameterType;
            private final ParameterDescription.ForLoadedParameter.ParameterAnnotationSource parameterAnnotationSource;

            protected OfLegacyVmConstructor(Constructor<?> constructor, ParameterDescription.ForLoadedParameter.ParameterAnnotationSource parameterAnnotationSource) {
                this.constructor = constructor;
                this.parameterType = constructor.getParameterTypes();
                this.parameterAnnotationSource = parameterAnnotationSource;
            }

            @Override
            public ParameterDescription.InDefinedShape get(int index) {
                return new ParameterDescription.ForLoadedParameter.OfLegacyVmConstructor(this.constructor, index, this.parameterType, this.parameterAnnotationSource);
            }

            @Override
            public int size() {
                return this.parameterType.length;
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static class OfMethod
        extends ForLoadedExecutable<Method> {
            protected OfMethod(Method method, ParameterDescription.ForLoadedParameter.ParameterAnnotationSource parameterAnnotationSource) {
                super(method, parameterAnnotationSource);
            }

            @Override
            public ParameterDescription.InDefinedShape get(int index) {
                return new ParameterDescription.ForLoadedParameter.OfMethod((Method)this.executable, index, this.parameterAnnotationSource);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static class OfConstructor
        extends ForLoadedExecutable<Constructor<?>> {
            protected OfConstructor(Constructor<?> constructor, ParameterDescription.ForLoadedParameter.ParameterAnnotationSource parameterAnnotationSource) {
                super(constructor, parameterAnnotationSource);
            }

            @Override
            public ParameterDescription.InDefinedShape get(int index) {
                return new ParameterDescription.ForLoadedParameter.OfConstructor((Constructor)this.executable, index, this.parameterAnnotationSource);
            }
        }

        @JavaDispatcher.Proxied(value="java.lang.reflect.Executable")
        protected static interface Executable {
            @JavaDispatcher.Instance
            @JavaDispatcher.Proxied(value="isInstance")
            public boolean isInstance(Object var1);

            @JavaDispatcher.Proxied(value="getParameterCount")
            public int getParameterCount(Object var1);

            @JavaDispatcher.Proxied(value="getParameters")
            public Object[] getParameters(Object var1);
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class AbstractBase<S extends ParameterDescription>
    extends FilterableList.AbstractBase<S, ParameterList<S>>
    implements ParameterList<S> {
        @Override
        public boolean hasExplicitMetaData() {
            for (ParameterDescription parameterDescription : this) {
                if (parameterDescription.isNamed() && parameterDescription.hasModifiers()) continue;
                return false;
            }
            return true;
        }

        @Override
        public ByteCodeElement.Token.TokenList<ParameterDescription.Token> asTokenList(ElementMatcher<? super TypeDescription> matcher) {
            ArrayList tokens = new ArrayList(this.size());
            for (ParameterDescription parameterDescription : this) {
                tokens.add(parameterDescription.asToken(matcher));
            }
            return new ByteCodeElement.Token.TokenList<ParameterDescription.Token>(tokens);
        }

        @Override
        public TypeList.Generic asTypeList() {
            ArrayList<TypeDescription.Generic> types = new ArrayList<TypeDescription.Generic>(this.size());
            for (ParameterDescription parameterDescription : this) {
                types.add(parameterDescription.getType());
            }
            return new TypeList.Generic.Explicit(types);
        }

        @Override
        public ParameterList<ParameterDescription.InDefinedShape> asDefined() {
            ArrayList declaredForms = new ArrayList(this.size());
            for (ParameterDescription parameterDescription : this) {
                declaredForms.add(parameterDescription.asDefined());
            }
            return new Explicit<ParameterDescription.InDefinedShape>(declaredForms);
        }

        @Override
        protected ParameterList<S> wrap(List<S> values) {
            return new Explicit<S>(values);
        }
    }
}

