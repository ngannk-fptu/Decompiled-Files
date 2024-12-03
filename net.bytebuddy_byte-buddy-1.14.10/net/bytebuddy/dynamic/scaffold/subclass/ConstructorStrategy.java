/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.dynamic.scaffold.subclass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.Transformer;
import net.bytebuddy.dynamic.scaffold.MethodRegistry;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.implementation.attribute.MethodAttributeAppender;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.matcher.LatentMatcher;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ConstructorStrategy {
    public List<MethodDescription.Token> extractConstructors(TypeDescription var1);

    public MethodRegistry inject(TypeDescription var1, MethodRegistry var2);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class ForDefaultConstructor
    implements ConstructorStrategy {
        private final ElementMatcher<? super MethodDescription> elementMatcher;
        private final MethodAttributeAppender.Factory methodAttributeAppenderFactory;

        public ForDefaultConstructor() {
            this(ElementMatchers.any());
        }

        public ForDefaultConstructor(ElementMatcher<? super MethodDescription> elementMatcher) {
            this(elementMatcher, MethodAttributeAppender.NoOp.INSTANCE);
        }

        public ForDefaultConstructor(MethodAttributeAppender.Factory methodAttributeAppenderFactory) {
            this(ElementMatchers.any(), methodAttributeAppenderFactory);
        }

        public ForDefaultConstructor(ElementMatcher<? super MethodDescription> elementMatcher, MethodAttributeAppender.Factory methodAttributeAppenderFactory) {
            this.elementMatcher = elementMatcher;
            this.methodAttributeAppenderFactory = methodAttributeAppenderFactory;
        }

        @Override
        public List<MethodDescription.Token> extractConstructors(TypeDescription instrumentedType) {
            TypeDescription.Generic superClass = instrumentedType.getSuperClass();
            if (superClass == null) {
                throw new IllegalArgumentException("Cannot extract constructors for " + instrumentedType);
            }
            if (((MethodList)superClass.getDeclaredMethods().filter(ElementMatchers.isConstructor())).isEmpty()) {
                throw new IllegalStateException("Cannot define default constructor for class without super class constructor");
            }
            return Collections.singletonList(new MethodDescription.Token(1));
        }

        @Override
        public MethodRegistry inject(TypeDescription instrumentedType, MethodRegistry methodRegistry) {
            TypeDescription.Generic superClass = instrumentedType.getSuperClass();
            if (superClass == null) {
                throw new IllegalArgumentException("Cannot inject constructors for " + instrumentedType);
            }
            MethodList candidates = (MethodList)superClass.getDeclaredMethods().filter(ElementMatchers.isConstructor().and(this.elementMatcher));
            if (candidates.isEmpty()) {
                throw new IllegalStateException("No possible candidate for super constructor invocation in " + superClass);
            }
            if (!((MethodList)candidates.filter(ElementMatchers.takesArguments(0))).isEmpty()) {
                candidates = (MethodList)candidates.filter(ElementMatchers.takesArguments(0));
            } else if (candidates.size() > 1) {
                throw new IllegalStateException("More than one possible super constructor for constructor delegation: " + candidates);
            }
            MethodCall methodCall = MethodCall.invoke((MethodDescription)candidates.getOnly());
            for (TypeDescription typeDescription : ((MethodDescription)candidates.getOnly()).getParameters().asTypeList().asErasures()) {
                methodCall = methodCall.with(typeDescription.getDefaultValue());
            }
            return methodRegistry.append(new LatentMatcher.Resolved(ElementMatchers.isConstructor().and(ElementMatchers.takesArguments(0))), new MethodRegistry.Handler.ForImplementation(methodCall), this.methodAttributeAppenderFactory, Transformer.NoOp.<MethodDescription>make());
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
            if (!this.elementMatcher.equals(((ForDefaultConstructor)object).elementMatcher)) {
                return false;
            }
            return this.methodAttributeAppenderFactory.equals(((ForDefaultConstructor)object).methodAttributeAppenderFactory);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.elementMatcher.hashCode()) * 31 + this.methodAttributeAppenderFactory.hashCode();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Default implements ConstructorStrategy
    {
        NO_CONSTRUCTORS{

            @Override
            protected List<MethodDescription.Token> doExtractConstructors(TypeDescription superClass) {
                return Collections.emptyList();
            }

            @Override
            protected MethodRegistry doInject(MethodRegistry methodRegistry, MethodAttributeAppender.Factory methodAttributeAppenderFactory) {
                return methodRegistry;
            }
        }
        ,
        DEFAULT_CONSTRUCTOR{

            @Override
            protected List<MethodDescription.Token> doExtractConstructors(TypeDescription instrumentedType) {
                MethodList.Empty defaultConstructors;
                TypeDescription.Generic superClass = instrumentedType.getSuperClass();
                MethodList methodList = defaultConstructors = superClass == null ? new MethodList.Empty() : (MethodList)superClass.getDeclaredMethods().filter(ElementMatchers.isConstructor().and(ElementMatchers.takesArguments(0)).and(ElementMatchers.isVisibleTo(instrumentedType)));
                if (defaultConstructors.size() == 1) {
                    return Collections.singletonList(new MethodDescription.Token(1));
                }
                throw new IllegalArgumentException(instrumentedType.getSuperClass() + " declares no constructor that is visible to " + instrumentedType);
            }

            @Override
            protected MethodRegistry doInject(MethodRegistry methodRegistry, MethodAttributeAppender.Factory methodAttributeAppenderFactory) {
                return methodRegistry.append(new LatentMatcher.Resolved(ElementMatchers.isConstructor()), new MethodRegistry.Handler.ForImplementation(SuperMethodCall.INSTANCE), methodAttributeAppenderFactory, Transformer.NoOp.<MethodDescription>make());
            }
        }
        ,
        IMITATE_SUPER_CLASS{

            @Override
            protected List<MethodDescription.Token> doExtractConstructors(TypeDescription instrumentedType) {
                TypeDescription.Generic superClass = instrumentedType.getSuperClass();
                return (superClass == null ? new MethodList.Empty() : (MethodList)superClass.getDeclaredMethods().filter(ElementMatchers.isConstructor().and(ElementMatchers.isVisibleTo(instrumentedType)))).asTokenList(ElementMatchers.is(instrumentedType));
            }

            @Override
            public MethodRegistry doInject(MethodRegistry methodRegistry, MethodAttributeAppender.Factory methodAttributeAppenderFactory) {
                return methodRegistry.append(new LatentMatcher.Resolved(ElementMatchers.isConstructor()), new MethodRegistry.Handler.ForImplementation(SuperMethodCall.INSTANCE), methodAttributeAppenderFactory, Transformer.NoOp.<MethodDescription>make());
            }
        }
        ,
        IMITATE_SUPER_CLASS_PUBLIC{

            @Override
            protected List<MethodDescription.Token> doExtractConstructors(TypeDescription instrumentedType) {
                TypeDescription.Generic superClass = instrumentedType.getSuperClass();
                return (superClass == null ? new MethodList.Empty() : (MethodList)superClass.getDeclaredMethods().filter(ElementMatchers.isPublic().and(ElementMatchers.isConstructor()))).asTokenList(ElementMatchers.is(instrumentedType));
            }

            @Override
            public MethodRegistry doInject(MethodRegistry methodRegistry, MethodAttributeAppender.Factory methodAttributeAppenderFactory) {
                return methodRegistry.append(new LatentMatcher.Resolved(ElementMatchers.isConstructor()), new MethodRegistry.Handler.ForImplementation(SuperMethodCall.INSTANCE), methodAttributeAppenderFactory, Transformer.NoOp.<MethodDescription>make());
            }
        }
        ,
        IMITATE_SUPER_CLASS_OPENING{

            @Override
            protected List<MethodDescription.Token> doExtractConstructors(TypeDescription instrumentedType) {
                TypeDescription.Generic superClass = instrumentedType.getSuperClass();
                return (superClass == null ? new MethodList.Empty() : (MethodList)superClass.getDeclaredMethods().filter(ElementMatchers.isConstructor().and(ElementMatchers.isVisibleTo(instrumentedType)))).asTokenList(ElementMatchers.is(instrumentedType));
            }

            @Override
            public MethodRegistry doInject(MethodRegistry methodRegistry, MethodAttributeAppender.Factory methodAttributeAppenderFactory) {
                return methodRegistry.append(new LatentMatcher.Resolved(ElementMatchers.isConstructor()), new MethodRegistry.Handler.ForImplementation(SuperMethodCall.INSTANCE), methodAttributeAppenderFactory, Transformer.NoOp.<MethodDescription>make());
            }

            @Override
            protected int resolveModifier(int modifiers) {
                return 1;
            }
        };


        @Override
        public List<MethodDescription.Token> extractConstructors(TypeDescription instrumentedType) {
            List<MethodDescription.Token> tokens = this.doExtractConstructors(instrumentedType);
            ArrayList<MethodDescription.Token> stripped = new ArrayList<MethodDescription.Token>(tokens.size());
            for (MethodDescription.Token token : tokens) {
                stripped.add(new MethodDescription.Token(token.getName(), this.resolveModifier(token.getModifiers()), token.getTypeVariableTokens(), token.getReturnType(), token.getParameterTokens(), token.getExceptionTypes(), token.getAnnotations(), token.getDefaultValue(), TypeDescription.Generic.UNDEFINED));
            }
            return stripped;
        }

        protected int resolveModifier(int modifiers) {
            return modifiers;
        }

        protected abstract List<MethodDescription.Token> doExtractConstructors(TypeDescription var1);

        @Override
        public MethodRegistry inject(TypeDescription instrumentedType, MethodRegistry methodRegistry) {
            return this.doInject(methodRegistry, MethodAttributeAppender.NoOp.INSTANCE);
        }

        protected abstract MethodRegistry doInject(MethodRegistry var1, MethodAttributeAppender.Factory var2);

        public ConstructorStrategy with(MethodAttributeAppender.Factory methodAttributeAppenderFactory) {
            return new WithMethodAttributeAppenderFactory(this, methodAttributeAppenderFactory);
        }

        public ConstructorStrategy withInheritedAnnotations() {
            return new WithMethodAttributeAppenderFactory(this, MethodAttributeAppender.ForInstrumentedMethod.EXCLUDING_RECEIVER);
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        protected static class WithMethodAttributeAppenderFactory
        implements ConstructorStrategy {
            private final Default delegate;
            private final MethodAttributeAppender.Factory methodAttributeAppenderFactory;

            protected WithMethodAttributeAppenderFactory(Default delegate, MethodAttributeAppender.Factory methodAttributeAppenderFactory) {
                this.delegate = delegate;
                this.methodAttributeAppenderFactory = methodAttributeAppenderFactory;
            }

            @Override
            public List<MethodDescription.Token> extractConstructors(TypeDescription instrumentedType) {
                return this.delegate.extractConstructors(instrumentedType);
            }

            @Override
            public MethodRegistry inject(TypeDescription instrumentedType, MethodRegistry methodRegistry) {
                return this.delegate.doInject(methodRegistry, this.methodAttributeAppenderFactory);
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
                if (!this.delegate.equals(((WithMethodAttributeAppenderFactory)object).delegate)) {
                    return false;
                }
                return this.methodAttributeAppenderFactory.equals(((WithMethodAttributeAppenderFactory)object).methodAttributeAppenderFactory);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.delegate.hashCode()) * 31 + this.methodAttributeAppenderFactory.hashCode();
            }
        }
    }
}

